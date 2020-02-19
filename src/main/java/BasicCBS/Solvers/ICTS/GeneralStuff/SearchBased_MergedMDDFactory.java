package BasicCBS.Solvers.ICTS.GeneralStuff;

import BasicCBS.Instances.Agent;
import BasicCBS.Solvers.A_Solver;
import BasicCBS.Solvers.ICTS.HighLevel.ICTS_Solver;

import java.util.*;

public abstract class SearchBased_MergedMDDFactory implements I_MergedMDDFactory {
    private int goalDepth;

    protected abstract void initializeSearch();

    protected abstract boolean isOpenEmpty();

    protected abstract void addToClosed(MergedMDDNode node);

    @Override
    public MergedMDD create(Map<Agent, MDD> agentMDDs, ICTS_Solver highLevelSolver) {
        initializeSearch();

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
        while (!isOpenEmpty() && !highLevelSolver.reachedTimeout()) {
            MergedMDDNode current = pollFromOpen();
            if (isGoal(current)) {
                mergedMDD.setGoal(current);
                return mergedMDD;
            }

            expand(current);
        }

        return null;
    }

    protected static void combinationUtil(List<List<FatherSonMDDNodePair>> agentFatherSonPairs, List<FatherSonMDDNodePair> currentCombination, List<MergedMDDNode> neighbors, int index, int mddNodeDepth) {
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

    protected static boolean isValidCombination(List<FatherSonMDDNodePair> currentCombination) {
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

    protected void expand(MergedMDDNode current) {
        List<MergedMDDNode> neighbors = getNeighbors(current);
        // TODO: 2/18/2020 maybe count how many expanded nodes here
        for (MergedMDDNode neighbor : neighbors) {
            addToOpen(neighbor);
            // TODO: 2/18/2020 maybe count how many generated nodes here
        }
        addToClosed(current);
    }

    protected abstract void addToOpen(MergedMDDNode node);

    protected abstract MergedMDDNode pollFromOpen();

    protected List<MergedMDDNode> getNeighbors(MergedMDDNode current) {
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

    protected boolean isGoal(MergedMDDNode current) {
        return current.getDepth() == goalDepth;
    }
}
