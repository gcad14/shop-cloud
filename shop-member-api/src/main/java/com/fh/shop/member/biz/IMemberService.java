package com.fh.shop.member.biz;

import com.fh.shop.common.ServerResponse;

public interface IMemberService {


    ServerResponse login(String memberName, String password);
}
