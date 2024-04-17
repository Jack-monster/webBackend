package com.example.demo.util;


import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;


public class ExcelToPdfConverter {

    public static void main(String[] args) throws Exception {
        convertExcelToPdf("C:\\Users\\86182\\Desktop\\Myworkspace\\demo\\src\\main\\resources\\static\\2023-2024学年第二学期教学日历.xls", "C:\\Users\\86182\\Desktop\\Myworkspace\\demo\\src\\main\\resources\\static\\2023-2024学年第二学期教学日历.pdf");
    }
    public static void convertExcelToPdf(String sourceExcelPath, String destinationPdfPath) throws Exception {
        // 创建 Workbook 对象，加载 Excel 文件
        Workbook workbook = new com.aspose.cells.Workbook(sourceExcelPath);

        // 创建 PdfSaveOptions 对象，设置 PDF 转换选项（此处使用默认设置）
        PdfSaveOptions options = new PdfSaveOptions();

        // 将 Excel 工作簿保存为 PDF 文件
        workbook.save(destinationPdfPath, options);

        // 清理资源
        workbook.dispose();
    }
}
