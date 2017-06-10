package com.want.batch.job.stock_collect.util;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>Title: HeWang2009</p>
 *
 * <p>Description: He Wang System</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Want Want Group</p>
 *
 * @author Zhang_Lei
 * @version 1.0
 * 2009-02-20
 */

public class DateFormater {

    //用来全局控制 上一周，本周，下一周的周数变化

    public DateFormater() {
    }

    //获得下个月的YEARMONTH
    public String getNextYearMonth(String YEARMONTH) {
        String str = "";
        SimpleDateFormat ym = new SimpleDateFormat("yyyyMM");
        String year = YEARMONTH.substring(0, 4);
        String month = YEARMONTH.substring(4, 6);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH, Integer.parseInt(month));
        str = ym.format(cal.getTime());
        return str;
    }

    //获取上个月的YEARMONTH
    public String getLastYearMonth(String YEARMONTH) {
        String str = "";
        SimpleDateFormat ym = new SimpleDateFormat("yyyyMM");
        String year = YEARMONTH.substring(0, 4);
        String month = YEARMONTH.substring(4, 6);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH, (Integer.parseInt(month) - 2));
        str = ym.format(cal.getTime());
        return str;
    }

    //根据YEARMONTH 计算当月最后一天
    public String getLastDayOfMonth(String YEARMONTH) {
        String str = "";
        SimpleDateFormat ym = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String year = YEARMONTH.substring(0, 4);
        String month = YEARMONTH.substring(4, 6);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH,(Integer.parseInt(month)-1));
        str = ym.format(cal.getTime());
        cal.set(Calendar.DATE, 1); //设为当前月的1号
        cal.add(Calendar.MONTH, 1); //加一个月，变为下月的1号
        cal.add(Calendar.DATE, -1); //减去一天，变为当月最后一天

        str = sdf.format(cal.getTime());
        return str;
    }



    /**
     * 跟据日期字符串获取该日期为当月第几周
     *
     * @param date String
     * @return int
     */
    public int getWeekOfMonth(String date) {
        String str = date;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("CTT"));
        try {
            calendar.setTime(formatter.parse(str));
        } catch (ParseException ex) {
        }
        int WeekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
        return WeekOfMonth;

    }

    /**
     * 跟据TimeStamp日期获取该日期为当月第几周
     *
     * @param date Timestamp
     * @return int
     */
    public int getWeekOfMonth(Timestamp date) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("CTT"));
        try {
         String str =  formatter.format(date);
            calendar.setTime(formatter.parse(str));
        } catch (ParseException ex) {
          ex.printStackTrace();
        }
        int WeekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
        return WeekOfMonth;
    }

    /**
     * 根据月 周 星期 信息得到日期
     *
     * @author zhang_lei
     * @parm YEARMONTH String 年月格式为yyyyMM(可根据需求自己调整)
     * @parm week_of_month int 第几周
     * @parm week_day int 星期几 2009-02-23
     * @param YEARMONTH String
     * @param week_of_month int
     * @param week_day int
     * @return String
     */
    public String getDayByWeekInfo(String YEARMONTH, int week_of_month,
                                   int week_day) {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String year = YEARMONTH.substring(0, 4);
        String month = YEARMONTH.substring(4, 6);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH, (Integer.parseInt(month) - 1));

        cal.set(Calendar.WEEK_OF_MONTH, week_of_month);
        switch (week_day) {
        case 0:
            cal.set(Calendar.DAY_OF_WEEK, 1);
            break;
        case 1:
            cal.set(Calendar.DAY_OF_WEEK, (week_day + 1));
            break;
        case 2:
            cal.set(Calendar.DAY_OF_WEEK, (week_day + 1));
            break;
        case 3:
            cal.set(Calendar.DAY_OF_WEEK, (week_day + 1));
            break;
        case 4:
            cal.set(Calendar.DAY_OF_WEEK, (week_day + 1));
            break;
        case 5:
            cal.set(Calendar.DAY_OF_WEEK, (week_day + 1));
            break;
        case 6:
            cal.set(Calendar.DAY_OF_WEEK, (week_day + 1));
            break;

        }

        str = sdf.format(cal.getTime());
        return str;
    }

    /**
     * 将String类型转换为Timestamp类型
     *
     * @author zhang_lei
     * @parm date String
     * @return Timestamp
     * @param date String
     */
    public Timestamp parseTimpStampByString(String date) {
        Timestamp s_date = null;
        try {
            SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
            Date dt = ymd.parse(date);
            s_date = new Timestamp(dt.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s_date;
    }

    /**
     * 跟据日期字符串获取该日期为星期几 0-6
     *
     * @param date String
     * @return int
     */
    public int getDayOfWeek(String date) {
       String str = date;
       SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
       Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("CTT"));
       try {
           calendar.setTime(formatter.parse(str));
       } catch (ParseException ex) {
         ex.printStackTrace();
       }
       int WeekOfMonth = calendar.get(Calendar.DAY_OF_WEEK)-1;
       return WeekOfMonth;
   }
   /**
     * 跟据日期字符串获取该日期为星期几 0-6
     *
     * @param date Timestamp
     * @return int
     */
   public int getDayOfWeek(Timestamp date) {

      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
      Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("CTT"));
      try {
        calendar.setTime(formatter.parse(formatter.format(date)));
      } catch (ParseException ex) {
        ex.printStackTrace();
      }
      int WeekOfMonth = calendar.get(Calendar.DAY_OF_WEEK)-1;
      return WeekOfMonth;
  }

    /**
     * 获取本月的YearMonth
     * getCurrentYearMonth
     *
     * @return String
     */
    public String getCurrentYearMonth() {
          String YEARMONTH = "";
          try {
              SimpleDateFormat ymd = new SimpleDateFormat(
                      "yyyyMM");
              Timestamp s_date = new Timestamp(System.currentTimeMillis());
              YEARMONTH = ymd.format(s_date);
          } catch (Exception e) {

              //e.printStackTrace();
          }
          return YEARMONTH;
    }

    /**
     * 计算某年某月第几个星期几为哪天
     *
     * @param YEARMONTH String
     * @param num int
     * @param week_day int
     * @throws ParseException
     * @return String
     */
    public String getDayByWeekDay(String YEARMONTH, int num,
                                    int week_day) {

         String str = "";

         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
         String year = YEARMONTH.substring(0, 4);
         String month = YEARMONTH.substring(4, 6);
         Calendar cal = Calendar.getInstance();
         cal.set(Calendar.YEAR, Integer.parseInt(year));
         cal.set(Calendar.MONTH, (Integer.parseInt(month) - 1));
         cal.set(Calendar.WEEK_OF_MONTH,num);
         cal.set(Calendar.DAY_OF_WEEK,(week_day+1));

         str = sdf.format(cal.getTime());
         String y = str.substring(0,4);
         String m = str.substring(5,7);
         String ym = y+m;
         if(!ym.equals(YEARMONTH)){
         cal.add(Calendar.DATE,7);
         str = sdf.format(cal.getTime());
         }

         return str;

    }
        //抓取昨天的日期
        public String getYesterday() {
            String str = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
             cal.add(Calendar.DATE, -1); //减去一天，变为当月最后一天
            str = sdf.format(cal.getTime());

            return str;
    }
}
