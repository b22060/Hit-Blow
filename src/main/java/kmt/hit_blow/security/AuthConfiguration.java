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
    UserDetails user1 = User.withUsername("user1")
        .password("{bcrypt}$2y$05$PDAWN.TQDlz4h9irY60K8e/GYCwyOzhDAJZJEZF5e01vZkOEwpdzK").roles("USER").build();
    UserDetails user2 = User.withUsername("user2")
        .password("{bcrypt}$2y$05$HKk3Q50KDpi3DdK8SS02u.3HBZm8wlQZCWq/X2yRc2i.SgGcKoYC.").roles("USER").build();

    return new InMemoryUserDetailsManager(user1, user2);
  }

}
