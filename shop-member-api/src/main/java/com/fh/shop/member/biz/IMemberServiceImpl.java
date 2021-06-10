package com.fh.shop.member.biz;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fh.shop.common.Constants;
import com.fh.shop.common.KeyUtil;
import com.fh.shop.common.ResponseEnum;
import com.fh.shop.common.ServerResponse;
import com.fh.shop.member.mapper.IMemberMapper;
import com.fh.shop.member.po.Member;
import com.fh.shop.vo.MemberVo;
import com.fh.shop.util.Md5Util;
import com.fh.shop.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Base64;

@Service("IMemberService")
@Transactional(rollbackFor = Exception.class)
public class IMemberServiceImpl implements IMemberService {

    @Resource
    private IMemberMapper iMemberMapper;

//    @Autowired
//    private MailForm mailForm;
//
//    @Autowired
//    private HttpServletRequest request;
//
//    @Autowired
//    private MailProducer mailProducer;

    @Override
    public ServerResponse login(String memberName, String password) {
        //验证非空
        if (StringUtils.isEmpty(memberName) || StringUtils.isEmpty(password)) {
            return ServerResponse.error(ResponseEnum.MEMBER_VIP_NULL);
        }
        //获取用户信息(根据用户名进行查询)
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.eq("memberName", memberName);
        Member member = iMemberMapper.selectOne(wrapper);
        //判断是否有此用户
        if (member == null) {
            return ServerResponse.error(ResponseEnum.MEMBER_VIP_NULL);
        }
        //验证密码是否正确
        if (!member.getPassword().equals(Md5Util.md5(password))) {
            return ServerResponse.error(ResponseEnum.MEMEBER_PASSWORD_ERROR);
        }
        //判断是否为会员
//        if (member.getStart() != 1) {//当不是会员时
//            String uuid = UUID.randomUUID().toString();
//            String jmuuid = KeyUtil.builIActiveMemberKeyCode(uuid);
//            Long id = member.getId();//获取到会员的id
//            RedisUtil.setex(jmuuid, id + "", Constants.TOKEN_EXPIRE);
//            String url = "http://localhost:8010/api/member/activeMember?id=" + uuid;
//            String a = "<a href='" + url + "'>激活</a>";
//            mailForm.sendEmailMesssage(member.getMail(), "会员激活", "请激活后登录，激活地址为：" + a);
//            return ServerResponse.error();
//        }
        //生成签名
        //向前台响应数据
        MemberVo memberVo = new MemberVo();
        memberVo.setId(member.getId());
        memberVo.setMemberName(member.getMemberName());
        memberVo.setNickName(member.getNickName());
        Long id = member.getId();
        String memberJSON = JSON.toJSONString(memberVo);
        String sign = Md5Util.sign(memberJSON, Constants.SECRET);
        //将用户信息进行base64编码
        String memberBase = Base64.getEncoder().encodeToString(memberJSON.getBytes());
        //将签名信息进行base64编码
        String sercrtBase = Base64.getEncoder().encodeToString(sign.getBytes());
        //将用户信息返回给前台【但不包括密码】
        //将用户信息和签名通过,分割成一个字符串【x,y】
        //设置JWT的过期时间
        RedisUtil.setex(KeyUtil.builMemberKey(id), "", Constants.TOKEN_EXPIRE);

        return ServerResponse.success(memberBase + "." + sercrtBase);
    }

}

