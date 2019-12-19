package com.mol.notification;

import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import entity.NotificationModel;
import entity.ServiceResult;

public interface SendNotification {
    public ServiceResult sendOaFromE(String userIdList, String userName, String token, long agentId );

    public ServiceResult sendOaFromE(String userIdList, String userName, String token, long agentId,String title,String content,String imagePath,String messageUrl);

    public ServiceResult sendOaFromThird(String purId,String userIdList,Long agentId,String token);

    public ServiceResult sendOaFromExpert(String userIdList,Long agentId,String token);

    public static SendNotification getSendNotification(){
        return  SendNotificationImp.getSendNotification();
    }

    public OapiMessageCorpconversationAsyncsendV2Response sendOANotification(NotificationModel notificationModel);
}
