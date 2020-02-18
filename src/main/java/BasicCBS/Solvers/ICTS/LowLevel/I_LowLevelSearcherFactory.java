package BasicCBS.Solvers.ICTS.LowLevel;

import BasicCBS.Instances.MAPF_Instance;

public interface I_LowLevelSearcherFactory {
    A_LowLevelSearcher createSearcher(MAPF_Instance instance, DistanceTableAStarHeuristicICTS heuristic);
}
