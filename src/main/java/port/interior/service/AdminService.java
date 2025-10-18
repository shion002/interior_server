package port.interior.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import port.interior.dto.LoginDto;
import port.interior.dto.AdminResponseDto;
import port.interior.entity.Admin;
import port.interior.repository.AdminRepository;
import port.interior.util.SecurityUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminService {
    private final AdminRepository adminRepository;

    @Value("${admin.id}")
    private String adminId;

    @Value("${admin.password}")
    private String adminPassword;

    public AdminResponseDto login(LoginDto loginDto){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Admin admin = adminRepository.findByUsername(loginDto.getUsername()).orElse(null);
        if(admin != null && encoder.matches(loginDto.getPassword(), admin.getPassword())) {
            return new AdminResponseDto(admin.getId(), admin.getUsername());
        }
        return null;
    }

    public void save(){
        Admin admin = new Admin(adminId, adminPassword);
        adminRepository.save(admin);
    }

    public List<Admin> findByAll(){
        return adminRepository.findAll();
    }
    public void deleteAdmin(){
        adminRepository.deleteAll();
    }

    public Admin currentAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("현재 인증된 사용자: {}", authentication.getName());

        if (!authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        return SecurityUtil.getCurrentAdmin(adminRepository)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 관리자를 찾을 수 없습니다"));
    }
}
