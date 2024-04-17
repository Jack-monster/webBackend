package com.example.demo.controller;

import com.example.demo.util.ExcelToPdfConverter;
import com.example.demo.util.Utils;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    @GetMapping("/get")
    public ResponseEntity<byte[]> getPdf(@RequestParam("name") String name) throws Exception {
        // 假设 PDF 文件存储在项目的 resources/static/pdfs 目录下

        Workbook workbook = Utils.getResultExcel(name);
        String filePath = "C:\\Users\\86182\\Desktop\\Myworkspace\\demo\\src\\main\\resources\\static\\" + name + ".xls";
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        workbook.write(fileOutputStream);

        ExcelToPdfConverter.convertExcelToPdf(filePath, "C:\\Users\\86182\\Desktop\\Myworkspace\\demo\\src\\main\\resources\\static\\" + name  + ".pdf");

        // 使用 Spring Resource API 获取文件
        ClassPathResource resource = new ClassPathResource("static/" + name  + ".pdf");
        File newfile = resource.getFile();

        // 读取文件内容为字节数组
        FileInputStream fis = new FileInputStream(newfile);
        byte[] pdfBytes = fis.readAllBytes();
        fis.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline").filename(name + ".pdf").build());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}