package com.whut.service;

public interface AliPayService {

    // 下单生成二维码
    String createTrade();

    // 查询订单状态
    String queryOrder(String outTradeNo);

    // 关闭订单
    String closeOrder(String outTradeNo);

}
