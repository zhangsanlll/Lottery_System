package com.example.lotterysystem;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class JWTUtilTest {
    //过期毫秒时⻓30分钟
    public static final long Expiration=30*60*1000;
    //密钥
    private static final String secretString="bAYtsuA0weATJrh1cNukTyFWwSe4WTOk07i46vYLI3M=";
    //⽣成安全密钥
    private static final SecretKey KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
    @Test
    public void genJwt(){
        //⾃定义信息
        Map<String,Object> claim = new HashMap<>();
        claim.put("id",1);
        claim.put("username","zhangsan");
        String jwt = Jwts.builder()
                .setClaims(claim) //⾃定义内容(负载)
                .setIssuedAt(new Date())//设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + Expiration)) //设置过期时间
                .signWith(KEY) //签名算法
                .compact();
        System.out.println(jwt);
    }

    /**
     ⽣成密钥
     */
    @Test
    public void genKey(){
        //创建了⼀个密钥对象，使⽤HS256签名算法。
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        //将密钥编码为Base64字符串。
        String secretString = Encoders.BASE64.encode(key.getEncoded());
        System.out.println(secretString);
    }

    @Test
    public void parseJWT(){
        String token="eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ6aGFuZ3NhbiIsImlhd" +
                "CI6MTcyNTE5NjM0MCwiZXhwIjoxNzI1MTk4MTQwfQ.WaYORS3pW4Bwow0bpa5wTPVMOqRqo9jsmlkj5yuckhA";
        //创建解析器,设置签名密钥
        JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder().setSigningKey(KEY);
        //解析token
        Claims claims = jwtParserBuilder.build().parseClaimsJws(token).getBody();
        System.out.println(claims);
    }

}

