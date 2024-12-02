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

import dev.rm.recipes.security.JwtRequestInterceptor;
import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final HttpSession httpSession;

  public SecurityConfig(HttpSession httpSession) {
    this.httpSession = httpSession;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/register", "/login", "/recipes", "/recipes/search/**", "/recipes/reset",
                "/css/**", "/js/**")
            .permitAll()
            .anyRequest().authenticated())
        .formLogin(form -> form
            .loginPage("/login")
            .permitAll())
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, authException) -> {
              response.sendRedirect("/login");
            }));
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
    JwtRequestInterceptor jwtRequestInterceptor = new JwtRequestInterceptor(httpSession);
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getInterceptors().add(jwtRequestInterceptor);
    return restTemplate;
  }

}
