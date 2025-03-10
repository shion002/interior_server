package port.interior;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {
    @Bean
    public Dotenv dotenv() {
        // .env 파일 경로를 명시적으로 설정
        return Dotenv.configure().directory("/etc/secrets").load();
    }
}
