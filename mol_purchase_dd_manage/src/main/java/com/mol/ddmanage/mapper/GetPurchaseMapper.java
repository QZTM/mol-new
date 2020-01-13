package com.mol.ddmanage.mapper;

import com.mol.ddmanage.Ben.App_user_table;
import com.mol.ddmanage.Ben.Purchase_track_ben;
import com.mol.ddmanage.Ben.Supplier_Review_ben;
import com.mol.ddmanage.Ben.Workench.GetNeedQuoteAlertben;
import com.mol.ddmanage.Ben.Workench.GetProductTypeben;
import com.mol.ddmanage.Ben.Workench.GethistoryQuotesben;

import java.util.ArrayList;
import java.util.Map;

public interface GetPurchaseMapper {
    String Get_Purchase_bar(String status, String history_time);//订单状态数
    String Get_Supplier(String history_time);

    ArrayList<Purchase_track_ben> Purchase_track_mapper(String status);//按订单编号在采购订单表里查基本信息

    App_user_table  Get_Purchase_staff(String dd_user_id);//获取采购人员信息

    ArrayList<Supplier_Review_ben>  Get_Supplier_Review_mapper(String supstate);//传入供应商审核状态，获取所有正在审核的供应商

    ArrayList<Supplier_Review_ben> Get_this_month_Supplier_number_mapper(String time_start, String time_end);

    ArrayList<Map> GetNewSupplierView(String time1,String time2);

    ArrayList<String> GetMaterialsType();
    ArrayList<GetProductTypeben> GetProductType(String time1,String time2);

   // ArrayList<QuoteAlertben> GetQuoteAlert();
    ArrayList<GethistoryQuotesben>GethistoryQuotes(String material_id,String time_range);//获取这个物料的历史报价
    ArrayList<GetNeedQuoteAlertben> GetNeedQuoteAlert(String time_day);//获取最近那些物料报价超过10%


}
