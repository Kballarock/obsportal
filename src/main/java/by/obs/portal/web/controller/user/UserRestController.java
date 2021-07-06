package by.obs.portal.web.controller.user;

import by.obs.portal.persistence.model.user.User;
import by.obs.portal.service.PasswordResetTokenService;
import by.obs.portal.service.UserService;
import by.obs.portal.service.VerificationTokenService;
import by.obs.portal.utils.UriUtil;
import by.obs.portal.utils.user.UserUtil;
import by.obs.portal.validation.UniqueMailValidator;
import by.obs.portal.validation.view.ErrorSequence;
import by.obs.portal.web.dto.UpdateUserPasswordDto;
import by.obs.portal.web.dto.UserDto;
import by.obs.portal.web.registration.RegistrationCompleteEvent;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.UUID;

import static by.obs.portal.common.Constants.*;
import static by.obs.portal.utils.MailUtil.resendResetPasswordTokenEmail;
import static by.obs.portal.utils.MailUtil.resendVerificationTokenEmail;
import static by.obs.portal.utils.UriUtil.getAppUrlForRest;

@RestController
@RequestMapping(value = UserRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserRestController {

    static final String REST_URL = "/rest";
    Logger log = LoggerFactory.getLogger(getClass());
    UserService userService;
    ApplicationEventPublisher eventPublisher;
    UniqueMailValidator uniqueMailValidator;
    JavaMailSender mailSender;
    PasswordResetTokenService passwordResetTokenService;
    VerificationTokenService verificationTokenService;
    MessageSource messages;

    @Autowired
    public UserRestController(UserService userService, ApplicationEventPublisher eventPublisher,
                              UniqueMailValidator uniqueMailValidator, JavaMailSender mailSender,
                              PasswordResetTokenService passwordResetTokenService,
                              VerificationTokenService verificationTokenService,
                              @Qualifier("messageSource") MessageSource messages) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.uniqueMailValidator = uniqueMailValidator;
        this.mailSender = mailSender;
        this.passwordResetTokenService = passwordResetTokenService;
        this.verificationTokenService = verificationTokenService;
        this.messages = messages;
    }

    @InitBinder({"userDto"})
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.addValidators(uniqueMailValidator);
    }

    @PostMapping(value = "/registration", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<User> registerNewUserAccount(@Valid @RequestBody final UserDto userDto,
                                                       final HttpServletRequest request) {
        log.debug("Register new user account: {}", userDto);

        final var newUser = userService.create(UserUtil.createNewFromDto(userDto));
        final var uriOfNewResource = UriUtil.createNewURI(REST_URL, newUser.getId());

        eventPublisher.publishEvent(new RegistrationCompleteEvent(newUser, request.getLocale(), getAppUrlForRest(request)));
        return ResponseEntity.created(uriOfNewResource).body(newUser);
    }

    @GetMapping(value = "/confirmRegistration")
    public ResponseEntity<String> confirmRegistration(final HttpServletRequest request,
                                                      @RequestParam("token") final String token) {

        final var locale = request.getLocale();
        final var result = verificationTokenService.validateVerificationToken(token);

        if (result.equals(TOKEN_INVALID)) {
            return new ResponseEntity<>(
                    messages.getMessage("message.invalidRegToken", null, locale),
                    HttpStatus.BAD_REQUEST);
        }

        if (result.equals(TOKEN_EXPIRED)) {
            return new ResponseEntity<>(
                    messages.getMessage("message.expiredRegToken", null, locale),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(
                messages.getMessage("message.accountVerified", null, locale),
                HttpStatus.OK);
    }

    @GetMapping(value = "/resendRegistrationToken")
    public ResponseEntity<String> resendRegistrationToken(@RequestParam("token") final String token,
                                                          final HttpServletRequest request) {

        final var newToken = verificationTokenService.generateNewVerificationToken(token);
        final var user = verificationTokenService.getByVerificationToken(newToken.getToken());

        mailSender.send(resendVerificationTokenEmail(getAppUrlForRest(request), request.getLocale(), newToken, user));

        return new ResponseEntity<>(
                messages.getMessage("message.resendToken", null, request.getLocale()),
                HttpStatus.OK);
    }

    @GetMapping(value = "/service/resetPassword")
    public ResponseEntity<String> resetPassword(final HttpServletRequest request,
                                                @RequestParam("email") final String email) {

        final var locale = request.getLocale();
        final var user = userService.getByEmail(email);

        if (user == null) {
            return new ResponseEntity<>(
                    messages.getMessage("message.userNotFound", new String[]{email}, locale),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        final var token = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetToken(user, token);

        final var resetMessage = resendResetPasswordTokenEmail(getAppUrlForRest(request), locale, token, user);
        mailSender.send(resetMessage);

        return new ResponseEntity<>(
                messages.getMessage("message.resetPasswordEmail", null, locale),
                HttpStatus.OK);
    }

    @GetMapping("/service/changeNewPassword")
    public RedirectView changeNewPassword(final RedirectAttributes redirectAttributes, @RequestParam("id") final int id,
                                          @RequestParam("token") final String token) {

        final var locale = LocaleContextHolder.getLocale();
        final var result = passwordResetTokenService.validatePasswordResetToken(id, token);

        if (result.equals(TOKEN_INVALID)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messages.getMessage("message.invalidToken", null, locale));
            return new RedirectView(LOGIN_URL);
        }

        if (result.equals(TOKEN_EXPIRED)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messages.getMessage("message.expiredLink", null, locale));
            return new RedirectView(LOGIN_URL);
        }
        return new RedirectView(UPDATE_PSW_URL);
    }

    @PostMapping(value = "/service/savePassword", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> savePassword(@Validated(ErrorSequence.class)
                                               @RequestBody UpdateUserPasswordDto updatePasswordDto) {

        final var user = userService.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        userService.updatePassword(user, updatePasswordDto.getNewPassword());

        return new ResponseEntity<>(
                messages.getMessage("message.resetPasswordSuccess", null, LocaleContextHolder.getLocale()),
                HttpStatus.OK);
    }

    @PutMapping(value = "/service/updatePassword", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updatePassword(@Validated(ErrorSequence.class)
                                                 @RequestBody UpdateUserPasswordDto updatePasswordDto) {

        final var user = userService.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if (!userService.validOldPassword(user, updatePasswordDto.getOldPassword())) {
            return new ResponseEntity<>(
                    messages.getMessage("message.updatePasswordError", null, LocaleContextHolder.getLocale()),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        userService.updatePassword(user, updatePasswordDto.getNewPassword());

        return new ResponseEntity<>(
                messages.getMessage("message.resetPasswordSuccess", null, LocaleContextHolder.getLocale()),
                HttpStatus.OK);
    }
}