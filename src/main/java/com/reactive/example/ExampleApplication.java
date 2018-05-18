package com.reactive.example;

import com.reactive.example.domain.Car;
import com.reactive.example.domain.CarEvents;
import com.reactive.example.repository.CarRepository;
import com.reactive.example.service.FluxCarService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@SpringBootApplication
@RequiredArgsConstructor
public class ExampleApplication implements CommandLineRunner {

	private final CarRepository carRepository;

	public static void main(String[] args) {
		SpringApplication.run(ExampleApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		carRepository.deleteAll()
				.thenMany(
						Flux.just("Koenigsegg One:1", "Hennessy Venom GT", "Bugatti Veyron Super Sport",  "SSC Ultimate Aero", "McLaren F1", "Pagani Huayra", "Noble M600",
								"Aston Martin One-77", "Ferrari LaFerrari", "Lamborghini Aventador")
								.map(model -> new Car(UUID.randomUUID().toString(), model))
								.flatMap(carRepository::save))
				.subscribe(System.out::println);
	}
}
