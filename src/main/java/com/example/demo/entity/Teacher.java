package com.example.demo.entity;

import lombok.Data;

@Data
public class Teacher {

    public Teacher(String name) {
        this.name = name;
    }
    //老师姓名

    private String name;

    //老师工号
    //private BigDecimal workNumber;
}
