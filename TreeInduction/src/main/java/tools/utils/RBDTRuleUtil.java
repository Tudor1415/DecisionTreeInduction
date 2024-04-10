package tools.utils;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import tools.rules.RBDTRule;
import tools.rules.Rule;

public class RBDTRuleUtil {

    /**
     * Reads the rule from the json file and adds the "don't care" values before
     * constructing the RBDT rule's antecedent.
     * 
     * @param fileString The json file from which to read the rules.
     * @param attributes The possible attributes for the rules. It maps the
     *                   attribute index to the possible attribute values.
     * @return The list of rules read from the file.
     */
    public static List<RBDTRule> readRulesFromFile(String fileString, Map<Integer, String[]> attributeValueMap) {
        List<RBDTRule> rules = new ArrayList<>();
        Map<String, Integer> valueAttributeMap = invertMap(attributeValueMap);

        try (FileReader reader = new FileReader(fileString)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Rule>>() {
            }.getType();
            List<Rule> ruleList = gson.fromJson(reader, listType);

            for (Rule rule : ruleList) {
                Map<Integer, String> attributeValueMapWithDC = new HashMap<>();

                // Initializing the antecedent with don't care values.
                for (Integer key : attributeValueMap.keySet()) {
                    attributeValueMapWithDC.put(key, "DC");
                }

                // Overriding the "don't care" values with values red from the JSON file.
                for (String value : rule.getItemsInX()) {
                    int att = valueAttributeMap.get(value);
                    attributeValueMapWithDC.put(att, value);

                }

                RBDTRule newRule = new RBDTRule(rule.getY(), attributeValueMapWithDC, rule.getFreqX(), rule.getFreqY(),
                        rule.getFreqZ());

                rules.add(newRule);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rules;
    }

    /**
     * This code will produce the inverted map where each string item from the
     * arrays of the original map becomes a key in the inverted map, and the
     * corresponding value the integer representing the key from the
     * original map where that string item was associated. The requirement is that
     * one string value can only appear in relationship with one key.
     * 
     * @param originalMap The original map to be inverted.
     * @return The inverted map.
     */
    public static Map<String, Integer> invertMap(Map<Integer, String[]> originalMap) {
        Map<String, Integer> invertedMap = new HashMap<>();

        for (Map.Entry<Integer, String[]> entry : originalMap.entrySet()) {
            int key = entry.getKey();
            String[] values = entry.getValue();
            for (String value : values) {
                invertedMap.put(value, key);
            }
        }

        return invertedMap;
    }

}
