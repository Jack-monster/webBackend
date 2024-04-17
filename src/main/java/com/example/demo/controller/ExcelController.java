package com.example.demo.controller;

import com.example.demo.util.Utils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/excel")
public class ExcelController {


    @GetMapping("/get")
    public ResponseEntity<byte[]> getExcel(@RequestParam("name") String name, HttpServletResponse response) throws IOException {
        // 假设您已经有了一个方法 generateWorkbook()，它负责根据业务逻辑生成 Workbook 对象
        Workbook workbook = Utils.getResultExcel(name);

//        String filePath = "C:\\Users\\86182\\Desktop\\Myworkspace\\demo\\src\\main\\resources\\static" + name + ".xls";
//        File file = new File(filePath);
//        if (!file.getParentFile().exists()) {
//            file.getParentFile().mkdirs();
//        }
//        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
//        workbook.write(fileOutputStream);

        // 将 Workbook 对象转换为字节数组（二进制数据）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        byte[] excelBytes = baos.toByteArray();

        // 设置响应头，指示浏览器以 "attachment" 方式下载文件，并指定文件名
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "report.xls");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        // 返回 200 OK 状态码，附带 Excel 文件的二进制数据
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }


}
