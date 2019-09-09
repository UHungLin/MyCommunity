package fun.linyuhong.myCommunity.async;

/**
 * @author linyuhong
 * @date 2019/9/6
 */
public enum  EventType {

    LIKE("like"),
    FOLLOW("follow"),
    COMMENT("comment"),
    /**
     * 发帖
     */
    PUBLISH("publish"),
    /**
     * 注册
     */
    REGISTER("register"),
    /**
     * 删帖
     */
    DELETE("delete"),

    /**
     * 表示系统通知，包括注册、登录 等等等
     */
    SYSTEM("system");

//    LIKE(0, "like"),
//    FOLLOW(1, "follow"),
//    COMMENT(2, "comment"),
//    /**
//     * 发帖
//     */
//    PUBLISH(3, "publish");

    private String value;
//    private String type;

    EventType(String value) {
        this.value = value;
    }

//    EventType(int value, String type) {
//        this.value = value;
//        this.type = type;
//    }

    public String getValue(){
        return this.value;
    }

//    public String getType() {
//        return this.type;
//    }
}
