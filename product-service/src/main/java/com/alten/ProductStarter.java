package com.alten;


import com.alten.security.EnableKeycloakSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableKeycloakSecurity
@SpringBootApplication(scanBasePackages = {
        "com.alten",
        "com.alten.security"
})
public class ProductStarter {
    public static void main(String[] args) {
        SpringApplication.run(ProductStarter.class, args);
    }
}