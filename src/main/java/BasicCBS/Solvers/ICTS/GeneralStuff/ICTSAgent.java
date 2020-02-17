package BasicCBS.Solvers.ICTS.GeneralStuff;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Solvers.ICTS.LowLevel.AStar;
import BasicCBS.Solvers.ICTS.LowLevel.I_LowLevelSearcher;
import BasicCBS.Solvers.ICTS.LowLevel.Node;

import java.util.HashMap;
import java.util.Map;

public class ICTSAgent extends Agent {
    Map<Integer, MDD> mdds;
    I_LowLevelSearcher searcher;

    public ICTSAgent(int iD, I_Coordinate source, I_Coordinate target) {
        super(iD, source, target);
        mdds = new HashMap<Integer, MDD>();
    }

    public void setSearcher(I_LowLevelSearcher searcher){
        this.searcher = searcher;
    }

    public MDD getMDD(int depth){
        if(!mdds.containsKey(depth))
        {
            Node goal = searcher.continueSearching(depth);
            MDD curr = new MDD(goal);
            mdds.put(depth, curr);
        }
        return mdds.get(depth);
    }
}
