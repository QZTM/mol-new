package entity;

import lombok.Data;

@Data
public class NotificationModel {

    public NotificationModel() {
        this.msgType = "oa";
        this.bgcolor="dddddd";
    }



    private String msgType;//消息类型
    private String image;//图片地址
    private String Content;//消息体的内容
    private String bgcolor;//消息头部的背景颜色
    private String text;//消息的头部标题
    private String messageUrl;//PC端点击消息时跳转到的地址
    private Boolean toAllUser;//是否发送给全体人员
    private Long agentId;//应用的agentId
    private String userList;//接收者的用户userid列表，最大列表长度：20
    private String title;//消息体的标题
    private String token;
    private String purId;

    //消息发往那个平台，有订单id时必填
    //1==purchase
    //2==supplier
    //3==expert
    private Integer messageToPlatform;//


    public Integer getMessageToPlatform() {
        return messageToPlatform;
    }

    public void setMessageToPlatform(Integer messageToPlatform) {
        if (messageToPlatform!=null){
            if (messageToPlatform==1 || messageToPlatform==2 || messageToPlatform==3){
                this.messageToPlatform=messageToPlatform;
            }else {
                this.messageToPlatform=null;
            }
        }else {
            this.messageToPlatform=null;
        }
    }

    public String getPurId() {
        return purId;
    }

    public void setPurId(String purId) {
        this.purId = purId;
    }

    public String getMsgType() {
        return msgType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getBgcolor() {
        return bgcolor;
    }



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMessageUrl() {
        return messageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }

    public Boolean getToAllUser() {
        return toAllUser;
    }

    public void setToAllUser(Boolean toAllUser) {
        this.toAllUser = toAllUser;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getUserList() {
        return userList;
    }

    public void setUserList(String userList) {
        this.userList = userList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
