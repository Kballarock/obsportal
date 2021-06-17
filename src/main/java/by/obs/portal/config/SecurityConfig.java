package by.obs.portal.config;

import by.obs.portal.security.ObsRememberMeServices;
import by.obs.portal.security.ObsUserDetailsService;
import by.obs.portal.security.handlers.*;
import by.obs.portal.security.jwt.JwtAuthenticationFilter;
import by.obs.portal.security.handlers.ObsOAuth2AuthenticationSuccessHandler;
import by.obs.portal.security.rest.RestAuthenticationEntryPoint;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

@ComponentScan("by.obs.portal.security")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Configuration
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class UISecurityConfig extends WebSecurityConfigurerAdapter {

        private static final int TOKEN_VALIDITY_SECONDS = 60 * 60;

        OidcUserService oidcUserService;
        ObsOAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;
        ObsAuthenticationSuccessHandler successHandler;
        ObsAuthenticationFailureHandler failureHandler;
        ObsLogoutSuccessHandler logoutSuccessHandler;
        ObsUserDetailsService userDetailsService;
        ObsAccessDeniedHandler accessDeniedHandler;

        @Autowired
        public UISecurityConfig(OidcUserService oidcUserService,
                                ObsAuthenticationSuccessHandler successHandler,
                                ObsAuthenticationFailureHandler failureHandler,
                                ObsLogoutSuccessHandler logoutSuccessHandler,
                                ObsUserDetailsService userDetailsService,
                                ObsAccessDeniedHandler accessDeniedHandler,
                                ObsOAuth2AuthenticationSuccessHandler oAuth2SuccessHandler) {
            this.oidcUserService = oidcUserService;
            this.successHandler = successHandler;
            this.failureHandler = failureHandler;
            this.logoutSuccessHandler = logoutSuccessHandler;
            this.userDetailsService = userDetailsService;
            this.accessDeniedHandler = accessDeniedHandler;
            this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        }

        @Override
        public void configure(final WebSecurity web) {
            web.ignoring().antMatchers("/resources/**", "/webjars/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/index", "/forgetPassword*", "/service/resetPassword*", "/login*",
                            "/registration*", "/service/changeNewPassword*", "/successRegistration", "/exception",
                            "/confirmRegistration*", "/registrationError*", "/resendRegistrationToken*").permitAll()
                    .antMatchers("/**/admin/**", "/users").hasAuthority("WRITE_PRIVILEGE")
                    .antMatchers("/home/**").hasAnyAuthority("READ_PRIVILEGE", "WRITE_PRIVILEGE")
                    .antMatchers("/service/updatePassword*", "/profile/editPassword*",
                            "/service/savePassword*", "/updatePassword*").hasAuthority("CHANGE_PASSWORD_PRIVILEGE")

                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .failureUrl("/login?error=true")
                    .successHandler(successHandler)
                    .failureHandler(failureHandler)
                    .permitAll()

                    .and()
                    .logout()
                    .logoutSuccessHandler(logoutSuccessHandler)
                    .invalidateHttpSession(false)
                    .deleteCookies("JSESSIONID")
                    .permitAll()

                    .and()
                    .rememberMe()
                    .rememberMeServices(rememberMeServices())
                    .key("theKey")
                    .tokenValiditySeconds(TOKEN_VALIDITY_SECONDS)

                    .and()
                    .sessionManagement()
                    .invalidSessionUrl("/login?invalidSession=true")
                    .maximumSessions(1).sessionRegistry(sessionRegistry())
                    .and()
                    .sessionFixation().none()

                    .and()
                    .exceptionHandling().accessDeniedHandler(accessDeniedHandler)

                    .and()
                    .oauth2Login()
                    .loginPage("/login")
                    .redirectionEndpoint()
                    .baseUri("/oauth2/callback/*")

                    .and()
                    .userInfoEndpoint()
                    .oidcUserService(oidcUserService)

                    .and()
                    .authorizationEndpoint()
                    .baseUri("/oauth2/authorize")
                    .authorizationRequestRepository(customAuthorizationRequestRepository())

                    .and()
                    .successHandler(oAuth2SuccessHandler)
                    .permitAll();

            http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        }

        @Bean
        public JwtAuthenticationFilter authenticationTokenFilterBean() {
            return new JwtAuthenticationFilter();
        }

        @Bean
        public AuthorizationRequestRepository<OAuth2AuthorizationRequest> customAuthorizationRequestRepository() {
            return new HttpSessionOAuth2AuthorizationRequestRepository();
        }

        @Bean
        public RememberMeServices rememberMeServices() {
            return new ObsRememberMeServices("theKey", userDetailsService, new InMemoryTokenRepositoryImpl());
        }

        @Bean
        public SessionRegistry sessionRegistry() {
            return new SessionRegistryImpl();
        }
    }

    @Configuration
    @Order(1)
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class RestSecurityConfig extends WebSecurityConfigurerAdapter {

        RestAuthenticationEntryPoint authenticationEntryPoint;

        @Autowired
        public RestSecurityConfig(RestAuthenticationEntryPoint authenticationEntryPoint) {
            this.authenticationEntryPoint = authenticationEntryPoint;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .requestMatchers()
                    .antMatchers("/rest/**")

                    .and()
                    .authorizeRequests()
                    .antMatchers("/rest/admin/**").hasAuthority("WRITE_PRIVILEGE")
                    .antMatchers("/rest/registration", "/rest/confirmRegistration*",
                            "/rest/service/resetPassword*", "/rest/resendRegistrationToken*",
                            "/rest/service/changeNewPassword*").anonymous()
                    .antMatchers("/rest/service/updatePassword*",
                            "/rest/service/savePassword*").hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
                    .antMatchers("/**").authenticated()

                    .and()
                    .httpBasic()
                    .authenticationEntryPoint(authenticationEntryPoint)

                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }
    }
}