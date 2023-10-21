package com.chenjiayan.service;

import com.chenjiayan.pojo.Member;

import java.util.List;

public interface MemberService {
    Member findByTelephone(String telephone);
    void add(Member member);
    List<Integer> findMemberCountByMonths(List<String> months);
}
