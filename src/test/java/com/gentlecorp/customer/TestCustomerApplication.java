package com.gentlecorp.customer;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;

public class TestCustomerApplication {

    public static void main(String[] args) {
        // .env-Datei laden
        Dotenv dotenv = Dotenv.configure().load();

        // Umgebungsvariablen setzen
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.from(CustomerApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
