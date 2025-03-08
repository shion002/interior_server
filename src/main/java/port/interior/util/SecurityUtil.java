package port.interior.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import port.interior.entity.Admin;
import port.interior.repository.AdminRepository;

import java.util.Optional;

@Slf4j
public class SecurityUtil {
    public static Optional<String> getCurrentAdminUsername(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication.getName() == null){
            return Optional.empty();
        }

        return Optional.of(authentication.getName());
    }

    public static Optional<Admin> getCurrentAdmin(AdminRepository adminRepository){
        Optional<String> usernameOpt = getCurrentAdminUsername();
        log.info("현재 로그인한 사용자: {}", usernameOpt.orElse("없음"));

        if (usernameOpt.isPresent()) {
            Optional<Admin> admin = adminRepository.findByUsername(usernameOpt.get());
            log.info("DB에서 찾은 관리자: {}", admin.orElse(null));
            return admin;
        }

        return getCurrentAdminUsername().flatMap(adminRepository::findByUsername);
    }
}
