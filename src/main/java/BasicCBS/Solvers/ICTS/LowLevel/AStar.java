package BasicCBS.Solvers.ICTS.LowLevel;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ICTS.GeneralStuff.MDD;
import BasicCBS.Solvers.ICTS.HighLevel.ICTS_Solver;

import java.util.*;

public class AStar extends A_LowLevelSearcher {

    private Queue<Node> openList;
    /**
     * The key will not be updated, although, the value will be the last version of this node.
     * Will contain everything in the open list, so we could modify them (add to their parents) while they are in the Priority Queue.
     */
    private Map<Node, Node> contentOfOpen;
    private Map<Node, Node> closeList;
    private MAPF_Instance instance;
    private Agent agent;
    private DistanceTableAStarHeuristicICTS heuristic;

    /**
     * Constructor for the AStar searcher
     *
     * @param instance - we assume that it is a "subproblem" used the function "getSubproblemFor" in "MAPF_Instance" class
     * @param heuristic - the heuristics table that will enable us to get a more accurate heuristic
     */
    public AStar(ICTS_Solver highLevelSearcher, MAPF_Instance instance, DistanceTableAStarHeuristicICTS heuristic) {
        super(highLevelSearcher);
        this.instance = instance;
        openList = new PriorityQueue<>();
        contentOfOpen = new HashMap<>();
        closeList = new HashMap<>();
        agent = instance.agents.get(0); //only one agent in the instance
        this.heuristic = heuristic;

        initializeSearch();
    }

    private void initializeSearch() {
        Node start = new Node(agent, instance.map.getMapCell(agent.source), 0, heuristic);
        addToOpen(start);
    }

    private void addToOpen(Node node){
        if(contentOfOpen.containsKey(node)){
            //Do not add this node twice to the open list, just add it's parents to the already "inOpen" node.
            Node inOpen = contentOfOpen.get(node);
            inOpen.addParents(node.getParents());
        }
        else if(closeList.containsKey(node)){
            Node inClosed = closeList.get(node);
            inClosed.addParents(node.getParents());
        }
        else{
            openList.add(node);
            contentOfOpen.put(node, node);
        }
    }

    private Node pollFromOpen(){
        Node next = openList.poll();
        contentOfOpen.remove(next);
        return next;
    }

    private void addToClose(Node node) {
        closeList.put(node, node);
    }

    @Override
    public MDD continueSearching(int depthOfSolution) {
        Node goal = null;
        while(true){
            if(highLevelSearcher.reachedTimeout())
                return null;
            Node current = pollFromOpen();
            if(current.getF() > depthOfSolution)
            {
                addToOpen(current);
                break;
            }
            if(isGoalState(current)){
                if(current.getG() == depthOfSolution){
                    if(goal == null){
                        goal = current;
                        // Don't do continue here, because we want to add the sons of current to the open list for later
                    }
                    else{
                        //goal.addParents(current.getParents());
                        try {
                            throw new Exception("Should not enter here, because goal is already in closed list, so it already added the parents of the new solution to the goal");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    try {
                        throw new Exception("It is not logical that we will receive a different goal in a different depth");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            // Don't do else here, because we want to add the sons of current to the open list for later
            expand(current);
        }
        return new MDD(goal);
    }

    private void expand(Node node){
        expandedNodesNum++;
        List<I_Location> neighborLocations = node.getNeighborLocations();
        for (I_Location location : neighborLocations) {
            generatedNodesNum++;
            Node neighbor = new Node(agent, location, node.getG() + 1, heuristic);
            neighbor.addParent(node);
            addToOpen(neighbor);
        }
        addToClose(node);
    }

    private boolean isGoalState(Node node) {
        return node.getLocation().getCoordinate().equals(agent.target);
    }
}
