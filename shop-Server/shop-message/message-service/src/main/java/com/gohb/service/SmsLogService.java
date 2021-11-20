package com.gohb.service;

import com.aliyuncs.CommonResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gohb.domain.SmsLog;
import com.gohb.model.AliSmsModel;
import com.gohb.model.WxMsgModel;


public interface SmsLogService extends IService<SmsLog> {


    /**
     * 记录发短信的数据库
     *
     * @param aliSmsModel
     */
    void saveMsg(AliSmsModel aliSmsModel, CommonResponse commonResponse);

    /**
     * 记录发微信消息的数据库
     *
     * @param wxMsgModel
     */
    void saveWxMsg(WxMsgModel wxMsgModel);
}
