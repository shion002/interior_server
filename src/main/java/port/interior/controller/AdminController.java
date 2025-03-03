package port.interior.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import port.interior.dto.AdminResponseDto;
import port.interior.dto.LoginDto;
import port.interior.entity.Admin;
import port.interior.jwt.JwtService;
import port.interior.service.AdminService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletResponse response){
        AdminResponseDto loginMember = adminService.login(loginDto);

        if(loginMember == null){
            return ResponseEntity.status(401).body(
                    Map.of(
                            "status", 401,
                            "error", "Unauthorized",
                            "message", "Invalid username or password",
                            "timestamp", java.time.LocalDateTime.now().toString()
                    )
            );
        }

        String jwtToken = jwtService.generateToken(loginMember.getUsername());

        response.setHeader("Authorization", "Bearer " + jwtToken);

        return ResponseEntity.ok(Map.of(
                "token", jwtToken,
                "username", loginMember.getUsername()
        ));

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response){
        response.setHeader("Authorization", "");

        return ResponseEntity.ok(Map.of("message", "Logout Success"));
    }
}





















