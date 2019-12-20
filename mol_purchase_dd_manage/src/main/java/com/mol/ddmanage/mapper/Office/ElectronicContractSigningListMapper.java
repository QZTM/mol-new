package com.mol.ddmanage.mapper.Office;

import com.mol.ddmanage.Ben.Office.ElectronicContractSigningListben;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

public interface ElectronicContractSigningListMapper
{
    ArrayList<ElectronicContractSigningListben> GetElectronicContractSigningList(@Param(value = "AuthorityStatus") String AuthorityStatus, @Param(value = "userid") String userid,@Param(value = "electronic_contract") String electronic_contract);
}
