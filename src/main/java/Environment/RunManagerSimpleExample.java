package Environment;

import BasicCBS.Solvers.ICTS.GeneralStuff.BreadthFirstSearch_MergedMDDFactory;
import BasicCBS.Solvers.ICTS.GeneralStuff.DFS_ID_MergedMDDFactory;
import BasicCBS.Solvers.ICTS.GeneralStuff.DepthFirstSearch_MergedMDDFactory;
import BasicCBS.Solvers.ICTS.GeneralStuff.I_MergedMDDFactory;
import BasicCBS.Solvers.ICTS.HighLevel.ICTS_Solver;
import BasicCBS.Solvers.ICTS.HighLevel.ICT_NodeMakespanComparator;
import BasicCBS.Solvers.ICTS.LowLevel.AStarFactory;
import Environment.IO_Package.IO_Manager;
import BasicCBS.Instances.InstanceBuilders.InstanceBuilder_BGU;
import BasicCBS.Instances.InstanceBuilders.InstanceBuilder_MovingAI;
import BasicCBS.Instances.InstanceManager;
import BasicCBS.Instances.InstanceProperties;
import BasicCBS.Instances.Maps.MapDimensions;
import BasicCBS.Solvers.AStar.SingleAgentAStar_Solver;
import BasicCBS.Solvers.CBS.CBS_Solver;
import BasicCBS.Solvers.PrioritisedPlanning.PrioritisedPlanning_Solver;


public class RunManagerSimpleExample extends A_RunManager {

    /*  = Set BasicCBS.Solvers =  */
    @Override
    void setSolvers() {
        //this.solvers.add(new PrioritisedPlanning_Solver(new SingleAgentAStar_Solver()));
        this.solvers.add(new CBS_Solver());
        this.solvers.add(createNewICTSSolver(true));
        //this.solvers.add(createNewICTSSolver(false));
    }

    ICTS_Solver createNewICTSSolver(boolean usePairWiseGoalTest){
        I_MergedMDDFactory mergedMDDFactory = new DFS_ID_MergedMDDFactory();
        //I_MergedMDDFactory mergedMDDFactory = new DepthFirstSearch_MergedMDDFactory();
        //I_MergedMDDFactory mergedMDDFactory = new BreadthFirstSearch_MergedMDDFactory();
        return new ICTS_Solver(new ICT_NodeMakespanComparator(), new AStarFactory(), mergedMDDFactory, usePairWiseGoalTest);
    }

    /*  = Set Experiments =  */
    @Override
    void setExperiments() {
        addExperiment_brc202d();
        addExperiment_den520d();
        addExperiment_lak303d();
        /*
        addExperiment_16_7();
        addExperimentMovingAI_8room();
        */
    }


    /* = Experiments =  */
    private int [] agentNumberExperiments = new int[]{2,3,4,5,6,7,8,9,10};

    private void addExperiment_brc202d(){
        addExperiment("brc202d", agentNumberExperiments/*new int[]{5,10,15,20,25}*/);
    }

    private void addExperiment_den520d(){
        addExperiment("den520d", agentNumberExperiments/*new int[]{5,10,15,20,25,30,35,40,45,50,55}*/);
    }

    private void addExperiment_lak303d(){
        addExperiment("lak303d", agentNumberExperiments/*new int[]{5,10,15,20,25,30,35,40}*/);
    }

    private void addExperiment(String name, int[] agentNums){
        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.resources_Directory,
                "Instances\\\\Experiments\\\\" + name});

        /*  =   Set Properties   =  */
        InstanceProperties properties = new InstanceProperties(null, -1, agentNums);


        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, new InstanceBuilder_MovingAI(), properties);

        /*  =   Add new experiment   =  */
        Experiment gridExperiment = new Experiment("Experiment_" + name, instanceManager);
        this.experiments.add(gridExperiment);
    }

    private void addExperiment_16_7(){
        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.resources_Directory,
                                                            "Instances\\\\BGU_Instances"});

        /*  =   Set Properties   =  */
        InstanceProperties properties = new InstanceProperties(new MapDimensions(16,16), 0, new int[]{7});
        int numOfInstances = 1;

        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, new InstanceBuilder_BGU(),properties);

        /*  =   Add new experiment   =  */
        Experiment gridExperiment = new Experiment("Experiment_16_7", instanceManager,numOfInstances);
        this.experiments.add(gridExperiment);
    }

    private void addExperimentMovingAI_8room(){
        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.resources_Directory,
                                                            "Instances\\\\MovingAI_Instances"});

        /*  =   Set Properties   =  */
        InstanceProperties properties = new InstanceProperties(new MapDimensions(512,512), -1, new int[]{7,10,15});


        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(path, new InstanceBuilder_MovingAI(), properties);

        /*  =   Add new experiment   =  */
        Experiment gridExperiment = new Experiment("Experiment_8_Room", instanceManager);
        this.experiments.add(gridExperiment);
    }



}
