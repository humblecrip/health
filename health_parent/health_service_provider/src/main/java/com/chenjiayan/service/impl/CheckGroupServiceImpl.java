package com.chenjiayan.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.chenjiayan.dao.CheckGroupDao;
import com.chenjiayan.entitiy.PageResult;
import com.chenjiayan.entitiy.QueryPageBean;
import com.chenjiayan.pojo.CheckGroup;
import com.chenjiayan.service.CheckGroupService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {
    @Autowired
    private CheckGroupDao checkGroupDao;
    /**
     * 新增检查组，同时需要让检查组关联检查项
     * @param checkitemIds
     * @param checkGroup
     */
    @Override
    public void add(Integer[]  checkitemIds, CheckGroup checkGroup) {
        // 新增检查组 ，操作t_checkgroup表
        checkGroupDao.add(checkGroup);
        // 设置检查组和检查项的多对多的关联关系 操作t_checkgroup_checkitem
        Integer id = checkGroup.getId();
        setCheckGroupAndCheckItem(id,checkitemIds);

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
        Page<CheckGroup> page =checkGroupDao.findByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 根据ID查询检查组
     * @param id
     * @return
     */
    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    /**
     * 根据检查组ID查询关联的检查项IDs
     * @param id
     * @return
     */
    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    /**
     * 编辑检查组信息，同时需要关联检查项
     * @param checkitemIds
     * @param checkGroup
     */
    @Override
    public void edit(Integer[] checkitemIds, CheckGroup checkGroup) {
        Integer checkGroupId = checkGroup.getId();
        // 修改检查组基本信息，操作 t_checkgroup
        checkGroupDao.edit(checkGroup);
        // 删除之前检查组对应的检查项，操作 t_checkgroup_checkitem
        checkGroupDao.deleteAssociation(checkGroupId);
        // 保存检查组和检查项的关联关系
        setCheckGroupAndCheckItem(checkGroupId,checkitemIds);

    }

    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }


    // 建立检查组和检查项多对多关系
    public void setCheckGroupAndCheckItem(Integer id,Integer[] checkitemIds){
        if(checkitemIds!=null&& checkitemIds.length>0){
            for (Integer checkitemId : checkitemIds) {
                Map<String, Integer> map = new HashMap<>();
                map.put("checkGroupId",id);
                map.put("checkItemId",checkitemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }
}
