package BasicCBS.Solvers.ICTS.LowLevel;

import BasicCBS.Solvers.ICTS.GeneralStuff.MDD;

public abstract class A_LowLevelSearcher {
    protected int expandedNodesNum;
    protected int generatedNodesNum;

    public A_LowLevelSearcher() {
        expandedNodesNum = 0;
        generatedNodesNum = 0;
    }

    public int getExpandedNodesNum() {
        return expandedNodesNum;
    }

    public int getGeneratedNodesNum() {
        return generatedNodesNum;
    }

    /**
     * Searches for all the solutions in a wanted depth
     * Continuing the search from the last "checkpoint" that means that all of the open list and closed list is already saved in the searcher.
     * @param depthOfSolution - the depth of the wanted solutions
     * @return the goal state, which can easily be transferred to an MDD
     */
    public abstract MDD continueSearching(int depthOfSolution);
}
