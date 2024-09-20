package com.example.myprojectbackend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    @Value("${spring.security.jwt.key}")
    String key;

    @Value("${spring.security.jwt.expire}")
    int expire;

    @Autowired
    StringRedisTemplate redisTemplate;

    public boolean checkToken(String headerToken){
        String token = convertToken(headerToken);
        if (token==null)
            return false;
        Algorithm algorithm=Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            return new Date().before(decodedJWT.getExpiresAt());
        }catch (JWTVerificationException e){
            return false;
        }
    }

    public boolean invalidateJwt(String headerToken){
        String token = convertToken(headerToken);
        if (token==null)
            return false;
        Algorithm algorithm=Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try{
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            return deleteToken(decodedJWT.getId(),decodedJWT.getExpiresAt());
        }catch (JWTVerificationException e){
            return false;
        }
    }

    private boolean deleteToken(String uuid,Date time){
        if (isInvalidToken(uuid))
            return false;
        Date now=new Date();
        Long expire=Math.max(time.getTime()-now.getTime(),0);
        //将令牌加入redis黑名单
        redisTemplate.opsForValue().set(Const.JWT_BLACK_LIST+uuid,uuid,expire, TimeUnit.MILLISECONDS);
        return true;

    }

    private boolean isInvalidToken(String uuid){
         return Boolean.TRUE.equals(redisTemplate.hasKey(Const.JWT_BLACK_LIST + uuid));
    }



    public DecodedJWT resolveJwt(String headToken){
        String token=convertToken(headToken);
        if (token==null)
            return null;
        Algorithm algorithm=Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            if (isInvalidToken(decodedJWT.getId()))
                return null;
            Date expiresAt = decodedJWT.getExpiresAt();
            return new Date().after(expiresAt)?null:decodedJWT;
        }catch (JWTVerificationException e){
            return null;
        }
    }

    private String convertToken(String headToken) {
        if (headToken==null||!headToken.startsWith("Bearer "))
            return null;
        return headToken.substring(7);
    }

    public String createJwt(UserDetails details,int id,String username){
        Algorithm algorithm=Algorithm.HMAC256(key);
        Date expire=this.expireTime();
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id",id)
                .withClaim("username",username)
                .withClaim("authorities",details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(expire)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    private Date expireTime() {
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.HOUR,expire*24);
        return calendar.getTime();
    }

    public UserDetails toUser(DecodedJWT decodedJWT) {
        Map<String, Claim> claims = decodedJWT.getClaims();
        return User
                .withUsername(claims.get("username").asString())
                .password("******")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    public int toId(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("id").asInt();
    }
}
