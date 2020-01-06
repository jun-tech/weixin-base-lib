package com.superbeetle.weixinbase.service.impl;

import org.springframework.context.event.ContextRefreshedEvent;
import rxframework.utility.cache.MCacheUtils;
import rxplatform.system.service.ISystemInitService;
import rxui.manager.SysParamCache;

public class SysBaseConfigInit implements ISystemInitService {
    @Override
    public void init() {
        // 初始化一些必要的系统参数
        MCacheUtils.put(SysParamCache.NAME_SPACE, "PLATFORM_NAMESPACE", "PLATFORM_WEIXIN_DEV");
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

    }
}
