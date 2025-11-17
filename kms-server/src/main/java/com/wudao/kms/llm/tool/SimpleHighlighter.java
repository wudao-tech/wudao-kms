package com.wudao.kms.llm.tool;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 最简版高亮工具：传入【检索内容 query】与【检索结果 text】，输出带 <em> 标签的 HTML 字符串。
 * 改进：
 * - 不再要求空格分词，短语和单词都会在文本中匹配（语义检索通常无需边界约束）。
 * - 大小写不敏感。
 * - 仍支持引号短语（"短语"），但普通词也会在长词中匹配。
 */
public final class SimpleHighlighter {

    private SimpleHighlighter() {}

    public static String highlight(String query, String text) {
        return highlight(query, text, "<em>", "</em>");
    }

    public static String highlight(String query, String text, String preTag, String postTag) {
        if (text == null) return "";
        if (query == null || query.trim().isEmpty()) {
            return escapeHtml(text);
        }

        // 解析查询：引号短语 + 普通词
        List<String> needles = parseNeedles(query);

        AtomicReference<String> highlighted = new AtomicReference<>(text);
        // 先短语，后单词
        needles.stream().filter(s -> s.contains(" ")).forEach(n -> highlighted.set(ciReplace(highlighted.get(), n, preTag, postTag)));
        needles.stream().filter(s -> !s.contains(" ")).forEach(n -> highlighted.set(ciReplace(highlighted.get(), n, preTag, postTag)));

        return escapeHtmlExceptTags(highlighted.get(), preTag, postTag);
    }

    private static List<String> parseNeedles(String query) {
        List<String> phrases = new ArrayList<>();
        Matcher m = Pattern.compile("\"([^\"]+)\"").matcher(query);
        while (m.find()) phrases.add(m.group(1).trim());
        String rest = query.replaceAll("\"[^\"]+\"", " ");
        List<String> terms = new ArrayList<>();
        for (String t : rest.split("\\s+")) if (!t.isEmpty()) terms.add(t.trim());
        LinkedHashSet<String> set = new LinkedHashSet<>();
        phrases.forEach(set::add);
        terms.forEach(set::add);
        return new ArrayList<>(set);
    }

    private static String ciReplace(String src, String needle, String preTag, String postTag) {
        if (needle == null || needle.isEmpty() || src.isEmpty()) return src;
        String lowerSrc = src.toLowerCase(Locale.ROOT);
        String lowerNeedle = needle.toLowerCase(Locale.ROOT);
        StringBuilder sb = new StringBuilder();
        int from = 0, idx;
        while ((idx = lowerSrc.indexOf(lowerNeedle, from)) >= 0) {
            sb.append(src, from, idx);
            sb.append(preTag).append(src, idx, idx + needle.length()).append(postTag);
            from = idx + needle.length();
        }
        sb.append(src.substring(from));
        return sb.toString();
    }

    private static String escapeHtml(String s) {
        return s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static String escapeHtmlExceptTags(String s, String pre, String post) {
        final String P1 = "__PRE__";
        final String P2 = "__POST__";
        s = s.replace(pre, P1).replace(post, P2);
        s = escapeHtml(s);
        return s.replace(P1, pre).replace(P2, post);
    }

    public static void main(String[] args) {
        String query = "高亮OpenSearch";
        String text = "OpenSearch的highlight很好用，我们也想在Java里实现类似的高亮片段。";
        System.out.println(SimpleHighlighter.highlight(query, text));
        // 输出：<em>OpenSearch</em>的highlight很好用，我们也想在Java里实现类似的<em>高亮</em>片段。
    }

}

