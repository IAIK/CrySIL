package org.crysil.instance;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

/**
 * Enables authentication with client TLS certificates
 * 
 * @see X509CertificateAuthenticationManager
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		X509AuthenticationFilter filter = new X509AuthenticationFilter();
		filter.setAuthenticationManager(new X509CertificateAuthenticationManager());
		http.addFilterAfter(filter, SecurityContextPersistenceFilter.class);
		http.authorizeRequests().anyRequest().permitAll();
		http.csrf().disable();
		http.httpBasic().disable();
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		X509AuthenticationFilter filter = new X509AuthenticationFilter();
		registrationBean.setFilter(filter);
		registrationBean.setEnabled(false);
		return registrationBean;
	}
}