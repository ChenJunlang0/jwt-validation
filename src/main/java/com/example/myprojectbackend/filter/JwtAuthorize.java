package com.example.myprojectbackend.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.myprojectbackend.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthorize extends OncePerRequestFilter {

    @Autowired
    JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String headerToken = request.getHeader("Authorization");
        DecodedJWT decodedJWT = jwtUtils.resolveJwt(headerToken);
        //对于有效的token进行user信息存储
        if (decodedJWT!=null){
            UserDetails user=jwtUtils.toUser(decodedJWT);
            //创建用户凭证
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            //将当前http请求的相关信息存储在user中
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            //将user信息存储在安全上下文中，允许后续处理中访问这个user信息
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //将JWT中的用户ID设置为请求属性，这样它可以在后续的处理中被访问
            request.setAttribute("id",jwtUtils.toId(decodedJWT));
        }
        filterChain.doFilter(request,response);


    }
}
