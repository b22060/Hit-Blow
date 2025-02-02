package kmt.hit_blow.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.formLogin(login -> login
        .permitAll())
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/"))
        .authorizeHttpRequests(authz -> authz
            .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/*"))
            .permitAll() // /h2-console/* は認証不要
            .anyRequest()
            .authenticated()) // それ以外は認証必須
        .csrf(csrf -> csrf
            .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/*"))) // /h2-console/* はCSRF保護対象外
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions
                .sameOrigin()));
    return http.build();
  }

  @Bean
  public InMemoryUserDetailsManager userDetailsService() {
    UserDetails user1 = User.withUsername("User1")
        .password("{bcrypt}$2y$05$gTjyU0Cy8WLahOVTsRYPx.zzsVpbOcQfTLwiuQO6Su9FsNYzlXKn2").roles("USER").build();
    UserDetails user2 = User.withUsername("User2")
        .password("{bcrypt}$2y$05$gTjyU0Cy8WLahOVTsRYPx.zzsVpbOcQfTLwiuQO6Su9FsNYzlXKn2").roles("USER").build();
    UserDetails Spectator = User.withUsername("Spectator")
        .password("{bcrypt}$2y$05$gTjyU0Cy8WLahOVTsRYPx.zzsVpbOcQfTLwiuQO6Su9FsNYzlXKn2").roles("SPECTATORS").build();
    return new InMemoryUserDetailsManager(user1, user2, Spectator);
  }

}
