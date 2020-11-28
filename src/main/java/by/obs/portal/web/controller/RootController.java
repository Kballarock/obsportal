package by.obs.portal.web.controller;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static by.obs.portal.common.Constants.*;

@Controller
public class RootController {

    @RequestMapping("/login")
    public String redirectAuthUserFromLoginPage() {
        if (getAuthUserPrivilege("WRITE_PRIVILEGE")) {
            return "redirect:" + ADMIN_URL;
        } else if (getAuthUserPrivilege("READ_PRIVILEGE")) {
            return "redirect:" + HOME_URL;
        }
        return "login";
    }

    @RequestMapping("/registration")
    public String redirectAuthUserFromRegistrationPage() {
        if (getAuthUserPrivilege("WRITE_PRIVILEGE")) {
            return "redirect:" + ADMIN_URL;
        } else if (getAuthUserPrivilege("READ_PRIVILEGE")) {
            return "redirect:" + HOME_URL;
        }
        return "registration";
    }

    private boolean getAuthUserPrivilege(String privilege) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority(privilege));
    }
}