package com.example.demo.util;

import com.alibaba.excel.EasyExcel;
import com.example.demo.entity.Course;
import com.example.demo.entity.MetaData;
import com.example.demo.entity.Teacher;
import lombok.extern.slf4j.XSlf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

//    public static void main(String[] args) throws IOException {
//        Workbook resultExcel = getResultExcel("安翔");
//        System.out.println(resultExcel);
//    }
    /**
     * 找到该课程需要填写的单元格行数
     * @param target
     * 课次信息如“星期四[1-2节]"
     * @return
     * 返回行数
     */
    public static List<Integer> findRows(String target) {

        //解析字符串是否符合规范
        try {
            boolean isValid = checkFormat(target);
            if (isValid) {
                System.out.println("String format is valid.");
            } else {
                System.out.println("String format is invalid.");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }

        // 解析星期
        String weekdayStr = target.substring(2, 3); // 获取"星期四"中的"四"
        int weekday = parseWeekday(weekdayStr);

        // 解析节次范围
        String rangeStr = target.substring(target.indexOf("[")+1 , target.indexOf("节")); // 获取课次跨度
        String[] rangeParts = rangeStr.split("-");

        int startLesson = Integer.parseInt(rangeParts[0]); //获取最开始的课次
        int endLesson = Integer.parseInt(rangeParts[1]); // 获取结束的课次
        int baseRow = (weekday - 1) * 5 + 5; // 基准行数
        int offset = 0; //设置偏差值，最后的行数应该为baseRow+offset
        List<Integer> rows = new ArrayList<>();

        if(startLesson >= 9){ //已经超出了课程范围，但确实有这种情况 “11-11” 以及“9-11”，我们统一认为是最后两节
            offset = 5;

            rows.add(baseRow+offset);
        }else{  //如果行数小于9，则存在跨行的情况，我们得到初始课次对应的行数，以及结束课次对应的行数，形成数字序列返回即可
            int startOffset = getOffset(startLesson);
            int endOffset = getOffset(endLesson);
            List<Integer> rowList = parseNumberSequence(Integer.toString(startOffset+baseRow) + '-' + Integer.toString(endOffset+baseRow));

            rows.addAll(rowList);
        }
        return rows;
    }

    /**
     * 找到该课程的列信息
     * @param target
     * 列信息对应的字符串例如“2-9,11-15周”
     * @return
     * 范围列数
     */
    public static List<Integer> findColumns(String target){
        String columnStr    = target.substring(0, target.indexOf("周")); //得到数字字符串
        List<Integer> list = parseNumberSequence(columnStr);
        for (int i = 0; i < list.size(); i++) {
            list.set(i,list.get(i) + 2);
        }
        return list;
    }

    public static int parseWeekday(String weekdayStr) {
        switch (weekdayStr) {
            case "一":
                return 1;
            case "二":
                return 2;
            case "三":
                return 3;
            case "四":
                return 4;
            case "五":
                return 5;
            case "六":
                return 6;
            case "日":
                return 7;
            default:
                throw new IllegalArgumentException("Invalid weekday string: " + weekdayStr);
        }
    }


    public static boolean checkFormat(String target) {
        // 正则表达式匹配字符串格式
        String regex = "^星期[一二三四五六日]\\[\\d+-\\d+节$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(target);
        return matcher.matches();
    }

    /**
     * 用于输入数字字符串序列 例如“1-2,5,6-7”,得到对应的list数组
     * @param input
     * 输入的字符串序列
     * @return
     * 处理过后的数字序列
     */
    public static List<Integer> parseNumberSequence(String input) {
        List<Integer> sequence = new ArrayList<>();

        // 切割字符串
        String[] parts = input.split(",");

        // 遍历每个部分
        for (String part : parts) {
            // 如果部分包含 "-"，则解析数字范围
            if (part.contains("-")) {
                String[] rangeParts = part.split("-");
                if (rangeParts.length != 2) {
                    throw new IllegalArgumentException("Invalid range format");
                }
                int start = Integer.parseInt(rangeParts[0]);
                int end = Integer.parseInt(rangeParts[1]);
                for (int i = start; i <= end; i++) {
                    sequence.add(i);
                }
            } else {
                // 否则，解析单个数字
                sequence.add(Integer.parseInt(part));
            }
        }

        return sequence;
    }

    /**
     * 得到对应课次的偏差值
     * @param courseIndex
     * 课次
     * @return
     * 返回偏差值
     */
    public static int getOffset(int courseIndex){
        if(isOdd(courseIndex)){
            return (courseIndex+1)/2;
        }else{
            return courseIndex/2;
        }
    }

    public static boolean isOdd(int number) {
        return number % 2 != 0;
    }

    /**
     * 输入文件路径读取Excel
     * @param path
     * @return
     */
    public static List<MetaData> getExcel(String path){
        InputStream resourceAsStream = Utils.class.getResourceAsStream(path);
        return EasyExcel.read(resourceAsStream).sheet(0).head(MetaData.class).doReadSync();
    }

    public static Workbook getResultExcel(String name) throws IOException {
        HSSFWorkbook workbook = null;
        try(InputStream resourceAsStream = Utils.class.getResourceAsStream(PathInfo.calendarPath)){
            if (resourceAsStream == null) {
                throw new IllegalArgumentException("Resource not found: " + PathInfo.calendarPath);
            }
            workbook = new HSSFWorkbook(resourceAsStream);
        } catch (IOException e) {
            System.out.println("读取Excel文件时发生错误: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println("出现意外错误: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("读取Excel文件时发生意外错误", e);
        }
//        String excelFilePath = "E:\\idea_java_projects\\mavenLearning\\good\\src\\main\\resources\\2023-2024学年第二学期教学日历.xls";
//        FileInputStream fileInputStream = new FileInputStream(excelFilePath);


        HashMap<Teacher, List<Course>> teacherListHashMap = new HashMap<>();

        //处理teacherListHashMap
        List<MetaData> excel = getExcel(PathInfo.coursePath);
        for (MetaData line :
                excel) {
            String[] teacherNames = line.getTeachersName().split(",");
            String courseName = line.getCourseName();
            String courseArrange = line.getCourseArrange();
            Course course = new Course(courseName, courseArrange);
            for (String Tname :
                    teacherNames) {
                Teacher teacher = new Teacher(Tname);
                addValue(teacherListHashMap,teacher,course);
            }
        }

        Teacher teacherToSearch = new Teacher(name);
        List<Course> courses = teacherListHashMap.get(teacherToSearch);
        if (courses == null){
            return workbook;
        }
        for (Course course:
             courses) {
            String courseName = course.getName().substring(0,2);
            String arrangement = course.getArrangement();
            String[] split = arrangement.split(";");
            for (String everyArrange :
                    split) {
                String[] s = everyArrange.split(" ");
                String weekArrange = s[0];
                String dayArrange = s[1].substring(0,s[1].indexOf("节")+1);
                List<Integer> columns = findColumns(weekArrange);
                List<Integer> rows = findRows(dayArrange);
                if(rows.size() == 1){
                    simpleFill(workbook,rows.get(0),columns,courseName);
                }else if(rows.size() > 1){
                    mergeAndFill(workbook,rows,columns,courseName);
                }

            }
        }

        return workbook;
    }

    /**
     * 不存在则创建一个arraylist，然后加上value
     * @param map
     * @param key
     * @param value
     */
    public static void addValue(Map<Teacher, List<Course>> map, Teacher key, Course value) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    public static void simpleFill(Workbook workbook, int rowNumber, List<Integer> columns, String name) {
        CellStyle style = createCellStyle(workbook);
        Sheet sheet = workbook.getSheetAt(0); // 假设只有一个sheet

        // 遍历行数和列数列表，进行纵向合并和填充
            Row row = sheet.getRow(rowNumber - 1); // Excel行数从0开始，而行数列表从1开始
            if (row == null) {
                row = sheet.createRow(rowNumber - 1);
            }
            for (int columnNumber : columns) {
                Cell cell = row.createCell(columnNumber - 1); // Excel列数从0开始，而列数列表从1开始
                cell.setCellValue(name);
                cell.setCellStyle(style);
            }


    }

    public static void mergeAndFill(Workbook workbook, List<Integer> rows, List<Integer> columns, String name) throws IOException {
        CellStyle style = createCellStyle(workbook);
        Sheet sheet = workbook.getSheetAt(0); // 假设只有一个sheet

        // 遍历行数和列数列表，进行纵向合并和填充
        for (int rowNumber : rows) {
            Row row = sheet.getRow(rowNumber - 1); // Excel行数从0开始，而行数列表从1开始
            if (row == null) {
                row = sheet.createRow(rowNumber - 1);
            }
            for (int columnNumber : columns) {
                Cell cell = row.createCell(columnNumber - 1); // Excel列数从0开始，而列数列表从1开始
                cell.setCellValue(name);
                cell.setCellStyle(style);

            }

        }
        for (int col :
                columns) {
//            sheet.addMergedRegion(new CellRangeAddress(rows.get(0) - 1, rows.get(rows.size() - 1) - 1, col-1,col-1 ));
            mergeCells(sheet,rows.get(0)-1,rows.get(rows.size() - 1)-1,col-1,col-1);

        }

    }

    private static CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    public static void mergeCells(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        // 检查目标区域是否已经存在合并单元格
        CellRangeAddress existingMergedRegion = findExistingMergedRegion(sheet, firstRow, lastRow, firstCol, lastCol);
        if (existingMergedRegion != null) {
            // 取消原来的合并
            sheet.removeMergedRegion(sheet.getMergedRegions().indexOf(existingMergedRegion));
        }

        // 进行新的合并
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    private static CellRangeAddress findExistingMergedRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
            int firstRow1 = mergedRegion.getFirstRow();
            int lastRow1 = mergedRegion.getLastRow();
            int firstCol1 = mergedRegion.getFirstColumn();
            int lastCol1 = mergedRegion.getLastColumn();
            if(isOverlap(firstRow1,lastRow1,firstCol1,lastCol1,firstRow,lastRow,firstCol,lastCol)){
                return mergedRegion;
            }
        }
        return null;
    }

    public static boolean isOverlap(int firstRowA, int lastRowA, int firstColA, int lastColA,
                                    int firstRowB, int lastRowB, int firstColB, int lastColB) {
        // 矩形A的右边界小于矩形B的左边界，或者矩形A的左边界大于矩形B的右边界，不重叠
        if (lastColA < firstColB || firstColA > lastColB) {
            return false;
        }

        // 矩形A的下边界小于矩形B的上边界，或者矩形A的上边界大于矩形B的下边界，不重叠
        if (lastRowA < firstRowB || firstRowA > lastRowB) {
            return false;
        }

        // 其他情况下重叠
        return true;
    }
}
