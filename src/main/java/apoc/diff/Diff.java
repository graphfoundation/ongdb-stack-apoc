package apoc.diff;

import apoc.Description;
import apoc.util.MapUtil;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.procedure.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;

/**
 * @author Benjamin Clauss
 * @author bradnussbaum
 * @since 15.06.2018
 */
public class Diff {

    @Context
    public GraphDatabaseService db;

    @UserFunction()
    @Description("apoc.diff.nodes([leftNode],[rightNode]) returns a detailed diff of both nodes")
    public Map<String, Object> nodes(@Name("leftNode") Node leftNode, @Name("rightNode") Node rightNode, @Name(value = "propertyExcludes", defaultValue = "") List<String> excludedPropertyKeys) {
        Map<String, Object> allLeftProperties = leftNode.getAllProperties().entrySet().stream()
                .filter(e -> !excludedPropertyKeys.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<String, Object> allRightProperties = rightNode.getAllProperties().entrySet().stream()
                .filter(e -> !excludedPropertyKeys.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, Object> result = new HashMap<>();
        result.put("leftOnly", getPropertiesOnlyLeft(allLeftProperties, allRightProperties));
        result.put("rightOnly", getPropertiesOnlyLeft(allRightProperties, allLeftProperties));
        result.put("inCommon", getPropertiesInCommon(allLeftProperties, allRightProperties));
        result.put("different", getPropertiesDiffering(allLeftProperties, allRightProperties));

        return result;
    }

    @UserFunction()
    @Description("apoc.diff.nodesWithEdges(leftNode,rightNode) returns a detailed diff including relationships of both nodes")
    public Map<String, Object> nodesWithEdges(@Name("leftNode") Node leftNode, @Name("rightNode") Node rightNode, @Name(value = "propertyExcludes", defaultValue = "") List<String> excludedPropertyKeys) {
        return nodes(leftNode, rightNode, excludedPropertyKeys);
    }

    private Map<String, Object> getPropertiesOnlyLeft(Map<String, Object> left, Map<String, Object> right) {
        Map<String, Object> leftOnly = new HashMap<>();
        leftOnly.putAll(left);
        leftOnly.keySet().removeAll(right.keySet());
        return leftOnly;
    }

    private Map<String, Object> getPropertiesInCommon(Map<String, Object> left, Map<String, Object> right) {
        Map<String, Object> inCommon = new HashMap<>(left);
        inCommon.entrySet().retainAll(right.entrySet());
        return inCommon;
    }

    private Map<String, Map<String, Object>> getPropertiesDiffering(Map<String, Object> left, Map<String, Object> right) {
        Map<String, Map<String, Object>> different = new HashMap<>();
        Map<String, Object> keyPairs = new HashMap<>();
        keyPairs.putAll(left);
        keyPairs.keySet().retainAll(right.keySet());

        for (Map.Entry<String, Object> entry : keyPairs.entrySet()) {
            if (!left.get(entry.getKey()).equals(right.get(entry.getKey()))) {
                Map<String, Object> pairs = new HashMap<>();
                pairs.put("left", left.get(entry.getKey()));
                pairs.put("right", right.get(entry.getKey()));
                different.put(entry.getKey(), pairs);
            }
        }
        return different;
    }
}
