package com.example.demo.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


@Data
public class MetaData {

    @ExcelProperty(value = "课程名称")
    private String courseName;

    @ExcelProperty(value = "任课教师")
    private String teachersName;

    @ExcelProperty(value = "课程安排")
    private String courseArrange;

}
