package com.whut.service;


import com.alibaba.fastjson.JSON;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.whut.entity.WxPayBean;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

@Service
public class WxPayServiceImpl implements WxPayService {

    private CloseableHttpClient httpClient;

    private static final Logger logger = LoggerFactory.getLogger(WxPayServiceImpl.class);

    private String mchId; // 商户号
    private String mchSerialNo; // 商户证书序列号
    private String privateKey; // 私钥
    private String apiV3Key; // V3密钥

    @PostConstruct
    public void setUp(){

        try {
            // 加载商户私钥
            PrivateKey key = PemUtil.loadPrivateKey(new ByteArrayInputStream(privateKey.getBytes("utf-8")));

            // 加载平台证书
            AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(new WechatPay2Credentials(mchId,
                    new PrivateKeySigner(mchSerialNo,key)),apiV3Key.getBytes("utf-8"));

            // 初始化httpClient
            httpClient = WechatPayHttpClientBuilder.create()
                    .withMerchant(mchId, mchSerialNo, key)
                    .withValidator(new WechatPay2Validator(verifier)).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void close(){
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 下单生成二维码
     * @param wxPayBean
     * @return
     */
    @Override
    public String createNative(WxPayBean wxPayBean) {

        CloseableHttpResponse response = null;
        String res = null;

        try {
            HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/native");
            String reqData = wxPayBean.toString();
            StringEntity entity = new StringEntity(reqData,"utf-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");

            // 完成签名并执行请求
            response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200){
                logger.info("下单成功，返回二维码链接");
                res = EntityUtils.toString(response.getEntity());
            }else if (statusCode == 204){
                logger.info("下单成功，无返回");

            }else{
                logger.info("下单失败");
                throw new Exception("下单失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * 查询订单状态:根据商户号和订单号查询
     * @param wxPayBean
     * @return
     */
    @Override
    public Map queryOrder(WxPayBean wxPayBean) {
        Map<String,String> infoMap = new HashMap<>();

        CloseableHttpResponse response = null;


        try {
            String uri = "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/"+ wxPayBean.getTradeNo();
            URIBuilder uriBuilder = new URIBuilder(uri);
            uriBuilder.setParameter("mchid",wxPayBean.getMchId());

            // 完成签名并执行请求
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.addHeader("Accept", "application/json");
            response = httpClient.execute(httpGet);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200){
                logger.info("订单支付成功，有返回信息");

                // 转成Map
                infoMap = JSON.parseObject(EntityUtils.toString(response.getEntity()),Map.class);
            }else if (statusCode == 204){
                logger.info("订单支付成功，无返回信息");
            }else{
                logger.info("订单支付失败,statusCode = " + statusCode + ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new Exception("订单支付失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return infoMap;
    }


    /**
     * 关闭订单
     * @param tradeNo：订单号
     */
    @Override
    public void closeOrder(String tradeNo) {

        String url = "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/{out_trade_no}/close";
        url.replace("{" + "out_trade_no" + "}",tradeNo);

        String reqData = "{\"mchid\": \""+mchId+"\"}";
        StringEntity entity = new StringEntity(reqData,"utf-8");
        entity.setContentType("application/json");

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        CloseableHttpResponse response = null;

        try {
            response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200){
                logger.info("订单关闭成功，有返回信息");
            }else if (statusCode == 204){
                logger.info("订单关闭成功，无返回信息");
            }else{
                logger.info("订单关闭失败");
                throw new IOException("订单关闭失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
