package com.mintit.incentive.common.util;


import com.mintit.incentive.user.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

    private SecretKey key;
    private SecretKey refreshKey;
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.refresh.secret}")
    private String refreshSecretKey;
    private JwtParser jwtParser;


    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(this.secretKey.getBytes());
        this.refreshKey = Keys.hmacShaKeyFor(this.refreshSecretKey.getBytes());
        this.jwtParser = Jwts.parserBuilder().setSigningKey(this.key).build();
    }

    /**
     * AccessToken 발급 - URL { /auth/** , /cert/** } 를 제외한 모든 요청에 사용
     *
     * @param userEntity
     * @return jwt token
     */
    public String generateJwtToken(UserEntity userEntity) {
        JwtBuilder builder = Jwts.builder()
                                 .claim("userUuid", userEntity.getUserUuid())
                                 .claim("userNm", userEntity.getUserNm())
                                 .setIssuedAt(Date.from(Instant.now()))
                                 .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                                 .signWith(this.key);
        return builder.compact();
    }

    /**
     * Refresh Token 발급 - Access Token 을 재발급 받을때 사용
     *
     * @param userEntity
     * @return refresh jwt token
     */
    public String generateRefreshJwtToken(UserEntity userEntity) {
        JwtBuilder builder = Jwts.builder()
                                 .claim("userUuid", userEntity.getUserUuid())
                                 .claim("userNm", userEntity.getUserNm())
                                 .setIssuedAt(Date.from(Instant.now()))
                                 .setExpiration(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                                 .signWith(this.refreshKey);
        return builder.compact();
    }


    /**
     * 타입별 파서를 지정
     *
     * @param type
     * @return this;
     */
    public JWTUtil setJwtParser(String type) {
        switch (type.toUpperCase()) {
            case "JWT":
                this.jwtParser = Jwts.parserBuilder().setSigningKey(this.key).build();
                break;
            case "REFRESH":
                this.jwtParser = Jwts.parserBuilder().setSigningKey(this.refreshKey).build();
                break;
        }
        return this;
    }

    /**
     * jwt - 유저 아이디 반환
     *
     * @param token
     * @return userId
     */
    //    public String getLoginIdByJwt(String token) {
    //        Claims claims = jwtParser.parseClaimsJws(token).getBody();
    //        return (String) claims.get("userId");
    //    }

    /**
     * jwt - 유저 uuid 반환
     *
     * @param token
     * @return uuid
     */
    public String getLoginUuidByJwt(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return (String) claims.get("userUuid");
    }

    /**
     * jwt 모든 정보 반환
     *
     * @param token
     * @return claims
     */
    public Claims getLoginByJwt(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    /**
     * 유효한 토큰인지 확인하여 반환
     *
     * @param user
     * @param token
     * @return boolean
     */
    public boolean validateJwtToken(UserDetails user, String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claims.getExpiration().after(Date.from(Instant.now()));
    }
}
