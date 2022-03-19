package ru.totalexx.cartographer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CartographerApplication {

	public static void main(String[] args) {
		ImageModel.setImagesDir(args[0]);
		SpringApplication.run(CartographerApplication.class, args);
	}

}
