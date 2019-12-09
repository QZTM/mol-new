package com.mol.ddmanage.Controller.Tools;

import com.mol.ddmanage.Service.Tools.ApprovalProcessinforService;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

@RestController
@RequestMapping("/ApprovalProcessinforContraller")
public class ApprovalProcessinforContraller
{
    @Resource
    ApprovalProcessinforService approvalProcessinforService;

    /**
     *  获取审批信息
     * @param id 审批中间表id
     * @return
     */
    @RequestMapping("/GetApprovalInfor")
    public Map GetApprovalInfor(@RequestParam String id)//获取单个审批信息
    {
        return approvalProcessinforService.GetApprovalInforLogic(id);
    }

    @Test
    public void test2()
    {
       try {
           URL url = new URL("http://140.249.22.202:8082/ac/deploy?name=111&processId=123453333&processName=12322&orgId=1204069409234190336");//实例化一个URL对象，用百度有道翻译进行了测试/hello
          // URL url = new URL("http://140.249.22.202:8082/ac/hello");
           URLConnection connection = url.openConnection();//通过URL对象的openConnection()方法得到一个java.net.URLConnection;
           InputStream is = connection.getInputStream();  // 获取输入流
           InputStreamReader isr = new InputStreamReader(is,"utf-8");//在InputStreamReader中指定编码，手动指定为UTF-8
           BufferedReader br = new BufferedReader(isr);//实例化一个BufferedReader对象用来存放转换后的字符
           String line;
           StringBuilder builder = new StringBuilder();
           while ((line = br.readLine()) != null) {  // 读取数据
               builder.append(line+"\n");
           }
           br.close();//关闭流
           isr.close();
           is.close();
           System.out.println(builder.toString());
       }
       catch (Exception e)
       {
            String p="";
       }
    }

    /**
     * 插入审配人
     * @param id 审批中间表 id
     * @param amountMin 金额区间小
     * @param amountMax 金额区间大
     * @param status 启用状态
     * @param approval_ids 审批人列表
     * @return
     */
    @RequestMapping("/SubmitApprovalData")
    public Map SubmitApprovalData(@RequestParam String id,@RequestParam String amountMin,@RequestParam String amountMax, @RequestParam String status, @RequestParam String approval_ids)
    {
        return approvalProcessinforService.SubmitApprovalDataLogic(id,amountMin,amountMax,status,approval_ids);
    }
}
