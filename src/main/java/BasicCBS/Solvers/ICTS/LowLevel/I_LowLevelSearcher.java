package BasicCBS.Solvers.ICTS.LowLevel;

import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Solvers.Solution;

public interface I_LowLevelSearcher {
    /**
     * Searches for all the solutions in a wanted depth
     * Continuing the search from the last "checkpoint" that means that all of the open list and closed list is already saved in the searcher.
     * @param depthOfSolution - the depth of the wanted solutions
     * @return the goal state, which can easily be transferred to an MDD
     */
    Node continueSearching(int depthOfSolution);
}
