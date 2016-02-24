package org.crysil.communications.http;

import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableWebMvc
@Configuration
@ComponentScan({ "org.crysil.communications.http" })
public class WebConfig extends WebMvcConfigurerAdapter {

	public WebConfig() {
		super();
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		super.configureMessageConverters(messageConverters);
	}
}
