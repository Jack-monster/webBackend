package com.example.demo.Service;

import com.example.demo.entity.Teacher;

public class UserService {


    public Teacher authenticate(String name, String password){
        System.out.println("authenticate");
        return new Teacher(name);
    }
}
