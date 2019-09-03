package fun.linyuhong.myCommunity;


import fun.linyuhong.myCommunity.util.XORUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@SpringBootTest
public class XORUtilTests {


    @Test
    public void test(){
        byte[] b = new byte[]{1, 2};
        System.out.println(XORUtil.encryptId(12, b));
        System.out.println(XORUtil.encryptId(13, b));
        System.out.println(XORUtil.encryptId(14, b));
        System.out.println(XORUtil.encryptId(10, b));
        System.out.println(XORUtil.encryptId(100, b));
        System.out.println(XORUtil.encryptId(1000, b));
        System.out.println(XORUtil.encryptId(10000, b));
        System.out.println(XORUtil.encryptId(100000, b));
        System.out.println(XORUtil.encryptId(1000000, b));
    }

}
