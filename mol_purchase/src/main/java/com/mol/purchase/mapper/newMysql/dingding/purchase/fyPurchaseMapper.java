package com.mol.purchase.mapper.newMysql.dingding.purchase;

import com.mol.base.BaseMapper;
import com.mol.purchase.entity.dingding.purchase.enquiryPurchaseEntity.StraregyObj;
import com.mol.purchase.entity.dingding.solr.fyPurchase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.mol.purchase.entity.dingding.purchase.enquiryPurchaseEntity.PurchaseDetail;

import java.util.List;

/**
 * fypurchase表
 */
@Mapper
public interface fyPurchaseMapper extends BaseMapper<fyPurchase> {

    void insertStrategyPur(StraregyObj obj);

    List<fyPurchase> findListByStatus(String status_f, String status_s, String status_t);

    int findCountByStatus(String status_f, String status_s, String status_t);

    String  getQuoteCountsByPurchaseId(String id);

    void addQuoteCountsByPurchaseId(String id, String counts);

    List<fyPurchase> findListByIdAndGoodsType(String buyId, String goodsType);

    int findCountByIdAndStatus(@Param("buyId") String buyId, @Param("status") String status, @Param("list") List<PurchaseDetail> list);

    List<fyPurchase> findListByIdAndStatus(String buyId, String status);

    List<fyPurchase> selectOrderByDeadLine(String nowDateTime);

    void updateStatusById(String id, String status);

    List<fyPurchase> findListByPurchaseDetailList(List<PurchaseDetail> purDetailList);

    List<fyPurchase> findList(@Param("buyId") String buyId, @Param("status") String status, @Param("list") List<PurchaseDetail> list);

    List<fyPurchase> findListByIdlistAndStatus(@Param("status_f") String status, @Param("status_s") String status_s, @Param("status_t") String status_t, @Param("list") List<String> quoteIdList);

    List<fyPurchase> findListByOrgIdAndStatus(String orgId, String status, String secondStatus);

    fyPurchase findOneById(String id);

    int updateAppUserListById(String id, String arr);

    String findAppUserListById(String id);

    void updateExplainById(String purId, String explain);

    void updateStatusAndApproverProposalById(String purId, String passOrNot, String approverProposal);

    List<fyPurchase> findListByOrgIdAndStaffId(String orgId, String userId);

    List<fyPurchase> findPurchaseByIdList(List<String> arr);

    List<fyPurchase> findPurListByLikeCreateTime(String nowOnlyDate,String buyChannalId);

    void updataApprovalStartTime(String purId,String time);

    int updataApprovalEndTime(String purId, String time);

    List<fyPurchase> findListByOrgIdAndBuychannelAndStatus(@Param("orgId") String orgId,@Param("status") String status,@Param("statusSecond")String statusSecond, @Param("list") List<String> buyChannelList);

    List<fyPurchase> findFyPurchaseByNegotiatePersonNotNullAndOrgId(String orgId);

    List<fyPurchase> findListByOrgIdAndUserIdAndStatus(String orgId, String userId, String status, String status_second);

    Integer findCountByStatusAndChnnelList(@Param("statusPass") String statusPass,@Param("statusRefuse") String statusRefuse,@Param("list") List<String> channelIdList);

    List<fyPurchase> findListByStatusAndChnnelList(@Param("statusPass") String statusPass,@Param("statusRefuse") String statusRefuse,@Param("list") List<String> channelIdList);
}
