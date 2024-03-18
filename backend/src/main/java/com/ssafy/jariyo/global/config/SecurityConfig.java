package com.ssafy.jariyo.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.jariyo.domain.user.repository.UserRepository;
import com.ssafy.jariyo.global.jwt.filter.JwtAuthenticationFilter;
import com.ssafy.jariyo.global.jwt.filter.JwtExceptionFilter;
import com.ssafy.jariyo.global.oauth2.handler.OAuth2LoginFailureHandler;
import com.ssafy.jariyo.global.oauth2.handler.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ssafy.jariyo.global.jwt.service.JwtService;
import com.ssafy.jariyo.global.oauth2.service.OAuth2UserService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * 인증은 CustomJsonUsernamePasswordAuthenticationFilter에서 authenticate()로 인증된 사용자로 처리
 * JwtAuthenticationProcessingFilter는 AccessToken, RefreshToken 재발급
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

//    private final LoginService loginService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final OAuth2UserService OAuth2UserService;
    private final JwtExceptionFilter jwtExceptionFilter;

    // SWAGGER 필터 진행 X
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring()
//                .requestMatchers(SWAGGER);
//    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(form -> form.disable()
                )   // formLogin 사용 X
//                .httpBasic(AbstractHttpConfigurer::disable
//                )   // httpBasic 사용 X
//                .cors((cors) -> cors.disable())
                .csrf( (csrf) -> csrf.disable())   // csrf 사용 X
                .headers((headersConfig) ->
                        headersConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable
                        )
                )
                .sessionManagement((httpSecuritySessionManagementConfigurer) ->
                           httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ) // 세션 사용하지 않으므로 STATELESS로 설정

                //== URL별 권한 관리 옵션 ==//
                // 아이콘, css, js 관련
                // 기본 페이지, css, image, js 하위 폴더에 있는 자료들은 모두 접근 가능
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers("/**","/css/**","/images/**","/js/**","/favicon.ico").permitAll()
//                                .requestMatchers("/sign-up").permitAll() // 회원가입 접근 가능
                                .anyRequest().authenticated() // 위의 경로 이외에는 모두 인증된 사용자만 접근 가능
                )
                //== 소셜 로그인 설정 ==//
                .oauth2Login((httpSecurityOAuth2LoginConfigurer) ->
                    httpSecurityOAuth2LoginConfigurer
                            .successHandler(oAuth2LoginSuccessHandler) // 동의하고 계속하기를 눌렀을 때 Handler 설정
                            .failureHandler(oAuth2LoginFailureHandler) // 소셜 로그인 실패 시 핸들러 설정
                            .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(OAuth2UserService))

                );
//                .exceptionHandling((exceptionConfig)-> exceptionConfig.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
//                //== 로그아웃 시 쿠키 삭제 ==//
//                .logout((logoutconfig) ->
//                        logoutconfig
//                                .logoutUrl("/logout")
//                                .clearAuthentication(true)
//                                .deleteCookies("JSESSIONID")
//                                .logoutSuccessUrl("/")
//                );
//                                .logoutSuccessUrl("/"));
//                                .logoutSuccessHandler(oAuth2LogoutSucceessHandler));
        // 원래 스프링 시큐리티 필터 순서가 LogoutFilter 이후에 로그인 필터 동작
        // 따라서, LogoutFilter 이후에 우리가 만든 필터 동작하도록 설정
        // 순서 : LogoutFilter -> JwtAuthenticationProcessingFilter -> CustomJsonUsernamePasswordAuthenticationFilter
//        http.addFilterAfter(new JwtAuthenticationFilter(), jwtAuthenticationProcessingFilter());
//        http.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class);
//        http.addFilterBefore(jwtAuthenticationProcessingFilter(), LoginAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtExceptionFilter,JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * AuthenticationManager 설정 후 등록
     * PasswordEncoder를 사용하는 AuthenticationProvider 지정 (PasswordEncoder는 위에서 등록한 PasswordEncoder 사용)
     * FormLogin(기존 스프링 시큐리티 로그인)과 동일하게 DaoAuthenticationProvider 사용
     * UserDetailsService는 커스텀 LoginService로 등록
     * 또한, FormLogin과 동일하게 AuthenticationManager로는 구현체인 ProviderManager 사용(return ProviderManager)
     *
     */
//    @Bean
//    public AuthenticationManager authenticationManager() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(passwordEncoder());
//        provider.setUserDetailsService(loginService);
//        return new ProviderManager(provider);
//    }

//    /**
//     * 로그인 성공 시 호출되는 LoginSuccessJWTProviderHandler 빈 등록
//     */
//    @Bean
//    public LoginSuccessHandler loginSuccessHandler() {
//        return new LoginSuccessHandler(jwtService, userRepository);
//    }
//
//    /**
//     * 로그인 실패 시 호출되는 LoginFailureHandler 빈 등록
//     */
//    @Bean
//    public LoginFailureHandler loginFailureHandler() {
//        return new LoginFailureHandler();
//    }
//
//    /**
//     * CustomJsonUsernamePasswordAuthenticationFilter 빈 등록
//     * 커스텀 필터를 사용하기 위해 만든 커스텀 필터를 Bean으로 등록
//     * setAuthenticationManager(authenticationManager())로 위에서 등록한 AuthenticationManager(ProviderManager) 설정
//     * 로그인 성공 시 호출할 handler, 실패 시 호출할 handler로 위에서 등록한 handler 설정
//     */
//    @Bean
//    public LoginAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
//        LoginAuthenticationFilter customJsonUsernamePasswordLoginFilter
//                = new LoginAuthenticationFilter(objectMapper);
//        customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
//        customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
//        customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
//        return customJsonUsernamePasswordLoginFilter;
//    }




    @Bean
    public JwtAuthenticationFilter jwtAuthenticationProcessingFilter() {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userRepository);
        return jwtAuthenticationFilter;
    }



}
