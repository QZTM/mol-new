package com.mol.ddmanage.Service.DepartmentManagement;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mol.ddmanage.Ben.DepartmentManagement.AddJurisdictionben;
import com.mol.ddmanage.Util.DataUtil;
import com.mol.ddmanage.Util.Dingding_Tools;
import com.mol.ddmanage.mapper.DepartmentMangement.AddJurisdictionMapper;
import org.springframework.stereotype.Service;
import util.IdWorker;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

@Service
public class AddJurisdictionService
{
    @Resource
    AddJurisdictionMapper addJurisdictionMapper;
    public boolean AddJurisdictionLogic(AddJurisdictionben addJurisdictionben,HttpSession httpSession)
    {
        try
        {
            String userid= httpSession.getAttribute("userid").toString();//Token_hand.Review_My_Token(httpSession.getAttribute("token").toString());//获取token里的userid
            addJurisdictionben.setJurisdictionId(String.valueOf(new IdWorker().nextId()) );//id
            addJurisdictionben.setDataViewingPermissionsid(String.valueOf(new IdWorker().nextId()));//角色查看数据的表id
            addJurisdictionben.setCreatTime(DataUtil.GetNowSytemTime());//获取系统时间
            addJurisdictionben.setCreadStaff(userid);//钉钉人员id
            addJurisdictionMapper.AddJurisdictionMapper(addJurisdictionben);

            AddJurisdictionben addJurisdictionben1=new AddJurisdictionben();

            Field []fields=addJurisdictionben1.getClass().getDeclaredFields();
            for (int i=0;i<fields.length;i++)
            {
                // 获取属性的名字
                String name = fields[i].getName();
                // 将属性的首字符大写，方便构造get，set方法
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                // 获取属性的类型
                String type = fields[i].getGenericType().toString();
                // 如果type是类类型，则前面包含"class "，后面跟类名
                if (type.equals("class java.lang.String")) {
                    Method m = addJurisdictionben1.getClass().getMethod("get" + name);
                    // 调用getter方法获取属性值
                    String value = (String) m.invoke(addJurisdictionben1);
                    System.out.println("数据类型为：String");
                    if (value == null || value.equals("")) {
                        //set值
                        Class[] parameterTypes = new Class[1];
                        parameterTypes[0] = fields[i].getType();
                        m = addJurisdictionben1.getClass().getMethod("set" + name, parameterTypes);
                        String string = new String("0");
                        Object[] objects = new Object[1];
                        objects[0] = string;
                        m.invoke(addJurisdictionben1, objects);
                    }
                }
            }
            addJurisdictionben1.setCreatTime(DataUtil.GetNowSytemTime());//获取系统时间
            addJurisdictionben1.setCreadStaff(userid);//钉钉人员id
            addJurisdictionben1.setStatus("1");
            addJurisdictionben1.setDataViewingPermissionsid(addJurisdictionben.getDataViewingPermissionsid());//角色查看数据表的id
            addJurisdictionMapper.AddDataviewingpermissions(addJurisdictionben1);//在角色查看数据表中插入一条数据
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public String ViewRoles()//查看角色是否有超级管理员
    {
        try {
            Map addJurisdiction=addJurisdictionMapper.select_Super_administrator("超级管理员");
            if (addJurisdiction==null)//如果没有超级管理员，需要创建超级管理员，并且老板为超级管理员的用户
            {
                String dingding_userid="";
               String Getadmin= Dingding_Tools.Get_admin();
                JSONObject jsonObjectGetadmin=JSONObject.parseObject(Getadmin);
                String adminlist=jsonObjectGetadmin.getString("adminList");
                JSONArray jsonArray=JSONObject.parseArray(adminlist);
                for (int n=0 ;n<jsonArray.size();n++)
                {
                    String item= jsonArray.get(n).toString();
                    JSONObject items=JSONObject.parseObject(item);
                    if (items.getString("sys_level").equals("1"))
                    {
                        dingding_userid=items.getString("userid");
                        break;
                    }
                }
               String jurisdictionId=String.valueOf(new IdWorker().nextId());//权限表id
                String DataViewingPermissionsid=String.valueOf(new IdWorker().nextId());//角色表查看数据权限表id
               addJurisdictionMapper.Insert_admin(jurisdictionId,DataViewingPermissionsid,"超级管理员",DataUtil.GetNowSytemTime(),dingding_userid);//插入一条权限
                addJurisdictionMapper.insert_bac_user_position(String.valueOf(new IdWorker().nextId()),dingding_userid,jurisdictionId);



                AddJurisdictionben addJurisdictionben1=new AddJurisdictionben();
                Field []fields=addJurisdictionben1.getClass().getDeclaredFields();
                for (int i=0;i<fields.length;i++)
                {
                    // 获取属性的名字
                    String name = fields[i].getName();
                    // 将属性的首字符大写，方便构造get，set方法
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    // 获取属性的类型
                    String type = fields[i].getGenericType().toString();
                    // 如果type是类类型，则前面包含"class "，后面跟类名
                    if (type.equals("class java.lang.String")) {
                        Method m = addJurisdictionben1.getClass().getMethod("get" + name);
                        // 调用getter方法获取属性值
                        String value = (String) m.invoke(addJurisdictionben1);
                        //System.out.println("数据类型为：String");
                        if (value == null || value.equals("")) {
                            //set值
                            Class[] parameterTypes = new Class[1];
                            parameterTypes[0] = fields[i].getType();
                            m = addJurisdictionben1.getClass().getMethod("set" + name, parameterTypes);
                            String string = new String("0");
                            Object[] objects = new Object[1];
                            objects[0] = string;
                            m.invoke(addJurisdictionben1, objects);
                        }
                    }
                }
                addJurisdictionben1.setCreatTime(DataUtil.GetNowSytemTime());//获取系统时间
                addJurisdictionben1.setCreadStaff(dingding_userid);//钉钉人员id
                addJurisdictionben1.setStatus("1");
                addJurisdictionben1.setDataViewingPermissionsid(DataViewingPermissionsid);//角色查看数据表的id
                addJurisdictionMapper.AddDataviewingpermissions(addJurisdictionben1);//在角色查看数据表中插入一条数据


                return "超级管理员权限注册完毕";
            }
           return "";
        }
        catch (Exception e)
        {
          return "超级管理员注册失败"+e.toString();
        }

    }

}
