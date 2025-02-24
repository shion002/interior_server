package port.interior.service;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import port.interior.dto.AdminResponseDto;
import port.interior.dto.LoginDto;
import port.interior.entity.Admin;
import port.interior.repository.AdminRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Commit
class AdminServiceTest {
    @Autowired AdminService adminService;
    @Autowired
    EntityManager em;

    @Test
    void 로그인(){

        AdminResponseDto login = adminService.login(new LoginDto("admin", "admin123"));
        System.out.println("login = " + login);
        Assertions.assertThat(login.getUsername()).isEqualTo("admin");
    }
}






























