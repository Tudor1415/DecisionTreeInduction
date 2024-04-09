package DecisionTrees;

import java.util.HashMap;
import java.util.Map;

// A Node class for the decision tree
public class Node {
    String attribute;
    Map<String, Node> branches;
    String decisionClass;

    Node(String attribute) {
        this.attribute = attribute;
        this.branches = new HashMap<>();
        this.decisionClass = null;
    }
}
