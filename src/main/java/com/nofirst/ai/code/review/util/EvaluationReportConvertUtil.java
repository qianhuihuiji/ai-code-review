package com.nofirst.ai.code.review.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nofirst.ai.code.review.model.chat.EvaluationReport;
import com.nofirst.ai.code.review.model.chat.Question;
import com.nofirst.ai.code.review.model.chat.Result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Evaluation report convert util.
 */
public class EvaluationReportConvertUtil {

    /**
     * 将 EvaluationReport 对象转成 markdown 格式
     *
     * @param report the report
     * @return the string
     */
    public static String convertToMarkdown(EvaluationReport report) {
        StringBuilder md = new StringBuilder();
        md.append("# Auto Code Review Result\n\n**总分**: ").append(report.getTotalScore()).append(" 满分：100\n\n---\n\n");

        int sectionIndex = 1; // 评分项序号从1开始
        for (Result result : report.getResults()) {
            // 带序号的评分项标题
            md.append("## ")
                    .append(sectionIndex++)
                    .append(". ")
                    .append(result.getType())
                    .append("\n\n**得分**: ")
                    .append(result.getScore())
                    .append(" **该项满分**: ")
                    .append(result.getFullScore())
                    .append("\n\n");

            // 问题列表处理
            md.append("### 问题列表\n");
            if (result.getQuestions().isEmpty()) {
                md.append("暂无\n");
            } else {
                for (Question question : result.getQuestions()) {
                    md.append("- **")
                            .append(question.getTitle())
                            .append("**  \n  ")  // 两个空格实现Markdown换行
                            .append(question.getContent())
                            .append("\n");
                }
            }

            // 改进建议处理
            md.append("\n### 改进建议\n");
            if (result.getAdvices().isEmpty()) {
                md.append("暂无\n");
            } else {
                int adviceIndex = 1;
                for (String advice : result.getAdvices()) {
                    md.append(adviceIndex++)
                            .append(". ")
                            .append(advice)
                            .append("\n");
                }
            }
            md.append("\n---\n\n");
        }
        return md.toString();
    }

    /**
     * 将chat返回的消息转成 EvaluationReport 对象
     *
     * @param chatContent the chatContent
     * @return the evaluation report
     */
    public static EvaluationReport convertFromChatContent(String chatContent) {
        String pureJson = pureJson(chatContent);
        EvaluationReport report;
        ObjectMapper mapper = new ObjectMapper();
        try {
            report = mapper.readValue(pureJson, EvaluationReport.class);

            String markdownContent = EvaluationReportConvertUtil.convertToMarkdown(report);
            report.setMarkdownContent(markdownContent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return report;
    }

    /**
     * chat返回的格式为 :```json ..... ```
     * 此方法提取出json文本
     *
     * @param chatContent the review result
     * @return the string
     */
    private static String pureJson(String chatContent) {
        // 定义正则表达式，匹配 ```json 包裹的JSON内容（支持多行）
        String regex = "```json\\s*(.*?)\\s*```";
        // DOTALL模式匹配换行符
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(chatContent);

        if (matcher.find()) {
            // 提取第一个捕获组并去除首尾空白
            return matcher.group(1).trim();
        }
        return null;
    }
}
