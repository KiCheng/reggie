package com.Lijiacheng.controller;

import com.Lijiacheng.common.Result;
import com.Lijiacheng.domain.User;
import com.Lijiacheng.service.UserService;
import com.Lijiacheng.utils.SMSUtils;
import com.Lijiacheng.utils.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session){
        // 获取手机号
        String phone = user.getPhone();

        if(StringUtils.hasText(phone)){
            // 生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code -> {}", code);
            // 调用阿里云提供的短信服务API完成短信发送
//            SMSUtils.sendMessage("瑞吉外卖", "您的验证码为：${code}，请勿泄露于他人！", phone, code);
            // 需要将生成的验证码保存到Session中
            session.setAttribute(phone, code);

            return Result.success("手机短信验证码发送成功");
        }

        return Result.error("手机短信验证码发送失败");
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody Map userMap, HttpSession session){
        log.info(userMap.toString());

        // 获取手机号
        String phone = userMap.get("phone").toString();
        // 获取验证码
        String code = userMap.get("code").toString();

        // 从session中获取保存的验证码
        String codeInSession = session.getAttribute(phone).toString();

        // 进行验证码的比对（页面提交的验证码和Session中保存的验证码做比对）
        if(codeInSession != null && codeInSession.equals(code)){
            // 如果能够比对成功，说明登录成功
            // 判断当前手机号对应的用户是否为新用户
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(lambdaQueryWrapper);

            if(user == null){
                // 如果是新用户则自动注册
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return Result.success(user);
        }

        return Result.error("登录失败");
    }


}
