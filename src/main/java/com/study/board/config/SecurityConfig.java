package com.study.board.config;

import com.study.board.repository.MemberRepository;
import com.study.board.jwt.JwtUtil;
import com.study.board.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/favicon.ico",
                                "/board/list", "/board/view/**", "/board/save", "/member/",
                                "/oauth2/**", "/board/login", "/board/write").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .defaultSuccessUrl("/board/list", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")  // 로그아웃 후 리디렉션
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )


                // ✅ JWT 필터 등록
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, memberRepository),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
