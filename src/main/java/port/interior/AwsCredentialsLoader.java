package port.interior;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class AwsCredentialsLoader {
    @PostConstruct
    public void loadEnvFile() {
        try {
            File file = new File("/etc/secrets/aws-credentials.env");
            if (!file.exists()) {
                System.out.println("AWS credentials file not found, skipping loading.");
                return; // 파일이 없으면 로딩을 건너뜀 (앱이 크래시되지 않음)
            }

            Properties props = new Properties();
            props.load(new FileInputStream(file));
            props.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));

            System.out.println("AWS credentials loaded successfully.");
        } catch (IOException e) {
            System.err.println("Failed to load AWS credentials: " + e.getMessage());
        }
    }
}

