package BasicCBS.Solvers.ICTS.HighLevel;

import BasicCBS.Solvers.Solution;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

//extends SingleAgentAStar_Solver.AStarState
public class ICTS_Node_Old implements Comparable<ICTS_Node_Old> {

    /*  =  = fields =  */

    /**
     * The solution in this node. For every non-root node, this solution is after rerouting (solving low level) an
     * agent to overcome a conflict.
     * Holds references to the same {@link BasicCBS.Solvers.SingleAgentPlan plans} as in {@link #parent}, apart from the plan
     * of the re-routed agent.
     */
    private Solution solution;
    /**
     * The cost of the solution.
     */
    private float solutionCost;


    /*  =  =  = CBS tree branches =  =  */


    private ICTS_Node_Old parent;

    private List<ICTS_Node_Old> children;


    /*  =  = constructors =  */

    /**
     * Root constructor.
     *
     * @param solution     an initial solution for all agents.
     * @param solutionCost the cost of the solution.
     */
    public ICTS_Node_Old(Solution solution, float solutionCost) {
        this.solution = solution;
        this.solutionCost = solutionCost;
        this.parent = null;
    }

    /**
     * Non-root constructor.
     */
    public ICTS_Node_Old(Solution solution, float solutionCost, ICTS_Node_Old parent) {
        this.solution = solution;
        this.solutionCost = solutionCost;
        this.parent = parent;
    }

    public List<ICTS_Node_Old> expend(){
        return null; // TODO: 17/02/2020  rotem freak
    }


    /*  =  = when expanding a node =  */

    public void setChildren(List<ICTS_Node_Old> list){
        this.children = list;
    }


    /*  =  = getters =  */

    public Solution getSolution() {
        return solution;
    }

    public float getSolutionCost() {
        return solutionCost;
    }


    public ICTS_Node_Old getParent() {
        return parent;
    }

    public List<ICTS_Node_Old> getChildren(){
        return children;
    }

    @Override
    public int compareTo(ICTS_Node_Old o) {
        return Objects.compare(this, o, Comparator.comparing(ICTS_Node_Old::getSolutionCost));
    }


}
