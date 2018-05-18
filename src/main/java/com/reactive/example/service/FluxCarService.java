package com.reactive.example.service;

import com.reactive.example.domain.Car;
import com.reactive.example.domain.CarEvents;
import com.reactive.example.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FluxCarService {

    private final CarRepository carRepository;

    public Flux<Car> all() {
        return carRepository.findAll();
    }

    public Mono<Car> byId(String id) {
        return carRepository.findById(id);
    }

    public Flux<CarEvents> streams(String carId) {
        return byId(carId).flatMapMany(car -> {
            Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
            Flux<CarEvents> events = Flux.fromStream(
                    Stream.generate(() -> new CarEvents(car, LocalDateTime.now())));
            return Flux.zip(interval, events).map(Tuple2::getT2);
        });
    }
}
