package edu.ksu.canvas.attendance.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@EnableAutoConfiguration
@ComponentScan
@Profile("embedded")
@SpringBootApplication
public class AppInit {

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(AppConfig.class, AppInit.class).run(args);
    }

}
