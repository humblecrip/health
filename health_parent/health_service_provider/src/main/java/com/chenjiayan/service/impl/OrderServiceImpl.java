package com.chenjiayan.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.chenjiayan.constant.MessageConstant;
import com.chenjiayan.dao.MemberDao;
import com.chenjiayan.dao.OrderDao;
import com.chenjiayan.dao.OrderSettingDao;
import com.chenjiayan.entitiy.Result;
import com.chenjiayan.pojo.Member;
import com.chenjiayan.pojo.Order;
import com.chenjiayan.pojo.OrderSetting;
import com.chenjiayan.service.OrderService;
import com.chenjiayan.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OrderDao orderDao;
    /**
     * 体检预约
     * @param map
     * @return
     */
    @Override
    public Result order(Map map) throws Exception {
        //1、检查用户所选择的预约日期是否已经提前进行了预约设置，如果没有设置则无法进行预约
        String orderDate = (String) map.get("orderDate"); // 预约日期
        OrderSetting orderSetting =orderSettingDao.findByOrderDate(DateUtils.parseString2Date(orderDate));
        if(orderSetting == null){
            // 指定日期没有进行预约设置，无法完成体检预约
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }
        //2、检查用户所选择的预约日期是否已经约满，如果已经约满则无法预约
        if(orderSetting.getNumber()<=orderSetting.getReservations()){
            // 已经约满，无法预约
            return new Result(false,MessageConstant.ORDER_FULL);
        }
        //3、检查用户是否重复预约（同一个用户在同一天预约了同一个套餐），如果是重复预约则无法完成再次预约
        String telephone = (String) map.get("telephone");
        Member member = memberDao.findByTelephone(telephone);
        if(member!=null){
            // 判断是否在重复预约
            Integer memberId = member.getId(); // 会员ID
            Date order_date = DateUtils.parseString2Date(orderDate); // 预约日期
            Integer setmealId = Integer.parseInt((String) map.get("setmealId"));
            Order order = new Order(memberId,order_date,setmealId);
            // 根据条件进行查询
            List<Order> list = orderDao.findByCondition(order);
            if(list!=null && list.size()>0){
                // 说明用户在重复预约，无法完成再次预约
                return new Result(false,MessageConstant.HAS_ORDERED);
            }
        }else{
            //4、检查当前用户是否为会员，如果是会员则直接完成预约，如果不是会员则自动完成注册并进行预约
            member = new Member();
            member.setName((String) map.get("name"));
            member.setPhoneNumber(telephone);
            member.setIdCard((String) map.get("idCard"));
            member.setSex((String) map.get("sex"));
            member.setRegTime(new Date());
            memberDao.add(member); // 自动完成会员注册
        }
        //5、预约成功，更新当日的已预约人数
        Order order = new Order();
        order.setMemberId(member.getId()); // 设置会员ID
        order.setOrderDate(DateUtils.parseString2Date(orderDate)); // 预约日期
        order.setOrderType((String) map.get("orderType"));
        order.setOrderStatus(Order.ORDERSTATUS_NO);
        order.setSetmealId(Integer.parseInt((String)map.get("setmealId")));
        orderDao.add(order);
        orderSetting.setReservations(orderSetting.getReservations()+1);
        orderSettingDao.editReservationsByOrderDate(orderSetting);
        return new Result(true,MessageConstant.ORDER_SUCCESS,order.getId());
    }

    /**
     * 根据预约ID查询预约相关信息（体检人姓名，预约日期，套餐名称，预约类型）
     * @param id
     * @return
     */
    @Override
    public Map findById(Integer id) throws Exception {
        Map<Object, Object> map = orderDao.findById4Detail(id);
        if(map!=null){
            Date orderDate = (Date) map.get("orderDate");
            map.put("orderDate",DateUtils.parseDate2String(orderDate));
        }
        return map;
    }

}
