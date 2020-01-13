package com.mol.ddmanage.Dingding;

import com.mol.ddmanage.config.Basic_config;
import com.mol.ddmanage.config.Dingding_config;
import com.mol.ddmanage.mapper.StartInspectionMapper;
import lombok.extern.java.Log;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;

@Component
@Order(value = 1)
@Log
public class Onload  implements ApplicationRunner
{
    @Resource
    StartInspectionMapper startInspectionMapper;
    @Override
    public void run(ApplicationArguments args)
    {
        try {
            ArrayList<Map> app_auth_org=startInspectionMapper.Get_app_auth_org(Dingding_config.CorpId);
            Basic_config.open_id=app_auth_org.get(0).get("id").toString();
            log.info("获取公司id成功");
        }
        catch (Exception e)
        {
            log.info("获取公司id失败 原因"+e.toString());
        }
    }
}
