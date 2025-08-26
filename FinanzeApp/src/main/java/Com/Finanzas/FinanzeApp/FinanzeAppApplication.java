package Com.Finanzas.FinanzeApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling   // ✅ esto activa las tareas programadas

@SpringBootApplication
public class FinanzeAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanzeAppApplication.class, args);
	}

}
