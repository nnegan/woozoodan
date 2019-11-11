package com.wzd.gateway.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class JwtController {

    @Autowired
    private JwtService jwtService;

    // JWT 생성
    @PostMapping("/jwt/create")
    public String createJwt(HttpServletRequest res) throws Exception {

        return jwtService.makeJwt(res);
    }
    
    // 키 점검
    @PostMapping("/jwt/auth")
    public boolean authToken(HttpServletRequest res) throws Exception {
        //String jwt = res.getParameter("jwt");
    	String jwt = res.getHeader("Authorization");

    	if(jwt == null) {
            return false;
        }else {
            return jwtService.checkJwt(jwt);
        }
    }
}


