package fun.linyuhong.myCommunity;

import fun.linyuhong.myCommunity.util.HttpUtil;


/**
 * @author linyuhong
 * @date 2019/9/2
 */
public class HttpUtilTests {

    public static void main(String[] args) {
        System.out.println(HttpUtil.sendGet("https://api.uomg.com/api/rand.avatar"));
    }
}
