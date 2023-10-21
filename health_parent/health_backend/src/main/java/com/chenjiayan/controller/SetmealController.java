package com.chenjiayan.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chenjiayan.constant.MessageConstant;
import com.chenjiayan.constant.RedisConstant;
import com.chenjiayan.entitiy.PageResult;
import com.chenjiayan.entitiy.QueryPageBean;
import com.chenjiayan.entitiy.Result;
import com.chenjiayan.pojo.Setmeal;
import com.chenjiayan.service.SetmealService;
import com.chenjiayan.utils.AliyunUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * 体检套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    // 使用RedisPool操作Redis服务
    @Autowired
    private JedisPool jedisPool;

    @Reference
    private SetmealService setmealService;
    /**
     * 上传图片
     * @param imgFile
     * @return
     */
    @PostMapping("/upload")
    public Result upload(@RequestBody MultipartFile imgFile){
        String originalFilename = imgFile.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String extension = originalFilename.substring(index);
        String fileName = UUID.randomUUID().toString()+extension;
        try {
            AliyunUtils.upload2Aliyun(imgFile.getBytes(),fileName);
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
        return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS,fileName);
    }

    /**
     * 新增套餐
     * @return
     */
    @PostMapping("/add")
    public Result add(Integer[] checkGroupIds, @RequestBody Setmeal setmeal){
        try{
            setmealService.add(checkGroupIds,setmeal);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_SETMEAL_FAIL);
        }
        return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
    }
    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    @PostMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) throws UnsupportedEncodingException {
        return setmealService.pageQuery(queryPageBean);
    }
}
