package ar.com.manflack.ada;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;


@SpringBootApplication
@Configuration
public class Application
{
	@Value("${config.timezone:GMT}")
	private String timezone;

	@PostConstruct
	public void init()
	{
		TimeZone.setDefault(TimeZone.getTimeZone(timezone));
	}

	public static void main(String[] args)
	{
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().components(new Components())
				.info(new Info()
						.title("Api Rest Client")
						.description("ADA")
						.version("1.0")
						.license(new License().name("This is a easter egg")));
	}
}
