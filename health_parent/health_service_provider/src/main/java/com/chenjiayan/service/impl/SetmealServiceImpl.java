package com.chenjiayan.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.chenjiayan.constant.RedisConstant;
import com.chenjiayan.dao.SetmealDao;
import com.chenjiayan.entitiy.PageResult;
import com.chenjiayan.entitiy.QueryPageBean;
import com.chenjiayan.pojo.Setmeal;
import com.chenjiayan.service.SetmealService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


/**
 * 体检套餐服务
 */
@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService{
    @Autowired
    private SetmealDao setmealDao;

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Value("${out_put_path}")
    private String outPutPath; //从属性文件中
    /**
     * 新增套餐信息，同时需要关联检查组
     * @param checkGroupIds
     * @param setmeal
     */
    @Override
    public void add(Integer[] checkGroupIds, Setmeal setmeal) {
        setmealDao.add(setmeal);
        Integer setmealId = setmeal.getId();
        setSetmealAndCheckGroup(checkGroupIds,setmealId);
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());
        // 当添加套餐后需要重新生成静态页面
        generateMobilStaticHtml();
    }

    /**
     * 生成当前方法所需的静态页面
     */
    public void generateMobilStaticHtml(){
        // 在生成静态页面之前需要查询数据
        List<Setmeal> list = setmealDao.getAll();
        // 需要生成套餐列表静态页面
        generateMobileSetmealListHtml(list);
        // 需要生成套餐详情静态页面
        generateMobileSetmealDetailHtml(list);
    }

    public void generateMobileSetmealListHtml(List<Setmeal> list){
        Map map = new HashMap();
        map.put("setmealList",list);
        generateHtml("mobile_setmeal.ftl","m_setmeal.html",map);
    }

    public void generateMobileSetmealDetailHtml(List<Setmeal> list){
        for(Setmeal item : list){
            Map map = new HashMap();
            map.put("setmeal", setmealDao.findSetMealById(item.getId()));
            generateHtml("mobile_setmeal_detail.ftl", "setmeal_detail_" + item.getId() + ".html", map);
        }
    }
    /**
     * 生成静态页面
     * @param templeName
     * @param htmlPageName
     * @param map
     */
    public void generateHtml(String templeName,String htmlPageName,Map map){
        Configuration configuration = freeMarkerConfig.getConfiguration();
        Writer out = null;
        try {
            Template template = configuration.getTemplate(templeName);
            // 构造输出流
            out = new FileWriter(new File(outPutPath+"/"+htmlPageName));
            // 输出文件
            template.process(map,out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage,pageSize);
        Page<Setmeal> page =setmealDao.findByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public List<Setmeal> getAllSetmeal() {
        return setmealDao.getAll();
    }

    /**
     * 根据套餐ID查询套餐详情，（套餐基本信息，套餐对应的检查组信息，检查组对应的检查项信息）
     * @param id
     * @return
     */
    @Override
    public Setmeal findById(Integer id) {
        return setmealDao.findSetMealById(id);
    }

    /**
     * 查询套餐预约占比
     * @return
     */
    @Override
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }

    public void setSetmealAndCheckGroup(Integer[] checkGroupIds,Integer setmealId){
        if(checkGroupIds!=null&&checkGroupIds.length>0){
            for (Integer checkGroupId : checkGroupIds) {
                Map<String, Integer> map = new HashMap<>();
                map.put("setmealId",setmealId);
                map.put("checkGroupId",checkGroupId);
                setmealDao.setSetmealAndCheckGroup(map);
            }
        }
    }
}
