package hu.dorin.felelj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.javafaker.Faker;

@Configuration
public class DataConfig {
	@Bean
    Faker faker() {
        return new Faker();
    }
}

