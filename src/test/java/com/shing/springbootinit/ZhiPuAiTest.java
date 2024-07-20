package com.shing.springbootinit;

import com.shing.springbootinit.constant.KeyConstant;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 智谱AI测试类，用于测试AI模型的调用能力。
 *
 * @author shing
 */
@SpringBootTest(
        // 通过指定MainApplication类作为应用程序的入口点来启动Spring Boot应用上下文
        classes = {MainApplication.class,},
        // 设置web环境为MOCK，表示测试中不启动实际的Web服务器，而是使用Spring Boot的MockWebServer
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        // 指定配置文件路径，允许我们在测试环境中使用不同的配置，例如数据库连接、服务端口等
        properties = {"spring.config.location=classpath:application-local.yml"}
)
// 指定配置文件路径
//@ActiveProfiles("local")
public class ZhiPuAiTest {

    @Resource
    private ClientV4 clientV4;


    /**
     * 测试AI模型的调用方法。
     * 该方法通过构造请求并发送到智谱AI的API，以获取模型的响应。
     */
    @Test
    public void testAi() {

        // 初始化AI客户端，使用预先定义的API密钥。
        ClientV4 client = new ClientV4.Builder(KeyConstant.AI_KEY).build();

        // 构造用户请求的消息列表。
        List<ChatMessage> messages = new ArrayList<>();
        // 添加一条用户请求消息。
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "作为一名项目经理专家，请为iq博士这个ai开放平台创作一个吸引人的slogan");
        messages.add(chatMessage);

        // 生成唯一的请求ID，用于追踪和日志记录。
        String requestId = String.valueOf(System.currentTimeMillis());

        // 构造API请求对象，包含模型类型、消息内容、请求ID等信息。
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .build();

        // 调用AI模型API，发送请求并获取响应。
        // 调用
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        // 打印模型的响应消息。
        System.out.println("model output:" + invokeModelApiResp.getMsg());

    }

    @Test
    public void testAiClient() {

        // 构造用户请求的消息列表。
        List<ChatMessage> messages = new ArrayList<>();
        // 添加一条用户请求消息。
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "作为一名项目经理专家，请为iq博士这个ai开放平台创作一个吸引人的slogan");
        messages.add(chatMessage);

        // 构造API请求对象，包含模型类型、消息内容、请求ID等信息。
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();

        // 调用AI模型API，发送请求并获取响应。
        // 调用
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        // 打印模型的响应消息。
        System.out.println("model output:" + invokeModelApiResp.getMsg());

    }
}
