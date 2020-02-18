package BasicCBS.Solvers.ICTS.GeneralStuff;

import BasicCBS.Instances.Agent;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Implements a BFS Factory for Merged MDD.
 * We don't need a closed set because MDDs are DAGS. (no cycles...)
 */
public class BreadthFirstSearch_MergedMDDFactory implements I_MergedMDDFactory {

    private Map<MergedMDDNode, MergedMDDNode> contentOfOpen;
    private Queue<MergedMDDNode> openList;
    /**
     * We implement a closed list only for being able to say that we have an error, and when we realize that the MDD is not a DAG
     */
    private Set<MergedMDDNode> closedList;

    private int goalDepth;

    @Override
    public MergedMDD create(Map<Agent, MDD> agentMDDs) {
        openList = createNewOpenList();
        contentOfOpen = new HashMap<>();
        closedList = createNewClosedList();

        MergedMDD mergedMDD = new MergedMDD();

        MergedMDDNode start = new MergedMDDNode(0);
        mergedMDD.setStart(start);

        goalDepth = 0;

        for (Agent agent : agentMDDs.keySet()) {
            MDD mdd = agentMDDs.get(agent);
            start.addValue(mdd.getStart());

            int currentDepth = mdd.getDepth();
            if (currentDepth > goalDepth)
                goalDepth = currentDepth;
        }

        addToOpen(start);

        //Let the search begin!
        while (!openList.isEmpty()) {
            MergedMDDNode current = pollFromOpen();
            if (isGoal(current)) {
                mergedMDD.setGoal(current);
                return mergedMDD;
            }

            expand(current);
        }

        return null;
    }

    private static void combinationUtil(List<List<FatherSonMDDNodePair>> agentFatherSonPairs, List<FatherSonMDDNodePair> currentCombination, List<MergedMDDNode> neighbors, int index, int mddNodeDepth) {
        // Current combination is ready to be checked. check if it is a valid combination
        if (index == agentFatherSonPairs.size()) {
            if (isValidCombination(currentCombination)) {
                MergedMDDNode current = new MergedMDDNode(mddNodeDepth);
                for (FatherSonMDDNodePair pair : currentCombination) {
                    current.addValue(pair.getSon());
                }
                neighbors.add(current);
            }
            return;
        }

        // Current combination is not yet ready to be checked.
        for (FatherSonMDDNodePair possiblePair : agentFatherSonPairs.get(index)) {
            List<FatherSonMDDNodePair> nextCombination = new ArrayList<>(currentCombination);
            nextCombination.add(possiblePair);
            combinationUtil(agentFatherSonPairs, nextCombination, neighbors, index + 1, mddNodeDepth);
        }
    }

    private static boolean isValidCombination(List<FatherSonMDDNodePair> currentCombination) {
        int breakpoint = 0;
        for (int i = 0; i < currentCombination.size(); i++) {
            FatherSonMDDNodePair currentI = currentCombination.get(i);
            for (int j = i + 1; j < currentCombination.size(); j++) {
                FatherSonMDDNodePair currentJ = currentCombination.get(j);
                if (currentI.equals(currentJ)) {
                    try {
                        throw new Exception("currentI and currentJ can't be equals. if they are, we have an error...");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (currentCombination.get(i).colliding(currentCombination.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    private void expand(MergedMDDNode current) {
        List<MergedMDDNode> neighbors = getNeighbors(current);
        // TODO: 2/18/2020 maybe count how many expanded nodes here
        for (MergedMDDNode neighbor : neighbors) {
            addToOpen(neighbor);
            // TODO: 2/18/2020 maybe count how many generated nodes here
        }
        closedList.add(current);
    }

    private void addToOpen(MergedMDDNode node) {
        if (contentOfOpen.containsKey(node)) {
            MergedMDDNode inOpen = contentOfOpen.get(node);
            inOpen.addParents(node.getParents());
            for (MergedMDDNode parent : node.getParents()) {
                parent.fixNeighbor(inOpen);
            }
        } else if (closedList.contains(node)) {
            try {
                throw new Exception("The MDD supposed to be DAG, but we now found a cyclic path");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            openList.add(node);
            contentOfOpen.put(node, node);
        }
    }

    private MergedMDDNode pollFromOpen() {
        MergedMDDNode next = openList.poll();
        contentOfOpen.remove(next);
        return next;
    }

    private List<MergedMDDNode> getNeighbors(MergedMDDNode current) {
        Map<Agent, List<FatherSonMDDNodePair>> fatherSonPairs = current.getFatherSonPairs();
        List<List<FatherSonMDDNodePair>> agentFatherSonPairs = new ArrayList<>(fatherSonPairs.values());
        List<MergedMDDNode> neighbors = new LinkedList<>();
        int neighborDepth = current.getDepth() + 1;
        List<FatherSonMDDNodePair> currentCombination = new ArrayList<>();
        combinationUtil(agentFatherSonPairs, currentCombination, neighbors, 0, neighborDepth);

        for (MergedMDDNode neighbor : neighbors) {
            neighbor.addParent(current);
            current.addNeighbor(neighbor);
        }
        return neighbors;
    }

    private boolean isGoal(MergedMDDNode current) {
        return current.getDepth() == goalDepth;
    }

    /**
     * Can override this method to create your own search based Factory
     *
     * @return new closed list
     */
    protected Set<MergedMDDNode> createNewClosedList() {
        return new HashSet<>();
    }

    /**
     * Can override this method to create your own search based Factory
     *
     * @return new open list
     */
    protected Queue<MergedMDDNode> createNewOpenList() {
        return new LinkedList<>();
    }
}
