package com.nofirst.ai.code.review.util;

import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParser {

    public List<Section> parse(String markdown) {
        List<Section> sections = new ArrayList<>();
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        Formatter formatter = Formatter.builder().build();

        Heading currentHeading = null;
        List<Node> currentContentNodes = new ArrayList<>();

        // 遍历文档节点
        for (Node node = document.getFirstChild(); node != null; node = node.getNext()) {
            if (node instanceof Heading) {
                Heading heading = (Heading) node;
                if (heading.getLevel() == 3) {
                    // 处理之前收集的内容
                    if (currentHeading != null) {
                        sections.add(createSection(currentHeading, currentContentNodes, formatter));
                    }
                    // 开始新标题
                    currentHeading = heading;
                    currentContentNodes = new ArrayList<>();
                } else if (currentHeading != null) {
                    // 遇到更高级标题，结束当前收集
                    sections.add(createSection(currentHeading, currentContentNodes, formatter));
                    currentHeading = null;
                    currentContentNodes = new ArrayList<>();
                }
            } else if (currentHeading != null) {
                currentContentNodes.add(node);
            }
        }

        // 处理最后一个标题
        if (currentHeading != null) {
            sections.add(createSection(currentHeading, currentContentNodes, formatter));
        }

        return sections;
    }

    private Section createSection(Heading heading, List<Node> contentNodes, Formatter formatter) {
        Section section = new Section();
        section.setTitle(heading.getText().toString());

        List<ContentItem> contentItems = new ArrayList<>();
        for (Node node : contentNodes) {
            if (node instanceof BulletList) {
                BulletList list = (BulletList) node;
                for (Node listItem : list.getChildren()) {
                    String itemMarkdown = formatter.render(listItem);
                    contentItems.add(parseContentItem(itemMarkdown));
                }
            }
        }

        section.setContents(contentItems);
        return section;
    }

    private ContentItem parseContentItem(String markdown) {
        ContentItem item = new ContentItem();
        // 移除列表编号前缀（如 "1. "）
        String cleaned = markdown.replaceFirst("^\\d+\\.\\s*", "");
        // 正则匹配问题和建议
        Pattern pattern = Pattern.compile("(?s)(.*?)\\*\\*建议\\*\\*\\s*:(.*)");
        Matcher matcher = pattern.matcher(cleaned);
        if (matcher.find()) {
            item.setIssue(matcher.group(1).trim());
            item.setSuggestion("**建议**: " + matcher.group(2).trim());
        } else {
            item.setIssue(cleaned.trim());
            item.setSuggestion("");
        }
        return item;
    }
}
