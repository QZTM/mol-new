package com.mol.purchase.entity.activiti;

import lombok.Data;

import java.util.Date;

/**
 * 历史变量表( act_hi_varinst )
 */
@Data
public class ActHiVarinst {
    private String id;
    private String procInstId;
    private String executionId;
    private String taskId;
    private String name;
    private String varType;
    private int rev;
    private String bytearrayId;
    private Double double_;
    private Long long_;
    private String text;
    private String text2;
    private Date createTime;
    private Date lastUpdatedTime;
}
