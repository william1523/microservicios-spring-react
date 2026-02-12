package ec.com.will1523.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthApplication {

	public static void main(String[] args) {
		System.out.println("Iniciando aplicación");
		SpringApplication.run(AuthApplication.class, args);
	}

}
