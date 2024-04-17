package com.example.demo.controller;

import com.example.demo.entity.LoginRequest;
import com.example.demo.entity.Teacher;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class LoginController {
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest loginRequest)
    {
        System.out.println("here in login");
        String token = JwtUtil.generateToken(loginRequest.username);
        return Result.ok().data("token", token);
    }

    @GetMapping("/info")
    public Result info(String token){
        String username = JwtUtil.getClaimsFromToken(token).getSubject();
        String url = "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif";
        return Result.ok().data("name", username).data("avatar", url);
    }

    @PostMapping("/logout")
    public Result logout(){
        return Result.ok();
    }
}
