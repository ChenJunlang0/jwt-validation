package com.example.myprojectbackend.filter;

import com.example.myprojectbackend.entity.Result;
import com.example.myprojectbackend.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Order(Const.FLOW_ORDER)
public class FlowFilter extends HttpFilter {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String ip = request.getRemoteAddr();
        if (flowLimit(ip)){
            chain.doFilter(request,response);
        }else {
            writeBlockMessage(response);
        }
    }

    private void writeBlockMessage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(Result.failure(403,"请求频繁").asJsonString());
    }


    private boolean flowLimit(String ip) {
        synchronized (ip.intern()){
            if(forbiddenIp(ip))
                return false;
            return flowLimitCheck(ip);
        }


    }

    private boolean flowLimitCheck(String ip) {
        if (redisTemplate.opsForValue().get(Const.FLOW_LIMIT_CHECK+ip)==null){
            redisTemplate.opsForValue().set(Const.FLOW_LIMIT_CHECK+ip,"1",3,TimeUnit.SECONDS);
        }else {
            Long increment = redisTemplate.opsForValue().increment(Const.FLOW_LIMIT_CHECK + ip);
            if (increment>=10){
                redisTemplate.opsForValue().set(Const.FLOW_FORBIDDEN_IP+ip,"",3,TimeUnit.SECONDS);
                return false;
            }
        }
        return true;
    }

    private boolean forbiddenIp(String ip) {
        return redisTemplate.opsForValue().get(Const.FLOW_FORBIDDEN_IP + ip) != null;
    }


}
