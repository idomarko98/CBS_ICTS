package BasicCBS.Solvers.ICTS.LowLevel;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Solvers.AStar.AStarHeuristic;
import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class AStar implements I_LowLevelSearcher{

    private Queue<Node> openList;
    private Set<Node> closeList;
    private MAPF_Instance instance;
    private Agent agent;
    private DistanceTableAStarHeuristicICTS heuristic;

    /**
     * Constructor for the AStar searcher
     *
     * @param instance - we assume that it is a "subproblem" used the function "getSubproblemFor" in "MAPF_Instance" class
     * @param heuristic - the heuristics table that will enable us to get a more accurate heuristic
     */
    public AStar(MAPF_Instance instance, DistanceTableAStarHeuristicICTS heuristic) {
        this.instance = instance;
        openList = new PriorityQueue<>();
        closeList = new HashSet<>();
        agent = instance.agents.get(0); //only one agent in the instance
        this.heuristic = heuristic;

        initializeSearch();
    }

    private void initializeSearch() {
        Node start = new Node(agent, instance.map.getMapCell(agent.source), 0, heuristic);

        openList.add(start);
    }

    @Override
    public Node continueSearching(int depthOfSolution) {
        Node goal = null;
        while(true){
            Node current = openList.poll();
            if(current.getF() > depthOfSolution)
            {
                openList.add(current);
                break;
            }
            if(isGoalState(current)){
                if(current.getG() == depthOfSolution){
                    if(goal == null){
                        goal = current;
                    }
                }
            }
        }
    }

    private boolean isGoalState(Node node) {
        return node.getLocation().getCoordinate().equals(agent.target);
    }
}
