package com.shing.intelligentquiz.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * AI配置类，用于管理与AI相关的配置项。
 * 通过@ConfigurationProperties注解，将配置文件中以"ai"为前缀的属性绑定到此类的字段上。
 *
 * @author shing
 */
@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfig {

    /**
     * AI平台的API密钥，用于身份验证和访问API。
     * 请从AI开放平台获取
     */
    private String apiKey;

    /**
     * 创建并返回一个ClientV4实例，用于与AI平台进行通信。
     * 使用apiKey初始化Builder，然后构建ClientV4实例。
     *
     * @return ClientV4实例，用于AI相关的操作。
     */
    @Bean
    public ClientV4 getClientV4() {
        return new ClientV4.Builder(apiKey).build();

    }
}

