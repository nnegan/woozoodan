package com.wzd.gateway.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*makeJwt 메소드를보면 헤더에 alg와 typ를 설정해주었고,
클레임에는 비공개클레임으로 간단히 email과 name을 설정해주었다.
또한 만료시간으로는 1분으로 잡았다.


아래에 checkJwt메소드에서는 try문에서 받아온 Jwt를 이용하여 파싱을하는데 여기서 파싱이 된다면
정상적인 토큰으로 간주하고 여기서 파싱이 되지 않는다면 catch문에 잡힐 것이다.*/
@Service
public class JwtServiceImpl implements JwtService {

    private String secretKey = "AAAABBBBDDFFFRRTTWWWSS";

    private Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    @Override
    public String makeJwt(HttpServletRequest res) throws Exception {
    	// 서명 알고리즘
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // 유효기간 밀리세컨즈
        Date expireTime = new Date();
        expireTime.setTime(expireTime.getTime() + 1000 * 60 * 1);
        // Base64로 인코딩
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        
        logger.info("apiKeySecretBytes=>" + apiKeySecretBytes);
        
        // Key + 알고리즘
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        Map<String, Object> headerMap = new HashMap<String, Object>();

        headerMap.put("typ","JWT");
        headerMap.put("alg","HS256");

        Map<String, Object> map= new HashMap<String, Object>();

        String name = res.getParameter("name");
        String email = res.getParameter("email");

        map.put("name", name);
        map.put("email", email);
        
        // JWT 생성
        JwtBuilder builder = Jwts.builder().setHeader(headerMap)
                .setClaims(map)
                .setExpiration(expireTime)
                .signWith(signatureAlgorithm, signingKey);
        
        String jwt = builder.compact();
        
        logger.info("jwt=>" + jwt);
     
        return jwt;
    }

    @Override
    public boolean checkJwt(String jwt) throws Exception {
        try {
            Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                    .parseClaimsJws(jwt).getBody(); // 정상 수행된다면 해당 토큰은 정상토큰

            logger.info("expireTime :" + claims.getExpiration());
            logger.info("name :" + claims.get("name"));
            logger.info("Email :" + claims.get("email"));

            return true;
        } catch (ExpiredJwtException exception) {
            logger.info("토큰 만료");
            return false;
        } catch (JwtException exception) {
            logger.info("토큰 변조");
            return false;
        }
    }
}


