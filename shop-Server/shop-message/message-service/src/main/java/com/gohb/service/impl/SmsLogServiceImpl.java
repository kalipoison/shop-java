package com.gohb.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonResponse;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gohb.domain.SmsLog;
import com.gohb.mapper.SmsLogMapper;
import com.gohb.model.AliSmsModel;
import com.gohb.model.WxMsgModel;
import com.gohb.service.SmsLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class SmsLogServiceImpl extends ServiceImpl<SmsLogMapper, SmsLog> implements SmsLogService {

    @Autowired
    private SmsLogMapper smsLogMapper;


    /**
     * 记录数据库
     *
     * @param aliSmsModel
     */
    @Override
    public void saveMsg(AliSmsModel aliSmsModel, CommonResponse commonResponse) {
        String phoneNumbers = aliSmsModel.getPhoneNumbers();
        String templateParam = aliSmsModel.getTemplateParam();
        JSONObject jsonObject = JSON.parseObject(templateParam);
        String code = jsonObject.getString("code");
        SmsLog smsLog = new SmsLog();
        smsLog.setUserPhone(phoneNumbers);
        smsLog.setContent("尊敬的用户，您的注册会员动态密码为：" + code + "，请勿泄漏于他人！");
        smsLog.setMobileCode(code);
        smsLog.setRecDate(new Date());
        String data = commonResponse.getData();
        JSONObject jsonObject1 = JSON.parseObject(data);
        String code1 = jsonObject1.getString("Code");
        smsLog.setResponseCode(String.valueOf(commonResponse.getHttpStatus()));
        if (code1.equals("OK")) {
            smsLog.setStatus(1);
        } else {
            smsLog.setStatus(0);
        }
        smsLog.setType(2);
        // 插入数据库
        smsLogMapper.insert(smsLog);
    }

    /**
     * 记录发微信消息的数据库
     *
     * @param wxMsgModel
     */
    @Override
    public void saveWxMsg(WxMsgModel wxMsgModel) {
        SmsLog smsLog = new SmsLog();
        smsLog.setContent("发微信消息成功");
        smsLog.setUserId(wxMsgModel.getToUser());
        smsLog.setResponseCode("200");
        smsLog.setRecDate(new Date());
        smsLog.setStatus(0);
        smsLog.setType(2);
        // 插入数据库
        smsLogMapper.insert(smsLog);
    }
}
