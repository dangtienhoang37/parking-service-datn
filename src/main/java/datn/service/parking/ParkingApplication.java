package datn.service.parking;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ParkingApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load();
		ApplicationContext context = SpringApplication.run(ParkingApplication.class, args);
//		System.out.println(dotenv.get("DBUrl"));
//		System.out.println(dotenv.get("DBUsername"));
//		System.out.println(dotenv.get("DBPassword"));
		Environment env = context.getEnvironment();


	}

}
