## 项目简介

本项目是一个基于大模型的自动化代码审查工具，帮助开发团队在代码合并或提交时，快速进行智能化的审查(Code Review)，提升代码质量和开发效率。

## 功能

- 多模型支持
    - 兼容 DeepSeek、SiliconFlow、Ollama等
- 消息即时推送
    - 审查结果一键直达 钉钉

## 原理

当开发人员在 GitLab 上提交代码时，GitLab 将自动触发 webhook 事件（如 Merge Request、Push 等），将时间消息推送到本系统。
系统随后通过第三方大模型对代码进行审查，并将审查结果添加到 Gitlab 的 Note 中，便于查看和处理。

## 启动项目

### 环境准备

- jdk 1.8
- Maven
- Postgres 数据库（如使用MySQL或其他数据库，可自行添加对应依赖，并修改数据库连接配置）

### 数据库初始化

### 修改配置

在`application.yml`文件中修改对应配置，然后运行代码即可

## 参考资料

- [deepseek4j简介](https://javaai.pig4cloud.com/deepseek)
- [机器人回复/发送消息](https://open.dingtalk.com/document/orgapp/robot-reply-and-send-messages)
- [gitlab4j-api](https://github.com/gitlab4j/gitlab4j-api)

## 待办

- 超时重试机制
- 文件类型过滤
- 定时日报统计
- review 具体到行数，方便定位
- 支持review工程