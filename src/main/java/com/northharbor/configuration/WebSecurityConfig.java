package com.northharbor.configuration;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.northharbor.service.NhUserDetailsService;

@Configuration
public class WebSecurityConfig {

  @Value("${security.allowed.origins.urlList}")
  private String urlList;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      AuthenticationProvider authenticationProvider) throws Exception {

    http.authenticationProvider(authenticationProvider)
        .authorizeHttpRequests(
            authorize -> authorize.requestMatchers("/", "/error", "/css/**", "/js/**", "/images/**")
                .permitAll().requestMatchers("/login", "/register").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN").anyRequest().authenticated())
        .formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login")
            .defaultSuccessUrl("/profile", true).failureUrl("/login?error").permitAll())
        .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout")
            .invalidateHttpSession(true).deleteCookies("JSESSIONID"))
        .cors(cors -> {
        }).csrf(csrf -> csrf.disable());

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider(NhUserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder) {

    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;

  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(getAllowedOrigins());
    configuration.setAllowedMethods(Arrays.asList("GET"));
    configuration.setAllowedHeaders(Arrays.asList("*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  public List<String> getAllowedOrigins() {
    return Arrays.asList(urlList.split(";"));
  }
}
