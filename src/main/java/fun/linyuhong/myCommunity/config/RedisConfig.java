package fun.linyuhong.myCommunity.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    // RedisConnectionFactory 连接工厂，Spring会自动注入这个Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 设置key的序列化方式
        template.setKeySerializer(redisSerializer); // 序列化为String
        // 设置value的序列化方式
        template.setValueSerializer(jackson2JsonRedisSerializer);
//        // 设置hash的key的序列化方式   当value为hash时，hash有key和value，所以需要设置
//        template.setHashKeySerializer(RedisSerializer.string());
//        // 设置hash的value的序列化方式
//        template.setHashValueSerializer(RedisSerializer.json());
        //value hashmap序列化
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();  // 使上面设置生效
        return template;
    }

}
