package com.joseluisestevez.msa.zuul.oauth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@RefreshScope
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Value("${config.security.oauth.jwt.key}")
	private String jwtKey;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources)
			throws Exception {
		resources.tokenStore(tokenStore());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/api/security/oauth/**")
				.permitAll()
				.antMatchers(HttpMethod.GET, "/api/products/list",
						"/api/items/listar", "/api/users/users")
				.permitAll()
				.antMatchers(HttpMethod.GET, "/api/products/view/{id}",
						"/api/items/detail/{id}/quantity/{quantity}",
						"/api/users/users/{id}")
				.hasAnyRole("ADMIN", "USER")

				// .antMatchers(HttpMethod.POST, "/api/products/create",
				// "/api/items/create", "/api/users/users")
				// .hasRole("ADMIN")
				// .antMatchers(HttpMethod.PUT, "/api/products/edit/{id}",
				// "/api/items/edit/{id}", "/api/users/users/{id}")
				// .hasRole("ADMIN")
				// .antMatchers(HttpMethod.DELETE, "/api/products/delete/{id}",
				// "/api/items/delete/{id}", "/api/users/users/{id}")
				// .hasRole("ADMIN")
				// Usamos la forma al final todas las reglas genericas
				.antMatchers("/api/products/**", "/api/items/**",
						"/api/users/**")
				.hasRole("ADMIN")
				// Al final cualquier otra ruta necesita autenticacion
				.anyRequest().authenticated().and().cors()
				.configurationSource(corsConfigurationSource());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		tokenConverter.setSigningKey(jwtKey);
		return tokenConverter;
	}

	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfig = new CorsConfiguration();
		// corsConfig.addAllowedOrigin("http://localhost:4200"); // angular
		// corsConfig.addAllowedOrigin("*"); // todos
		corsConfig
				.setAllowedOrigins(Arrays.asList("http://localhost:4200", "*"));
		corsConfig.setAllowedMethods(
				Arrays.asList("POST", "PUT", "GET", "DELETE", "OPTIONS"));
		corsConfig.setAllowCredentials(true);
		corsConfig.setAllowedHeaders(
				Arrays.asList("Autorization", "Content-Type"));

		// Rutas que aplica la configuracion
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);
		return source;
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(
				new CorsFilter(corsConfigurationSource()));
		// Prioridad alta
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}
}
