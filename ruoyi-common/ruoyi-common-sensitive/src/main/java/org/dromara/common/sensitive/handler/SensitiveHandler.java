package org.dromara.common.sensitive.handler;

import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.spring.SpringUtils;
import org.dromara.common.sensitive.annotation.Sensitive;
import org.dromara.common.sensitive.annotation.SensitiveIgnore;
import org.dromara.common.sensitive.core.SensitiveService;
import org.dromara.common.sensitive.core.SensitiveStrategy;
import org.springframework.beans.BeansException;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 数据脱敏json序列化工具
 *
 * @author Yjoioooo
 */
@Slf4j
public class SensitiveHandler extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveStrategy strategy;
    private String[] roleKey;
    private String[] perms;
    private SaMode mode;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null || isIgnore(gen)) {
            gen.writeString(value);
            return;
        }
        try {
            SensitiveService sensitiveService = SpringUtils.getBean(SensitiveService.class);
            if (ObjectUtil.isNotNull(sensitiveService) && sensitiveService.isSensitive(roleKey, perms, mode)) {
                gen.writeString(strategy.desensitizer().apply(value));
            } else {
                gen.writeString(value);
            }
        } catch (BeansException e) {
            log.error("脱敏实现不存在, 采用默认处理 => {}", e.getMessage());
            gen.writeString(value);
        }
    }

    /**
     * 判断是否忽略脱敏
     */
    private boolean isIgnore(JsonGenerator gen) {
        // 结构中存在忽略脱敏注解则写入结束
        JsonStreamContext context = gen.getOutputContext();
        for (; !context.inRoot(); context = context.getParent()) {
            Class<?> clz = context.getCurrentValue().getClass();
            Field field;
            Method method;
            if (clz.isAnnotationPresent(SensitiveIgnore.class)) {
                return true;
            }
            if (!context.hasCurrentName()) {
                return false;
            }
            if ((field = ReflectionUtils.findField(clz, context.getCurrentName())) != null
                && field.isAnnotationPresent(SensitiveIgnore.class)) {
                return true;
            }
            String methodName = "get" + StringUtils.capitalize(context.getCurrentName());
            if ((method = ReflectionUtils.findMethod(clz, methodName)) != null
                && method.isAnnotationPresent(SensitiveIgnore.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        Sensitive annotation;
        if (Objects.equals(String.class, property.getType().getRawClass())
            && property.getAnnotation(SensitiveIgnore.class) == null
            && Objects.nonNull(annotation = property.getAnnotation(Sensitive.class))) {
            this.strategy = annotation.strategy();
            this.roleKey = annotation.roleKey();
            this.perms = annotation.perms();
            this.mode = annotation.mode();
            return this;
        }
        return prov.findValueSerializer(property.getType(), property);
    }
}
