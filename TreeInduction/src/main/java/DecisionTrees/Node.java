package DecisionTrees;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

// A Node class for the decision tree
@Getter
public class Node {
    private String attribute;
    private Map<String, Node> branches;
    private String decisionClass;

    public Node(String attribute) {
        this.attribute = attribute;
        this.branches = new HashMap<>();
        this.decisionClass = null;
    }
}
