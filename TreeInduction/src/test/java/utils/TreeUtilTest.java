package utils;

import org.junit.Test;

import DecisionTrees.Node;
import tools.utils.TreeUtil;

public class TreeUtilTest {

    @Test
    public void testPrintToFile() {
        Node root = new Node("Root");
        Node child1 = new Node("Child 1");
        Node child2 = new Node("Child 2");
        root.getBranches().put("A", child1);
        root.getBranches().put("B", child2);
        child1.getBranches().put("C", new Node("Leaf 1"));
        child1.getBranches().put("D", new Node("Leaf 2"));
        child2.getBranches().put("E", new Node("Leaf 3"));

        TreeUtil.printTreeToFile("tree.txt", root);
    }
}
