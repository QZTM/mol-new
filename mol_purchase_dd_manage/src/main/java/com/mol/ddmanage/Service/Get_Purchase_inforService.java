package com.mol.ddmanage.Service;

import com.mol.ddmanage.Ben.Purchase_track_ben;
import com.mol.ddmanage.Ben.Supplier_Review_ben;
import com.mol.ddmanage.Ben.Workench.GetNeedQuoteAlertben;
import com.mol.ddmanage.Ben.Workench.GetProductTypeben;
import com.mol.ddmanage.Ben.Workench.GethistoryQuotesben;
import com.mol.ddmanage.Util.DataUtil;
import com.mol.ddmanage.mapper.GetPurchaseMapper;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Log
public class Get_Purchase_inforService
{
    @Resource
    GetPurchaseMapper get_purchase;
    public Map Purchase_bar_infor_service(String number)
    {
        Map map =new HashMap();
        ArrayList<String> bar=new ArrayList<>();

        String history_time="1999-00-00 00:00:00";
        if (number.equals("0"))
        {
            history_time= DataUtil.getHistoryTime(7);
        }
        else if (number.equals("1"))
        {
            history_time= DataUtil.getHistoryTime(30);
        }
        else if (number.equals("2"))
        {
            history_time= DataUtil.getHistoryTime(365);
        }

        for (int n=1;n<4;n++)
        {
            if (n<3)
            {
                bar.add(get_purchase.Get_Purchase_bar(String.valueOf(n),history_time));
            }
            else
            {
                bar.add(String.valueOf(Integer.parseInt(get_purchase.Get_Purchase_bar(String.valueOf(n),history_time))+Integer.parseInt(get_purchase.Get_Purchase_bar(String.valueOf(n+1),history_time))));

                bar.add(get_purchase.Get_Supplier("1999-00-00 00:00:00"));//所有的供应商
                bar.add(get_purchase.Get_Supplier(history_time));//一定日期的供应商
            }
        }
        map.put("bar",bar);

        return map;
    }

    public ArrayList<Purchase_track_ben> Purchase_track_Service()
    {
        ArrayList<Purchase_track_ben> bens=get_purchase.Purchase_track_mapper("1");//进行中的订单
        for (int n=0;n<bens.size();n++)
        {
           if (bens.get(n).getBuy_channel_id().equals("3"))
           {
               bens.get(n).setBuy_channel_id("战略采购");
           }
           else if (bens.get(n).getBuy_channel_id().equals("4"))
           {
               bens.get(n).setBuy_channel_id("询价采购");
           }
           else if (bens.get(n).getBuy_channel_id().equals("5"))
           {
               bens.get(n).setBuy_channel_id("单一采购");
           }
           else if (bens.get(n).getBuy_channel_id().equals("6"))
           {
               bens.get(n).setBuy_channel_id("加工,维修");
           }

           if (get_purchase.Get_Purchase_staff(bens.get(n).getStaff_id())!=null )//数据表中必须有对应的人员
           {
               bens.get(n).setStaff_id(get_purchase.Get_Purchase_staff(bens.get(n).getStaff_id()).getUser_name());
           }
           else
           {
               bens.get(n).setStaff_id("没有找到");
           }

        }

       return bens;
    }
    public ArrayList<Supplier_Review_ben> Get_Supplier_Review_Service()
    {
        ArrayList<Supplier_Review_ben> review_bens=new ArrayList<>();
        review_bens = get_purchase.Get_Supplier_Review_mapper("2");
        for (int i=0;i<review_bens.size();i++)
        {
           if (review_bens.get(i).getSupplier_attr().equals("0"))
           {
               review_bens.get(i).setSupplier_attr("暂未设置");
           }
            else if (review_bens.get(i).getSupplier_attr().equals("1"))
            {
                review_bens.get(i).setSupplier_attr("基础供应商");
            }
           else if (review_bens.get(i).getSupplier_attr().equals("2"))
           {
               review_bens.get(i).setSupplier_attr("战略采购供应商");
           }
           else if (review_bens.get(i).getSupplier_attr().equals("3"))
           {
               review_bens.get(i).setSupplier_attr("单一供应商");
           }
        }
        return review_bens;
    }

    public ArrayList<Integer> Get_this_month_Supplier_number()
    {
        ArrayList<Integer> integers=new ArrayList<>();
        String time_start= (DataUtil.GetNowSytemTime().split(" "))[0].substring(0,8)+"01 00:00:00";
        String time_end= (DataUtil.GetNowSytemTime().split(" "))[0].substring(0,8)+"01 00:00:00";
      get_purchase.Get_this_month_Supplier_number_mapper("","");

      return integers;
    }


    public Map GetNewSupplierViewLogic(String times)
    {
        if (times.equals(""))
        {
            times=DataUtil.GetNowSytemTime().split("-")[0]+DataUtil.GetNowSytemTime().split("-")[1];
        }
        Map data=new HashMap();
        try
        {
            ArrayList<Integer> newsuppliernum=new ArrayList<Integer>();//存储一个月中每天的供应商新增数
            ArrayList<Integer> numberDay= new ArrayList<>();//这个月的天数标记
            times=times.replace("-","");
            Map GetMonth= DataUtil.GetMonth(times);//获取本月的第一个月和最后一个月的日期

            Integer Monthnumber= Integer.valueOf ((GetMonth.get("time2").toString()).split("-")[2]);//获取这个月一共有多少天

            ArrayList<Map> Suppliers= get_purchase.GetNewSupplierView(GetMonth.get("time1").toString(),GetMonth.get("time2").toString());

            for (int n=1;n<=Monthnumber;n++)
            {
                Integer n_1=0;
                for (Map item : Suppliers)
                {
                    String ss=((item.get("regist_time").toString()).split("-")[2]).substring(0,2);
                    if (n==Integer.valueOf(ss))
                    {
                        n_1++;
                    }
                }
                newsuppliernum.add(n_1);
                numberDay.add(n);
            }
            data.put("newsuppliernum",newsuppliernum);
            data.put("numberDay",numberDay);
            return data;

        }
        catch (Exception e)
        {
           System.out.println(e.toString());
            return data;
        }
    }


    public Map GetProductTypeLogic(String times)
    {
        Map map=new HashMap();
        try
        {
           // times="2019-12";
            if (times.equals(""))
            {
                times=DataUtil.GetNowSytemTime().split("-")[0]+DataUtil.GetNowSytemTime().split("-")[1];
            }
            times=times.replace("-","");//去掉日期里的-
            Map GetMonth= DataUtil.GetMonth(times);//获取本月的第一个月和最后一个月的日期

            ArrayList<String> MaterialsType=get_purchase.GetMaterialsType();//获取产品类
            ArrayList<Object> Prices=new ArrayList<>();//存放各个产品类的采购金额
            ArrayList<GetProductTypeben> getProductTypebens=get_purchase.GetProductType(GetMonth.get("time1").toString(),GetMonth.get("time2").toString());//获取这个时间范围内的采购金额


            for (int n=0;n<MaterialsType.size();n++)//遍历产品类
            {
                double Price=0;
                for (int n_1=0;n_1<getProductTypebens.size();n_1++)//遍历采购金额
                {
                    if (getProductTypebens.get(n_1).getGoods_type().equals(MaterialsType.get(n)))
                    {
                        Price=Price+Double.valueOf(getProductTypebens.get(n_1).getQuote());
                    }
                }
                Prices.add(Price);
            }
            MaterialsType.add("产品采购金额");
            Prices.add("产品采购金额");

            Collections.reverse(MaterialsType);
            Collections.reverse(Prices);

            map.put("MaterialsType",MaterialsType);
            map.put("Prices",Prices);

            return map;
        }
        catch (Exception e)
        {

            return map;
        }
    }

   public ArrayList<GetNeedQuoteAlertben> GetQuoteAlertLogic(String time_day)
    {


        ArrayList<GetNeedQuoteAlertben> getNeedQuoteAlertbens=new ArrayList<>();
        try
        {
            if (time_day.equals(""))
            {
                time_day=DataUtil.GetNowSytemTime().substring(0,11);
            }
             getNeedQuoteAlertbens=get_purchase.GetNeedQuoteAlert(time_day);//获取一定时间内出现的报价异常物料
             for (int n=0;n<getNeedQuoteAlertbens.size();n++)
             {
                 Double Quote_difference=Double.valueOf(getNeedQuoteAlertbens.get(n).getQuote_difference());//获取报价差
                 Double NeedQuote=Double.valueOf(getNeedQuoteAlertbens.get(n).getLast_quote());//获取上一次报价
                 String f=String.valueOf((Quote_difference/NeedQuote)*10).substring(0,4);//获取前4位
                 if (f.substring(f.length()-1,f.length()).equals("."))//判断最后一位是否有小数点
                 {
                     f=f.replace(".","");
                    getNeedQuoteAlertbens.get(n).setFloating_ratio(f+"%");
                 }
                 else
                 {
                     getNeedQuoteAlertbens.get(n).setFloating_ratio(f);
                 }

                 ArrayList<GethistoryQuotesben> gethistoryQuotesbens=get_purchase.GethistoryQuotes(getNeedQuoteAlertbens.get(n).getPkMaterialId(),DataUtil.getHistoryTime(180));//获取这个物料最近半年的已成交价格
                  ArrayList<String> times=new ArrayList<>();//X轴时间字符传
                  ArrayList<Double> quotes=new ArrayList<>();//物料的历史价格
                 for (int n_1=0;n_1<gethistoryQuotesbens.size();n_1++)
                 {
                     times.add(gethistoryQuotesbens.get(n_1).getTimes());
                     quotes.add(Double.valueOf(gethistoryQuotesbens.get(n_1).getQuotes()));
                 }
                 getNeedQuoteAlertbens.get(n).setQuotes(quotes);
                 getNeedQuoteAlertbens.get(n).setTimes(times);
             }

            return getNeedQuoteAlertbens;
        }
        catch (Exception e)
        {
           return getNeedQuoteAlertbens;
        }
    }


}
