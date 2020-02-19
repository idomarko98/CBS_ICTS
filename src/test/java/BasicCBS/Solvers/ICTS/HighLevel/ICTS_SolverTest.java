package BasicCBS.Solvers.ICTS.HighLevel;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.InstanceBuilders.InstanceBuilder_BGU;
import BasicCBS.Instances.InstanceManager;
import BasicCBS.Instances.InstanceProperties;
import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Instances.Maps.*;
import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Solvers.ICTS.GeneralStuff.*;
import BasicCBS.Solvers.ICTS.LowLevel.AStarFactory;
import BasicCBS.Solvers.ICTS.LowLevel.A_LowLevelSearcher;
import BasicCBS.Solvers.RunParameters;
import BasicCBS.Solvers.Solution;
import Environment.IO_Package.IO_Manager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class ICTS_SolverTest {
    private final Enum_MapCellType e = Enum_MapCellType.EMPTY;
    private final Enum_MapCellType w = Enum_MapCellType.WALL;
    private Enum_MapCellType[][] map_2D_circle = {
            {w, w, w, w, w, w},
            {w, w, e, e, e, w},
            {w, w, e, w, e, w},
            {w, w, e, e, e, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
    };
    private I_Map mapCircle = MapFactory.newSimple4Connected2D_GraphMap(map_2D_circle);

    private Enum_MapCellType[][] map_multiple_agents = {
            {w, w, w, w, w, w},
            {w, w, e, w, w, w},
            {w, w, e, w, e, w},
            {w, w, e, e, e, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
    };
    private I_Map mapMultipleAgents = MapFactory.newSimple4Connected2D_GraphMap(map_multiple_agents);

    private Enum_MapCellType[][] map_clear_circle = {
            {w, w, w, w, w, w},
            {w, w, e, e, e, w},
            {w, w, e, e, e, w},
            {w, w, e, e, e, w},
            {w, w, w, w, w, w},
            {w, w, w, w, w, w},
    };
    private I_Map mapClearCircle = MapFactory.newSimple4Connected2D_GraphMap(map_clear_circle);

    private Enum_MapCellType[][] map_multiple_agents2WayForOne = {
            {w, w, w, w, w, w},
            {w, e, e, e, w, w},
            {w, e, w, e, w, w},
            {w, e, e, e, e, e},
            {w, w, w, w, w, e},
            {w, w, w, w, w, e},
    };
    private I_Map mapMultipleAgents2WayForOne = MapFactory.newSimple4Connected2D_GraphMap(map_multiple_agents2WayForOne);

    Enum_MapCellType[][] map_2D_empty = {
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
            {e, e, e, e, e, e},
    };
    private I_Map mapEmpty = MapFactory.newSimple4Connected2D_GraphMap(map_2D_empty);

    Enum_MapCellType[][] map_2D_withPocket = {
            {e, w, e, w, e, w},
            {e, w, e, e, e, e},
            {w, w, e, w, w, e},
            {e, e, e, e, e, e},
            {e, e, w, e, w, w},
            {w, e, w, e, e, e},
    };
    private I_Map mapWithPocket = MapFactory.newSimple4Connected2D_GraphMap(map_2D_withPocket);

    private I_Coordinate coor12 = new Coordinate_2D(1, 2);
    private I_Coordinate coor13 = new Coordinate_2D(1, 3);
    private I_Coordinate coor14 = new Coordinate_2D(1, 4);
    private I_Coordinate coor22 = new Coordinate_2D(2, 2);
    private I_Coordinate coor24 = new Coordinate_2D(2, 4);
    private I_Coordinate coor32 = new Coordinate_2D(3, 2);
    private I_Coordinate coor33 = new Coordinate_2D(3, 3);
    private I_Coordinate coor34 = new Coordinate_2D(3, 4);

    private I_Coordinate coor11 = new Coordinate_2D(1, 1);
    private I_Coordinate coor43 = new Coordinate_2D(4, 3);
    private I_Coordinate coor53 = new Coordinate_2D(5, 3);
    private I_Coordinate coor05 = new Coordinate_2D(0, 5);
    private I_Coordinate coor55 = new Coordinate_2D(5, 5);
    private I_Coordinate coor45 = new Coordinate_2D(4, 5);

    private I_Coordinate coor04 = new Coordinate_2D(0, 4);
    private I_Coordinate coor00 = new Coordinate_2D(0, 0);

    private I_Location cell12Circle = mapCircle.getMapCell(coor12);
    private I_Location cell13Circle = mapCircle.getMapCell(coor13);
    private I_Location cell14Circle = mapCircle.getMapCell(coor14);
    private I_Location cell22Circle = mapCircle.getMapCell(coor22);
    private I_Location cell24Circle = mapCircle.getMapCell(coor24);
    private I_Location cell32Circle = mapCircle.getMapCell(coor32);
    private I_Location cell33Circle = mapCircle.getMapCell(coor33);
    private I_Location cell34Circle = mapCircle.getMapCell(coor34);

    private I_Location cell11 = mapCircle.getMapCell(coor11);
    private I_Location cell43 = mapCircle.getMapCell(coor43);
    private I_Location cell53 = mapCircle.getMapCell(coor53);
    private I_Location cell05 = mapCircle.getMapCell(coor05);

    private I_Location cell04 = mapCircle.getMapCell(coor04);
    private I_Location cell00 = mapCircle.getMapCell(coor00);

    private Agent agent33to12 = new ICTSAgent(0, coor33, coor12);
    private Agent agent12to33 = new ICTSAgent(1, coor12, coor33);
    private Agent agent53to05 = new ICTSAgent(0, coor53, coor05);
    private Agent agent43to11 = new ICTSAgent(0, coor43, coor11);
    private Agent agent04to00 = new ICTSAgent(0, coor04, coor00);
    private Agent agent33to24 = new ICTSAgent(0, coor33, coor24);
    private Agent agent33to22 = new ICTSAgent(0, coor33, coor22);
    private Agent agent32to12 = new ICTSAgent(1, coor32, coor12);
    private Agent agent14to32 = new ICTSAgent(1, coor14, coor32);
    private Agent agent33to55 = new ICTSAgent(0, coor33, coor55);
    private Agent agent22to12 = new ICTSAgent(1, coor22, coor12);
    private Agent agent22to13 = new ICTSAgent(1, coor22, coor13);
    private Agent agent14to34 = new ICTSAgent(2, coor14, coor34);
    private Agent agent33to45 = new ICTSAgent(0, coor33, coor45);
    private Agent agent13to24 = new ICTSAgent(0, coor13, coor24);
    private Agent agent34to13 = new ICTSAgent(1, coor34, coor13);
    private Agent agent32to34 = new ICTSAgent(2, coor32, coor34);

    InstanceBuilder_BGU builder = new InstanceBuilder_BGU();
    InstanceManager im = new InstanceManager(IO_Manager.buildPath(new String[]{IO_Manager.testResources_Directory, "Instances"}),
            new InstanceBuilder_BGU(), new InstanceProperties(new MapDimensions(new int[]{6, 6}), 0f, new int[]{1}));

    private MAPF_Instance instanceEmpty1 = new MAPF_Instance("instanceEmpty", mapEmpty, new Agent[]{agent53to05});
    private MAPF_Instance instanceEmpty2 = new MAPF_Instance("instanceEmpty", mapEmpty, new Agent[]{agent43to11});
    private MAPF_Instance instance1stepSolution = im.getNextInstance();
    private MAPF_Instance instanceCircle1 = new MAPF_Instance("instanceCircle1", mapCircle, new Agent[]{agent33to12});
    private MAPF_Instance instanceMapMultipleAgents = new MAPF_Instance("instanceMapMultipleAgents", mapMultipleAgents, new Agent[]{agent33to24, agent32to12});
    private MAPF_Instance instanceMap3Agents = new MAPF_Instance("instanceMap3Agents", mapCircle, new Agent[]{agent33to22, agent22to13, agent14to34});
    private MAPF_Instance instanceMap3AgentsDifferentPathSizeWithCollisions = new MAPF_Instance("instanceMap3AgentsDifferentPathSizeWithCollisions", mapClearCircle, new Agent[]{agent13to24, agent34to13, agent32to34});
    private MAPF_Instance instanceMapMultipleAgentsCollidingInCircle = new MAPF_Instance("instanceMapMultipleAgentsCollidingInCircle", mapCircle, new Agent[]{agent33to22, agent14to32});
    private MAPF_Instance instanceMapMultipleAgentsDifferentPathSize = new MAPF_Instance("instanceMapMultipleAgentsDifferentPathSize", mapMultipleAgents, new Agent[]{agent33to24, agent22to12});
    private MAPF_Instance instanceMapMultipleAgents2WayForOne = new MAPF_Instance("instanceMapMultipleAgents2WayForOne", mapMultipleAgents2WayForOne, new Agent[]{agent33to55, agent32to12});
    private MAPF_Instance instanceMapMultipleAgents2WayForOneDifferentPathSize = new MAPF_Instance("instanceMapMultipleAgents2WayForOneDifferentPathSize", mapMultipleAgents2WayForOne, new Agent[]{agent33to45, agent32to12});
    private MAPF_Instance instanceCircle2 = new MAPF_Instance("instanceCircle1", mapCircle, new Agent[]{agent12to33});
    private MAPF_Instance instanceUnsolvable = new MAPF_Instance("instanceUnsolvable", mapWithPocket, new Agent[]{agent04to00});

    Map<Agent, A_LowLevelSearcher> aStars;
    Map<Agent, MDD> mdds;

    @BeforeEach
    void setUp() {

    }

    @Test
    void Test1WayEachSameDepthDebug() {
        RunTest(instanceMapMultipleAgents);
    }

    @Test
    void Test1WayEachSameDepth3AgentsDebug() {
        RunTest(instanceMap3Agents);
    }

    @Test
    void Test1WayEachDifferentDepthDebug() {
        RunTest(instanceMapMultipleAgentsDifferentPathSize);
    }

    @Test
    void Test2WayForOneSameDepthDebug() {
        RunTest(instanceMapMultipleAgents2WayForOne);
    }

    @Test
    void Test2WayForOneDifferentDepthDebug() {
        RunTest(instanceMapMultipleAgents2WayForOneDifferentPathSize);
    }

    @Test
    void Test2WayForOneDifferentDepthOneCollidingDebug() {
        RunTest(instanceMapMultipleAgentsCollidingInCircle);
    }

    @Test
    void Test3AgentsDifferentPathSizeWithCollisionsDebug() {
        RunTest(instanceMap3AgentsDifferentPathSizeWithCollisions);
    }

    void RunTest(MAPF_Instance instance){
        MAPF_Instance testInstance = instance;
        ICTS_Solver solver = new ICTS_Solver(new ICT_NodeMakespanComparator(), new AStarFactory(), new BreadthFirstSearch_MergedMDDFactory(), true);
        Solution solution = solver.runAlgorithm(testInstance, new RunParameters());

        int breakPoint = 0;
    }
}


