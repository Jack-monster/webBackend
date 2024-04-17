package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


import java.util.Date;

public class JwtUtil {
    private static long expire = 60 * 60 * 24 * 7;

    private static String secrete = "MonsterJack";

    public static String generateToken(String username){
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + expire * 1000);
        return Jwts.builder()
                .setHeaderParam("type","JWT")
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expireTime)
                .signWith(SignatureAlgorithm.HS256,secrete)
                .compact();
    }

    public static Claims getClaimsFromToken(String token){
        return Jwts.parser()
                .setSigningKey(secrete)
                .parseClaimsJws(token)
                .getBody();
    }
}
