package com.gentlecorp.customer;

import org.springframework.boot.SpringApplication;

public class TestCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.from(CustomerApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
