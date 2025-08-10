import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors()  // Enable CORS support
            .and()
            .csrf().disable()  // Disable CSRF if your frontend uses tokens (stateless)
            .authorizeRequests()
                .antMatchers("/users/register", "/users/login", "/users/verify", "/users/forgot-password", "/users/reset-password", "/manifest.json", "/favicon.ico", "/public/**")
                .permitAll()  // public endpoints that don't need auth
                .anyRequest()
                .authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);  // JWT stateless session

        // Add your JWT filter here (if applicable)

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList(
            "https://turf-booking-frontend.vercel.app",
            "https://turf-booking-an7sfm399-monishs-projects-29844c66.vercel.app",
            "http://localhost:3000"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
