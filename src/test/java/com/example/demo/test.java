package com.example.demo;

import com.example.demo.util.PathInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


public class test {
    public static void main(String[] args) {
        System.out.println(PathInfo.basePath+PathInfo.pdfPath);
    }
}
