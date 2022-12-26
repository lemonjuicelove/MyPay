package com.whut.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WxPayBean {

    private String appId; // 应用Id
    private String mchId; // 商户Id
    private String description; // 商品描述
    private String tradeNo; // 订单号
    private String notifyUrl; // 通知地址
    private String total; // 商品价格


    @Override
    public String toString() {
        return "{"
                + "\"mchid\":\"" + mchId + "\","
                + "\"appid\":\"" + appId + "\","
                + "\"description\":\"" + description + "\","
                + "\"out_trade_no\":\"" + tradeNo + "\","
                + "\"notifyUrl\":\"" + notifyUrl + "\","
                + "\"amount\":{"
                    + "\"total\":" +  total + ","
                    + "\"currency\":\"CNY\""
                + "},"
                + "}";
    }



}
