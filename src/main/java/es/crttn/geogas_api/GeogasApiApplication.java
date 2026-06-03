package es.crttn.geogas_api;

import es.crttn.geogas_api.services.GasStationSyncService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GeogasApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeogasApiApplication.class, args);
	}

	// Código de prueba para descargar los primeros datos
	@Bean
    CommandLineRunner testSync(GasStationSyncService syncService) {
		return args -> {
			System.out.println("INICIANDO PRUEBA DE DESCARGA Y GUARDADO");

			//syncService.syncData();

			System.out.println("PRUEBA FINALIZADA. REVISA TU BASE DE DATOS");
		};
	}

}
