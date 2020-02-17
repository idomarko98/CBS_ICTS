package BasicCBS.Solvers.ICTS.HighLevel;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Solvers.*;
import BasicCBS.Solvers.AStar.AStarHeuristic;
import BasicCBS.Solvers.AStar.RunParameters_SAAStar;
import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;
import BasicCBS.Solvers.ConstraintsAndConflicts.Constraint.ConstraintSet;
import Environment.Metrics.InstanceReport;
import Environment.Metrics.S_Metrics;

import java.util.Comparator;
import java.util.Objects;

public class ICTS_Solver extends A_Solver {

//    I_Solver lowLevelSolver;
    I_OpenList<ICTS_Node> openList;
    ICTS_Solver.OpenListManagementMode openListManagementMode;
//    ICTS_Solver.CBSCostFunction costFunction;
//    Comparator<? super ICTS_Node> cbsNodeComparator;
    private int generatedNodes;
    private int expandedNodes;
    private MAPF_Instance instance;
    private final I_Solver lowLevelSolver;
    private AStarHeuristic aStarHeuristic;
    private final ICTSCostFunction costFunction;





    @Override
    protected Solution runAlgorithm(MAPF_Instance instance, RunParameters parameters) {
        initOpen(Objects.requireNonNullElseGet(parameters.constraints, ConstraintSet::new));
        ICTS_Node goal = mainLoop();
        //return solutionFromGoal(goal);
        return null; // TODO: 17/02/2020
    }

    private ICTS_Node mainLoop() {
        while(!openList.isEmpty() && !checkTimeout()){
            ICTS_Node node = openList.poll();

            // verify solution (find conflicts)
            //I_ConflictManager cat = getConflictAvoidanceTableFor(node);
            //node.setSelectedConflict(cat.selectConflict());

            if(/*isGoal(node)*/true)// TODO: 17/02/2020  
                return node;
            else
                expandNode(node);
        }

        return null; //probably a timeout
    }

    //todo: complete
    private void expandNode(ICTS_Node node) {
        this.expandedNodes++;

//        Constraint[] constraints = node.selectedConflict.getPreventingConstraints();
//        // make copies of data structures for left child, while reusing the parent's data structures on the right child.
//        node.leftChild = generateNode(node, constraints[0], true);
//        node.rightChild = generateNode(node, constraints[1], false);
//
//        if(node.leftChild == null || node.rightChild == null){
//            return; //probably a timeout in the low level. should abort.
//        }
//        addToOpen(node.leftChild);
//        addToOpen(node.rightChild);
    }

    /**
     * Initialises the {@link #openList OPEN} and inserts the root.
     * @param initialConstraints a set of initial constraints on the agents.
     */
    private void initOpen(ConstraintSet initialConstraints) {
        if(this.openListManagementMode == OpenListManagementMode.AUTOMATIC ||
                this.openListManagementMode == OpenListManagementMode.AUTO_INIT_MANUAL_CLEAR){
            addToOpen(generateRoot(initialConstraints));
        }
    }

    private boolean addToOpen(ICTS_Node node) {
        return openList.add(node);
    }

    /**
     * Creates a root node.
     */
    private ICTS_Node generateRoot(ConstraintSet initialConstraints) {
        this.generatedNodes++;

        Solution solution = new Solution(); // init an empty solution
        // for every agent, add its plan to the solution
        for (Agent agent :
                this.instance.agents) {
            solution = solveSubproblem(agent, solution, initialConstraints);
        }

        return new ICTS_Node(solution, costFunction.solutionCost(solution, this));
    }

    private Solution solveSubproblem(Agent agent, Solution currentSolution, ConstraintSet constraints) {
        InstanceReport instanceReport = S_Metrics.newInstanceReport();
        RunParameters subproblemParameters = getSubproblemParameters(currentSolution, constraints, instanceReport);
        Solution subproblemSolution = this.lowLevelSolver.solve(this.instance.getSubproblemFor(agent), subproblemParameters);
        digestSubproblemReport(instanceReport);
        return subproblemSolution;
    }

    private RunParameters getSubproblemParameters(Solution currentSolution, ConstraintSet constraints, InstanceReport instanceReport) {
        long timeLeftToTimeout = super.maximumRuntime - (System.currentTimeMillis() - super.startTime);
        RunParameters subproblemParametes = new RunParameters(timeLeftToTimeout, constraints, instanceReport, currentSolution);
        if(this.lowLevelSolver instanceof SingleAgentAStar_Solver){ // upgrades to a better heuristic
            subproblemParametes = new RunParameters_SAAStar(subproblemParametes, this.aStarHeuristic);
        }
        return subproblemParametes;
    }

    private void digestSubproblemReport(InstanceReport subproblemReport) {
        Integer statesGenerated = subproblemReport.getIntegerValue(InstanceReport.StandardFields.generatedNodesLowLevel);
        super.totalLowLevelStatesGenerated += statesGenerated==null ? 0 : statesGenerated;
        Integer statesExpanded = subproblemReport.getIntegerValue(InstanceReport.StandardFields.expandedNodesLowLevel);
        super.totalLowLevelStatesExpanded += statesExpanded==null ? 0 : statesExpanded;
        Integer lowLevelRuntime = subproblemReport.getIntegerValue(InstanceReport.StandardFields.elapsedTimeMS);
        super.instanceReport.integerAddition(InstanceReport.StandardFields.totalLowLevelTimeMS, lowLevelRuntime);
        //we consolidate the subproblem report into the main report, and remove the subproblem report.
        S_Metrics.removeReport(subproblemReport);
    }

    public ICTS_Solver(I_Solver lowLevelSolver, I_OpenList<ICTS_Node> openList, ICTS_Solver.OpenListManagementMode openListManagementMode,
                      ICTSCostFunction costFunction, Comparator<? super ICTS_Node> ictsNodeComparator) {
        this.lowLevelSolver = Objects.requireNonNullElseGet(lowLevelSolver, SingleAgentAStar_Solver::new);
//        this.openList = Objects.requireNonNullElseGet(openList, OpenList::new);
//        this.openListManagementMode = openListManagementMode != null ? openListManagementMode : CBS_Solver.OpenListManagementMode.AUTOMATIC;
//        clearOPEN();
//        // if a specific cost function is not provided, use standard SOC (Sum of Individual Costs)
        this.costFunction = costFunction != null ? costFunction : (solution, cbs) -> solution.costFunction();//solution.sumIndividualCosts();
//        this.cbsNodeComparator = cbsNodeComparator != null ? cbsNodeComparator : Comparator.comparing(CBS_Solver.CBS_Node::getSolutionCost);
    }
//
//
//
//
public enum OpenListManagementMode{
    /**
     * Will handle OPEN automatically. This is the standard mode of operation. The solver will clear OPEN before and
     * after every run, and initialize OPEN at the start of every run with a single root {@link ICTS_Node node}.
     */
    AUTOMATIC,
    /**
     * Will initialize OPEN automatically, but clearing it before or after a run will be controlled manually.
     * Note that this means the solver keeps part of its state after running. If you want to reuse the solver, you
     * have to manually handle the clearing of OPEN. If you keep references to many such solvers, this may adversely
     * affect available memory.
     */
    AUTO_INIT_MANUAL_CLEAR,
    /**
     * Will not initialize OPEN (assumes that it was already initialized), but will clear it after running.
     * It is not cleared before running. If it were to be cleared before running, manual initialization would be
     * impossible.
     */
    MANUAL_INIT_AUTO_CLEAR,
    /**
     * Will not initialize OPEN (assumes that it was already initialized).
     * Will not clear OPEN automatically.
     * Note that this means the solver keeps part of its state after running. If you want to reuse the solver, you
     * have to manually handle the clearing of OPEN. If you keep references to many such solvers, this may adversely
     * affect available memory.
     */
    MANUAL
}
}
