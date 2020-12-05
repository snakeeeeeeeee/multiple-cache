package com.zy.github.multiple.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MultipleCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultipleCacheApplication.class, args);
    }

}
