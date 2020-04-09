package com.leyou.sms.test;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.leyou.LeyouSmsApplication;
import com.leyou.sms.config.SmsProperties;
import org.apache.http.HttpException;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest(classes = LeyouSmsApplication.class)
@RunWith(SpringRunner.class)
@EnableConfigurationProperties(SmsProperties.class)
public class SmsTest {

    @Autowired
    private SmsProperties smsProperties;

    @Test
    public void send(){
        try {
            SmsSingleSender sender = new SmsSingleSender(smsProperties.getAccessKeyId(),smsProperties.getAccessKeySecret());
            String[] param = {"999999"};
            SmsSingleSenderResult result = sender.sendWithParam("86","15635502811",smsProperties.getTemplate(),param,smsProperties.getSignName(),"","");
            System.out.println(result);
        }catch (JSONException | IOException | HTTPException e){
            e.printStackTrace();
        }
    }
}
