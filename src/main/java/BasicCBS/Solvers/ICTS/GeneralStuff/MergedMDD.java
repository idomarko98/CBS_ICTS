package BasicCBS.Solvers.ICTS.GeneralStuff;

import BasicCBS.Instances.Agent;
import BasicCBS.Solvers.Move;
import BasicCBS.Solvers.SingleAgentPlan;
import BasicCBS.Solvers.Solution;

import java.util.*;

public class MergedMDD {
    private MergedMDDNode start;
    private MergedMDDNode goal;

    /**
     * The constructor can be accessed only via the Factory (in the same package)
     */
    MergedMDD() {
    }

    public MergedMDDNode getStart() {
        return start;
    }

    public MergedMDDNode getGoal() {
        return goal;
    }

    public void setStart(MergedMDDNode start) {
        this.start = start;
    }

    public void setGoal(MergedMDDNode goal) {
        this.goal = goal;
    }

    public Solution getSolution() {
        Solution solution = new Solution();
        Map<Agent, List<Move>> agentMoves = new HashMap<>();
        for (Agent agent : goal.getValues().keySet()) {
            agentMoves.put(agent, new ArrayList<>());
        }

        MergedMDDNode current = goal;
        while (!current.getParents().isEmpty()) {
            MergedMDDNode parent = current.getParents().get(0); //It doesn't matter which parent it was, we take a single path.
            for (Agent agent : current.getValues().keySet()) {
                MDDNode currentValue = current.getValue(agent);
                MDDNode parentValue = parent.getValue(agent);
                if (!currentValue.equals(parentValue)) {
                    //Only consider moves that changed something.
                    //this equal calculation uses also the "g" value,
                    //so we will distinguish between being at state s at time g and at time g + 1
                    //(staying in the same location as part of the solution)
                    Move move = new Move(agent, currentValue.getValue().getG(), parentValue.getValue().getLocation(), currentValue.getValue().getLocation());
                    agentMoves.get(agent).add(move); //insert the move to the agent's moves
                }
            }
            current = parent;
        }

        for(Agent agent : goal.getValues().keySet()){
            List<Move> moves = agentMoves.get(agent);
            Collections.reverse(moves);
            SingleAgentPlan plan = new SingleAgentPlan(agent, moves);
            solution.putPlan(plan);
        }

        return solution;
    }
}
