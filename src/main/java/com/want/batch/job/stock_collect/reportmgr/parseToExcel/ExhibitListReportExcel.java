package com.want.batch.job.stock_collect.reportmgr.parseToExcel;

import java.util.ArrayList;



import com.want.batch.job.stock_collect.storemgr.bo.ExhibitViewBO;

import edu.npu.fastexcel.Sheet;
import edu.npu.fastexcel.Workbook;

public class ExhibitListReportExcel {
  public ExhibitListReportExcel() {
  }

  private int linesPerPage = 65500 ;

 public boolean setSheet(String title, Workbook wb, ArrayList list) {
   try {
     String header[] = {
        "事业部","分公司", "営业所", "三级地区", "客户编号", "客户名称","单据号","业代编码","业代姓名","业代状态","终端ID",
        "终端名称","填写状况","特陈位置","陈列形式","陈列面积","拜访次数"};
     int pages = this.getPageNumbers(list);
     for (int z = 0; z < pages; z++) {
       Sheet sheet = wb.addSheet("sheet" + z);
       sheet.setRow(0, header);
       int limit = (z + 1) * linesPerPage;

       limit = Math.min(limit, list.size());
       int line = 1;
       for (int i = z * linesPerPage; i < limit; i++) {
         ExhibitViewBO vo = (ExhibitViewBO) list.get(i);
         sheet.setCell(line, 0, vo.getDIVSION());
         sheet.setCell(line, 1, vo.getCOMPANY());
         sheet.setCell(line, 2, vo.getBRANCH());
         sheet.setCell(line, 3, vo.getTHIRD());
         sheet.setCell(line, 4, vo.getCUSTOMER_ID());
         sheet.setCell(line, 5, vo.getCUSTOMER_NAME());
         sheet.setCell(line, 6, vo.getSD_NO());
         sheet.setCell(line, 7, vo.getEMP_ID());
         sheet.setCell(line, 8, vo.getEMP_NAME());
         sheet.setCell(line, 9, vo.getEMP_STATUS());
         sheet.setCell(line, 10, vo.getSTORE_ID());
         sheet.setCell(line, 11, vo.getSTORE_NAME());
         sheet.setCell(line, 12, vo.getSTATUS());
         sheet.setCell(line, 13, vo.getLOCATION());
         sheet.setCell(line, 14, vo.getDISPLAY());
         sheet.setCell(line, 15, vo.getDISACREAGE());
         sheet.setCell(line, 16, vo.getCOUNT());


         line++;
         if (i > 0) {
           list.set(i - 1, null);
         }
       }
     }
     list = null;
     return true;
   }
   catch (Exception ex) {

     ex.printStackTrace();
     return false;

   }
   finally {

   }

 }

 private int getPageNumbers(ArrayList list) {
   if (list != null && list.size() > linesPerPage) {
     int segments = list.size() / linesPerPage;
     if ( (list.size() % linesPerPage) > 0) {
       segments = segments + 1;
     }
     return segments;
   }
   return 1;
 }

}
