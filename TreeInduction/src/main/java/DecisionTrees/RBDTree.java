package DecisionTrees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import tools.rules.RBDTRule;

public class RBDTree {

    @Getter
    private Node root;

    // Constructor
    public RBDTree(List<RBDTRule> CR) {
        this.root = buildDecisionTree(CR);
    }

    // Helper method to recursively build the decision tree
    private Node buildDecisionTree(List<RBDTRule> CR) {
        if (CR.isEmpty()) {
            return new Node(getMostFrequentClass(CR)); // Leaf node with most frequent class
        }

        int fitAttribute = selectFitAttribute(CR);

        Node node = new Node("A_" + fitAttribute);

        // Create branches for each value of the fit attribute
        Set<String> attributeValues = getAttributeValues(CR, fitAttribute);
        for (String value : attributeValues) {
            List<RBDTRule> RR = getSubsetRules(CR, fitAttribute, value);

            if (RR.isEmpty()) {
                node.branches.put(value, new Node(getMostFrequentClass(CR))); // Leaf node with most frequent class
            } else if (allRulesBelongToSameClass(RR)) {
                node.branches.put(value, new Node(RR.get(0).getY())); // Leaf node with decision class
            } else {
                node.branches.put(value, buildDecisionTree(RR)); // Recursively build subtree
            }
        }

        return node;
    }

    // Method to get the most frequent class in the rules
    private String getMostFrequentClass(List<RBDTRule> rules) {
        Map<String, Integer> classCounts = new HashMap<>();

        // Count occurrences of each class
        for (RBDTRule rule : rules) {
            String currentClass = rule.getY();
            classCounts.put(currentClass, classCounts.getOrDefault(currentClass, 0) + 1);
        }

        // Find the class with the highest frequency
        int maxFrequency = 0;
        String mostFrequentClass = null;
        for (Map.Entry<String, Integer> entry : classCounts.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mostFrequentClass = entry.getKey();
            }
        }

        return mostFrequentClass;
    }

    // Method to select the fit attribute based on attribute selection criteria
    private int selectFitAttribute(List<RBDTRule> rules) {
        // Implementation of selecting the fit attribute
        return 0; // Placeholder, implement your logic here
    }

    // Method to get the unique values of an attribute in the rules
    private Set<String> getAttributeValues(List<RBDTRule> rules, int A_j) {
        Set<String> attributeValues = new HashSet<>();

        // Collect unique values of the specified attribute
        for (RBDTRule rule : rules) {
            String attributeValue = rule.getAntecedents()[A_j];
            attributeValues.add(attributeValue);
        }

        return attributeValues;
    }

    // Method to get the subset of rules with a specific attribute value
    private List<RBDTRule> getSubsetRules(List<RBDTRule> rules, int A_j, String value) {
        List<RBDTRule> subsetRules = new ArrayList<>();

        // Filter rules based on the specific attribute value
        for (RBDTRule rule : rules) {
            if (rule.getAntecedents()[A_j].equals(value)) {
                subsetRules.add(rule);
            }
        }

        return subsetRules;
    }

    // Method to check if all rules belong to the same decision class
    private boolean allRulesBelongToSameClass(List<RBDTRule> rules) {
        if (rules.isEmpty()) {
            return true; // If there are no rules, they technically all belong to the same class
        }

        // Get the decision class of the first rule
        String decisionClass = rules.get(0).getY();

        // Check if all rules have the same decision class
        for (RBDTRule rule : rules) {
            if (!rule.getY().equals(decisionClass)) {
                return false; // If any rule has a different class, return false
            }
        }

        return true; // If all rules have the same class, return true
    }
}
