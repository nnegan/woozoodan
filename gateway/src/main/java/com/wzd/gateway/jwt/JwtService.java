package com.wzd.gateway.jwt;

import javax.servlet.http.HttpServletRequest;

public interface JwtService {
	public String makeJwt(HttpServletRequest res) throws Exception;
	public boolean checkJwt(String jwt) throws Exception;
	
}
