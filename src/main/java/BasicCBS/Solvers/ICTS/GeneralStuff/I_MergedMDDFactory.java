package BasicCBS.Solvers.ICTS.GeneralStuff;

import BasicCBS.Instances.Agent;

import java.util.Map;

public interface I_MergedMDDFactory {
    MergedMDD create(Map<Agent, MDD> agentMDDs);
}