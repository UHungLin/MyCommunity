package fun.linyuhong.myCommunity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author linyuhong
 * @date 2019/9/6
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test() {
        redisTemplate.opsForList().leftPush("queueTestKey", 1);
        redisTemplate.opsForList().leftPush("queueTestKey", 2);
        redisTemplate.opsForList().leftPush("queueTestKey", 3);
        redisTemplate.opsForList().leftPush("queueTestKey", 6);
        redisTemplate.opsForList().leftPush("queueTestKey", 7);
        redisTemplate.opsForList().leftPush("queueTestKey", 8);

        while (true) {

            System.out.println(redisTemplate.opsForList().rightPop("queueTestKey", 0,  TimeUnit.MINUTES));
        }

//        System.out.println(redisTemplate.opsForList().rightPop("queueTestKey", 0, TimeUnit.SECONDS));
//        System.out.println(redisTemplate.opsForList().range("queueTestKey", 0, -1));
//        System.out.println(redisTemplate.opsForList().range("queueTestKey", 0, -1));
//        while (true) {
////            System.out.println(redisTemplate.opsForList().rightPop("queueTestKey", 0, TimeUnit.SECONDS));
////            System.out.println(redisTemplate.opsForList().rightPop("queueTestKey"));
////            System.out.println(message);
////            System.out.println(redisTemplate.opsForList().range("queueTestKey", 0, 2));
////            redisTemplate.opsForList().trim("queueTestKey", 3, -1);
////            System.out.println(redisTemplate.opsForList().range("queueTestKey", 0, -1));
//
//            Integer message = (Integer) redisTemplate.opsForList().rightPop("queueTestKey", 10L, TimeUnit.HOURS);
//            System.out.println(message);
//        }
    }



}



