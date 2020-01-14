package com.mol.deptpurchase.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 初始化应用配置类
 */
@Component
public class AppParam {

    @Value(value = "${supplierappkey}")
    @Getter
    @Setter
    private String supplierAppSecret;

    @Value(value = "${supplierappsecket}")
    @Getter
    @Setter
    private String supplierAppKey;

}
