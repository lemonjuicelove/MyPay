package com.whut.service;

import com.whut.entity.WxPayBean;

import java.util.Map;

public interface WxPayService {

        // 生成二维码
        String createNative(WxPayBean wxPayBean);

        // 查询订单状态
        Map queryOrder(WxPayBean wxPayBean);

        // 关闭订单
        void closeOrder(String  tradeNo);
}
