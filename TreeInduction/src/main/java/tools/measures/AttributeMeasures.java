package tools.measures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import tools.rules.RBDTRule;

/**
 * Class to compute different measures for a rule dataset (inspired by :
 * <a href=
 * "https://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=5381350">A.
 * Abdelhalim et. al.</a>)
 *
 */
public class AttributeMeasures {

    // For rounding errors
    public static double epsilon = 0.01;

    // Measure names
    public static final String AE = "attribute effectiveness";
    public static final String AA = "attribute autonomy";
    public static final String MVD = "minimum value distribution";

    // The dataset of rules per class.
    HashMap<String, List<RBDTRule>> rules;

    // The number of distinct attributes in the dataset
    int nbAttributes;

    // An array mapping an index to a class value
    String[] classesString;

    /**
     * Constructs a new instance of attribute measures.
     * 
     * @param rules         The dataset of rules per class. Each key of the map
     *                      should correspond to a class and each value associated
     *                      with the key should correspond to a list containing all
     *                      the rules that infer the class.
     * @param nbAttributes  The number of distinct attributes in the dataset.
     * @param classesString The array of class values. In the paper, the class j is
     *                      the class value with index j in this array;
     */
    public AttributeMeasures(HashMap<String, List<RBDTRule>> rules, int nbAttributes, String[] classesString) {
        if (rules == null) {
            throw new RuntimeException("Rule dataset must not be null");
        }

        this.rules = rules;
        this.nbAttributes = nbAttributes;
        this.classesString = classesString;
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
     * @return The Attribute Effectiveness (AE) for attribute {@code a_j}.
     */
    public double attributeEffectiveness(int attributeIndex) {
        int m = 0; // Total number of instances
        int DC_count = 0; // Total count of "don't care" values for attribute a_j

        // Calculate m and sum C_{i,j}(DC)
        for (List<RBDTRule> classRules : rules.values()) {
            for (RBDTRule rule : classRules) {
                m++;
                if (rule.getAntecedents()[attributeIndex].equals("DC")) {
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
                .map(rule -> rule.getAntecedents()[A_j])
                .collect(Collectors.toSet());

        // The set of values that the attribute A_j can take in rules that infer the
        // class k.
        Set<String> V_kj = R_ji.stream()
                .filter(rule -> rule.getY().equals(C_k))
                .map(rule -> rule.getAntecedents()[A_j])
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
     * @return The value AA(A_j, i) as defined in the paper.
     */
    private double attributeAutonomy(int A_j, String C_i, Set<Integer> attSet, List<RBDTRule> R_ji) {
        // Computing the value of MaxADS_ji as stated in the paper
        double maxADS_ji = R_ji.stream()
                .map(rule -> attributeDisjointness(A_j, C_i, rule.getY(), R_ji))
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);

        // As stated in the paper the maxADS_ji value should be between 0 and 3*m*(m-1).
        int m = rules.keySet().size();
        checkMeasure(maxADS_ji, 0, 3*m*(m-1), "Maximum Attribute Disjointness");

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
    public double attributeAutonomy(int A_j, Set<Integer> attSet) {
        // v_j = {v_j1 ... v_jp_j}, is the set of possible values for attribute a_j
        // including the "don't care" value. p_j is the size of this set. R_ji denotes
        // the rule subset consisting of the rules that have a_j appearing with the
        // value v_ji.

        Set<String> v_j = new HashSet<>();
        for (List<RBDTRule> classGroup : rules.values()) {
            for (RBDTRule rule : classGroup) {
                v_j.add(rule.getAntecedents()[A_j]);
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
                    .filter(rule -> rule.getAntecedents()[A_j].equals(v_ji))
                    .collect(Collectors.toList());
            R_ji.put(v_ji, ruleSubset);
        }

        int p_j = v_j.size();
        double sum = 0.0;
        // Computing the attribute autonomy for each class.
        for (int i = 1; i <= p_j; i++) {
            String classString = classesString[i];
            List<RBDTRule> R_ji_class = R_ji.get(classString);
            double autonomy = attributeAutonomy(A_j, classString, attSet, R_ji_class);
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
                v_j.add(rule.getAntecedents()[A_j]);
            }
        }

        // THe number of different values that A_j can have in the dataset
        return v_j.size();
    }
}
