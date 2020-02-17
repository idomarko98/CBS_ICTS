package BasicCBS.Solvers.ICTS.GeneralStuff;

import BasicCBS.Instances.Agent;
import BasicCBS.Solvers.ICTS.LowLevel.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MDD {
    private MDDNode start;
    private MDDNode goal;

    public MDD(Node goal){
        initialize(goal);
    }

    private void initialize(Node goal){
        MDDNode mddGoal = new MDDNode(goal);
        Agent agent = goal.getAgent();

        Queue<MDDNode> currentLevel = new LinkedList<>();
        currentLevel.add(mddGoal);
        this.goal = mddGoal;

        while (true) {
            if(currentLevel.size() == 1 && currentLevel.peek().getValue(agent).getG() == 0) {
                //We are at the start state, so we can finish the building of the MDD
                break;
            }
            HashMap<Node, MDDNode> previousLevel = new HashMap<>();
            while (!currentLevel.isEmpty()) {
                MDDNode current = currentLevel.poll();
                Node currentValue = current.getValue(agent);
                List<Node> currentParents = currentValue.getParents();
                for (Node parent : currentParents) {
                    MDDNode mddParent;
                    if(previousLevel.containsKey(parent)){
                        mddParent = previousLevel.get(parent);
                    }
                    else{
                        mddParent = new MDDNode(parent);
                        previousLevel.put(parent, mddParent);
                    }
                    mddParent.addNeighbor(current);
                }
            }
            currentLevel.addAll(previousLevel.values());
        }
        this.start = currentLevel.poll();
    }

    public MDDNode getStart() {
        return start;
    }

    public MDDNode getGoal() {
        return goal;
    }
}
