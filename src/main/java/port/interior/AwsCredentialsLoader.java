package port.interior;

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
            Properties props = new Properties();
            props.load(new FileInputStream("/etc/secrets/aws-credentials.env"));
            props.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load AWS credentials", e);
        }
    }
}

