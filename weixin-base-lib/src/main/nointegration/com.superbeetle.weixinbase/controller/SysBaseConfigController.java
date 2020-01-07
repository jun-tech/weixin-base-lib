package com.superbeetle.weixinbase.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import rxframework.utility.cache.MCacheUtils;
import rxui.manager.SysParamCache;

import javax.annotation.PostConstruct;

@Controller
@RequestMapping("/sysBaseConfig")
public class SysBaseConfigController {

    @PostConstruct
    public void init() {
        // 初始化一些必要的系统参数
        MCacheUtils.put(SysParamCache.NAME_SPACE, "PLATFORM_NAMESPACE", "PLATFORM_WEIXIN_DEV");
    }

}
