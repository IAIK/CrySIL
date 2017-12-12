package org.crysil.instance;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.crysil.instance.datastore.DeviceRepository;
import org.crysil.instance.util.CertificateUtil;
import org.crysil.instance.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * Enables this Spring Boot application by registering the WebSocket handlers. Everything else gets handled by the
 * annotations below.
 * 
 * @see RegistrationWebSocketHandler
 * @see ManagementWebSocketHandler
 * @see ServerWebSocketHandler
 */
@ComponentScan
@EnableAutoConfiguration
@EnableWebSocket
@Configuration
public class Application extends SpringBootServletInitializer implements WebSocketConfigurer {

	{
		Security.addProvider(new BouncyCastleProvider());
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	@Autowired
	private AppConfiguration config;

	@Autowired
	private DeviceRepository repository;

	@Bean
	public WebSocketHandler serverWebSocketHandler() {
		return new ServerWebSocketHandler();
	}

	@Bean
	public WebSocketHandler registrationWebSocketHandler() {
		return new RegistrationWebSocketHandler();
	}

	@Bean
	public WebSocketHandler managementWebSocketHandler() {
		return new ManagementWebSocketHandler();
	}

	@Bean
	public HandshakeInterceptor serverWebSocketHandshakeInterceptor() {
		return new WebSocketHandshakeInterceptor();
	}

	@Bean
	public WebSocketSecurityHandshakeHandler websocketSecurityHandshakeHandler() {
		return new WebSocketSecurityHandshakeHandler();
	}

	@Bean
	public CertificateUtil certificateUtil() {
		return new CertificateUtil(config, repository);
	}

	@Bean
	public ServletServerContainerFactoryBean createWebSocketContainer() {
		ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
		container.setMaxTextMessageBufferSize(1024 * 1024);
		container.setMaxBinaryMessageBufferSize(1024 * 1024);
		return container;
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(serverWebSocketHandler(), Constants.API_CRYSIL_SERVER)
				.addInterceptors(serverWebSocketHandshakeInterceptor())
				.setHandshakeHandler(websocketSecurityHandshakeHandler()).setAllowedOrigins("*");
		registry.addHandler(registrationWebSocketHandler(), Constants.API_REGISTER).addInterceptors(
				serverWebSocketHandshakeInterceptor()).setAllowedOrigins("*");
		registry.addHandler(managementWebSocketHandler(), Constants.API_MANAGE)
				.addInterceptors(serverWebSocketHandshakeInterceptor())
				.setHandshakeHandler(websocketSecurityHandshakeHandler()).setAllowedOrigins("*");
	}

	/**
	 * Enables starting directly from the CLI with integrated web server
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
