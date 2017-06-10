package com.want.batch.job.stock_collect.reportmgr.parseToExcel;
import java.util.ArrayList;

import com.want.batch.job.stock_collect.routemgr.bo.RouteDetailBO;

import edu.npu.fastexcel.Sheet;
import edu.npu.fastexcel.Workbook;
public class RouteReportExcel {
  public RouteReportExcel() {
  }

  private int linesPerPage = 64000;

  public boolean setSheet(String title, Workbook wb, ArrayList list) {
    try {
      int pages = this.getPageNumbers(list);
      String header[] = {
          "分公司", "営业所", "路线", "子路线", "是否必拜访", "拜访时间",
          "终端编码", "终端名称", "终端类别", "老板", "地址", "电话1", "电话2", "手机1", "手机2",
          "客户编号", "客户名称", "业代工号", "业代姓名", "上个月业绩", "上三个月份总业绩"};
      for (int z = 0; z < pages; z++) {
        Sheet sheet = wb.addSheet("sheet" + z);
        sheet.setRow(0, header);
        int limit = (z + 1) * linesPerPage;
        limit = Math.min(limit, list.size());
        int line = 1;
        for (int i = z * linesPerPage; i < limit; i++) {
          RouteDetailBO vo = (RouteDetailBO) list.get(i);
          sheet.setCell(line, 0, vo.getCOMPANY_NAME());
          sheet.setCell(line, 1, vo.getBRANCH_NAME());
          sheet.setCell(line, 2, vo.getROUTE_NAME());
          sheet.setCell(line, 3, vo.getSUBROUTE_NAME());
          sheet.setCell(line, 4, vo.getMUST_VISIT());
          sheet.setCell(line, 5, vo.getVISIT_DATE().toString());
          sheet.setCell(line, 6, vo.getSTORE_ID());
          sheet.setCell(line, 7, vo.getSTORE_NAME());
          sheet.setCell(line, 8, vo.getSTORE_TYPE());
          sheet.setCell(line, 9, vo.getSTORE_OWNER());
          sheet.setCell(line, 10, vo.getADDRESS());
          sheet.setCell(line, 11, vo.getPHONE1());
          sheet.setCell(line, 12, vo.getPHONE2());
          sheet.setCell(line, 13, vo.getSTORE_MOBILE1());
          sheet.setCell(line, 14, vo.getSTORE_MOBILE2());
          sheet.setCell(line, 15, vo.getFORWARDER_ID());
          sheet.setCell(line, 16, vo.getFORWARDER_NAME());
          sheet.setCell(line, 17, vo.getEMP_ID());
          sheet.setCell(line, 18, vo.getEMP_NAME());
          sheet.setCell(line, 19, vo.getLAST_MONTH_AMT());
          sheet.setCell(line, 20, vo.getLAST_3_MONTH_AMT());
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
