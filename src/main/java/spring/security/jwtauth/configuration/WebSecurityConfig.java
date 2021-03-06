package spring.security.jwtauth.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring.security.jwtauth.entity.ApiPath;
import spring.security.jwtauth.security.filter.JwtAuthFilter;
import spring.security.jwtauth.service.CustomUserDetailsService;

import javax.servlet.Filter;
import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private CustomUserDetailsService customUserDetailsService;

    private JwtAuthFilter jwtAuthFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests().antMatchers(List.of(ApiPath.values()).stream().map(apiPath -> apiPath.getPath())
                        .toArray(String[]::new)).permitAll().anyRequest().authenticated()
                .and().addFilterBefore((Filter) jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
