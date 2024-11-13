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
            .requestMatchers(AntPathRequestMatcher.antMatcher("/hit-blow"))
            .authenticated() // /jankenは認証が必要
            .requestMatchers(AntPathRequestMatcher.antMatcher("/**"))
            .permitAll())
        .csrf(csrf -> csrf
            .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/*")))
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

    return new InMemoryUserDetailsManager(user1, user2);
  }

}
