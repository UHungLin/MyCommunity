package fun.linyuhong.myCommunity.util;

import java.util.UUID;

/**
 * @author linyuhong
 * @date 2019/9/1
 */
public class GetGenerateUUID {

    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
