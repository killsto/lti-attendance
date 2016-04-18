package config;

import edu.ksu.lti.config.CommonAppConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@Configuration
@EnableAutoConfiguration
@EnableWebMvcSecurity
@ComponentScan({"edu.ksu.canvas", "edu.ksu.lti", "edu.ksu.canvas.biodemo.jms"})
@EntityScan(basePackages = "edu.ksu.canvas")
@PropertySource({"classpath:application.properties","classpath:aviation-reporting" +
        ".properties"})
public class AppConfig extends CommonAppConfig {
}
