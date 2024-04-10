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

        // Assert the result
        assertNotNull(rules);
        assertEquals(70, rules.size()); 
    }

    @Test
    public void testInvertMap() {
        // Create test data
        Map<Integer, String[]> originalMap = new HashMap<>();
        originalMap.put(1, new String[]{"A", "B"});
        originalMap.put(2, new String[]{"X", "Y"});

        // Call the method
        Map<String, Integer> invertedMap = RBDTRuleUtil.invertMap(originalMap);

        // Assert the result
        assertNotNull(invertedMap);
        assertEquals(4, invertedMap.size()); // Assuming 4 key-value pairs are generated, adjust accordingly
        assertEquals(1, invertedMap.get("A"));
        assertEquals(1, invertedMap.get("B"));
        assertEquals(2, invertedMap.get("X"));
        assertEquals(2, invertedMap.get("Y"));
        // Add more assertions as needed
    }
}
