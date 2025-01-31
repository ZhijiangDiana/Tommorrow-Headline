package com.heima.wemedia.utils;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ACAutomation {

    // 字典树的节点类
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        TrieNode fail;  // 失败指针
        List<String> output = new ArrayList<>();  // 匹配的关键词
    }

    private static TrieNode root;  // 字典树的根节点

    // 初始化字典树并插入所有关键词
    public void reload(List<String> keywords) {
        root = new TrieNode();

        // 构建trie树
        for (String keyword : keywords) {
            TrieNode node = root;
            for (char c : keyword.toCharArray())
                node = node.children.computeIfAbsent(c, k -> new TrieNode());
            node.output.add(keyword);  // 在叶子节点上添加匹配的关键词
        }


        // 构建失败指针
        Queue<TrieNode> queue = new LinkedList<>();
        // 初始化根节点的子节点的失败指针为根节点
        for (TrieNode childNode : root.children.values()) {
            childNode.fail = root;
            queue.offer(childNode);
        }
        // 广度优先遍历构建失败指针
        while (!queue.isEmpty()) {
            TrieNode currentNode = queue.poll();

            for (Map.Entry<Character, TrieNode> entry : currentNode.children.entrySet()) {
                char c = entry.getKey();
                TrieNode childNode = entry.getValue();

                // 寻找失败指针
                TrieNode failNode = currentNode.fail;
                while (failNode != null && !failNode.children.containsKey(c)) failNode = failNode.fail;
                if (failNode != null) childNode.fail = failNode.children.get(c);
                else childNode.fail = root;

                // 合并失败指针的输出
                if (childNode.fail != null) childNode.output.addAll(childNode.fail.output);

                queue.offer(childNode);
            }
        }
    }

    // 查找文本中是否包含关键词
    public List<String> search(String text) {
        List<String> foundKeywords = new ArrayList<>();
        TrieNode node = root;
        
        // 遍历文本中的字符
        for (char c : text.toCharArray()) {
            // 如果当前节点没有匹配的字符，则回溯到失败指针
            while (node != root && !node.children.containsKey(c)) {
                node = node.fail;
            }

            if (node.children.containsKey(c)) {
                node = node.children.get(c);
            }

            // 如果当前节点有匹配的关键词，加入到结果中
            if (!node.output.isEmpty()) {
                foundKeywords.addAll(node.output);
            }
        }

        return foundKeywords;
    }

//    public static void main(String[] args) throws IOException {
//        // 读取敏感词库
////        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\14838\\Desktop\\sensitive_words.txt"));
////        List<String> lines = new ArrayList<>();
////        String line;
////        while ((line = br.readLine()) != null) {
////            lines.add(line);
////        }
////        List<String> words = new ArrayList<>();
////        for (String l : lines) {
////            words.addAll(Arrays.asList(l.split(",")).stream().filter(x -> !x.isEmpty()).collect(Collectors.toList()));
////        }
////        words = words.stream().distinct().filter(x -> x.length() > 1).collect(Collectors.toList());
////        BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\sensitive_words.txt"));
////        words.stream().forEach(x -> {
////            try {
////                bw.write(x);
////                bw.write(',');
////            } catch (IOException e) {
////                throw new RuntimeException(e);
////            }
////        });
//
//        // 输入要检查的文本（包含汉字、字母、标点符号）
//        String text = "看奶龙就风风光光操你妈去";
//
//        // 创建Aho-Corasick自动机
//        List<String> words = Arrays.asList("卧槽", "操你妈", "奶龙");
//        ACAutomation ac = ACAutomation.getInstance(words);
//
//        // 查找并输出匹配的关键词
//        List<String> foundKeywords = ac.search(text);
//        if (foundKeywords.isEmpty()) {
//            System.out.println("没有找到匹配的关键词");
//        } else {
//            System.out.println("匹配的关键词: " + foundKeywords);
//        }
//    }
}
