package org.crysil.communications.http;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;

public class WebAppInitializer implements WebApplicationInitializer {

	/** The request schema. */
	public static JsonNode requestSchema;

	/** The response schema. */
	public static JsonNode responseSchema;

	/** The configuration */
	private static Configuration configuration;

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(WebConfig.class);

		servletContext.addListener(new ContextLoaderListener(context));
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("DispatcherServlet",
				new DispatcherServlet(context));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/*");

		try {
			requestSchema = JsonLoader.fromResource("/requests.jsonSchema");
			responseSchema = JsonLoader.fromResource("/responses.jsonSchema");
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			configuration = (Configuration) Class.forName(Configuration.class.getName().concat("Impl")).newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new ServletException(e);
		}
	}

	public static Configuration getConfiguration() {
		return configuration;
	}
}
