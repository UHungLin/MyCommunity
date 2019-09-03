package fun.linyuhong.myCommunity.util;

/**
 * @author linyuhong
 * @date 2019/9/1
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_KAPTCHA = "kaptcha";

    /**
     * 生成登录凭证
     * @param ticket  UUID
     * @return  ticket:UUID
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // 登录验证码
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }
}




