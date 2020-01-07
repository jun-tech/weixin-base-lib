package com.superbeetle.weixinbase.util;

import com.superbeetle.weixinbase.BaseTestCase;
import com.superbeetle.weixinbase.constant.WeixinConfig;
import com.superbeetle.weixinbase.model.to.WeixinTemplate;
import com.superbeetle.weixinbase.model.to.WeixinTemplateData;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class WeixinBaseTest extends BaseTestCase {

    @Test
    public void testSendTemplateMessage() {
        WeixinTemplate template = new WeixinTemplate();
        template.setUrl("http://aqjy.jiuwoding.com/");
        template.setTouser("oC9pxszK2QGhu2RiFpMplu9gdQCA");//我的测试openid
//        template.setTouser("oC9pxs4WW_1upnHjtTa9gOdLNMUI");
        template.setTopcolor("#000000");
        template.setTemplate_id("GCuAzqUH1UaAnfdK0APmqSmcuomegwebYcVvzIT4ULM");
        Map<String, WeixinTemplateData> m = new HashMap<String, WeixinTemplateData>();
        template.setData(m);
        WeixinTemplateData first = new WeixinTemplateData();
        first.setColor("#929232");
        first.setValue("您有待处理的工作，请及时【处理】");
        m.put("first", first);

        WeixinTemplateData keyword1 = new WeixinTemplateData();
        keyword1.setColor("#000");
        keyword1.setValue("关键字1");
        m.put("keyword1", first);

        WeixinTemplateData keyword2 = new WeixinTemplateData();
        keyword2.setColor("#000");
        keyword2.setValue("关键字2");
        m.put("keyword2", keyword2);

        WeixinTemplateData keyword3 = new WeixinTemplateData();
        keyword3.setColor("#000");
        keyword3.setValue("关键字3");
        m.put("keyword3", keyword3);

        WeixinTemplateData keyword4 = new WeixinTemplateData();
        keyword4.setColor("#000");
        keyword4.setValue("关键字4");
        m.put("keyword4", keyword4);

        WeixinTemplateData remark = new WeixinTemplateData();
        remark.setColor("#929232");
        remark.setValue("备注一下");
        m.put("remark", remark);
        WeixinBase.sendTemplateMessage(WeixinConfig.WEIXIN_APPID, WeixinConfig.WEIXIN_SECRET, template);

    }

    @Test
    public void testSendSubscribeMessage() {
        WeixinBase.sendSubscribeMessage(WeixinConfig.WEIXIN_APPID, WeixinConfig.WEIXIN_SECRET);
    }
}
