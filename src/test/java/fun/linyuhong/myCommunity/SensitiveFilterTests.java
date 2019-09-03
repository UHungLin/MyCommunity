package fun.linyuhong.myCommunity;

import fun.linyuhong.myCommunity.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author linyuhong
 * @date 2019/9/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = MyCommunityApplication.class)
public class SensitiveFilterTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        System.out.println(sensitiveFilter.filter("你好赌博"));
    }
}
