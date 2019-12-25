package com.mol.supplier.util;

import com.mol.supplier.config.MicroAttr;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;

@Component
public class PageUrlUtils {



    @Autowired
    private MicroAttr microAttr;

    /**
     * 当前链接使用的协议 + "://" + 服务器地址 + ":" +端口号 + 应用名称，如果应用名称
     * @param request
     * @return
     */
    public String getPageUrl(HttpServletRequest request){
        String queryString = request.getQueryString();
        if(!StringUtils.isEmpty(queryString) || "null".equals(queryString)){
            queryString = "?"+queryString;
        }else{
            queryString = "";
        }
        String domain = request.getScheme()
                //+ "://" + request.getServerName()
                +"://"+ microAttr.getDomain()
                + request.getRequestURI()
                +queryString;
        return domain;
    }

}
