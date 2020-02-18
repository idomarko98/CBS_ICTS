package BasicCBS.Solvers.ICTS.LowLevel;

import BasicCBS.Instances.MAPF_Instance;

public class AStarFactory implements I_LowLevelSearcherFactory {
    @Override
    public A_LowLevelSearcher createSearcher(MAPF_Instance instance, DistanceTableAStarHeuristicICTS heuristic) {
        return new AStar(instance, heuristic);
    }
}
