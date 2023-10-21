package com.chenjiayan.jobs;

import com.chenjiayan.constant.RedisConstant;
import com.chenjiayan.utils.AliyunUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * 自定义job,实现定时清理垃圾图片
 */
public class ClearImgJob {
    @Autowired
    private JedisPool jedisPool;
    public void clearImg(){
        // 根据Redis中保存的两个set集合进行差值计算，获得垃圾图片名称集合
        Set<String> sdiff = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        if(sdiff!=null){
            for (String picName : sdiff) {
                // 删除阿里云服务器上的图片
                AliyunUtils.deleteFileFromAliyun(picName);
                // 从Redis集合中删除图片名称
                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,picName);
                System.out.println("自定义任务执行，清理垃圾图片："+picName);
            }
        }
    }
}
