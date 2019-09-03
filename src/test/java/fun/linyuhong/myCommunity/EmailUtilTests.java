package fun.linyuhong.myCommunity;

import fun.linyuhong.myCommunity.util.EmailUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linyuhong
 * @date 2019/9/2
 */
public class EmailUtilTests {

    @Test
    public void test() {
        System.out.println(EmailUtil.isEmail("test@qq.com"));
    }

    public static void main(String[] args) {
        Map map = new HashMap();
        System.out.println(map != null);
    }
}
