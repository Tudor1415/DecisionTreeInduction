package DecisionTrees;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import tools.rules.RBDTRule;
import tools.utils.RBDTRuleUtil;
import tools.utils.TreeUtil;

/**
 * Unit test for simple RBDTree.
 */
public class RBDTreeTest {
    @Test
    public void testRBDTree() {
        Map<Integer, String[]> attributeValueMap = new HashMap<>();
        attributeValueMap.put(0, new String[] { "1", "2", "3" }); // Petal length
        attributeValueMap.put(1, new String[] { "4", "5", "6" }); // Petal width

        attributeValueMap.put(2, new String[] { "7", "8", "9" }); // Sepal length
        attributeValueMap.put(3, new String[] { "10", "11", "12" }); // Sepal width

        // The list of rules red from the file
        List<RBDTRule> rules = RBDTRuleUtil.readRulesFromFile(
            "src/test/resources/RandomlySampledRules.json",
                attributeValueMap);

        // Create an instance of RBDTree and test its functionality
        RBDTree tree = new RBDTree(rules, 4);

        TreeUtil.printTreeToFile("src/test/output/IrisTree.txt", tree.getRoot());
    }
}
