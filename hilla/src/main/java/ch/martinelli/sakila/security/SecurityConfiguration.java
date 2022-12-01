package ch.martinelli.sakila.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

//@EnableWebSecurity
//@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    public static final String LOGOUT_URL = "/";

    @Value("${ch.martinelli.sakila.auth.secret}")
    private String authSecret;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/")).permitAll();
        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/films")).permitAll();
        http.authorizeHttpRequests().requestMatchers(new AntPathRequestMatcher("/icons/*.png")).permitAll();

        super.configure(http);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        setLoginView(http, "/login", LOGOUT_URL);

        setStatelessAuthentication(http, new SecretKeySpec(Base64.getDecoder().decode(authSecret), JwsAlgorithms.HS256), "ch.martinelli.sakila");
    }

}
