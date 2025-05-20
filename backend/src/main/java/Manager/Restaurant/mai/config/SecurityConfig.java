package Manager.Restaurant.mai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())              .cors(cors -> cors.configurationSource(corsConfigurationSource()))            
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/auth/**", "/user/get-address").permitAll()
                // Restaurant endpoints - all GET endpoints are public
                .requestMatchers(HttpMethod.GET, "/restaurants/**").permitAll()
                // Admin-only endpoints
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Restaurant owner endpoints - for adding new items or categories (POST/PUT operations)
                .requestMatchers(HttpMethod.POST, "/restaurants/{id}/menu/**", "/restaurants/{id}/categories/**").hasAnyRole("ADMIN", "RESTAURANT_OWNER")                .requestMatchers(HttpMethod.PUT, "/restaurants/{id}/menu/**", "/restaurants/{id}/categories/**").hasAnyRole("ADMIN", "RESTAURANT_OWNER")
                // Cart endpoints - all require authentication
                .requestMatchers("/api/cart/**").authenticated()
                // Specific authenticated user endpoints
                .requestMatchers("/user/profile", "/user/update", "/user/delete", "/user/change-password", "/users/change-password").authenticated()
                // Any other endpoints
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
