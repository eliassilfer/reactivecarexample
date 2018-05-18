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

	@Component
	public static class RouteHandles {
		private final FluxCarService fluxCarService;

		public RouteHandles(FluxCarService fluxCarService) {
			this.fluxCarService = fluxCarService;
		}

		public Mono<ServerResponse> allCars(ServerRequest serverRequest) {
			return ServerResponse.ok()
					.body(fluxCarService.all(), Car.class)
					.doOnError(throwable -> new IllegalStateException("My godness NOOOOO!!"));
		}

		public Mono<ServerResponse> carById(ServerRequest serverRequest) {
			String carId = serverRequest.pathVariable("carId");
			return ServerResponse.ok()
					.body(fluxCarService.byId(carId), Car.class)
					.doOnError(throwable -> new IllegalStateException("oh boy... not againnn =(("));
		}

		public Mono<ServerResponse> events(ServerRequest serverRequest) {
			String carId = serverRequest.pathVariable("carId");
			return ServerResponse.ok()
					.contentType(MediaType.TEXT_EVENT_STREAM)
					.body(fluxCarService.streams(carId), CarEvents.class)
					.doOnError(throwable -> new IllegalStateException("I give up!! "));
		}


		@Bean
		RouterFunction<?> routes(RouteHandles routeHandles) {
			return RouterFunctions.route(
					RequestPredicates.GET("/cars"), routeHandles::allCars)
					.andRoute(RequestPredicates.GET("/cars/{carId}"), routeHandles::carById)
					.andRoute(RequestPredicates.GET("/cars/{carId}/events"), routeHandles::events);
		}
	}
}
