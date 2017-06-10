package com.want.batch.job.stock_collect.reportmgr.parseToExcel;

import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.util.Region;

import com.want.batch.job.stock_collect.routemgr.bo.RouteCardBO;
import com.want.batch.job.stock_collect.util.DateFormater;

public class RouteCardReportExcel {
  public RouteCardReportExcel() {
   }

   private DateFormater df = new DateFormater();
   private int linesPerPage = 64000;

   public void setSheet(String title, ArrayList list, HSSFCellStyle cs,
                        HSSFSheet sheet) {


     sheet.addMergedRegion(new Region( (short) 0, (short) 0, (short) 0,
                                      (short) 6)); //(赵始行,起始列,结束行,结束列)

     sheet.setColumnWidth( (short) 0, (short) 5000);
     sheet.setColumnWidth( (short) 1, (short) 5000);
     sheet.setColumnWidth( (short) 2, (short) 5000);
     sheet.setColumnWidth( (short) 3, (short) 5000);
     sheet.setColumnWidth( (short) 4, (short) 5000);
     sheet.setColumnWidth( (short) 5, (short) 5000);
     sheet.setColumnWidth( (short) 6, (short) 5000);

     HSSFRow titleRow = sheet.createRow(0);
     String MONTH= "";
     if(0<list.size()){
       RouteCardBO rcb_H = (RouteCardBO) list.get(0);
       String branch = rcb_H.getBRANCH_NAME();
       String company = rcb_H.getCOMPANY_NAME();
       String year = rcb_H.getVISIT_DATE().toString().substring(0, 4);
       String month = rcb_H.getVISIT_DATE().toString().substring(5, 7);
       String emp_id = rcb_H.getEMP_ID();
       String emp_name = rcb_H.getEMP_NAME();
       MONTH= month;

//     this.createStringCell(titleRow, (short) 0, company+"_"+branch+"_"+route+"区域_"+year+"年"+month+"月"+"行程卡", cs);
     this.createStringCell(titleRow, (short) 0, company+"_"+branch+"_"+emp_name+"("+emp_id+")_"+year+"年"+month+"月"+"行程卡", cs);

   }

     MONTH = MONTH+"月";

     HSSFRow headerRow = sheet.createRow(1);
     createStringCell(headerRow, (short) 0, "周日", cs);
     createStringCell(headerRow, (short) 1, "周一", cs);
     createStringCell(headerRow, (short) 2, "周二", cs);
     createStringCell(headerRow, (short) 3, "周三", cs);
     createStringCell(headerRow, (short) 4, "周四", cs);
     createStringCell(headerRow, (short) 5, "周五", cs);
     createStringCell(headerRow, (short) 6, "周六", cs);

     int line = 2;
     boolean flag = false;
     int nextday = 9;
     int day = 0;
     int maxday =Integer.parseInt(df.getLastDayOfMonth(df.getCurrentYearMonth()).substring(8, 10));

     for (int i = 0; i < list.size();) {
       HSSFRow dataRow = sheet.createRow( (short) line);
       RouteCardBO rcb = (RouteCardBO) list.get(i);
       int dayOfweek = df.getDayOfWeek(rcb.getVISIT_DATE());
       String cellValue = "";
       int total_count = 0;
       int total_count_2 = 0;


       //如果为星期日
       while (dayOfweek == 0) {

         if (day == 0) {
           day = Integer.parseInt(rcb.getVISIT_DATE().toString().substring(8, 10));
           nextday=day;
           cellValue = cellValue + MONTH+day+"日" + "\r\n";
         }

         cellValue = cellValue + rcb.getFORTH_LV_NAME() + "(" +rcb.getSTORE_COUNT_2()+"/"+
             rcb.getSTORE_COUNT() + ")" + "\n";
         total_count = total_count + rcb.getSTORE_COUNT();
         total_count_2 = total_count_2 + rcb.getSTORE_COUNT_2();
         if ( (i + 1) < list.size()){
           rcb = (RouteCardBO) list.get(++i);
           dayOfweek = df.getDayOfWeek(rcb.getVISIT_DATE());
           if (day !=
               Integer.parseInt(rcb.getVISIT_DATE().toString().substring(8, 10))) {
             break;
           }
         }else {
           dayOfweek = 0;
           i++;
           break;
         }
         flag = true;
       }
       if (!"".equals(cellValue)) {
         cellValue = cellValue + "合计(" + total_count_2+"/"+total_count + ")" + "\n";

       }
       else if(flag && maxday>nextday){
         cellValue = cellValue + (++nextday) + "\r\n";
       }

       createStringCell(dataRow, (short) 0, cellValue, cs);
       cellValue = ""; //清空
       total_count = 0; //清空
       total_count_2= 0;
       day = 0;

       //如果为星期一
       while (dayOfweek == 1) {

         if (day == 0) {
           day = Integer.parseInt(rcb.getVISIT_DATE().toString().substring(8, 10));
           nextday=day;
           cellValue = cellValue + MONTH+day+"日" + "\r\n";
         }
//         logger.info("rcb.getSTORE_COUNT_2()=" + rcb.getSTORE_COUNT_2());
         cellValue = cellValue + rcb.getFORTH_LV_NAME() + "(" +rcb.getSTORE_COUNT_2()+"/"+
             rcb.getSTORE_COUNT() + ")" + "\r\n";
         total_count = total_count + rcb.getSTORE_COUNT();
         total_count_2 = total_count_2 + rcb.getSTORE_COUNT_2();
         if ( (i + 1) < list.size()) {
           rcb = (RouteCardBO) list.get(++i);
           dayOfweek = df.getDayOfWeek(rcb.getVISIT_DATE());
           if (day !=
               Integer.parseInt(rcb.getVISIT_DATE().toString().substring(8, 10))) {
             break;
           }

         }
         else {
           dayOfweek = 0;
           i++;
           break;
         }
         flag = true;
       }
       if (!"".equals(cellValue)) {
         cellValue = cellValue + "合计(" + total_count_2+"/"+total_count + ")" + "\n";
//         logger.info("cellValue=" + cellValue);
       }else if(flag && maxday>nextday){
         cellValue = cellValue + (++nextday) + "\r\n";
       }
       createStringCell(dataRow, (short) 1, cellValue, cs);
       cellValue = ""; //清空
       total_count = 0; //清空
       total_count_2= 0;
       day = 0;

       //如果为星期二
       while (dayOfweek == 2) {

         if (day == 0) {
           day = Integer.parseInt(rcb.getVISIT_DATE().toString().substring(8, 10));
           nextday=day;
           cellValue = cellValue + MONTH+day+"日" + "\r\n";
         }
         cellValue = cellValue + rcb.getFORTH_LV_NAME() + "(" +rcb.getSTORE_COUNT_2()+"/"+
             rcb.getSTORE_COUNT() + ")" + "\n";
         total_count = total_count + rcb.getSTORE_COUNT();
         total_count_2 = total_count_2 + rcb.getSTORE_COUNT_2();
         if ( (i + 1) < list.size()) {
           rcb = (RouteCardBO) list.get(++i);
           dayOfweek = df.getDayOfWeek(rcb.getVISIT_DATE());
           if (day !=
               Integer.parseInt(rcb.getVISIT_DATE().toString().substring(8, 10))) {
             break;
           }

         }
         else {
           dayOfweek = 0;
           i++;
           break;
         }
         flag = true;
       }
       if (!"".equals(cellValue)) {
         cellValue = cellValue + "合计(" + total_count_2+"/"+total_count + ")" + "\n";
       }else if(flag && maxday>nextday){
         cellValue = cellValue + (++nextday) + "\r\n";
       }
       createStringCell(dataRow, (short) 2, cellValue, cs);
       cellValue = ""; //清空
       total_count = 0; //清空
       total_count_2= 0;
       day = 0;

       //如果为星期三
       while (dayOfweek == 3) {

         if (day == 0) {
           day = Integer.parseInt(rcb.getVISIT_DATE().toString().substring(8, 10));
           nextday=day;
           cellValue = cellValue + MONTH+day+"日" + "\r\n";
         }
        cellValue = cellValue + rcb.getFORTH_LV_NAME() + "(" +rcb.getSTORE_COUNT_2()+"/"+
             rcb.getSTORE_COUNT() + ")" + "\n";
         total_count = total_count + rcb.getSTORE_COUNT();
         total_count_2 = total_count_2 + rcb.getSTORE_COUNT_2();
         if ( (i + 1) < list.size()) {
           rcb = (RouteCardBO) list.get(++i);
           dayOfweek = df.getDayOfWeek(rcb.getVISIT_DATE());
           if (day !=
               Integer.parseInt(rcb.getVISIT_DATE().toString().substring(8, 10))) {
             break;
           }

         }
         else {
           dayOfweek = 0;
           i++;
           break;
         }
         flag = true;
       }
       if (!"".equals(cellValue)) {
         cellValue = cellValue + "合计(" + total_count_2+"/"+total_count + ")" + "\n";
       } else if(flag && maxday>nextday){
         cellValue = cellValue + (++nextday) + "\r\n";
       }
       createStringCell(dataRow, (short) 3, cellValue, cs);
       cellValue = ""; //清空
       total_count = 0; //清空
       total_count_2= 0;
       day = 0;
       //如果为星期四
       while (dayOfweek == 4) {
         if (day == 0) {
           day = Integer.parseInt(rcb.getVISIT_DATE().toString().substring(8, 10));
           nextday=day;
           cellValue = cellValue + MONTH+day+"日" + "\r\n";
         }
         cellValue = cellValue + rcb.getFORTH_LV_NAME() + "(" +rcb.getSTORE_COUNT_2()+"/"+
             rcb.getSTORE_COUNT() + ")" + "\n";
         total_count = total_count + rcb.getSTORE_COUNT();
         total_count_2 = total_count_2 + rcb.getSTORE_COUNT_2();
         if ( (i + 1) < list.size()) {
           rcb = (RouteCardBO) list.get(++i);
           dayOfweek = df.getDayOfWeek(rcb.getVISIT_DATE());
           if (day !=
               Integer.parseInt(rcb.getVISIT_DATE().toString().substring(8, 10))) {
             break;
           }

         }
         else {
           dayOfweek = 0;
           i++;
           break;
         }
         flag = true;
       }
       if (!"".equals(cellValue)) {
         cellValue = cellValue + "合计(" + total_count_2+"/"+total_count + ")" + "\n";
       } else if(flag && maxday>nextday){
         cellValue = cellValue + (++nextday) + "\r\n";
       }
       createStringCell(dataRow, (short) 4, cellValue, cs);
       cellValue = ""; //清空
       total_count = 0; //清空
       total_count_2= 0;
       day = 0;

       //如果为星期五
       while (dayOfweek == 5) {

         if (day == 0) {
           day = Integer.parseInt(rcb.getVISIT_DATE().toString().substring(8, 10));
           nextday=day;
          cellValue = cellValue + MONTH+day+"日" + "\r\n";
         }
         cellValue = cellValue + rcb.getFORTH_LV_NAME() + "(" +rcb.getSTORE_COUNT_2()+"/"+
             rcb.getSTORE_COUNT() + ")" + "\n";
         total_count = total_count + rcb.getSTORE_COUNT();
         total_count_2 = total_count_2 + rcb.getSTORE_COUNT_2();
         if ( (i + 1) < list.size()) {
           rcb = (RouteCardBO) list.get(++i);
           dayOfweek = df.getDayOfWeek(rcb.getVISIT_DATE());
           if (day !=
               Integer.parseInt(rcb.getVISIT_DATE().toString().substring(8, 10))) {
             break;
           }

         }
         else {
           dayOfweek = 0;
           i++;
           break;
         }
         flag = true;
       }
       if (!"".equals(cellValue)) {
         cellValue = cellValue + "合计(" + total_count_2+"/"+total_count + ")" + "\n";
       } else if(flag && maxday>nextday){
         cellValue = cellValue + (++nextday) + "\r\n";
       }
       createStringCell(dataRow, (short) 5, cellValue, cs);
       cellValue = ""; //清空
       total_count = 0; //清空
       total_count_2= 0;
       day = 0;

       //如果为星期六
       while (dayOfweek == 6) {

         if (day == 0) {
           day = Integer.parseInt(rcb.getVISIT_DATE().toString().substring(8, 10));
           nextday=day;
          cellValue = cellValue + MONTH+day+"日" + "\r\n";
         }
         cellValue = cellValue + rcb.getFORTH_LV_NAME() + "(" +rcb.getSTORE_COUNT_2()+"/"+
             rcb.getSTORE_COUNT() + ")" + "\r\n";
         total_count = total_count + rcb.getSTORE_COUNT();
         total_count_2 = total_count_2 + rcb.getSTORE_COUNT_2();
         if ( (i + 1) < list.size()) {
           rcb = (RouteCardBO) list.get(++i);
           dayOfweek = df.getDayOfWeek(rcb.getVISIT_DATE());
           if (day !=
               Integer.parseInt(rcb.getVISIT_DATE().toString().substring(8, 10))) {
             break;
           }

         }
         else {
           dayOfweek = 0;
           i++;
           break;
         }
         flag = true;
       }
       if (!"".equals(cellValue)) {
         cellValue = cellValue + "合计(" + total_count_2+"/"+total_count + ")" + "\n";
       }else if(flag && maxday>nextday){
         cellValue = cellValue + (++nextday) + "\r\n";
       }
       createStringCell(dataRow, (short) 6, cellValue, cs);
       cellValue = ""; //清空
       total_count = 0; //清空
       total_count_2= 0;
       day = 0;


       line++;
     }
   }

   private static void createStringCell(HSSFRow row, short index, String value,
                                        HSSFCellStyle cs) {

     HSSFCell cell = row.createCell(index);
     cell.setCellType(HSSFCell.CELL_TYPE_STRING);
     cell.setEncoding(HSSFCell.ENCODING_UTF_16);
     cell.setCellValue(value);
     cell.setCellStyle(cs);

   }
}
