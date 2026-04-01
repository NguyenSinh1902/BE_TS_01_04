package iuh.fit.se;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // thêm Annotation này để kích hoạt tính năng quét các phương thức có @Scheduled trong ứng dụng
public class Trasua01Application {

	public static void main(String[] args) {
		SpringApplication.run(Trasua01Application.class, args);
	}

}
