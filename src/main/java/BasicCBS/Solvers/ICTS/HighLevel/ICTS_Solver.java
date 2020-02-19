package BasicCBS.Solvers.ICTS.HighLevel;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.A_Solver;
import BasicCBS.Solvers.ICTS.GeneralStuff.*;
import BasicCBS.Solvers.ICTS.LowLevel.DistanceTableAStarHeuristicICTS;
import BasicCBS.Solvers.ICTS.LowLevel.A_LowLevelSearcher;
import BasicCBS.Solvers.ICTS.LowLevel.I_LowLevelSearcherFactory;
import BasicCBS.Solvers.RunParameters;
import BasicCBS.Solvers.Solution;
import Environment.Metrics.InstanceReport;

import java.util.*;

public class ICTS_Solver extends A_Solver {
    private Set<ICT_Node> contentOfOpen;
    private Queue<ICT_Node> openList;
    private Set<ICT_Node> closedList;
    private ICT_NodeComparator comparator;
    private I_LowLevelSearcherFactory searcherFactory;
    private I_MergedMDDFactory mergedMDDFactory;

    private int expandedHighLevelNodesNum;
    private int generatedHighLevelNodesNum;

    public ICTS_Solver(ICT_NodeComparator comparator, I_LowLevelSearcherFactory searcherFactory, I_MergedMDDFactory mergedMDDFactory) {
        this.comparator = comparator;
        this.searcherFactory = searcherFactory;
        this.mergedMDDFactory = mergedMDDFactory;
    }

    protected Queue<ICT_Node> createOpenList() {
        return new PriorityQueue<>(comparator);
    }

    protected Set<ICT_Node> createClosedList() {
        return new HashSet<>();
    }

    @Override
    protected Solution runAlgorithm(MAPF_Instance instance, RunParameters parameters) {
        instance = ICTS_MAPFInstance.Copy(instance);
        if (!initializeSearch(instance))
            return null;

        boolean checkPairWiseMDDs = false && instance.agents.size() > 2; // TODO: 2/18/2020 add this to the RunParameters so we will have control over it
        while (!openList.isEmpty()) {
            ICT_Node current = pollFromOpen();
            boolean pairFlag = true;
            if (checkPairWiseMDDs) {
                pairFlag = pairWiseGoalTest(instance, current);
            }
            if (!checkPairWiseMDDs || pairFlag) {
                Map<Agent, MDD> mdds = new HashMap<>();
                for (Agent a : instance.agents) {
                    ICTSAgent agent = (ICTSAgent) a;
                    MDD mdd = agent.getMDD(current.getCost(agent));
                    mdds.put(agent, mdd);
                }
                MergedMDD mergedMDD = mergedMDDFactory.create(mdds);
                if (mergedMDD != null) {
                    //We found the goal!
                    updateExpandedAndGeneratedNum(instance);
                    return mergedMDD.getSolution();
                }
            }
            expand(current);
        }

        //Not possible to get here!
        try {
            throw new Exception("ICTS does not stop until it finds a solution... not possible to get here!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateExpandedAndGeneratedNum(instance);
        return null;
    }

    private void updateExpandedAndGeneratedNum(MAPF_Instance instance) {
        int totalLowLevelExpanded = 0;
        int totalLowLevelGenerated = 0;
        for (Agent agent : instance.agents) {
            totalLowLevelExpanded += ((ICTSAgent) agent).getExpandedNodesNum();
            totalLowLevelGenerated += ((ICTSAgent) agent).getGeneratedNodesNum();
        }
        super.totalLowLevelStatesExpanded = totalLowLevelExpanded;
        super.totalLowLevelStatesGenerated = totalLowLevelGenerated;
    }

    @Override
    protected void writeMetricsToReport(Solution solution) {
        super.writeMetricsToReport(solution);
        super.instanceReport.putIntegerValue(InstanceReport.StandardFields.generatedNodes, this.generatedHighLevelNodesNum);
        super.instanceReport.putIntegerValue(InstanceReport.StandardFields.expandedNodes, this.expandedHighLevelNodesNum);
        super.instanceReport.putStringValue(InstanceReport.StandardFields.solver, getName());
    }

    private String getName() {
        return "ICTS";
    }

    private boolean pairWiseGoalTest(MAPF_Instance instance, ICT_Node current) {
        for (int i = 0; i < instance.agents.size(); i++) {
            Agent aI = instance.agents.get(i);
            for (int j = i + 1; j < instance.agents.size(); j++) {
                Agent aJ = instance.agents.get(j);

                ICTSAgent agentI = (ICTSAgent) aI;
                ICTSAgent agentJ = (ICTSAgent) aJ;
                MDD mddI = agentI.getMDD(current.getCost(agentI));
                MDD mddJ = agentJ.getMDD(current.getCost(agentJ));
                Map<Agent, MDD> pairwiseMap = new HashMap<>();
                pairwiseMap.put(agentI, mddI);
                pairwiseMap.put(agentJ, mddJ);
                MergedMDD pairwiseMergedMDD = mergedMDDFactory.create(pairwiseMap);
                if (pairwiseMergedMDD == null) //couldn't find solution between 2 agents
                    return false;
            }
        }
        return true;
    }

    private void expand(ICT_Node current) {
        List<ICT_Node> children = current.getChildren();
        expandedHighLevelNodesNum++;
        for (ICT_Node child : children) {
            addToOpen(child);
            generatedHighLevelNodesNum++;
        }
        addToClosed(current);
    }

    private void addToClosed(ICT_Node current) {
        closedList.add(current);
    }

    private boolean initializeSearch(MAPF_Instance instance) {
        openList = createOpenList();
        contentOfOpen = new HashSet<>();
        closedList = createClosedList();
        expandedHighLevelNodesNum = 0;
        generatedHighLevelNodesNum = 0;

        DistanceTableAStarHeuristicICTS heuristicICTS = new DistanceTableAStarHeuristicICTS(instance.agents, instance.map);
        Map<Agent, Integer> startCosts = new HashMap<>();
        for (Agent agent : instance.agents) {
            if (!(agent instanceof ICTSAgent)) {
                try {
                    throw new InputMismatchException("The agents of the ICTS_Searcher must be of type ICTSAgent");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            MAPF_Instance agentInstance = instance.getSubproblemFor(agent);
            A_LowLevelSearcher searcher = searcherFactory.createSearcher(agentInstance, heuristicICTS);
            ((ICTSAgent) agent).setSearcher(searcher);
            I_Location start = instance.map.getMapCell(agent.source);
            Integer depth = heuristicICTS.getDistanceDictionaries().get(agent).get(start);
            if (depth == null) {
                //The single agent path does not exist
                try {
                    throw new Exception("The single agent plan for agent " + agent.iD + " does not exist!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            startCosts.put(agent, depth);
        }
        ICT_Node startNode = new ICT_Node(startCosts);
        generatedHighLevelNodesNum++;
        addToOpen(startNode);
        return true;
    }

    private ICT_Node pollFromOpen() {
        ICT_Node current = openList.poll();
        contentOfOpen.remove(current);
        return current;
    }

    private void addToOpen(ICT_Node node) {
        if (!contentOfOpen.contains(node) && !closedList.contains(node)) {
            openList.add(node);
            contentOfOpen.add(node);
        }
    }
}
