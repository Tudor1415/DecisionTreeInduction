package DecisionTrees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import tools.rules.RBDTRule;

public class RBDTree {

    @Getter
    private Node root;

    private String[] classesString;

    private Map<String, List<RBDTRule>> rules;

    // For rounding errors
    public static double epsilon = 0.01;

    // Constructor
    public RBDTree(List<RBDTRule> CR) {
        this.classesString = getClassesString(CR);
        this.rules = groupRulesByClass(CR);
        this.root = buildDecisionTree(CR);
    }

    // Groups a list of rules by class
    private Map<String, List<RBDTRule>> groupRulesByClass(List<RBDTRule> CR) {
        Map<String, List<RBDTRule>> rules = new HashMap<>();

        for (RBDTRule rule : CR) {
            rules.computeIfAbsent(rule.getY(), k -> new ArrayList<>()).add(rule);
        }

        return rules;
    }

    // Generates an array of class names from a list of rules
    private String[] getClassesString(List<RBDTRule> CR) {
        return CR.stream()
                .map(RBDTRule::getY)
                .collect(Collectors.toSet())
                .toArray(new String[0]);
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
                node.getBranches().put(value, new Node(getMostFrequentClass(CR))); // Leaf node with most frequent class
            } else if (allRulesBelongToSameClass(RR)) {
                node.getBranches().put(value, new Node(RR.get(0).getY())); // Leaf node with decision class
            } else {
                node.getBranches().put(value, buildDecisionTree(RR)); // Recursively build subtree
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
            String attributeValue = rule.getItemsInX()[A_j];
            attributeValues.add(attributeValue);
        }

        return attributeValues;
    }

    // Method to get the subset of rules with a specific attribute value
    private List<RBDTRule> getSubsetRules(List<RBDTRule> rules, int A_j, String value) {
        List<RBDTRule> subsetRules = new ArrayList<>();

        // Filter rules based on the specific attribute value
        for (RBDTRule rule : rules) {
            if (rule.getItemsInX()[A_j].equals(value)) {
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

    /**
     * Calculates the Attribute Effectiveness (AE) for attribute {@code a_j}.
     * \f[
     * AE(a_j) = \frac{m - \sum_{i=1}^m C_{i,j}(DC)}{m}
     * \f]
     * where:
     * <ul>
     * <li>m is the total number of instances,</li>
     * <li>C_{i,j}(DC) is the count of "don't care" values for attribute
     * {@code a_j} in rules with class value i</li>
     * <li>a_j is the attribute being evaluated for AE.</li>
     * </ul>
     *
     * @param attributeIndex The attribute for which to compute the metric.
     * @param rules          The dataset of rules per class. Each key of the map
     *                       should correspond to a class and each value associated
     *                       with the key should correspond to a list containing all
     *                       the rules that infer the class.
     * @return The Attribute Effectiveness (AE) for attribute {@code a_j}.
     */
    public double attributeEffectiveness(int attributeIndex, HashMap<String, List<RBDTRule>> rules) {
        int m = 0; // Total number of instances
        int DC_count = 0; // Total count of "don't care" values for attribute a_j

        // Calculate m and sum C_{i,j}(DC)
        for (List<RBDTRule> classRules : rules.values()) {
            for (RBDTRule rule : classRules) {
                m++;
                if (rule.getItemsInX()[attributeIndex].equals("DC")) {
                    DC_count++;
                }
            }
        }

        // Calculate AE(a_j)
        double value = (double) (m - DC_count) / m;

        checkMeasure(value, 0, 1, "Attribute Effectiveness");

        return value;
    }

    /**
     * Computes the attribute disjointness given two classes.
     * 
     * @param A_j  The index of the attribute for which to compute the
     *             metric.
     * @param C_i  The name of the first class.
     * @param C_k  The name of the second class.
     * @param R_ji Represents the rule subset {@code R_ji} consisting of the rules
     *             that have {@code a_j} appearing with the value {@code v_ji}.
     * @return The disjointness of the attribute with respect to the two given
     *         classes: C_i and C_k.
     */
    private double attributeDisjointness(int A_j, String C_i, String C_k, List<RBDTRule> R_ji) {
        // The set of values that the attribute A_j can take in rules that infer the
        // class i.
        Set<String> V_ij = R_ji.stream()
                .filter(rule -> rule.getY().equals(C_i))
                .map(rule -> rule.getItemsInX()[A_j])
                .collect(Collectors.toSet());

        // The set of values that the attribute A_j can take in rules that infer the
        // class k.
        Set<String> V_kj = R_ji.stream()
                .filter(rule -> rule.getY().equals(C_k))
                .map(rule -> rule.getItemsInX()[A_j])
                .collect(Collectors.toSet());

        // Computing the intersection of sets V_ij and V_kj
        Set<String> intersection = new HashSet<>(V_ij);
        intersection.retainAll(V_kj);

        // Computing the ADS metric as stated in the paper
        if (V_ij.containsAll(V_kj))
            return 0.0;
        if (V_kj.containsAll(V_ij))
            return 1.0;
        if (intersection.isEmpty())
            return 3.0;

        // In the case where V_ij and V_kj are not disjoint and not a subset of one
        // another
        return 2.0;
    }

    /**
     * The first step in computing the Attribute Autonomy (AA) metric.
     * 
     * @param A_j    The index of the attribute to compute the metric for.
     * @param C_i    The class for which to compute the Max ADS.
     * @param attSet The set of attributes that achieved
     *               the highest (equal) AE score.
     * @param R_ji   Represents the rule subset {@code R_ji} consisting of the rules
     *               that have {@code a_j} appearing with the value {@code v_ji}.
     * @param rules  The dataset of rules per class. Each key of the map
     *               should correspond to a class and each value associated
     *               with the key should correspond to a list containing all
     *               the rules that infer the class.
     * @return The value AA(A_j, i) as defined in the paper.
     */
    private double attributeAutonomy(int A_j, String C_i, Set<Integer> attSet, List<RBDTRule> R_ji,
            HashMap<String, List<RBDTRule>> rules) {
        // Computing the value of MaxADS_ji as stated in the paper
        double maxADS_ji = R_ji.stream()
                .map(rule -> attributeDisjointness(A_j, C_i, rule.getY(), R_ji))
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);

        // As stated in the paper the maxADS_ji value should be between 0 and 3*m*(m-1).
        int m = rules.keySet().size();
        checkMeasure(maxADS_ji, 0, 3 * m * (m - 1), "Maximum Attribute Disjointness");

        // Computing the ADS value for each attribute in {@code attSet}.
        List<Double> ADS_List = attSet.stream()
                .filter(A_k -> A_k != A_j) // Ensure A_k is different from A_j
                .mapToDouble(A_k -> rules.keySet()
                        .stream()
                        .mapToDouble(C_k -> attributeDisjointness(A_k, C_i, C_k, R_ji))
                        .sum())
                .boxed()
                .collect(Collectors.toList());

        // Using the same notation as in the paper, s is the number of attributes that
        // achieved the highest AE score.
        int s = attSet.size();

        // Check if ADS_List contains maxADS_ji
        boolean Max_ADS_in_ADS_List = ADS_List.stream().anyMatch(value -> value == maxADS_ji);

        if (maxADS_ji == 0.0)
            return 0.0;
        if (s == 2 || Max_ADS_in_ADS_List)
            return 1.0;

        // ADS_List doesn't contain the item A_j since it was filtered out on compute
        return 1 + (s - 1) * maxADS_ji - ADS_List.stream().mapToDouble(Double::doubleValue).sum();
    }

    /**
     * The second step in computing the Attribute Autonomy (AA) metric.
     * 
     * @param A_j    The index of the attribute to compute the metric for.
     * @param attSet The set of attributes that achieved
     *               the highest (equal) AE score.
     * @return The value AA(A_j) as defined in the paper.
     */
    public double attributeAutonomy(int A_j, Set<Integer> attSet, HashMap<String, List<RBDTRule>> rules) {
        // v_j = {v_j1 ... v_jp_j}, is the set of possible values for attribute a_j
        // including the "don't care" value. p_j is the size of this set. R_ji denotes
        // the rule subset consisting of the rules that have a_j appearing with the
        // value v_ji.

        Set<String> v_j = new HashSet<>();
        for (List<RBDTRule> classGroup : rules.values()) {
            for (RBDTRule rule : classGroup) {
                v_j.add(rule.getItemsInX()[A_j]);
            }
        }

        /**
         * Represents the rule subset {@code R_ji} consisting of the rules that have
         * {@code a_j} appearing with the value {@code v_ji}.
         */
        Map<String, List<RBDTRule>> R_ji = new HashMap<>();
        for (String v_ji : v_j) {
            List<RBDTRule> ruleSubset = rules.values().stream()
                    .flatMap(List::stream)
                    .filter(rule -> rule.getItemsInX()[A_j].equals(v_ji))
                    .collect(Collectors.toList());
            R_ji.put(v_ji, ruleSubset);
        }

        int p_j = v_j.size();
        double sum = 0.0;

        // Computing the attribute autonomy for each class.
        for (int i = 1; i <= p_j; i++) {
            String classString = classesString[i];
            List<RBDTRule> R_ji_class = R_ji.get(classString);
            double autonomy = attributeAutonomy(A_j, classString, attSet, R_ji_class, rules);
            sum += autonomy;
        }
        return 1.0 / sum;
    }

    /**
     * Computes the Minimum value distribution (MVD) metric.
     * 
     * @param A_j    The index of the attribute to compute the metric for.
     * @param attSet The set of attributes that achieved
     *               the highest (equal) AA score.
     * @return The value MVD(A_j) as defined in the paper.
     */
    public double minimumValueDistribution(int A_j, Set<Integer> attSet) {
        Set<String> v_j = new HashSet<>();
        for (List<RBDTRule> classGroup : rules.values()) {
            for (RBDTRule rule : classGroup) {
                v_j.add(rule.getItemsInX()[A_j]);
            }
        }

        // THe number of different values that A_j can have in the dataset
        return v_j.size();
    }

    /**
     * Checks if the given value is within the specified range and throws a
     * RuntimeException if not.
     *
     * @param value       The value to be checked.
     * @param lb          The lower bound of the valid range.
     * @param ub          The upper bound of the valid range.
     * @param measureName The name of the measure being checked, used in the error
     *                    message.
     * @throws RuntimeException If the value is outside the valid range.
     */
    private void checkMeasure(double value, double lb, double ub, String measureName) {
        if (value > (ub + epsilon) || value < (lb - epsilon)) {
            throw new RuntimeException(
                    "Illegal value for measure " + measureName + ": value=" + value + ", should be between " +
                            lb + " and " + ub);
        }
    }

}
