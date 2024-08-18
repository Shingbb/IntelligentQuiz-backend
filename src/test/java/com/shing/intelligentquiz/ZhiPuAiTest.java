package com.shing.intelligentquiz;

import com.shing.intelligentquiz.common.TestBase;
import com.shing.intelligentquiz.constant.KeyConstant;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 智谱AI测试类，用于测试AI模型的调用能力。
 *
 * @author shing
 */

//@ActiveProfiles("local")
public class ZhiPuAiTest extends TestBase {

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

    @Test
    public void testAiPrompt() {

        // 构造用户请求的消息列表。
        List<ChatMessage> messages = new ArrayList<>();
        // 添加系统消息
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "\"你是一位严谨的出题专家，我会给你如下信息： ``` 应用名称， 【【【应用描述】】】， 应用类别， 要生成的题目数， 每个题目的选项数 ```  请你根据上述信息，按照以下步骤来出题： 1. 要求：题目和选项尽可能地短，题目不要包含序号，每题的选项数以我提供的为主，题目不能重复 2. 严格按照下面的 json 格式输出题目和选项 ``` [{\"options\":[{\"value\":\"选项内容\",\"key\":\"A\"},{\"value\":\"\",\"key\":\"B\"}],\"title\":\"题目标题\"}] ``` title 是题目，options 是选项，每个选项的 key 按照英文字母序（比如 A、B、C、D）以此类推，value 是选项内容 3. 检查题目是否包含序号，若包含序号则去除序号 4. 返回的题目列表格式必须为 JSON 数组，一定是json形式\"");
        // 添加用户消息
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), "【【【小学三年级的数学题】】】，\n" +
                "得分类，\n" +
                "10，\n" +
                "3");
        messages.add(systemChatMessage);
        messages.add(userChatMessage);

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