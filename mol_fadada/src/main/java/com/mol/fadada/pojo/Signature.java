package com.mol.fadada.pojo;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "fadada_signature")
@Data
public class Signature implements Serializable {

    private String id;
    private String customerId;
    private String signatureId;
    private String createTime;
    private String lastUpdateTime;

}
