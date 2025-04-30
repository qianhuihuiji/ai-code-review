package com.nofirst.ai.code.review.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nofirst.ai.code.review.model.chat.EvaluationReport;
import com.nofirst.ai.code.review.model.chat.Issue;
import com.nofirst.ai.code.review.model.chat.Result;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        md.append("# Auto Code Review Result\n\n**总分**: ").append(report.getTotalScore())
                .append(" **满分**: 100 \n\n")
                .append("**生成时间**: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                .append("\n\n---\n\n");

        int sectionIndex = 1; // 评分项序号从1开始
        for (Result result : report.getResults()) {
            // 带序号的评分项标题
            md.append("## ").append(sectionIndex++).append(". ").append(result.getDimension())
                    .append("  \n**得分**: ").append(result.getScore()).append("/").append(result.getFullScore())
                    .append("\n\n");

            // 问题列表处理
            if (result.getIssues().isEmpty()) {
                md.append("### ✅ 未发现问题\n");
            } else {
                md.append("### ❗ 问题列表 (").append(result.getIssues().size()).append("项)\n");
                for (Issue issue : result.getIssues()) {
                    // 根据严重等级应用样式
                    String severityStyle = getSeverityStyle(issue.getSeverity());

                    md.append("- **").append(severityStyle).append(issue.getTitle()).append("**") // 标题带样式
                            .append("  \n  ▸ **文件**: `").append(issue.getFile()).append("`")  // 代码块显示路径
                            .append("  \n  ▸ **严重性**: ").append(severityStyle).append("  ") // 等级样式
                            .append("  \n  ▸ **详情**: ").append(issue.getDetail())
                            .append("  \n  ▸ **建议**: ").append(issue.getAdvice().replace(";", ";\n    ")) // 建议换行处理
                            .append("\n\n");
                }
            }

            md.append("\n---\n\n");
        }
        return md.toString();
    }

    private static String getSeverityStyle(String severity) {
        switch (severity.toUpperCase()) {
            case "HIGH":
                return "<span style='color:red; font-weight:600'>"; // 红色加粗
            case "MEDIUM":
                return "<span style='color:#FFA500; font-weight:500'>"; // 橙色
            case "LOW":
                return "<span style='color:#6c757d'>"; // 灰色
            default:
                return "<span style='color:#6c757d'>"; // 灰色
        }
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
