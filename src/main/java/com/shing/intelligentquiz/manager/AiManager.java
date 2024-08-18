package com.shing.intelligentquiz.manager;

import com.shing.intelligentquiz.common.ErrorCode;
import com.shing.intelligentquiz.exception.BusinessException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用 AI调用类封装SDK
 *
 * @author shing
 */
@Component
public class AiManager {

    /**
     * V4版本的客户端接口，用于调用AI模型接口。
     */
    @Resource
    private ClientV4 clientV4;

    // 默认是0.95，小于0.95的随机数比较稳定，大于0.95的随机数比较不稳定
    // 稳定的随机数
    private static final float STABLE_TEMPERATURE = 0.05f;

    // 不稳定的随机数
    private static final float UNSTABLE_TEMPERATURE = 0.99f;


    /**
     * 同步请求（答案不稳定）
     *
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncUnstableRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, UNSTABLE_TEMPERATURE);
    }

    /**
     * 同步请求（答案较稳定）
     *
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncStableRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, STABLE_TEMPERATURE);
    }

    /**
     * 同步请求
     *
     * @param systemMessage
     * @param userMessage
     * @param temperature
     * @return
     */
    public String doSyncRequest(String systemMessage, String userMessage, Float temperature) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, temperature);
    }

    /**
     * 通用请求（简化消息传递）
     *
     * @param systemMessage 系统消息内容。
     * @param userMessage   用户消息内容。
     * @param stream        是否启用流式处理。
     * @param temperature   渲染结果的随机性温度。
     * @return AI生成的回复内容。
     */
    public String doRequest(String systemMessage, String userMessage, Boolean stream, Float temperature) {
        // 构造请求消息列表
        List<ChatMessage> messages = new ArrayList<>();
        // 添加系统消息
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        // 添加用户消息
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        messages.add(systemChatMessage);
        messages.add(userChatMessage);
        // 调用doRequest方法发起请求
        return doRequest(messages, stream, temperature);
    }

    /**
     * 通用请求
     *
     * @param messages    消息列表，包含系统和用户消息。
     * @param stream      是否启用流式处理。
     * @param temperature 渲染结果的随机性温度。
     * @return AI生成的回复内容。
     */
    public String doRequest(List<ChatMessage> messages, Boolean stream, Float temperature) {
        // 构造AI请求对象
        // 构造请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(stream)
                .invokeMethod(Constants.invokeMethod)
                .temperature(temperature)
                .messages(messages)
                .build();
        try {
            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            return invokeModelApiResp.getData().getChoices().get(0).toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }

    }

    /**
     * 通用请求（简化消息传递）
     *
     * @param systemMessage 系统消息内容。
     * @param userMessage   用户消息内容。
     * @param temperature   渲染结果的随机性温度。
     * @return AI生成的回复内容。
     */
    public Flowable<ModelData> doStreamRequest(String systemMessage, String userMessage, Float temperature) {
        // 构造请求消息列表
        List<ChatMessage> chatMessageList = new ArrayList<>();
        // 添加系统消息
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        // 添加用户消息
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        chatMessageList.add(systemChatMessage);
        chatMessageList.add(userChatMessage);
        // 调用doRequest方法发起请求
        return doStreamRequest(chatMessageList, temperature);
    }

    /**
     * 通用请求
     *
     * @param messages    消息列表，包含系统和用户消息。
     * @param temperature 渲染结果的随机性温度。
     * @return AI生成的回复内容。
     */
    public Flowable<ModelData> doStreamRequest(List<ChatMessage> messages, Float temperature) {
        // 构造AI请求对象
        // 构造请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .invokeMethod(Constants.invokeMethod)
                .temperature(temperature)
                .messages(messages)
                .build();
        try {
            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            return invokeModelApiResp.getFlowable();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }

    }
}
