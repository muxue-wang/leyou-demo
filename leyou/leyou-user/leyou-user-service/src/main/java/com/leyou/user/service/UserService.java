package com.leyou.user.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String KEY_PREFIX = "user:verify:";

    public Boolean checkUser(String data, Integer type) {
        User user = new User();
        if (type==1){
            user.setUsername(data);
        }else if (type==2){
            user.setPhone(data);
        }else{
            return null;
        }
        return this.userMapper.selectCount(user) == 0;
    }

    public void sendVerifyCode(String phone) {
        if (StringUtils.isBlank(phone)){
            return;
        }
        //生成验证码
        final String code = NumberUtils.generateCode(6);
        //把验证码保存到redis
        redisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
        //发送消息到rabbitMQ
        Map<String,String> msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE","verifycode.sms",msg);
    }

    public void register(User user, String code) {

        //查询redis的验证码
        final String s = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        //1、校验验证码
        if (!StringUtils.equals(code,s)){
            return;
        }
        //2、生成盐
        final String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //3、加盐加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        user.setId(null);
        user.setCreated(new Date());
        //4、新增用户
        userMapper.insert(user);

    }

    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        //判断user是否为空
        if (user==null){
            return null;
        }
        //获取盐，对用户输入的密码加盐加密
        password = CodecUtils.md5Hex(password,user.getSalt());
        //和数据库的密码对比
        if (StringUtils.equals(password,user.getPassword())){
            return user;
        }
        return null;
   }
}
