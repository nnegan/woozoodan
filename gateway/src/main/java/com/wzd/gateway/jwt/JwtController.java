package com.wzd.gateway.jwt;

import com.wzd.common.model.CommonDataModel;
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
    public CommonDataModel<String> createJwt(HttpServletRequest res) throws Exception {

        return new CommonDataModel<String>(jwtService.makeJwt(res));
    }
    
    // 키 점검
    @PostMapping("/jwt/auth")
    public CommonDataModel<Boolean> authToken(HttpServletRequest res) throws Exception {
        //String jwt = res.getParameter("jwt");
    	String jwt = res.getHeader("Authorization");

    	if(jwt == null) {
            return new CommonDataModel<Boolean>(false);
        }else {
            return new CommonDataModel<Boolean>(jwtService.checkJwt(jwt));
        }
    }
}


