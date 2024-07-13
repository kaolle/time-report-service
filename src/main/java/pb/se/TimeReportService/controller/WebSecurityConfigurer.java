package pb.se.TimeReportService.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.context.annotation.Profile;

//@Configuration
//@Profile({"dev", "test"}) //TODO add WebSecurityForProd or add prod to this profile
public class WebSecurityConfigurer {

    public static final String TIME_REPORT_USER = "time-report-user";

//    @Bean
//    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
//        UserDetails user = User.withUsername(TIME_REPORT_USER)
//                .password(passwordEncoder.encode("secret"))
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http.authorizeHttpRequests(request -> request.requestMatchers(new AntPathRequestMatcher("/TBD/**"))
//                        .hasRole("USER"))
//                .authorizeHttpRequests(request -> request.requestMatchers(new AntPathRequestMatcher("/time-report/**"))
//                        .permitAll())
//                .httpBasic(Customizer.withDefaults())
//                .build();
//    }
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}
