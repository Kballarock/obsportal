package by.obs.portal.security.oath2;

import by.obs.portal.persistence.model.user.AuthProvider;
import by.obs.portal.persistence.model.user.Role;
import by.obs.portal.persistence.model.user.User;
import by.obs.portal.persistence.repository.RoleRepository;
import by.obs.portal.persistence.repository.UserRepository;
import by.obs.portal.security.oath2.user.OAuth2UserInfo;
import by.obs.portal.security.oath2.user.OAuth2UserInfoFactory;
import by.obs.portal.utils.exception.OAuth2AuthenticationProcessingException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CustomOIdcUserService extends OidcUserService {

    BCryptPasswordEncoder passwordEncoder;
    UserRepository userRepository;
    RoleRepository roleRepository;

    @Autowired
    public CustomOIdcUserService(BCryptPasswordEncoder passwordEncoder,
                                 UserRepository userRepository,
                                 RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        var oIdcUser = super.loadUser(userRequest);
        processOAuth2User(userRequest, oIdcUser);
        return oIdcUser;
    }

    private void processOAuth2User(OidcUserRequest userRequest, OidcUser oidcUser) {
        var oAuth2UserInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(userRequest.getClientRegistration().getRegistrationId(), oidcUser.getAttributes());

        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        var user = userRepository.getByEmail(oAuth2UserInfo.getEmail());
        if (user != null) {
            if (!user.getProvider().equals(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            updateExistingUser(user, oAuth2UserInfo);
        } else {
            registerNewUser(userRequest, oAuth2UserInfo);
        }
    }


    private void registerNewUser(OidcUserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
        var user = new User();
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(roleRepository.getByName("ROLE_USER"));

        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setEnabled(true);
        user.setProvider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setRoles(userRoles);

        userRepository.save(user);
    }

    private void updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        userRepository.save(existingUser);
    }
}