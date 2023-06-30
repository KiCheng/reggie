package com.Lijiacheng.config;

import ch.qos.logback.classic.pattern.MessageConverter;
import com.Lijiacheng.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 拦截器类
 * */
@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 扩展mvc框架的消息转换器: springboot自己的消息转换器可以把返回的Result对象序列化成json数据响应给前端，而我们可以根据自己的需要扩展消息转换器
     * (方法在项目启动时调用)
     * */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器。。。");
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置对象转换器，底层使用jackson将Java对象转换成json数据
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0, messageConverter);
    }
}
