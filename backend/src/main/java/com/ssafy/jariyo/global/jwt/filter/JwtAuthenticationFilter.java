package com.ssafy.jariyo.global.jwt.filter;


import com.ssafy.jariyo.domain.user.entity.User;
import com.ssafy.jariyo.domain.user.repository.UserRepository;
import com.ssafy.jariyo.global.jwt.service.JwtService;
import com.ssafy.jariyo.global.jwt.util.PasswordUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Jwt 인증 필터
 * "/login" 이외의 URI 요청이 왔을 때 처리하는 필터
 *
 * 기본적으로 사용자는 요청 헤더에 AccessToken만 담아서 요청
 * AccessToken 만료 시에만 RefreshToken을 요청 헤더에 AccessToken과 함께 요청
 *
 * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken을 재발급하지는 않는다.
 * 2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 처리, 403 ERROR
 * 3. RefreshToken이 있는 경우 -> DB의 RefreshToken과 비교하여 일치하면 AccessToken 재발급, RefreshToken 재발급(RTR 방식)
 *                              인증 성공 처리는 하지 않고 실패 처리
 *
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

//    private static final String NO_CHECK_URL_KAKAO = "/api/user/login/kakao"; // ""으로 들어오는 요청은 Filter 작동 X
//    private static final String NO_CHECK_URL_NAVER = "/api/user/login/naver"; // ""으로 들어오는 요청은 Filter 작동 X
//    private static final String NO_CHECK_URL_GOOGLE = "/api/user/login/google"; // ""으로 들어오는 요청은 Filter 작동 X
//    private static final String NO_CHECK_URL_SUCCESS = "/login-success"; // ""으로 들어오는 요청은 Filter 작동 X

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    // 필터 적용 X path
    private final String[] excludePath = {
            "/api/user/login/kakao", "/api/user/login/naver", "/api/user/login/google", "/api/login-success",
            "/v3/api-docs", "/api/zpass/payment/success", "/api/zpass/payment/fail", "/api/zpass/payment/cancel",
            "/swagger-resources/**", "/configuration/security", "/webjars/**",
            "/api/board", "/api/playroom",
            "/swagger-ui/swagger-ui/",
            "/api-docs/swagger-config", "/api-docs",
            "/api/store/search", "/api/store/filter",
            "/api/store/address", "/api/store"

    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        log.info("요청 URI : "+request.getRequestURI());
//        if (request.getRequestURI().equals(NO_CHECK_URL_KAKAO)
//                || request.getRequestURI().equals(NO_CHECK_URL_NAVER)
//                || request.getRequestURI().equals(NO_CHECK_URL_GOOGLE)
//                || request.getRequestURI().contains(NO_CHECK_URL_SUCCESS)) {
//            filterChain.doFilter(request, response); // "/login" 요청이 들어오면, 다음 필터 호출
//            return; // return으로 이후 현재 필터 진행 막기 (안해주면 아래로 내려가서 계속 필터 진행시킴)
//        }

        log.info("로그인 필터 시작");

        // 사용자 요청 헤더에서 RefreshToken 추출
        // -> RefreshToken이 없거나 유효하지 않다면(DB에 저장된 RefreshToken과 다르다면) null을 반환
        // 사용자의 요청 헤더에 RefreshToken이 있는 경우는, AccessToken이 만료되어 요청한 경우밖에 없다.
        // 따라서, 위의 경우를 제외하면 추출한 refreshToken은 모두 null
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(accessToken -> jwtService.isTokenValid(accessToken, request))
                .orElse(null);
        log.info("리프레시 토큰 여부 : " + refreshToken);

        // 리프레시 토큰이 요청 헤더에 존재했다면, 사용자가 AccessToken 연장 신청을 위해
        // RefreshToken까지 보낸 것이므로 리프레시 토큰이 redis의 리프레시 토큰과 일치하는지 판단 후,
        // 일치한다면 AccessToken을 재발급해준다.
        if (refreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(request, response, refreshToken, filterChain);
            return; // RefreshToken을 보낸 경우에는 AccessToken을 재발급 하고 인증 처리는 하지 않게 하기위해 바로 return으로 필터 진행 막기
        }

        // RefreshToken이 없거나 유효하지 않다면, AccessToken을 검사하고 인증을 처리하는 로직 수행
        // AccessToken이 없거나 유효하지 않다면, 인증 객체가 담기지 않은 상태로 다음 필터로 넘어가기 때문에 403 에러 발생
        // AccessToken이 유효하다면, 인증 객체가 담긴 상태로 다음 필터로 넘어가기 때문에 인증 성공
        if (refreshToken == null) {
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }

    /**
     *  [리프레시 토큰으로 유저 정보 찾기 & 액세스 토큰/리프레시 토큰 재발급 메소드]
     *  파라미터로 들어온 헤더에서 추출한 리프레시 토큰으로 DB에서 유저를 찾고, 해당 유저가 있다면
     *  JwtService.createAccessToken()으로 AccessToken 생성,
     *  reIssueRefreshToken()로 리프레시 토큰 재발급 & DB에 리프레시 토큰 업데이트 메소드 호출
     *  그 후 JwtService.sendAccessTokenAndRefreshToken()으로 응답 헤더에 보내기
     */
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletRequest request, HttpServletResponse response, String refreshToken, FilterChain filterChain) throws ServletException, IOException {
        log.info("리프레시 토큰 받음");
        String accessToken = jwtService.extractAccessToken(request).orElseThrow();
        // 엑세스 토큰이 유효하다면
        if (jwtService.isTokenValid(accessToken, request)) {

            String email = jwtService.extractEmail(accessToken).get();
            String myRefreshToken= jwtService.getRedisRefreshToken(email);
            if (refreshToken.equals(myRefreshToken)) {
                log.info("--> 일치하는 리프레시 토큰 존재  >> 액세스 토큰 & 리프레시 토큰 재발급 진행");
                // 이전 ATK는 로그아웃 처리
                jwtService.expireAccessToken(accessToken);
                String reIssuedRefreshToken = reIssueRefreshToken(email);
                User user = userRepository.findByEmail(email).get();
                jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user, email), reIssuedRefreshToken);
            }
        }
        filterChain.doFilter(request, response);
//        userRepository.findByRefreshToken(refreshToken)
//                .ifPresent(user -> {
//                    String reIssuedRefreshToken = reIssueRefreshToken(user);
//                    jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getEmail()),
//                            reIssuedRefreshToken);
//                });

    }

    /**
     * [리프레시 토큰 재발급 & DB에 리프레시 토큰 업데이트 메소드]
     * jwtService.createRefreshToken()으로 리프레시 토큰 재발급 후
     * DB에 재발급한 리프레시 토큰 업데이트 후 Flush
     */
    private String reIssueRefreshToken(String email) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        Long refreshExpiration = jwtService.extractExpiration(reIssuedRefreshToken).get().getTime();
        jwtService.updateRefreshToken(email, reIssuedRefreshToken, refreshExpiration);
//        user.updateRefreshToken(reIssuedRefreshToken);
//        userRepository.saveAndFlush(user);
        return reIssuedRefreshToken;
    }

    /**
     * [액세스 토큰 체크 & 인증 처리 메소드]
     * request에서 extractAccessToken()으로 액세스 토큰 추출 후, isTokenValid()로 유효한 토큰인지 검증
     * 유효한 토큰이면, 액세스 토큰에서 extractEmail로 Email을 추출한 후 findByEmail()로 해당 이메일을 사용하는 유저 객체 반환
     * 그 유저 객체를 saveAuthentication()으로 인증 처리하여
     * 인증 허가 처리된 객체를 SecurityContextHolder에 담기
     * 그 후 다음 인증 필터로 진행
     */
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");

        String accessToken = jwtService.extractAccessToken(request).get();
        if(jwtService.isTokenValid(accessToken, request)){
            String email = jwtService.extractEmail(accessToken).get();
            User user = userRepository.findByEmail(email).get();
            saveAuthentication(user,request);
        }

//        jwtService.extractAccessToken(request)
//                .filter(accessToken -> jwtService.isTokenValid(accessToken, request))
//                .ifPresent(accessToken -> jwtService.extractEmail(accessToken)
//                        .ifPresent(email -> userRepository.findByEmail(email)
//                                .ifPresent((user)-> saveAuthentication(user, request))));

        filterChain.doFilter(request, response);
    }

    /**
     * [인증 허가 메소드]
     * 파라미터의 유저 : 우리가 만든 회원 객체 / 빌더의 유저 : UserDetails의 User 객체
     *
     * new UsernamePasswordAuthenticationToken()로 인증 객체인 Authentication 객체 생성
     * UsernamePasswordAuthenticationToken의 파라미터
     * 1. 위에서 만든 UserDetailsUser 객체 (유저 정보)
     * 2. credential(보통 비밀번호로, 인증 시에는 보통 null로 제거)
     * 3. Collection < ? extends GrantedAuthority>로,
     * UserDetails의 User 객체 안에 Set<GrantedAuthority> authorities이 있어서 getter로 호출한 후에,
     * new NullAuthoritiesMapper()로 GrantedAuthoritiesMapper 객체를 생성하고 mapAuthorities()에 담기
     *
     * SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후,
     * setAuthentication()을 이용하여 위에서 만든 Authentication 객체에 대한 인증 허가 처리
     * (소셜 로그인의 경우 password가 null인데, 인증 처리 시 password가 null이면 안 되므로, 랜덤 패스워드를 임의로 부여해줍니다.)
     */
    public void saveAuthentication(User myUser, HttpServletRequest request) {
        log.info("로그인 인증시작");
        String password = PasswordUtil.generateRandomPassword();


        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(myUser.getEmail())
                .password(password)
                .roles(myUser.getRole().name())
                .build();

        String accessToken = jwtService.extractAccessToken(request).orElseThrow();
        log.info("get AccessToken : " + accessToken);
        log.info("getAuthorites : " + authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, accessToken,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));
        log.info("Authentication 설정 >> " + authentication.getPrincipal().toString());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("SContext : " + SecurityContextHolder.getContext());

    }
}