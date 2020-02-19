package BasicCBS.Solvers.ICTS.LowLevel;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.InstanceBuilders.InstanceBuilder_BGU;
import BasicCBS.Instances.InstanceManager;
import BasicCBS.Instances.InstanceProperties;
import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Instances.Maps.*;
import BasicCBS.Instances.Maps.Coordinates.Coordinate_2D;
import BasicCBS.Instances.Maps.Coordinates.I_Coordinate;
import BasicCBS.Solvers.ICTS.GeneralStuff.MDD;
import Environment.IO_Package.IO_Manager;
import Environment.RunManagerSimpleExample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AStarTest {
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

    private I_Coordinate coor12 = new Coordinate_2D(1,2);
    private I_Coordinate coor13 = new Coordinate_2D(1,3);
    private I_Coordinate coor14 = new Coordinate_2D(1,4);
    private I_Coordinate coor22 = new Coordinate_2D(2,2);
    private I_Coordinate coor24 = new Coordinate_2D(2,4);
    private I_Coordinate coor32 = new Coordinate_2D(3,2);
    private I_Coordinate coor33 = new Coordinate_2D(3,3);
    private I_Coordinate coor34 = new Coordinate_2D(3,4);

    private I_Coordinate coor11 = new Coordinate_2D(1,1);
    private I_Coordinate coor43 = new Coordinate_2D(4,3);
    private I_Coordinate coor53 = new Coordinate_2D(5,3);
    private I_Coordinate coor05 = new Coordinate_2D(0,5);

    private I_Coordinate coor04 = new Coordinate_2D(0,4);
    private I_Coordinate coor00 = new Coordinate_2D(0,0);

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

    private Agent agent33to12 = new Agent(0, coor33, coor12);
    private Agent agent12to33 = new Agent(1, coor12, coor33);
    private Agent agent53to05 = new Agent(0, coor53, coor05);
    private Agent agent43to11 = new Agent(0, coor43, coor11);
    private Agent agent04to00 = new Agent(0, coor04, coor00);

    InstanceBuilder_BGU builder = new InstanceBuilder_BGU();
    InstanceManager im = new InstanceManager(IO_Manager.buildPath( new String[]{   IO_Manager.testResources_Directory,"Instances"}),
            new InstanceBuilder_BGU(), new InstanceProperties(new MapDimensions(new int[]{6,6}),0f,new int[]{1}));

    private MAPF_Instance instanceEmpty1 = new MAPF_Instance("instanceEmpty", mapEmpty, new Agent[]{agent53to05});
    private MAPF_Instance instanceEmpty2 = new MAPF_Instance("instanceEmpty", mapEmpty, new Agent[]{agent43to11});
    private MAPF_Instance instance1stepSolution = im.getNextInstance();
    private MAPF_Instance instanceCircle1 = new MAPF_Instance("instanceCircle1", mapCircle, new Agent[]{agent33to12});
    private MAPF_Instance instanceCircle2 = new MAPF_Instance("instanceCircle1", mapCircle, new Agent[]{agent12to33});
    private MAPF_Instance instanceUnsolvable = new MAPF_Instance("instanceUnsolvable", mapWithPocket, new Agent[]{agent04to00});

    A_LowLevelSearcher aStar;

    @BeforeEach
    void setUp() {

    }

    @Test
    void TestDebug() {/*
        MAPF_Instance testInstance = instanceCircle1;
        DistanceTableAStarHeuristicICTS heuristicICTS = new DistanceTableAStarHeuristicICTS(testInstance.agents, testInstance.map);
        aStar = new AStar(testInstance, heuristicICTS);
        I_Location start = testInstance.map.getMapCell(testInstance.agents.get(0).source);
        int depth = heuristicICTS.getDistanceDictionaries().get(testInstance.agents.get(0)).get(start);
        MDD solution = aStar.continueSearching(depth);
        int breakPoint = 0;
        MDD nextSolution = aStar.continueSearching(depth + 1);
        breakPoint = 1;
        MDD nextNextSolution = aStar.continueSearching(depth + 2);
        breakPoint = 2;
        */
    }

    @Test
    void TestHardMap() {
        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.resources_Directory,
                "Instances\\\\BGU_Instances\\\\den520d-10-0"});
        InstanceManager.InstancePath instancePath = new InstanceManager.InstancePath(path);


        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(null, new InstanceBuilder_BGU());
        /*
        MAPF_Instance instance = RunManagerSimpleExample.getInstanceFromPath(instanceManager, instancePath);

        MAPF_Instance testInstance = instance.getSubproblemFor(instance.agents.get(0));
        DistanceTableAStarHeuristicICTS heuristicICTS = new DistanceTableAStarHeuristicICTS(testInstance.agents, testInstance.map);
        aStar = new AStar(testInstance, heuristicICTS);
        I_Location start = testInstance.map.getMapCell(testInstance.agents.get(0).source);
        int depth = heuristicICTS.getDistanceDictionaries().get(testInstance.agents.get(0)).get(start);
        MDD solution = aStar.continueSearching(depth);
        int breakPoint = 0;
        MDD nextSolution = aStar.continueSearching(depth + 1);
        breakPoint = 1;
        MDD nextNextSolution = aStar.continueSearching(depth + 2);
        breakPoint = 2;*/
    }
}
