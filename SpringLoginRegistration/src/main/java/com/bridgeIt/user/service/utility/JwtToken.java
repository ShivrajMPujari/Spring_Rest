package com.bridgeIt.user.service.utility;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtToken {

	
	public String createJwt(String id,String subject,long millSec) {
		
	SignatureAlgorithm  signatureAlgorithm = SignatureAlgorithm.HS256;
	long currentTime=System.currentTimeMillis();
	Date now = new Date(currentTime);
	System.out.println(id+"--id and subject--"+subject);
	JwtBuilder builder =    Jwts.builder().setId(id)
					.setIssuedAt(now)
					.setSubject(subject)
					.signWith(signatureAlgorithm, "myKey");
	
	millSec=System.currentTimeMillis()+millSec;
	Date exp = new Date(millSec);
	builder.setExpiration(exp);
	return	 builder.compact();			
	}
	
	
	
	public Claims parseJwt(String jwt) {
		
		Claims claims=	Jwts.parser().setSigningKey("myKey")
				.parseClaimsJws(jwt).getBody();
		
		return claims;
	}
	
	public String getJwtId(String jwt) {
		
		Claims claims=	Jwts.parser().setSigningKey("myKey")
				.parseClaimsJws(jwt).getBody();
		
		return claims.getId();
		
	}
	
	public String getJwtSubject(String jwt) {
		
		Claims claims=	Jwts.parser().setSigningKey("myKey")
				.parseClaimsJws(jwt).getBody();
		
		return claims.getSubject();
		
	}
	
}
