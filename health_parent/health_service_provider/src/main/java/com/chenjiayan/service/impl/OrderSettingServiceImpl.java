package com.chenjiayan.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.chenjiayan.dao.OrderSettingDao;
import com.chenjiayan.pojo.OrderSetting;
import com.chenjiayan.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;
    // 批量导入预约设置数据
    @Override
    public void add(List<OrderSetting> list) {
        if(list!=null&&list.size()>0){
            for (OrderSetting orderSetting : list) {
                // 判断当前日期是否已经进行了预约设置
                long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                if(count>0){
                    // 已经进行了预约设置，执行更新操作
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                }else {
                    // 没有进行预约设置，执行插入操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }

    @Override
    public List<Map> getOrderSettingByMonth(String date) {
        String begin = date + "-1";
        String end = date + "-31";
        Map<String,String> map = new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        // 调用DAO，根据日期范围查询预约设置数据
        List<Map> result = null;
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(map);
        if(list!=null&&list.size()>0){
            result = list.stream().map(item -> {
                Map<String, Object> m = new HashMap<>();
                m.put("date", item.getOrderDate().getDate());
                m.put("number", item.getNumber());
                m.put("reservations", item.getReservations());
                return m;
            }).collect(Collectors.toList());
        }
        return result;
    }

    /**
     * 根据日期修改对应的预约设置数据
     * @param orderSetting
     */
    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if(count>0){
            // 当前日期已经进行了预约设置，需要执行更新操作
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else{
            // 没有进行预约设置，执行插入操作
            orderSettingDao.add(orderSetting);
        }

    }
}
