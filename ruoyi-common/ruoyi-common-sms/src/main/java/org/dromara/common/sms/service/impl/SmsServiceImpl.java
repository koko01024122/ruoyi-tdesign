package org.dromara.common.sms.service.impl;

import cn.hutool.core.util.StrUtil;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.helper.SysConfigHelper;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.sms.aspectj.SmsContextCache;
import org.dromara.common.sms.config.properties.SmsProperties;
import org.dromara.common.sms.handle.SmsContextHolder;
import org.dromara.common.sms.service.SmsService;
import org.dromara.sms4j.aliyun.config.AlibabaConfig;
import org.dromara.sms4j.aliyun.config.AlibabaFactory;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.tencent.config.TencentConfig;
import org.dromara.sms4j.tencent.config.TencentFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * 短信服务
 * 当执行多次的接口调用时，可以手动缓存对象到{@linkplain  SmsContextHolder}中，并在执行结束后移除缓存，避免修改配置后读取不到；
 * 或者可以使用{@linkplain SmsContextCache}注解在使用的SpringBean中使用，可以在整个方法中缓存发送对象
 * @author hexm
 * @date 2023/2/4
 */
@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public SmsBlend getSmsTemplate() {
        // 优先从上下文中获取
        SmsBlend template = SmsContextHolder.getSmsTemplate();
        if (template != null) {
            return template;
        }
        String smsJson = SysConfigHelper.getSysSms();
        if (StrUtil.isBlank(smsJson)) {
            throw new ServiceException("当前系统未配置短信功能！");
        }
        SmsProperties properties = JsonUtils.parseObject(smsJson, SmsProperties.class);
        if (properties != null && properties.getEnabled()) {
            template = switch (properties.getEndpoint()) {
                case "dysmsapi.aliyuncs.com" -> {
                    AlibabaConfig config = AlibabaConfig.builder()
                        .accessKeyId(properties.getAccessKeyId())
                        .accessKeySecret(properties.getAccessKeySecret())
                        .requestUrl(properties.getEndpoint())
                        .signature(properties.getSignName())
                        .build();
                    yield AlibabaFactory.instance().createSms(config);
                }
                case "sms.tencentcloudapi.com" -> {
                    TencentConfig config = TencentConfig.builder()
                        .accessKeyId(properties.getAccessKeyId())
                        .accessKeySecret(properties.getAccessKeySecret())
                        .requestUrl(properties.getEndpoint())
                        .signature(properties.getSignName())
                        .sdkAppId(properties.getSdkAppId())
                        .build();
                    yield TencentFactory.instance().createSms(config);
                }
                default -> throw new ServiceException("未找到对应的短信平台！");
            };
            return template;
        }
        throw new ServiceException("当前系统没有开启短信功能！");
    }

    @Override
    public SmsResponse send(String phones, String templateId, LinkedHashMap<String, String> param) {
        return getSmsTemplate().sendMessage(phones, templateId, param);
    }
}
