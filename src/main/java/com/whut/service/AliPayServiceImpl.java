package com.whut.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AliPayServiceImpl implements AliPayService {

    private static final Logger logger = LoggerFactory.getLogger(AliPayServiceImpl.class);

    private String appId; // 唯一应用Id
    private String privateKey; // 应用私钥
    private String aliPayPublicKey; // 支付宝公钥
    private String url = "https://openapi.alipaydev.com/gateway.do"; // 固定值

    /**
     * 下单生成二维码链接
     * @return 返回值是二维码链接
     */
    @Override
    public String createTrade() {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setAppId(appId);
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setAlipayPublicKey(aliPayPublicKey);
        alipayConfig.setServerUrl(url);
        alipayConfig.setFormat("json");
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");

        String res = null;

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();

            model.setOutTradeNo("20150320010101001");
            model.setTotalAmount("88.88");
            model.setSubject("Iphone6 16G");
            request.setBizModel(model);

            AlipayTradePrecreateResponse response = alipayClient.execute(request);

            if (response.isSuccess()){
                logger.info("下单生成二维码成功");
                res = response.getQrCode();

            }else{
                logger.info("下单生成二维码失败");
                throw new Exception("下单生成二维码失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }


    /**
     * 查询订单状态接口
     * @param outTradeNo:商户订单号
     * @return 订单支付状态
     */
    @Override
    public String queryOrder(String outTradeNo) {

        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setAppId(appId);
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setAlipayPublicKey(aliPayPublicKey);
        alipayConfig.setServerUrl(url);
        alipayConfig.setFormat("json");
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");

        String req = null;

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();

            model.setOutTradeNo(outTradeNo);
            request.setBizModel(model);

            AlipayTradeQueryResponse response = alipayClient.execute(request);


            if (response.isSuccess()){
                logger.info("查询订单状态成功");
                req = response.getTradeStatus();
            }else{
                logger.info("查询订单状态失败");
                throw new Exception("查询订单状态失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return req;
    }

    /**
     * 关闭订单接口
     * @param outTradeNo:商户订单号
     * @return 订单是否关闭成功
     */
    @Override
    public String closeOrder(String outTradeNo) {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setAppId(appId);
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setAlipayPublicKey(aliPayPublicKey);
        alipayConfig.setServerUrl(url);
        alipayConfig.setFormat("json");
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");

        String req = null;
        try{
            AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            AlipayTradeCloseModel model = new AlipayTradeCloseModel();

            model.setOutTradeNo(outTradeNo);
            request.setBizModel(model);

            AlipayTradeCloseResponse response = alipayClient.execute(request);

            response.getSubCode();

            if (response.isSuccess()){
                logger.info("关闭订单调用成功");
                req = response.getSubCode();
            }else{
                logger.info("关闭订单调用失败");
                throw new Exception("关闭订单调用失败");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return req;
    }
}
