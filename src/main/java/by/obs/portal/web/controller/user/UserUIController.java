package by.obs.portal.web.controller.user;

import by.obs.portal.persistence.model.User;
import by.obs.portal.security.ObsUserDetailsService;
import by.obs.portal.service.PasswordResetTokenService;
import by.obs.portal.service.UserService;
import by.obs.portal.service.VerificationTokenService;
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
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;

import static by.obs.portal.utils.MailUtil.resendResetPasswordTokenEmail;
import static by.obs.portal.utils.MailUtil.resendVerificationTokenEmail;
import static by.obs.portal.utils.UriUtil.getAppUrl;

@Controller
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserUIController {

    Logger log = LoggerFactory.getLogger(getClass());
    ObsUserDetailsService userDetailsService;
    UserService userService;
    VerificationTokenService verificationTokenService;
    PasswordResetTokenService passwordResetTokenService;
    MessageSource messages;
    JavaMailSender mailSender;
    ApplicationEventPublisher eventPublisher;
    UniqueMailValidator uniqueMailValidator;

    @Autowired
    public UserUIController(UserService userService, @Qualifier("messageSource") MessageSource messages,
                            JavaMailSender mailSender, ApplicationEventPublisher eventPublisher,
                            UniqueMailValidator uniqueMailValidator, ObsUserDetailsService userDetailsService,
                            VerificationTokenService verificationTokenService,
                            PasswordResetTokenService passwordResetTokenService) {
        this.userService = userService;
        this.messages = messages;
        this.mailSender = mailSender;
        this.eventPublisher = eventPublisher;
        this.uniqueMailValidator = uniqueMailValidator;
        this.userDetailsService = userDetailsService;
        this.verificationTokenService = verificationTokenService;
        this.passwordResetTokenService = passwordResetTokenService;
    }

    @InitBinder({"userDto"})
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.addValidators(uniqueMailValidator);
    }

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("userDto")) {
            model.addAttribute("userDto", new UserDto());
        }
        return "registration";
    }

    @PostMapping("/registration")
    public String registerNewUserAccount(final RedirectAttributes redirectAttributes, final HttpServletRequest request,
                                         @ModelAttribute("userDto") @Validated(ErrorSequence.class) final UserDto userDto,
                                         final BindingResult result) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userDto", result);
            redirectAttributes.addFlashAttribute("userDto", userDto);
            return "redirect:/registration";
        }

        log.debug("Register new user account: {}", userDto);
        final var newUser = userService.create(UserUtil.createNewFromDto(userDto));
        final var appUrl = getAppUrl(request);
        eventPublisher.publishEvent(new RegistrationCompleteEvent(newUser, request.getLocale(), appUrl));

        return "successRegistration";
    }

    @GetMapping("/confirmRegistration")
    public String confirmRegistration(final RedirectAttributes redirectAttributes, @RequestParam("token") final String token) {

        var locale = LocaleContextHolder.getLocale();
        final var verificationToken = verificationTokenService.getVerificationToken(token);

        if (verificationToken == null) {
            redirectAttributes.addFlashAttribute("message",
                    messages.getMessage("message.invalidRegToken", null, locale));
            return "redirect:/registrationError";
        }

        final var user = verificationToken.getUser();

        if (LocalDateTime.now().isAfter(verificationToken.getExpiryDate())) {
            redirectAttributes.addFlashAttribute("message",
                    messages.getMessage("message.expiredRegToken", null, locale));
            redirectAttributes.addFlashAttribute("expired", true);
            redirectAttributes.addFlashAttribute("token", token);
            return "redirect:/registrationError";
        }

        user.setEnabled(true);
        userService.saveRegisteredUser(user);
        redirectAttributes.addFlashAttribute("message",
                messages.getMessage("message.accountVerified", null, locale));
        return "redirect:/login";
    }

    @GetMapping("/resendRegistrationToken")
    public String resendRegistrationToken(final HttpServletRequest request, final RedirectAttributes redirectAttributes,
                                          @RequestParam("token") final String existingToken) {

        final var locale = LocaleContextHolder.getLocale();
        final var token = verificationTokenService.generateNewVerificationToken(existingToken);
        final var user = verificationTokenService.getByVerificationToken(token.getToken());

        try {
            log.debug("Resend registration token for user: {}", user.getEmail());
            final var appUrl = getAppUrl(request);
            final var email = resendVerificationTokenEmail(appUrl, locale, token, user);
            mailSender.send(email);
        } catch (final Exception e) {
            log.debug(e.getLocalizedMessage(), e);
            redirectAttributes.addFlashAttribute("message", e.getLocalizedMessage());
            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute("message",
                messages.getMessage("message.resendToken", null, locale));
        return "redirect:/login";
    }

    @PostMapping("/service/resetPassword")
    public String resetUserPassword(final HttpServletRequest request,
                                    final RedirectAttributes redirectAttributes, @RequestParam("email") final String email) {

        var locale = LocaleContextHolder.getLocale();
        final var user = userService.getByEmail(email);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error",
                    messages.getMessage("message.userNotFound", new String[]{email}, locale));
            log.debug("User with email [ {} ] not found. User is not registered", email);
            return "redirect:/forgetPassword";
        }

        final var token = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetToken(user, token);

        try {
            final var resetMessage = resendResetPasswordTokenEmail(getAppUrl(request), locale, token, user);
            mailSender.send(resetMessage);
            log.debug("Reset password message successfully sent to user: [ {} ]", user.getEmail());
        } catch (final MailAuthenticationException e) {
            log.debug("MailAuthenticationException", e);
            return "redirect:/login";
        } catch (final Exception e) {
            log.debug(e.getLocalizedMessage(), e);
            redirectAttributes.addFlashAttribute("message", e.getLocalizedMessage());
            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute("message",
                messages.getMessage("message.resetPasswordEmail", null, locale));

        return "redirect:/login";
    }

    @GetMapping("/service/changeNewPassword")
    public String changeNewPassword(final Model redirectAttributes,
                                    @RequestParam("id") final int id, @RequestParam("token") final String token) {

        final var locale = LocaleContextHolder.getLocale();
        final var passwordToken = passwordResetTokenService.getPasswordResetToken(token);

        if (passwordToken == null || passwordToken.getUser().id() != id) {
            redirectAttributes.addAttribute("error",
                    messages.getMessage("message.invalidToken", null, locale));
            return "redirect:/login";
        }

        if (LocalDateTime.now().isAfter(passwordToken.getExpiryDate())) {
            redirectAttributes.addAttribute("error",
                    messages.getMessage("message.expiredLink", null, locale));
            return "redirect:/login";
        }

        final var user = passwordToken.getUser();
        final var auth = new UsernamePasswordAuthenticationToken(user, null,
                userDetailsService.loadUserByUsername(user.getEmail()).getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        return "redirect:/updatePassword";
    }

    @GetMapping("/updatePassword")
    public String updatePassword(Model model) {
        if (!model.containsAttribute("updatePassword")) {
            model.addAttribute("updatePassword", new UpdateUserPasswordDto());
        }
        return "updatePassword";
    }

    @PostMapping("/service/savePassword")
    public String saveUpdatedPassword(final RedirectAttributes redirectAttributes, @Validated(ErrorSequence.class)
    @ModelAttribute("updatePassword") UpdateUserPasswordDto updatePassword,
                                      final BindingResult result) {
        final var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updatePassword", result);
            redirectAttributes.addFlashAttribute("updatePassword", updatePassword);
            return "redirect:/updatePassword";
        } else {
            userService.updatePassword(user, updatePassword.getConfirmNewPassword());
            log.debug("User: [ {} ] reset password successfully and redirect to 'Login' page.", user.getEmail());

            SecurityContextHolder.clearContext();
            redirectAttributes.addFlashAttribute("message",
                    messages.getMessage("message.resetPasswordSuccess", null, LocaleContextHolder.getLocale()));

            return "redirect:/login";
        }
    }
}