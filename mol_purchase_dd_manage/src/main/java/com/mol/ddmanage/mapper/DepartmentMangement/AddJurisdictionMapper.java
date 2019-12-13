package com.mol.ddmanage.mapper.DepartmentMangement;

import com.mol.ddmanage.Ben.DepartmentManagement.AddJurisdictionben;

import java.util.ArrayList;
import java.util.Map;

public interface AddJurisdictionMapper {

    void AddJurisdictionMapper(AddJurisdictionben addJurisdictionben);
    ArrayList<AddJurisdictionben>test1(ArrayList arrayList);

    Map select_Super_administrator(String jurisdictionName);

    void Insert_admin(String jurisdictionId,String jurisdictionName,String creatTime,String creadStaff);

    void insert_bac_user_position(String id,String ddUserId,String jurisdictionId);

}
