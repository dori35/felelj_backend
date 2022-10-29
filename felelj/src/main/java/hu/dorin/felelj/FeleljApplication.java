package hu.dorin.felelj;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class FeleljApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeleljApplication.class, args);
	}
	
	@Bean
	public ModelMapper modelMapper() {
	   return new ModelMapper();
	}


}
