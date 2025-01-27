package com.gentlecorp.customer.config;

import io.github.cdimascio.dotenv.Dotenv;

public interface EnvConfig {
    Dotenv DOTENV = Dotenv.configure()
        .filename(".env")
        .load();

    static String get(String key) {
        return DOTENV.get(key);
    }
}
