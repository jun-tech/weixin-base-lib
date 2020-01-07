package com.superbeetle.weixinbase;

import org.junit.Before;
import rxframework.utility.cache.MCacheUtils;
import rxui.manager.SysParamCache;

public class BaseTestCase {
    @Before
    public void init() {
        MCacheUtils.put(SysParamCache.NAME_SPACE, "PLATFORM_NAMESPACE", "PLATFORM_WEIXIN_DEV");
        System.out.println(MCacheUtils.get(SysParamCache.NAME_SPACE,"PLATFORM_NAMESPACE"));
    }
}
