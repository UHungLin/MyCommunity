package fun.linyuhong.myCommunity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyCommunityApplicationTests {

	@Test
	public void contextLoads() {
		String s = "abc";
		System.out.println(s.substring(0));
		System.out.println(s.substring(1));
		System.out.println(s.substring(3));
	}

}
