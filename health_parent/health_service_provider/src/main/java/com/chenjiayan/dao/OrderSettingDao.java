package com.chenjiayan.dao;

import com.chenjiayan.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingDao {
    void add(OrderSetting orderSetting);
    void editNumberByOrderDate(OrderSetting orderSetting);
    long findCountByOrderDate(Date orderDate);
    List<OrderSetting> getOrderSettingByMonth(Map<String, String> map);

    OrderSetting findByOrderDate(Date date);
    void editReservationsByOrderDate(OrderSetting orderSetting);
}
