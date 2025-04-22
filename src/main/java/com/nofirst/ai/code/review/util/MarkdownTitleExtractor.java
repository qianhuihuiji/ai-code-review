package com.nofirst.ai.code.review.util;

import java.util.List;

public class MarkdownTitleExtractor {


    public static void main(String[] args) {
        String markdown = "# Code Review Report\n" +
                "\n" +
                "## 1. 问题描述与优化建议\n" +
                "\n" +
                "### 功能实现正确性与健壮性\n" +
                "1. **线程池资源管理**  \n" +
                "   `RingBufferConfig` 中通过 `Executors.newFixedThreadPool(2)` 创建线程池但未提供关闭机制，可能导致线程泄漏。  \n" +
                "   **建议**: 使用 Spring 管理的 `ThreadPoolTaskExecutor` 并实现 `DisposableBean` 接口确保资源回收。\n" +
                "\n" +
                "2. **异常处理不足**  \n" +
                "   `PushEventReviewService.getCompareResults()` 中捕获 `GitLabApiException` 后直接抛出 `RuntimeException`，丢失原始上下文。  \n" +
                "   **建议**: 定义业务异常类并实现统一异常处理器，保留堆栈信息。\n" +
                "\n" +
                "### 安全性与潜在风险\n" +
                "1. **敏感信息暴露**  \n" +
                "   `DingTalkConfiguration` 的 webhook 地址未加密存储于配置文件中。  \n" +
                "   **建议**: 使用 Jasypt 或 Vault 对敏感配置进行加密。\n" +
                "\n" +
                "2. **SQL 注入防护**  \n" +
                "   MyBatis-Plus 使用中未明确禁用 `${}` 原生字符串替换，存在潜在风险。  \n" +
                "   **建议**: 在全局配置中设置 `sql-injector: com.baomidou.mybatisplus.core.injector.DefaultSqlInjector` 并禁用非必要方法。\n" +
                "\n" +
                "### 最佳实践\n" +
                "1. **注解冗余**  \n" +
                "   `DisruptorService` 类同时使用 `@Component` 和 `@Service` 注解。  \n" +
                "   **建议**: 保留单一 `@Service` 注解以符合 Spring 标准注解规范。\n" +
                "\n" +
                "2. **配置管理**  \n" +
                "   数据库连接信息硬编码在 `application.yml`。  \n" +
                "   **建议**: 拆分为 `application-{env}.yml` 多环境配置文件。\n" +
                "\n" +
                "### 性能与资源利用\n" +
                "1. **等待策略选择**  \n" +
                "   Disruptor 使用 `BlockingWaitStrategy` 在低延迟场景下可能产生较高 CPU 负载。  \n" +
                "   **建议**: 根据业务场景评估 `LiteBlockingWaitStrategy` 或 `TimeoutBlockingWaitStrategy`。\n" +
                "\n" +
                "### 提交信息规范\n" +
                "1. **冗余提交信息**  \n" +
                "   存在重复提交记录：\"接入Disruptor\" 和 \"update application.yml\"。  \n" +
                "   **建议**: 使用 `git commit --amend` 合并相似提交。\n" +
                "\n" +
                "## 2. 评分明细\n" +
                "\n" +
                "| 评分维度                | 得分 | 扣分说明 |\n" +
                "|-------------------------|------|----------|\n" +
                "| 功能正确性与健壮性      | 35   | 线程池管理缺陷(-3), 异常处理不足(-2) |\n" +
                "| 安全性与潜在风险        | 25   | 敏感信息未加密(-3), SQL注入防护不足(-2) |\n" +
                "| 最佳实践                | 18   | 注解冗余(-1), 配置管理不足(-1) |\n" +
                "| 性能与资源利用          | 5    | 等待策略选择适当 |\n" +
                "| 提交信息规范            | 4    | 存在重复提交(-1) |\n" +
                "\n" +
                "## 3. 总分\n" +
                "总分:87分\n" +
                "\n" +
                "> 关键改进点说明：  \n" +
                "> 1. 线程池管理采用 Spring 生命周期管理可显著提升系统稳定性  \n" +
                "> 2. 敏感信息加密是金融级应用的基本安全要求  \n" +
                "> 3. 异常处理体系的完善将大幅提升问题排查效率  \n" +
                "> 4. 等待策略优化预计可降低 15%-20% 的 99th 百分位延迟";

        MarkdownParser parser = new MarkdownParser();
        List<Section> sections = parser.parse(markdown);

        // 输出解析结果
        for (Section section : sections) {
            System.out.println("### " + section.getTitle());
            for (ContentItem item : section.getContents()) {
                System.out.println("- Issue: " + item.getIssue());
                System.out.println("  Suggestion: " + item.getSuggestion());
            }
        }
    }
}
