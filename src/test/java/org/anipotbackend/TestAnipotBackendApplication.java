package org.anipotbackend;

import org.springframework.boot.SpringApplication;

public class TestAnipotBackendApplication {

    public static void main(String[] args) {
        SpringApplication.from(AnipotBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
