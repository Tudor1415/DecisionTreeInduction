package tools.rules;

import lombok.Data;

@Data
public class Rule {
    /** The class of the decision rule */
    private String Y;

    /**
     * The values in the antecedent of the rule and the union of the values in the
     * antecedent and in the class (itemsInZ).
     */
    private String[] itemsInX;
    private String[] itemsInZ;

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
}
