package com.mol.purchase.entity.activiti;

import lombok.Data;

@Data
public class ActReProcdef {
    private String id;
    private int rev;
    private String category;
    private String name;
    private String key;
    private int version;
    private String deploymentId;
    private String resourceName;
    private String dgrmResourceName;
    private String description;
    private int hasStartFormKey;
    private int hasGraphicaalNotation;
    private int suspensionState;
    private String tenantId;
    private String engineVersion;
}
