package BasicCBS.Solvers.ICTS;

import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;
import BasicCBS.Solvers.Solution;

import java.util.List;
import java.util.Objects;

//extends SingleAgentAStar_Solver.AStarState
public class ICTS_Node  implements Comparable<ICTS_Node> {

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


    private ICTS_Node parent;

    private List<ICTS_Node> children;


    /*  =  = constructors =  */

    /**
     * Root constructor.
     *
     * @param solution     an initial solution for all agents.
     * @param solutionCost the cost of the solution.
     */
    public ICTS_Node(Solution solution, float solutionCost) {
        this.solution = solution;
        this.solutionCost = solutionCost;
        this.parent = null;
    }

    /**
     * Non-root constructor.
     */
    public ICTS_Node(Solution solution, float solutionCost, ICTS_Node parent) {
        this.solution = solution;
        this.solutionCost = solutionCost;
        this.parent = parent;
    }

    public List<ICTS_Node> expend(){

    }


    /*  =  = when expanding a node =  */

    public void setChildren(List<ICTS_Node> list){
        this.children = list;
    }


    /*  =  = getters =  */

    public Solution getSolution() {
        return solution;
    }

    public float getSolutionCost() {
        return solutionCost;
    }


    public ICTS_Node getParent() {
        return parent;
    }

    public List<ICTS_Node> getChildren(){
        return children;
    }

    @Override
    public int compareTo(ICTS_Node o) {
        return Objects.compare(this, o, ICTS_Solver.this.CBSNodeComparator);
    }


}
