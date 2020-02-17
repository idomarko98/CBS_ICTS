package BasicCBS.Solvers.ICTS.LowLevel;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ICTS.GeneralStuff.MDD;

import java.util.*;

public class AStar implements I_LowLevelSearcher{

    private Queue<Node> openList;
    /**
     * The key will not be updated, although, the value will be the last version of this node.
     * Will contain everything in the open list, so we could modify them (add to their parents) while they are in the Priority Queue.
     */
    private Map<Node, Node> contentOfOpen;
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
        contentOfOpen = new HashMap<>();
        closeList = new HashSet<>();
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
        else{
            openList.add(node); // TODO: 2/17/2020 check if it is already in closed and deal with it
            contentOfOpen.put(node, node);
        }
    }

    private Node pollFromOpen(){
        Node next = openList.poll();
        contentOfOpen.remove(next);
        return next;
    }

    @Override
    public MDD continueSearching(int depthOfSolution) {
        Node goal = null;
        while(true){
            Node current = pollFromOpen();
            if(current.getF() > depthOfSolution)
            {
                addToOpen(current); // TODO: 2/17/2020 check if this creates bugs
                break;
            }
            if(isGoalState(current)){
                if(current.getG() == depthOfSolution){
                    if(goal == null){
                        goal = current;
                        // Don't do continue here, because we want to add the sons of current to the open list for later
                    }
                    else{
                        // TODO: 2/17/2020 we are not supposed to get here, if we check the closed set before entering to the open. check it.
                        goal.addParents(current.getParents());
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
        // TODO: 2/17/2020 add 1 to expended nodes
        List<I_Location> neighborLocations = node.getNeighborLocations();
        for (I_Location location : neighborLocations) {
            // TODO: 2/17/2020 add 1 to generated nodes
            Node neighbor = new Node(agent, location, node.getG() + 1, heuristic);
            neighbor.addParent(node);
            addToOpen(neighbor);
        }
    }

    private boolean isGoalState(Node node) {
        return node.getLocation().getCoordinate().equals(agent.target);
    }
}
