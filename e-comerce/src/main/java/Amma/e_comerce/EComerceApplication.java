package Amma.e_comerce;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
public class EComerceApplication {
	public static void main(String[] args) {
		SpringApplication.run(EComerceApplication.class, args);
	}
}