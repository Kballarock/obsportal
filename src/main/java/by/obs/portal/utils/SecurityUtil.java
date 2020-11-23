package by.obs.portal.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static by.obs.portal.common.Constants.ADMIN;
import static by.obs.portal.common.Constants.USER;

@UtilityClass
public class SecurityUtil {

    public static String getUserAuthority(Collection<? extends GrantedAuthority> authorities) {
        boolean isUser = false;
        boolean isAdmin = false;

        for (final GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("READ_PRIVILEGE")) {
                isUser = true;
            } else if (grantedAuthority.getAuthority().equals("WRITE_PRIVILEGE")) {
                isAdmin = true;
                isUser = false;
                break;
            }
        }
        return isUser ? USER : (isAdmin ? ADMIN : "Unknown_user");
    }
}