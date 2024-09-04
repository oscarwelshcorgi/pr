package com.pr.config;

import com.pr.member.domain.Role;
import com.pr.member.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig { //WebSecurityConfigurerAdapter was deprecated

    private final MemberService memberService;

    public SecurityConfig(MemberService memberService) {
        this.memberService = memberService;
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  //기본값을 유지하면서 CSRF 보호를 비활성화
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .formLogin(formLogin -> formLogin.disable()) //폼 기반 로그인을 비활성화
                .httpBasic(httpBasic -> httpBasic.disable()) //HTTP 기본 인증을 비활성화
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/**").permitAll() // URL 별 권한 관리
                                .anyRequest().authenticated() // anyRerquest : 설정된 값들 이외 나머지 URL 나타냄. authenticated : 인증된 사용자
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login.userInfoEndpoint(userInfoEndpointConfig -> // oauth2 로그인 성공 후 가져올 때의 설정들
                                        userInfoEndpointConfig.userService(memberService) // 리소스 서버에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능 명시
                                )
                                //.defaultSuccessUrl("http://localhost:3030") // 로그인 후 vue 서버(3030)으로 리다이렉트(개발)
                                .defaultSuccessUrl("https://dongga.net") // 로그인 후 리다이렉트(운영)
                )
                //.logout((logout) -> logout.logoutSuccessUrl("http://localhost:3030")) // 로그아웃 후 vue 서버(3030)으로 리다이렉트(개발)
                .logout((logout) -> logout.logoutSuccessUrl("https://dongga.net")) // 로그아웃 후 리다이렉트(운영)
        ;
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3030"); //개발
        config.addAllowedOrigin("https://dongga.net"); //운영
        config.addAllowedOrigin("https://www.dongga.net"); //운영
        config.addAllowedOrigin("https://dongga.net:8080"); //운영
        config.addAllowedOrigin("https://www.dongga.net:8080"); //운영
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}