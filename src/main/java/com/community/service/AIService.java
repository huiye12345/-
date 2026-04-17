package com.community.service;

import com.community.config.AIConfig;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AI 服务类
 * 提供与硅基流动 AI 大模型的交互功能
 */
@Service
public class AIService {
    
    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    @Autowired
    private AIConfig aiConfig;

    private OkHttpClient httpClient;
    private Gson gson;

    @PostConstruct
    public void init() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(aiConfig.getTimeout(), TimeUnit.SECONDS)
                .readTimeout(aiConfig.getTimeout(), TimeUnit.SECONDS)
                .writeTimeout(aiConfig.getTimeout(), TimeUnit.SECONDS)
                .build();
        gson = new Gson();
    }

    /**
     * 发送对话请求
     *
     * @param messages 对话消息列表
     * @return AI 响应内容
     */
    public String chat(List<Map<String, String>> messages) {
        if (!aiConfig.isEnabled()) {
            return "AI 功能已禁用";
        }

        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", aiConfig.getDefaultModel());
            requestBody.addProperty("max_tokens", aiConfig.getMaxTokens());
            requestBody.addProperty("temperature", aiConfig.getTemperature());

            JsonArray messagesArray = new JsonArray();
            for (Map<String, String> message : messages) {
                JsonObject msg = new JsonObject();
                msg.addProperty("role", message.get("role"));
                msg.addProperty("content", message.get("content"));
                messagesArray.add(msg);
            }
            requestBody.add("messages", messagesArray);

            Request request = new Request.Builder()
                    .url(aiConfig.getBaseUrl() + "/chat/completions")
                    .header("Authorization", "Bearer " + aiConfig.getApiKey())
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "无错误详情";
                    log.error("AI API 请求失败: {}, 错误详情: {}", response.code(), errorBody);
                    return "AI 服务暂时不可用，请稍后重试";
                }

                String responseBody = response.body().string();
                JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

                if (jsonResponse.has("choices") && jsonResponse.getAsJsonArray("choices").size() > 0) {
                    JsonObject choice = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject();
                    JsonObject message = choice.getAsJsonObject("message");
                    return message.get("content").getAsString();
                }

                return "AI 响应解析失败";
            }
        } catch (IOException e) {
            log.error("AI 请求异常", e);
            return "网络请求失败，请检查网络连接";
        }
    }

    /**
     * 单轮对话
     *
     * @param prompt 用户输入
     * @return AI 响应
     */
    public String chat(String prompt) {
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        return chat(messages);
    }

    /**
     * 带系统提示的对话
     *
     * @param systemPrompt 系统提示
     * @param userPrompt   用户输入
     * @return AI 响应
     */
    public String chatWithSystem(String systemPrompt, String userPrompt) {
        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);
        messages.add(userMessage);

        return chat(messages);
    }

    // ==================== 业务功能方法 ====================

    /**
     * 智能客服 - 回答志愿服务相关问题
     */
    public String customerService(String question) {
        String systemPrompt = "你是社区志愿服务管理系统的智能助手。请用友好、专业的语气回答用户问题。" +
                "系统有三类用户：管理员（管理用户和活动）、志愿者（参与服务）、特殊人群（接受帮助）。" +
                "如果问题超出范围，请礼貌地引导用户联系人工客服。";
        return chatWithSystem(systemPrompt, question);
    }
    
    /**
     * 管理员专属 - 数据分析和决策支持
     */
    public String adminAssistant(String question, String systemData) {
        String systemPrompt = "你是社区志愿服务管理系统的AI数据分析专家，专门为管理员提供服务。\n\n" +
                "【项目背景】\n" +
                "- 系统名称：社区志愿服务管理系统\n" +
                "- 服务对象：特殊人群（老年人、残障人士等）和志愿者\n" +
                "- 核心功能：用户管理、公益活动、积分商城、紧急求助、AI助手\n\n" +
                "【当前系统数据】\n" + systemData + "\n\n" +
                "【你的职责】\n" +
                "1. 数据分析：分析志愿服务趋势、用户活跃度、活动效果\n" +
                "2. 决策建议：提供活动规划、资源配置、改进建议\n" +
                "3. 问题诊断：发现系统运营中的问题和异常\n" +
                "4. 报告生成：生成专业的数据报告和总结\n\n" +
                "请用专业、清晰的语气回答，必要时提供具体的数据支撑和行动建议。";
        return chatWithSystem(systemPrompt, question);
    }
    
    /**
     * 志愿者专属 - 安全培训和心理辅导
     */
    public String volunteerAssistant(String question) {
        String systemPrompt = "你是社区志愿服务系统的志愿者专属AI助手，提供安全培训和心理辅导服务。\n\n" +
                "【安全培训内容】\n" +
                "1. 服务前准备：了解服务对象、准备必要物品、确认安全环境\n" +
                "2. 服务中注意：保护个人隐私、避免单独行动、注意交通安全\n" +
                "3. 紧急情况处理：遇到突发状况的应对流程、紧急联系人、求助方式\n" +
                "4. 特殊人群服务技巧：与老年人沟通、协助残障人士、尊重隐私\n\n" +
                "【心理辅导内容】\n" +
                "1. 情绪支持：倾听志愿者的困惑和压力，给予鼓励和建议\n" +
                "2. 挫折处理：帮助应对服务中的困难和负面情绪\n" +
                "3. 动力激励：分享志愿服务的意义，提升参与积极性\n" +
                "4. 自我关怀：提醒注意休息，避免过度疲劳\n\n" +
                "请用温暖、专业的语气，像一位经验丰富的前辈志愿者一样提供帮助。";
        return chatWithSystem(systemPrompt, question);
    }
    
    /**
     * 特殊人群专属 - 健康科普和生活指导
     */
    public String specialUserAssistant(String question) {
        String systemPrompt = "你是社区志愿服务系统的健康科普助手，专门为特殊人群提供健康和生活指导。\n\n" +
                "【健康科普内容】\n" +
                "1. 日常保健：合理饮食、适度运动、作息规律\n" +
                "2. 疾病预防：常见疾病的预防知识、早期症状识别\n" +
                "3. 用药指导：用药注意事项、药物相互作用提醒\n" +
                "4. 康复训练：适合的运动方式、康复锻炼建议\n\n" +
                "【生活指导内容】\n" +
                "1. 居家安全：防跌倒、用电安全、紧急求助方法\n" +
                "2. 心理调适：保持乐观心态、社交建议、情绪管理\n" +
                "3. 智能设备使用：手机操作、紧急呼叫、视频通话\n" +
                "4. 社区资源：可用的社区服务、志愿者帮助、活动信息\n\n" +
                "【重要提醒】\n" +
                "- 你的建议仅供参考，不能替代专业医疗建议\n" +
                "- 如有严重健康问题，务必及时就医\n" +
                "- 紧急情况请立即拨打120或联系社区志愿者\n\n" +
                "请用亲切、易懂的语气，像一位耐心的社区工作者一样提供帮助。";
        return chatWithSystem(systemPrompt, question);
    }

    /**
     * 生成活动描述
     */
    public String generateActivityDescription(String title, String location, String target) {
        String prompt = String.format(
                "请为以下公益活动生成一段吸引人的活动描述（100-200字）：\n" +
                        "活动名称：%s\n" +
                        "活动地点：%s\n" +
                        "服务对象：%s",
                title, location, target
        );
        String systemPrompt = "你是社区志愿服务活动的文案策划专家。请生成生动、感人的活动描述，突出活动的意义和价值。";
        return chatWithSystem(systemPrompt, prompt);
    }

    /**
     * 生成服务评价回复
     */
    public String generateReply(String evaluation) {
        String prompt = "请根据以下服务评价生成一段感谢回复：\n" + evaluation;
        String systemPrompt = "你是社区志愿服务管理系统的客服人员。请生成真诚、温暖的感谢回复。";
        return chatWithSystem(systemPrompt, prompt);
    }

    /**
     * 紧急求助分析
     */
    public String analyzeEmergency(String emergencyInfo) {
        String prompt = "请分析以下紧急求助信息，给出处理建议：\n" + emergencyInfo;
        String systemPrompt = "你是社区紧急救助专家。请分析紧急情况的严重程度，并给出具体的处理建议。" +
                "分析应包括：1.紧急程度评估 2.建议采取的措施 3.需要注意的事项";
        return chatWithSystem(systemPrompt, prompt);
    }

    /**
     * 志愿者技能匹配建议
     */
    public String matchVolunteerSkills(String volunteerSkills, String requestNeeds) {
        String prompt = String.format(
                "志愿者技能：%s\n服务需求：%s\n请分析匹配度并给出建议。",
                volunteerSkills, requestNeeds
        );
        String systemPrompt = "你是社区志愿服务匹配专家。请分析志愿者技能与服务需求的匹配程度，给出匹配评分和建议。";
        return chatWithSystem(systemPrompt, prompt);
    }

    /**
     * 生成数据分析报告
     */
    public String generateDataReport(String dataSummary) {
        String prompt = "请根据以下志愿服务数据生成分析报告：\n" + dataSummary;
        String systemPrompt = "你是社区志愿服务数据分析专家。请生成专业的数据分析报告，包括趋势分析、问题发现和建议。";
        return chatWithSystem(systemPrompt, prompt);
    }

    /**
     * 智能推荐活动
     */
    public String recommendActivities(String userProfile, String availableActivities) {
        String prompt = String.format(
                "用户画像：%s\n可选活动：%s\n请推荐最适合的活动并说明理由。",
                userProfile, availableActivities
        );
        String systemPrompt = "你是社区志愿服务推荐专家。请根据用户特点推荐最合适的活动，并解释推荐理由。";
        return chatWithSystem(systemPrompt, prompt);
    }
}
