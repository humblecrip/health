package com.chenjiayan.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chenjiayan.constant.MessageConstant;
import com.chenjiayan.constant.RedisMessageConstant;
import com.chenjiayan.entitiy.Result;
import com.chenjiayan.pojo.Order;
import com.chenjiayan.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * 体检预约处理
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;
    /**
     * 在线体检预约
     * @param map
     * @return
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody Map map){
        String telephone = (String) map.get("telephone");
        // 从redis中获取保存的验证码
        String validateCodeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        String validateCode = (String) map.get("validateCode");
        // 将用户输入的验证码和Redis中保存的验证码进行比对
        if(validateCode!=null && validateCodeInRedis!=null && validateCode.equals(validateCodeInRedis)){
            // 比对成功，调用服务完成预约业务处理
            map.put("orderType",Order.ORDERTYPE_WEIXIN); // 设置预约类型，分为微信预约、电话预约
            Result result = null;
            try{
                result = orderService.order(map);
            }catch (Exception e){
                e.printStackTrace();
                return new Result(false,MessageConstant.SEND_VALIDATECODE_FAIL);
            }
            if(result.isFlag()){
                // 预约成功，为用户发送短信通知
                try {
                    // SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE,telephone,(String)map.get("orderDate"));
                    System.out.println("预约成功，为用户发送短信通知：+"+(String)map.get("orderDate"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }else{
            // 比对不成功，返回结果给页面
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
    }

    /**
     * 根据预约ID查询预约相关信息
     * @param id
     * @return
     */
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try{
            Map map = orderService.findById(id);
            return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,map);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
        }

    }
}
