package com.mol.ddmanage.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataUtil
{
    public static String GetNowSytemTime()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

    public static String GetTimestamp()
    {
        return  new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
    public static String getHistoryTime(int day)//获取过去几天的时间
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - day);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = format.format(today);
        return result;
    }

    /**
     * 获取某个月份的第一天和最后一天
     */

    public static Map  GetMonth(String v_month)
    {
       // String v_month = "201902";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        int year = Integer.valueOf(v_month.substring(0, 4));
        int month = Integer.valueOf(v_month.substring(4, 6));

        calendar.set(year, month - 1, 1);

        String firstDayOfMonth = format.format(calendar.getTime());

        Map map=new HashMap();
        map.put("time1",firstDayOfMonth);


        calendar.set(year, month, 1);//这里先设置要获取月份的下月的第一天
        calendar.add(Calendar.DATE, -1);//这里将日期值减去一天，从而获取到要求的月份最后一天

        String lastDayOfMonth = format.format(calendar.getTime());

        map.put("time2",lastDayOfMonth);
        return  map;
    }



}
