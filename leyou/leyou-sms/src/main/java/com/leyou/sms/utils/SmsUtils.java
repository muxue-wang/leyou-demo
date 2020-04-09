package com.leyou.sms.utils;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.leyou.sms.config.SmsProperties;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsUtils {

    @Autowired
    private SmsProperties smsProperties;

    public SmsSingleSenderResult sendSms(String phone,String param,int template,String signName){
        try {
            SmsSingleSender sender = new SmsSingleSender(smsProperties.getAccessKeyId(),smsProperties.getAccessKeySecret());
            String[] params = {param};
            SmsSingleSenderResult result = sender.sendWithParam("86",phone,template,params,signName,"","");
            System.out.println(result);
            return result;
        }catch (JSONException | IOException | HTTPException e){
            e.printStackTrace();
        }
        return null;
    }
}
