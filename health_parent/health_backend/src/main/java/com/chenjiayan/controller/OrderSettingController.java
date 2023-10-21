package com.chenjiayan.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chenjiayan.constant.MessageConstant;
import com.chenjiayan.entitiy.Result;
import com.chenjiayan.pojo.OrderSetting;
import com.chenjiayan.service.OrderSettingService;
import com.chenjiayan.utils.POIUtils;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ordersetting")
public class OrderSettingController {

    @Reference
    private OrderSettingService orderSettingService;

    @PostMapping("/upload")
    public Result upload(@RequestBody MultipartFile excelFile){
        try {
            List<String[]> list = POIUtils.readExcel(excelFile);
            List<OrderSetting> orderSettings = list.stream().map(item -> {

                OrderSetting orderSetting = new OrderSetting(new Date(item[0]), Integer.parseInt(item[1]));
                return orderSetting;
            }).collect(Collectors.toList());
            // 通过dubbo远程调用服务实现数据批量导入到数据库
            orderSettingService.add(orderSettings);
        } catch (IOException e) {
            e.printStackTrace();
            // 文件解析失败
            return new Result(false, MessageConstant.IMPORT_ORDERSETTING_FAIL);
        }
        return new Result(true, MessageConstant.IMPORT_ORDERSETTING_SUCCESS);
    }

    /**
     * 根据月份查询对应的预约设置数据
     * @param date
     * @return
     */
    @GetMapping("/getOrderSettingByMonth")
    public Result getOrderSettingByMonth(String date){
        try{
            List<Map> list = orderSettingService.getOrderSettingByMonth(date);
            return new Result(true,MessageConstant.GET_ORDERSETTING_SUCCESS,list);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_ORDERSETTING_FAIL);
        }
    }

    /**
     * 根据日期修改对应的预约设置数据
     * @param orderSetting
     * @return
     */
    @PostMapping("/editNumberByDate")
    public Result editNumberByDate(@RequestBody OrderSetting orderSetting){
        try{
            orderSettingService.editNumberByDate(orderSetting);
            return new Result(true,MessageConstant.ORDERSETTING_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ORDERSETTING_FAIL);
        }
    }
}
