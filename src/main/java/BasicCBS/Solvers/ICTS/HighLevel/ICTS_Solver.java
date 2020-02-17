package BasicCBS.Solvers.ICTS.HighLevel;

import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Solvers.*;

public class ICTS_Solver extends A_Solver {

//    I_Solver lowLevelSolver;
    I_OpenList<ICTS_Node> openList;
//    ICTS_Solver.OpenListManagementMode openListManagementMode;
//    ICTS_Solver.CBSCostFunction costFunction;
//    Comparator<? super ICTS_Node> cbsNodeComparator;

    @Override
    protected Solution runAlgorithm(MAPF_Instance instance, RunParameters parameters) {
        return null;
    }

//    public ICTS_Solver(I_Solver lowLevelSolver, I_OpenList<ICTS_Solver.CBS_Node> openList, ICTS_Solver.OpenListManagementMode openListManagementMode,
//                      CBS_Solver.CBSCostFunction costFunction, Comparator<? super CBS_Solver.CBS_Node> cbsNodeComparator) {
//        this.lowLevelSolver = Objects.requireNonNullElseGet(lowLevelSolver, SingleAgentAStar_Solver::new);
//        this.openList = Objects.requireNonNullElseGet(openList, OpenList::new);
//        this.openListManagementMode = openListManagementMode != null ? openListManagementMode : CBS_Solver.OpenListManagementMode.AUTOMATIC;
//        clearOPEN();
//        // if a specific cost function is not provided, use standard SOC (Sum of Individual Costs)
//        this.costFunction = costFunction != null ? costFunction : (solution, cbs) -> solution.costFunction();//solution.sumIndividualCosts();
//        this.cbsNodeComparator = cbsNodeComparator != null ? cbsNodeComparator : Comparator.comparing(CBS_Solver.CBS_Node::getSolutionCost);
//    }
//
//
//
//
}
