package com.chenjiayan.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.chenjiayan.dao.MemberDao;
import com.chenjiayan.pojo.Member;
import com.chenjiayan.service.MemberService;
import com.chenjiayan.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员服务
 */
@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService{

    @Autowired
    private MemberDao memberDao;

    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    /**
     * 保存会员信息
     * @param member
     */
    @Override
    public void add(Member member) {
        String password = member.getPassword();
        if(password!=null){
            // 使用md5将明文密码进行加密
            password = MD5Utils.md5(password);
            member.setPassword(password);
        }
        memberDao.add(member);
    }

    @Override
    public List<Integer> findMemberCountByMonths(List<String> months) {
        List<Integer> list = new ArrayList<>();
        months.forEach(item->{
            Integer count = memberDao.findMemberCountBeforeDate(item);
            list.add(count);
        });
        return list;
    }
}
