package BasicCBS.Solvers.ICTS.GeneralStuff;

import BasicCBS.Instances.Agent;
import BasicCBS.Solvers.ICTS.LowLevel.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MDDNode {
    private List<MDDNode> neighbors;
    private Map<Agent, Node> values;

    public MDDNode(Node current) {
        neighbors = new LinkedList<>();
        values = new HashMap<>();
        values.put(current.getAgent(), current);
    }

    public void addValue(Node value){
        values.put(value.getAgent(), value);
    }

    public void addNeighbor(MDDNode neighbor){
        neighbors.add(neighbor);
    }

    public List<MDDNode> getNeighbors() {
        return neighbors;
    }

    public Map<Agent, Node> getValues() {
        return values;
    }

    public Node getValue(Agent agent){
        return values.get(agent);
    }
}
