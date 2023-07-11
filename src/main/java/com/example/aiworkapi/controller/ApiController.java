package com.example.aiworkapi.controller;

import ch.qos.logback.core.util.FileUtil;
import com.example.aiworkapi.common.AiManager;
import com.example.aiworkapi.common.Constants;
import com.example.aiworkapi.model.BaseResponse;
import com.example.aiworkapi.model.chart.ChartResponse;
import com.example.aiworkapi.model.chart.GenChart;
import com.example.aiworkapi.model.chat.GenChat;
import com.example.aiworkapi.util.ResultUtils;
import com.yupi.yucongming.dev.common.ErrorCode;
import javafx.scene.chart.Chart;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static com.example.aiworkapi.common.Constants.BI_MODEL_ID;
import static com.example.aiworkapi.common.Constants.CHAT_ID;

/**
 * @author 86147
 * create  7/7/2023 下午8:16
 */
@RestController
public class ApiController {

    @Resource
    private AiManager aiManager;


    /**
     * 智能分析（同步）
     *
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/chart")
    public BaseResponse<ChartResponse> genChartByAi(GenChart genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String csv = genChartByAiRequest.getCsv();
        String chartType = genChartByAiRequest.getChartType();
        // 校验
        if (StringUtils.isBlank(goal)) return ResultUtils.error("没有目标");
        if (StringUtils.isNotBlank(name) && name.length() > 100) return ResultUtils.error("名称过长");
        final long ONE_MB = 1024 * 1024L;
        long biModelId = BI_MODEL_ID;
        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        // 压缩后的数据
        userInput.append(csv).append("\n");

        String result = aiManager.doChat(biModelId, userInput.toString());
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            return ResultUtils.error("ai生成失败");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        ChartResponse chartResponse = new ChartResponse();
        chartResponse.setGenChart(genChart);
        chartResponse.setGenResult(genResult);
        return ResultUtils.success(chartResponse);
    }

    @PostMapping("/chat")
    public BaseResponse<String> genChatByAi(@RequestBody GenChat genChat, HttpServletRequest request) {
        System.out.println(genChat.getQuestion());
        String question = genChat.getQuestion();
        String answer = aiManager.doChat(CHAT_ID, question);
        return ResultUtils.success(answer);
    }

}
