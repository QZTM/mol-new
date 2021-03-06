package com.mol.notification;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.mol.config.Constant;
import com.mol.config.NotificationConfig;
import com.mol.config.URLConstant;
import com.taobao.api.ApiException;
import entity.NotificationModel;
import entity.ServiceResult;
import lombok.extern.java.Log;
import util.TimeUtil;

@Log
public class SendNotificationImp implements SendNotification{


    private static SendNotificationImp sendNotificationImp = new SendNotificationImp();

    private SendNotificationImp(){ }

    public static SendNotificationImp getSendNotification(){
        if (sendNotificationImp==null){
            return new SendNotificationImp();
        }else {
            return sendNotificationImp;
        }
    }
    /**
     * E应用通知
     * @param userIdList 接受通知的id字符串
     * @param userName 当前处理人的名称
     * @param token token
     * @param agentId   应用agentId
     * @return
     */
    @Override
    public ServiceResult sendOaFromE(String userIdList,String userName,String token,long agentId ) {
        //服务器
        //return sendOaFromE(userIdList,userName,token,agentId,"审批",TimeUtil.getNowDateTime()+"有新的采购订单需要您审批，点击查看吧！","http://140.249.22.202:8082/static/upload/imgs/supplier/ask.png","eapp://pages/purchase/purchase");

        //本地
        return sendOaFromE(userIdList,userName,token,agentId,"审批",TimeUtil.getNowDateTime()+"有新的采购订单需要您审批，点击查看吧！",NotificationConfig.审批图片,"eapp://pages/purchase/purchase");
    }



    @Override
    public ServiceResult sendOaFromE(String userIdList,String userName,String token,long agentId,String title,String content ,String imagePath,String messageUrl) {
        log.info("****通过钉钉E应用发送通知****");
        //String user="266752374326324047";
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.MESSAGE_ASYNCSEND);

        OapiMessageCorpconversationAsyncsendV2Request messageRequest = new OapiMessageCorpconversationAsyncsendV2Request();
        messageRequest.setUseridList(userIdList);
        messageRequest.setAgentId(agentId);
        messageRequest.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setOa(new OapiMessageCorpconversationAsyncsendV2Request.OA());

//        msg.getOa().setMessageUrl("eapp://pages/purchase/workbench/tobeapproved/tobeapproved");
        msg.getOa().setMessageUrl(messageUrl);
        msg.getOa().setHead(new OapiMessageCorpconversationAsyncsendV2Request.Head());
        msg.getOa().getHead().setText("云采购");
        msg.getOa().setBody(new OapiMessageCorpconversationAsyncsendV2Request.Body());
        msg.getOa().getBody().setContent(content);
        msg.getOa().getBody().setImage(imagePath);
        msg.getOa().getBody().setTitle(title);
        if (userName!=null){
            msg.getOa().getBody().setAuthor(userName);//上一个审批人员
        }
        msg.setMsgtype("oa");
        messageRequest.setMsg(msg);

//        OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(messageRequest,microTokenService.getToken(MicroTokenService.MICROAPPTOKENKEY));
        OapiMessageCorpconversationAsyncsendV2Response rsp = null;
        try {
            rsp = client.execute(messageRequest,token);
            log.info("通知发送结果："+rsp.getMessage()+",token:"+token);
            return ServiceResult.success("发送成功:"+rsp);
        } catch (ApiException e) {
            e.printStackTrace();
            return ServiceResult.failure("发送失败:"+rsp);
        }

    }




    /**
     * 第三方报价平台发布
     * @param userIdList 发送人钉钉id的string ，如 111,222
     * @param agentId 应用agentId
     * @param token
     * @return
     */
    @Override
    public ServiceResult sendOaFromThird(String purId,String userIdList,Long agentId,String token){
        log.info("发送邀请供应商报价通知 参数purid:"+purId+",ddId:"+userIdList);
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.MESSAGE_ASYNCSEND);

        String messageUrl="http://"+Constant.getInstance().getSupplierDomain()+"/index/findAll?ddId="+userIdList+"&purId="+purId;
        log.info("邀请供应商报价，拼接的messageUrl:"+messageUrl);

        OapiMessageCorpconversationAsyncsendV2Request messageRequest = new OapiMessageCorpconversationAsyncsendV2Request();
        messageRequest.setUseridList(userIdList);
        messageRequest.setAgentId(agentId);
        messageRequest.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setOa(new OapiMessageCorpconversationAsyncsendV2Request.OA());
        msg.getOa().setMessageUrl(messageUrl);
        //msg.getOa().setMessageUrl("http://fyycg2.vaiwan.com/index/findAll");
        msg.getOa().setHead(new OapiMessageCorpconversationAsyncsendV2Request.Head());
        msg.getOa().getHead().setText("摩尔易购");
        msg.getOa().getHead().setBgcolor("FFBBBBBB");
        msg.getOa().setBody(new OapiMessageCorpconversationAsyncsendV2Request.Body());
        msg.getOa().getBody().setContent(TimeUtil.getNowDateTime()+"有新的采购订单来了，快去报价吧！");
        msg.getOa().getBody().setImage(NotificationConfig.报价图片);
        msg.getOa().getBody().setTitle("新订单");
        msg.setMsgtype("oa");
        messageRequest.setMsg(msg);

//        OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(messageRequest,microTokenService.getToken(MicroTokenService.MICROAPPTOKENKEY));
        OapiMessageCorpconversationAsyncsendV2Response rsp = null;
        try {
            rsp = client.execute(messageRequest,token);
            return ServiceResult.success("发送成功:"+rsp);
        } catch (ApiException e) {
            e.printStackTrace();
            return ServiceResult.failure("发送失败:"+rsp);
        }
    }

    /**
     * 专家平台
     * @param userIdList 发送人id的string ，如 111,222
     * @param agentId 应用agentId
     * @param token
     * @return
     */
    @Override
    public ServiceResult sendOaFromExpert(String userIdList,Long agentId,String token){
        DingTalkClient client = new DefaultDingTalkClient(URLConstant.MESSAGE_ASYNCSEND);

        OapiMessageCorpconversationAsyncsendV2Request messageRequest = new OapiMessageCorpconversationAsyncsendV2Request();
        messageRequest.setUseridList(userIdList);
        messageRequest.setAgentId(agentId);
        messageRequest.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setOa(new OapiMessageCorpconversationAsyncsendV2Request.OA());
        msg.getOa().setMessageUrl("http://"+ Constant.getInstance().getExpertDomain() +"/expert/findAll");
        msg.getOa().setHead(new OapiMessageCorpconversationAsyncsendV2Request.Head());
        msg.getOa().getHead().setText("摩尔易购");
        msg.getOa().setBody(new OapiMessageCorpconversationAsyncsendV2Request.Body());
        msg.getOa().getBody().setContent(TimeUtil.getNowDateTime()+"有新的采购订单来了，快去报价吧！");
        msg.getOa().getBody().setImage("http://140.249.22.202:8082/static/upload/imgs/supplier/ask.png");
        msg.getOa().getBody().setTitle("新订单");
        msg.setMsgtype("oa");
        messageRequest.setMsg(msg);

//        OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(messageRequest,microTokenService.getToken(MicroTokenService.MICROAPPTOKENKEY));
        OapiMessageCorpconversationAsyncsendV2Response rsp = null;
        try {
            rsp = client.execute(messageRequest,token);
            return ServiceResult.success("发送成功:"+rsp);
        } catch (ApiException e) {
            e.printStackTrace();
            return ServiceResult.failure("发送失败:"+rsp);
        }
    }

    @Override
    public OapiMessageCorpconversationAsyncsendV2Response sendOANotification(NotificationModel notificationModel) {
        log.info("发送通知方法，参数："+notificationModel);
        String messageUrl=notificationModel.getMessageUrl();
        //判断nm是否带有订单id；  +"/index/findAll?ddId="+userIdList+"&purId="+purId);
        log.info("判断参数是否有订单id："+notificationModel.getPurId());
        if (notificationModel.getPurId()!=null){
            log.info("通知模块，订单id"+notificationModel.getPurId());
            //前往供应商
            if (notificationModel.getMessageToPlatform()==2){
                log.info("发送到供应商端");
                messageUrl=messageUrl+"?"+"ddId="+notificationModel.getUserList()+"&"+"purId="+notificationModel.getPurId();
                log.info("拼接的messageUrl:"+messageUrl);
            }

        }

        DingTalkClient client = new DefaultDingTalkClient(URLConstant.MESSAGE_ASYNCSEND);

        OapiMessageCorpconversationAsyncsendV2Request messageRequest = new OapiMessageCorpconversationAsyncsendV2Request();
        messageRequest.setUseridList(notificationModel.getUserList());
        messageRequest.setAgentId(notificationModel.getAgentId());
        messageRequest.setToAllUser(notificationModel.getToAllUser());

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setOa(new OapiMessageCorpconversationAsyncsendV2Request.OA());
        msg.getOa().setMessageUrl(messageUrl);
        msg.getOa().setHead(new OapiMessageCorpconversationAsyncsendV2Request.Head());
        msg.getOa().getHead().setText(notificationModel.getText());
        msg.getOa().setBody(new OapiMessageCorpconversationAsyncsendV2Request.Body());
        msg.getOa().getBody().setContent(notificationModel.getContent());
        msg.getOa().getBody().setImage(notificationModel.getImage());
        msg.getOa().getBody().setTitle(notificationModel.getTitle());
        msg.setMsgtype(notificationModel.getMsgType());
        messageRequest.setMsg(msg);

        OapiMessageCorpconversationAsyncsendV2Response rsp = null;
        try {
            rsp = client.execute(messageRequest,notificationModel.getToken());
            log.info("发送是否成功："+rsp.isSuccess());
            log.info("errorCode:"+rsp.getErrcode());
            log.info("errorMsg:"+rsp.getErrmsg());
            return rsp;
        } catch (ApiException e) {
            e.printStackTrace();
            return rsp;
        }
    }


}
