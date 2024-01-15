 package antifraud;

 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.security.config.Customizer;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;
 import org.springframework.security.config.http.SessionCreationPolicy;
 import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.security.web.SecurityFilterChain;
 import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
 import org.springframework.http.HttpMethod;

 @Configuration
 public class SecurityConfig {
     private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

     public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
         this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
     }

     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         return http
                 .httpBasic(Customizer.withDefaults())
                 .csrf(CsrfConfigurer::disable)                           // For modifying requests via Postman
                 .exceptionHandling(handing -> handing
                     .authenticationEntryPoint(restAuthenticationEntryPoint) // Handles auth error: Voorheen stond hier restAuthenticationEntryPoint als value, maar in het voorbeeld verwees die nergens heen.
                 )
                 .headers(headers -> headers.frameOptions().disable())           // for Postman, the H2 console
                 .authorizeHttpRequests(requests -> requests                     // manage access
                                 .requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                                 .requestMatchers("/actuator/shutdown").permitAll()      // needs to run test
                                 .requestMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasAuthority("MERCHANT")
                                 .requestMatchers(HttpMethod.GET, "/api/auth/list/**").hasAnyAuthority("ADMINISTRATOR", "SUPPORT")
                                 .requestMatchers(HttpMethod.DELETE, "/api/auth/user/{username}").hasAuthority("ADMINISTRATOR")
                                 .requestMatchers(HttpMethod.PUT, "/api/auth/access/**").hasAuthority("ADMINISTRATOR")
                                 .requestMatchers(HttpMethod.PUT, "/api/auth/role/**").hasAuthority("ADMINISTRATOR")
                 )
                 .sessionManagement(session -> session
                         .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                 )
                 // other configurations
                 .build();
     }
     @Bean
     public PasswordEncoder passwordEncoder() {
         return new BCryptPasswordEncoder();
     }
 }