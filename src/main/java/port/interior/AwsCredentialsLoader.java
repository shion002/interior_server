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
                return;
            }

            Properties props = new Properties();
            props.load(new FileInputStream(file));
            props.forEach((key, value) -> {
                String envKey = key.toString();
                String envValue = value.toString();
                System.setProperty(envKey, envValue);
                System.out.println("Loaded env: " + envKey + " = " + envValue);
            });

            System.out.println("AWS credentials loaded successfully.");
        } catch (IOException e) {
            System.err.println("Failed to load AWS credentials: " + e.getMessage());
        }
    }
}

