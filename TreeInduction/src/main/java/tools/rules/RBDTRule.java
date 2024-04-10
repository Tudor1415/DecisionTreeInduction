package tools.rules;

import java.util.Map;

import lombok.Getter;

/**
 * This class implements the desired representation of decision rules as
 * described in the RBDT-1 paper that can be found here: DOI:
 * 10.1109/ICMLA.2009.25
 */
@Getter
public class RBDTRule {
    /** The class of the decision rule */
    private String Y;

    /** The map of the antecedents of the rule containing the don't care values */
    private Map<Integer, String> attributeValueMap;

    /**
     * The atomic frequencies of the rule:
     * <ul>
     * <li><b>freqX</b>: the frequency of the <i>antecedent</i> values (all present
     * at once) in the transactional dataset.</li>
     * <li><b>freqY</b>: the frequency of the <i>consequent</i> value (the class) in
     * the transactional dataset.</li>
     * <li><b>freqZ</b>: the frequency of both the <i>antecedent and consequent</i>
     * values present at the same time in the transactional dataset.</li>
     * </ul>
     */
    private int freqX, freqY, freqZ;

    /**
     * Constructs an {@code RBDTRule} with the specified class string, atributes,
     * and frequencies.
     *
     * @param Y                 The class of the decision rule.
     * @param attributeValueMap The map of attributes (attribute index -> value) of
     *                          the rule.
     *                          Contain's the don't care values "DC" for items that
     *                          aren't present in the rule's antecedent.
     * @param freqX             The frequency of the antecedent values (all present
     *                          at
     *                          once) in the dataset.
     * @param freqY             The frequency of the consequent value (the class) in
     *                          the dataset.
     * @param freqZ             The frequency of both the antecedent and consequent
     *                          values present at the same time.
     */
    public RBDTRule(String Y, Map<Integer, String> attributeValueMap, int freqX, int freqY, int freqZ) {
        this.Y = Y;
        this.attributeValueMap = attributeValueMap;
        this.freqX = freqX;
        this.freqY = freqY;
        this.freqZ = freqZ;
    }
}
