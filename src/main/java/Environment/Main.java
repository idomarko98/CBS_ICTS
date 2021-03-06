package Environment;

import BasicCBS.Instances.InstanceBuilders.InstanceBuilder_BGU;
import BasicCBS.Instances.InstanceManager;
import BasicCBS.Instances.MAPF_Instance;
import BasicCBS.Solvers.ICTS.GeneralStuff.BreadthFirstSearch_MergedMDDFactory;
import BasicCBS.Solvers.ICTS.GeneralStuff.ICTS_MAPFInstance;
import BasicCBS.Solvers.ICTS.HighLevel.ICTS_Solver;
import BasicCBS.Solvers.ICTS.HighLevel.ICT_NodeMakespanComparator;
import BasicCBS.Solvers.ICTS.LowLevel.AStarFactory;
import BasicCBS.Solvers.I_Solver;
import BasicCBS.Solvers.RunParameters;
import BasicCBS.Solvers.Solution;
import Environment.IO_Package.IO_Manager;
import Environment.Metrics.InstanceReport;
import Environment.Metrics.S_Metrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * We wanted to keep {@link #main(String[])} short and simple as possible
 * Things to consider before running:
 *      1. Check that the {@link #resultsOutputDir} is correct
 *      2. Check that {@link #outputResults()} is as you need
 *      3. Running an experiment should be done through {@link A_RunManager},
 *          Solving a single Instance is also possible by giving a path.
 *
 * For more information, view the examples below
 */
public class Main {

    // where to put generated reports. The default is a new folder called CBS_Results, under the user's home directory.
    public static final String resultsOutputDir = "D:\\Rotem\\Search_Course\\Project_Results";
    //public static final String resultsOutputDir = IO_Manager.buildPath(new String[]{System.getProperty("user.home"), "CBS_Results"});
//    public static final String resultsOutputDir = IO_Manager.buildPath(new String[]{   IO_Manager.testResources_Directory +
//                                                                                        "\\Reports default directory"});

    public static void main(String[] args) {
        if(verifyOutputPath()){
            // will solve a single instance and print the solution
            //solveOneInstanceExample();
            // will solve multiple instances and print a simple report for each instance
            runMultipleExperimentsExample();
            // will solve a set of instances. These instances have known optimal solution costs (found at
            // src\test\resources\TestingBenchmark\Results.csv), and so can be used as a benchmark.
            //runTestingBenchmarkExperiment();
            // all examples will also produce a report in CSV format, and save it to resultsOutputDir (see above)

        }
    }

    private static boolean verifyOutputPath() {
        File directory = new File(resultsOutputDir);
        if (! directory.exists()){
            boolean created = directory.mkdir();
            if(!created){
                String errString = "Could not locate or create output directory.";
                System.out.println(errString);
                return false;
            }
        }
        return true;
    }

    public static void solveOneInstanceExample(){

        /*  =   Set Path   =*/
        String path = IO_Manager.buildPath( new String[]{   IO_Manager.resources_Directory,
                                                            "Instances\\\\BGU_Instances\\\\den520d-2-0"});
        InstanceManager.InstancePath instancePath = new InstanceManager.InstancePath(path);


        /*  =   Set Instance Manager   =  */
        InstanceManager instanceManager = new InstanceManager(null, new InstanceBuilder_BGU());

        MAPF_Instance instance = RunManagerSimpleExample.getInstanceFromPath(instanceManager, instancePath);
        MAPF_Instance ictsInstance = ICTS_MAPFInstance.Copy(instance);
        // Solve
        I_Solver solver = new ICTS_Solver(new ICT_NodeMakespanComparator(), new AStarFactory(), new BreadthFirstSearch_MergedMDDFactory(), true);
        //CBS_Solver solver = new CBS_Solver();
        RunParameters runParameters = new RunParameters();
        Solution solution = solver.solve(ictsInstance, runParameters);

        System.out.println("Done!");
        //output results
        System.out.println(solution.readableToString());
        outputResults();
    }

    public static void runMultipleExperimentsExample(){
        RunManagerSimpleExample runManagerSimpleExample = new RunManagerSimpleExample();
        runManagerSimpleExample.runAllExperiments();

        outputResults();
    }

    public static void runTestingBenchmarkExperiment(){
        TestingBenchmarkRunManager testingBenchmarkRunManager = new TestingBenchmarkRunManager();
        testingBenchmarkRunManager.runAllExperiments();

        outputResults();
    }


    /**
     * An example of a simple output of results to a file. It is best to handle this inside your custom
     * {@link A_RunManager run managers} instead.
     * Note that you can add more fields here, if you want metrics that are collected and not exported.
     * Note that you can easily add other metrics which are not currently collected. see {@link S_Metrics}.
     */
    static void outputResults() {
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd-HH-mm-ss");
        String updatedPath = resultsOutputDir + "\\results " + dateFormat.format(System.currentTimeMillis()) + " .csv";
        try {
            S_Metrics.exportCSV(new FileOutputStream(updatedPath),
                    new String[]{   InstanceReport.StandardFields.experimentName,
                                    InstanceReport.StandardFields.mapName,
                                    InstanceReport.StandardFields.numAgents,
                                    InstanceReport.StandardFields.obstacleRate,
                                    InstanceReport.StandardFields.solver,
                                    InstanceReport.StandardFields.solved,
                                    InstanceReport.StandardFields.elapsedTimeMS,
                                    InstanceReport.StandardFields.expandedNodes,
                                    InstanceReport.StandardFields.generatedNodes,
                                    InstanceReport.StandardFields.expandedNodesLowLevel,
                                    InstanceReport.StandardFields.generatedNodesLowLevel,
                                    InstanceReport.StandardFields.solutionCost,
                                    InstanceReport.StandardFields.solution});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
