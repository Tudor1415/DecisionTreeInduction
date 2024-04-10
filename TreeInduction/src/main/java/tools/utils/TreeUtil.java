package tools.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import DecisionTrees.Node;

public class TreeUtil {

    public static void printTreeToFile(String filename, Node node) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            printNodeToFile(writer, node, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printNodeToFile(BufferedWriter writer, Node node, int depth) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("  "); // Adjust spacing for indentation
        }
        sb.append(node.getAttribute());
        if (node.getDecisionClass() != null) {
            sb.append(" -> ");
            sb.append(node.getDecisionClass());
        }
        writer.write(sb.toString());
        writer.newLine();

        for (Map.Entry<String, Node> entry : node.getBranches().entrySet()) {
            String branchValue = entry.getKey();
            Node branchNode = entry.getValue();
            writer.write(sb.toString() + " -> " + branchValue);
            writer.newLine();
            printNodeToFile(writer, branchNode, depth + 1);
        }
    }
}
