package com.chenjiayan.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.chenjiayan.constant.MessageConstant;
import com.chenjiayan.entitiy.PageResult;
import com.chenjiayan.entitiy.QueryPageBean;
import com.chenjiayan.entitiy.Result;
import com.chenjiayan.pojo.CheckGroup;
import com.chenjiayan.service.CheckGroupService;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 检查组管理
 */
@RestController
@RequestMapping("/checkgroup")
public class CheckGroupController {

    @Reference
    private CheckGroupService checkGroupService;

    /**
     * 新增检查组
     * @param checkitemIds
     * @param checkGroup
     * @return
     */
    @PostMapping("/add")
    public Result add(Integer[] checkitemIds, @RequestBody  CheckGroup checkGroup){

        try{
            checkGroupService.add(checkitemIds,checkGroup);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_CHECKGROUP_FAIL);
        }
        return new Result(true, MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    @PostMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) throws UnsupportedEncodingException {
        return checkGroupService.pageQuery(queryPageBean);
    }

    /**
     * 根据ID查询检查组
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public Result findById(Integer id){
        try{
            // 成功
            CheckGroup checkGroup = checkGroupService.findById(id);
            return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroup);
        }catch (Exception e){
            // 失败
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }

    /**
     * 根据检查组ID查询对应的所有检查项
     * @param id
     * @return
     */
    @GetMapping("/findCheckItemIdsByCheckGroupId")
    public Result findCheckItemIdsByCheckGroupId(Integer id) {
        try {
            // 成功
            List<Integer> list = checkGroupService.findCheckItemIdsByCheckGroupId(id);
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS, list);
        } catch (Exception e) {
            // 失败
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL);
        }
    }

    /**
     * 编辑检查组
     * @param checkitemIds
     * @param checkGroup
     * @return
     */
    @PostMapping("/edit")
    public Result edit(Integer[] checkitemIds, @RequestBody  CheckGroup checkGroup){

        try{
            checkGroupService.edit(checkitemIds,checkGroup);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.EDIT_CHECKGROUP_FAIL);
        }
        return new Result(true, MessageConstant.EDIT_CHECKGROUP_SUCCESS);
    }

    /**
     * 查询所有检查组
     * @param
     * @return
     */
    @GetMapping("/findAll")
    public Result findAll(){
        try{
            // 成功
            List<CheckGroup> checkGroups = checkGroupService.findAll();
            return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroups);
        }catch (Exception e){
            // 失败
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL);
        }
    }
}
