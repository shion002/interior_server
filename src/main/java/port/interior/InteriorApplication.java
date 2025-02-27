package port.interior;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InteriorApplication {

	public static void main(String[] args) {
		SpringApplication.run(InteriorApplication.class, args);

		// 환경 변수 출력 (서버 로그에서 확인 가능)
		System.out.println("AWS_REGION: " + System.getenv("S3_REGION"));
		System.out.println("AWS_BUCKET: " + System.getenv("S3_BUCKET_NAME"));
		System.out.println("AWS_ACCESS_KEY: " + System.getenv("AWS_ACCESS_KEY"));
		System.out.println("AWS_SECRET_KEY: " + System.getenv("AWS_SECRET_KEY"));
	}

}
