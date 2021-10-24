package me.jonasknobloch.msrpted;

import at.unisalzburg.dbresearch.apted.node.Node;
import at.unisalzburg.dbresearch.apted.node.StringNodeData;
import at.unisalzburg.dbresearch.apted.parser.InputParser;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PennInputParser implements InputParser<StringNodeData> {
    public PennInputParser() {}

    @Override
    public Node<StringNodeData> fromString(String s) {
        Pattern pattern = Pattern.compile("\\(\s*([^\s()]+)?|\\)|([^\s()]+)");
        Matcher matcher = pattern.matcher(s);

        Stack<Node<StringNodeData>> stack = new Stack<>();

        while (matcher.find()) {
            String match = matcher.group();

            switch (match.charAt(0)) {
                case '(' -> stack.push(newNodeWithLabel(match.substring(1).trim()));
                case ')' -> {
                    if (stack.size() == 1) {
                        continue;
                    }
                    Node<StringNodeData> last = stack.pop();
                    stack.lastElement().addChild(last);
                }
                default -> stack.lastElement().addChild(newNodeWithLabel(match.trim()));
            }
        }

        if (stack.size() > 1) {
            // TODO error
        }

        return stack.lastElement();
    }

    private static Node<StringNodeData> newNodeWithLabel(String label) {
        StringNodeData data = new StringNodeData(label);
        return new Node<>(data);
    }
}
