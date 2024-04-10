package tools.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.rules.RBDTRule;

public class RBDTRuleUtilTest {

    @Test
    public void testReadRulesFromFile() {
        // Create test data
        Map<Integer, String[]> attributeValueMap = new HashMap<>();
        attributeValueMap.put(0, new String[] { "1", "2", "3" }); // Petal length
        attributeValueMap.put(1, new String[] { "4", "5", "6" }); // Petal width

        attributeValueMap.put(2, new String[] { "7", "8", "9" }); // Sepal length
        attributeValueMap.put(3, new String[] { "10", "11", "12" }); // Sepal width

        String fileString = "src/test/resources/RandomlySampledRules.json";

        // The list of rules red from the file
        List<RBDTRule> rules = RBDTRuleUtil.readRulesFromFile(fileString, attributeValueMap);

        // Assertions
        assertNotNull(rules);
        assertEquals(70, rules.size());
    }

    @Test
    public void testInvertMap() {
        // Test data
        Map<Integer, String[]> originalMap = new HashMap<>();
        originalMap.put(1, new String[] { "A", "B" });
        originalMap.put(2, new String[] { "X", "Y" });

        Map<String, Integer> invertedMap = RBDTRuleUtil.invertMap(originalMap);

        // Assertions
        assertNotNull(invertedMap);
        assertEquals(4, invertedMap.size());
        assertEquals(1, invertedMap.get("A"));
        assertEquals(1, invertedMap.get("B"));
        assertEquals(2, invertedMap.get("X"));
        assertEquals(2, invertedMap.get("Y"));
    }
}
