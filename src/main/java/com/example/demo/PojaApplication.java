package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@PojaGenerated
public class PojaApplication {

  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure().load();
    dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    SpringApplication.run(PojaApplication.class, args);
  }
}
