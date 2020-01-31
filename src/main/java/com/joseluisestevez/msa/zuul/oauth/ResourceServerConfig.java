package com.joseluisestevez.msa.zuul.oauth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

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
				.anyRequest().authenticated();
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		tokenConverter.setSigningKey("HARD_CODE_SECRET_CODE");
		return tokenConverter;
	}

	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

}
