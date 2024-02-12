package com.example.orders.repository;

import com.example.orders.models.Order;
import com.example.orders.models.Status;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PreLoader {

    private static final Logger log = LoggerFactory.getLogger(PreLoader.class);

    @Bean
    CommandLineRunner initDatabase(OrderRepository repository) {
        return args -> {
            repository.save(new Order("Macbook Pro", Status.COMPLETED));
            repository.save(new Order("Iphone", Status.IN_PROGRESS));
            repository.save(new Order("Galaxy SX", Status.CANCELLED));

            repository.findAll().forEach(order -> log.info("Preloaded " + order));
        };
    }


}
