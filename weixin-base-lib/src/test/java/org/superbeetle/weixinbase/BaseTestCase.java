package org.superbeetle.weixinbase;

import org.junit.Before;
import org.superbeetle.framework.session.SysParamCache;
import org.superbeetle.framework.utility.cache.MCacheUtils;

public class BaseTestCase {
    @Before
    public void init() {
        MCacheUtils.put(SysParamCache.NAME_SPACE, "PLATFORM_NAMESPACE", "PLATFORM_WEIXIN_DEV");
        System.out.println(MCacheUtils.get(SysParamCache.NAME_SPACE,"PLATFORM_NAMESPACE"));
    }
}
