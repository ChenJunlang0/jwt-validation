package com.example.myprojectbackend.config;

import com.auth0.jwt.JWT;
import com.example.myprojectbackend.entity.Result;
import com.example.myprojectbackend.entity.dto.Account;
import com.example.myprojectbackend.entity.vo.request.AuthorizeVo;
import com.example.myprojectbackend.filter.JwtAuthorize;
import com.example.myprojectbackend.service.impl.AccountServiceImpl;
import com.example.myprojectbackend.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    JwtAuthorize jwtAuthorize;

    @Resource
    AccountServiceImpl accountService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf->conf
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(conf->conf
                        .loginProcessingUrl("/api/auth/login")
                        .failureHandler(this::onAuthenticationFailure)
                        .successHandler(this::onAuthenticationSuccess)
                )
                .logout(conf->conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )
                .exceptionHandling(conf->conf
                        .authenticationEntryPoint(this::onUnauthorized)
                        .accessDeniedHandler(this::onAccessDenied))
                .addFilterBefore(jwtAuthorize, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf->conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    void onAccessDenied(HttpServletRequest request,
                HttpServletResponse response,
                AccessDeniedException accessDeniedException) throws IOException, ServletException{
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(Result.accessDenied(accessDeniedException.getMessage()).asJsonString());
    }

    void onUnauthorized(HttpServletRequest request,
                  HttpServletResponse response,
                  AuthenticationException authException) throws IOException, ServletException{
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(Result.unAuthorized(authException.getMessage()).asJsonString());

    }


    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(Result.unAuthorized(exception.getMessage()).asJsonString());

    }

    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException{
        response.setContentType("application/json;charset=UTF-8");
        //认证成功颁发令牌
        User user = (User)authentication.getPrincipal();
        Account account = accountService.findAccountByNameOrEmail(user.getUsername());
        String token=jwtUtils.createJwt(user,account.getId(),account.getUsername());
        AuthorizeVo authorizeVo = new AuthorizeVo();
        BeanUtils.copyProperties(account,authorizeVo);
        authorizeVo.setExpire(JWT.decode(token).getExpiresAt());
        authorizeVo.setToken(token);
        response.getWriter().write(Result.success(authorizeVo).asJsonString());
    }

    void onLogoutSuccess(HttpServletRequest request,
                         HttpServletResponse response,
                         Authentication authentication) throws IOException, ServletException{
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        String headerToken = request.getHeader("Authorization");
        if (jwtUtils.invalidateJwt(headerToken))
            writer.write(Result.success("登出成功").asJsonString());
        else
            writer.write(Result.failure(400,"登出失败").asJsonString());
    }


}
