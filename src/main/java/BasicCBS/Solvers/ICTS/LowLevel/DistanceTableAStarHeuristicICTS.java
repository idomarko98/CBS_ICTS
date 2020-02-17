package BasicCBS.Solvers.ICTS.LowLevel;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Instances.Maps.I_Map;
import BasicCBS.Solvers.AStar.DistanceTableAStarHeuristic;
import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;

import java.util.List;
import java.util.Map;

public class DistanceTableAStarHeuristicICTS extends DistanceTableAStarHeuristic {
    public DistanceTableAStarHeuristicICTS(List<? extends Agent> agents, I_Map map) {
        super(agents, map);
    }

    public float getH(Node node) {
        Map<I_Location, Integer> relevantDictionary = getDistanceDictionaries().get(node.getAgent());
        return relevantDictionary.get(node.getLocation());
    }
}
