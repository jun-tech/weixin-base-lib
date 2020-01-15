package org.superbeetle.weixinbase.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import rxframework.base.model.ServiceResult;

@Controller
@RequestMapping("/auth")
public class AuthController {


    /**
     * 刷新缓存
     */
    @RequestMapping("login")
    @ResponseBody
    public ServiceResult login() {
        ServiceResult sr = new ServiceResult();
        return sr;
    }
}
