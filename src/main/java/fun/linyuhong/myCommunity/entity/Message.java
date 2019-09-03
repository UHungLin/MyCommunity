package fun.linyuhong.myCommunity.entity;

import java.util.Date;

public class Message {
    private Integer id;

    private Integer fromId;

    private Integer toId;

    private String conversationId;

    private Integer status;

    private Date createTime;

    private String content;

    public Message(Integer id, Integer fromId, Integer toId, String conversationId, Integer status, Date createTime, String content) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.conversationId = conversationId;
        this.status = status;
        this.createTime = createTime;
        this.content = content;
    }

    public Message() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId == null ? null : conversationId.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}