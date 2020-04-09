package com.leyou.user.test;

import com.leyou.LeyouUserApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = LeyouUserApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate template;

    @Test
    public void test(){
        template.opsForValue().set("code","测试66",60, TimeUnit.SECONDS);
        final String code = template.opsForValue().get("code");
        System.out.println(code);

    }

    @Test
    public void TestHash(){
        BoundHashOperations<String,Object,Object> hashOps = template.boundHashOps("user");
        //操作hash数据
        hashOps.put("name","jack");
        hashOps.put("age","21");

        //获取单个数据
        Object name = hashOps.get("name");
        System.out.println("name = "+name);

        //获取所以数据
        Map<Object,Object> map = hashOps.entries();
        for (Map.Entry<Object, Object> me : map.entrySet()) {
            System.out.println(me.getKey()+" = "+me.getValue());
        }

    }
}
