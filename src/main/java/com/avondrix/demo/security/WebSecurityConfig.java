package com.avondrix.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.concurrent.TimeUnit;

import static com.avondrix.demo.security.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public WebSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*")
                .permitAll()
                .antMatchers("/api/**")
                .hasRole(STUDENT.name())
//                .antMatchers(HttpMethod.DELETE, "/management/api/**")
//                .hasAuthority(COURSE_WRITE.getPermission())
//                .antMatchers(HttpMethod.POST, "/management/api/**")
//                .hasAuthority(COURSE_WRITE.getPermission())
//                .antMatchers(HttpMethod.PUT, "/management/api/**")
//                .hasAuthority(COURSE_WRITE.getPermission())
//                .antMatchers(HttpMethod.GET, "/management/api/**")
//                .hasAnyRole(ADMINTRAINEE.name(), ADMIN.name())
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
                .defaultSuccessUrl("/courses", true)
                .and()
                .rememberMe()
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/login");

    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails avonUser = User.builder()
                .username("avon")
                .password(passwordEncoder.encode("password"))
//                User's roles define on authorities, name like "ROLE_STUDENT", so if we want a user has a role,
//                just add a item to authorities list.
//                .roles(STUDENT.name())
                .authorities(STUDENT.getGrantedAuthority())
                .build();

        UserDetails duoUser = User.builder()
                .username("DUO")
                .password(passwordEncoder.encode("admin password"))
//                .roles(ApplicationUserRole.ADMIN.name())
                .authorities(ADMIN.getGrantedAuthority())
                .build();

        UserDetails tomUser = User.builder()
                .username("Tom")
                .password(passwordEncoder.encode("admin password"))
//                .roles(ADMINTRAINEE.name())
                .authorities(ADMINTRAINEE.getGrantedAuthority())
                .build();

        return new InMemoryUserDetailsManager(
                avonUser,
                duoUser,
                tomUser
        );
    }

}
