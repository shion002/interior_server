package port.interior;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import port.interior.repository.AdminRepository;
import port.interior.service.AdminService;

@SpringBootApplication
public class InteriorApplication {

	public static void main(String[] args) {
		SpringApplication.run(InteriorApplication.class, args);
	}

}
