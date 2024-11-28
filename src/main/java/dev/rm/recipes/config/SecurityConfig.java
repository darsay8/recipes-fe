package dev.rm.recipes.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

    return http.build();
  }

  @Bean
  public FilterRegistrationBean<HiddenHttpMethodFilter> hiddenHttpMethodFilter() {
    FilterRegistrationBean<HiddenHttpMethodFilter> filter = new FilterRegistrationBean<>();
    filter.setFilter(new HiddenHttpMethodFilter());
    filter.addUrlPatterns("/*");
    return filter;
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
