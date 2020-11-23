package by.obs.portal.web.controller;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RootController {

    @RequestMapping("/login")
    public String redirectAuthUserFromLoginPage() {
        if (getAuthUserPrivilege("WRITE_PRIVILEGE")) {
            return "redirect:/admin";
        } else if (getAuthUserPrivilege("READ_PRIVILEGE")) {
            return "redirect:/home";
        }
        return "login";
    }

    @RequestMapping("/registration")
    public String redirectAuthUserFromRegistrationPage() {
        if (getAuthUserPrivilege("WRITE_PRIVILEGE")) {
            return "redirect:/admin";
        } else if (getAuthUserPrivilege("READ_PRIVILEGE")) {
            return "redirect:/home";
        }
        return "registration";
    }

    private boolean getAuthUserPrivilege(String privilege) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority(privilege));
    }
}