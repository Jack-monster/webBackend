package com.example.demo.entity;

import lombok.Data;

@Data
public class Course {
    public Course(String name, String arrangement) {
        this.name = name;
        this.arrangement = arrangement;
    }

    private String name;

    private String arrangement;
}
