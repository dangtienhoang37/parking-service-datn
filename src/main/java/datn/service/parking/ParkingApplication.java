package datn.service.parking;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Objects;
import java.util.TimeZone;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableCaching
public class ParkingApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		Dotenv dotenv = Dotenv.load();
		ApplicationContext context = SpringApplication.run(ParkingApplication.class, args);
//		System.out.println(dotenv.get("DBUrl"));
//		System.out.println(dotenv.get("DBUsername"));
//		System.out.println(dotenv.get("DBPassword"));
		Environment env = context.getEnvironment();



	}
	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
	}
	@Bean
	public Cloudinary cloudinary(Environment env) {
		String cloudinaryUrl = env.getProperty("cloudinary.url");
		if (Objects.isNull(cloudinaryUrl) || cloudinaryUrl.isEmpty()) {
			throw new RuntimeException("Cloudinary URL is not configured!");
		}
		return new Cloudinary(cloudinaryUrl);
	}
}
