package tools.measures;

import java.util.HashMap;
import java.util.stream.IntStream;

import tools.rules.RBDTRule;

/**
 * Class to compute different measures for a rule (inspired by R code : <a href=
 * "https://github.com/mhahsler/arules/blob/master/R/interestMeasures.R">arules</a>)
 *
 * @param rule           The rule for which measures are computed.
 * @param nbTransactions The total number of transactions in the dataset.
 * @param smoothCounts   The smoothing factor for the counts.
 */
public class AttributeMeasures {

    // For rounding errors
    public static double epsilon = 0.01;

    // Measure names
    public static final String AE = "attribute effectiveness";
    public static final String AA = "attribute autonomy";
    public static final String MVD = "minimum value distribution";

    // The dataset of rules per class.
    HashMap<String, RBDTRule[]> rules;

    // The number of distinct attributes in the dataset
    int nbAttributes;

    /**
     * Constructs a new instance of attribute measures.
     * 
     * @param rules        The dataset of rules per class. Each key of the map
     *                     should
     *                     correspond to a class and each value associated with the
     *                     key
     *                     should correspond to an array containing all the rules
     *                     that
     *                     infer the class.
     * @param nbAttributes The number of distinct attributes in the dataset.
     */
    public AttributeMeasures(HashMap<String, RBDTRule[]> rules, int nbAttributes) {
        if (rules == null) {
            throw new RuntimeException("Rule dataset must not be null");
        }

        this.rules = rules;
        this.nbAttributes = nbAttributes;
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
    private double attributeEffectiveness(int attributeIndex) {
        int m = 0; // Total number of instances
        int DC_count = 0; // Total count of "don't care" values for attribute a_j

        // Calculate m and sum C_{i,j}(DC)
        for (RBDTRule[] classRules : rules.values()) {
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

    private double attributeAutonomy(int attributeIndex) {
        return 0.0;
    }

    private double minimumValueDistribution(int attributeIndex) {
        return 0.0;
    }

    /**
     * Computes multiple measures for the rule based on the provided measure names.
     *
     * @param measureNames An array of measure names for which measures should be
     *                     computed.
     * @return An array of computed measures corresponding to the given measure
     *         names.
     * @throws RuntimeException If an unknown measure name is encountered.
     */
    public double[][] computeMeasures(String[] measureNames) {
        double[][] measures = new double[measureNames.length][nbAttributes];
        for (int i = 0; i < measureNames.length; i++) {
            measures[i] = compute(measureNames[i]);
        }
        return measures;
    }

    /**
     * Computes the specified measure for the rule.
     *
     * @param measureName The name of the measure to be computed.
     * @return The computed value of the specified measure.
     * @throws RuntimeException If an unknown measure name is encountered.
     */
    private double[] compute(String measureName) {
        if (measureName.equals(AE))
            return IntStream.range(0, this.nbAttributes).mapToDouble(i -> attributeEffectiveness(i)).toArray();
        if (measureName.equals(AA))
            return IntStream.range(0, this.nbAttributes).mapToDouble(i -> attributeAutonomy(i)).toArray();
        if (measureName.equals(MVD))
            return IntStream.range(0, this.nbAttributes).mapToDouble(i -> minimumValueDistribution(i)).toArray();
        throw new RuntimeException("This measure doesn't exist : " + measureName);
    }
}
