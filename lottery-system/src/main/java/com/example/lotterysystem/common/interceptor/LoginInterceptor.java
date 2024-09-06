package com.example.lotterysystem.common.interceptor;


import com.example.lotterysystem.common.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 添加拦截器
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        //从header中获取token
        String token = request.getHeader("user_token");
        log.info("从header中获取token：{}",token);
        log.info("获取路径：{}",request.getRequestURI());
        //验证
        Claims claims = JWTUtil.parseJWT(token);
        if(null == claims){
            response.setStatus(401);
            log.error("解析JWT令牌失败，请重新登录");
            return false;
        }
        log.info("令牌通过，放行");
        return  true;
    }

}
