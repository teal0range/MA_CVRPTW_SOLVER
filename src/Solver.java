import Algorithm.Core;
import Algorithm.RoutesMinimizer;
import Common.AlgoParam;
import Common.Instance;
import Common.Nodes;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Solver {
    public static void main(String[] args) throws FileNotFoundException {
        AlgoParam param = new AlgoParam(
                "VRPTW",
                "181870063",
                "Guo Junjie",
                "EAMA",
                60,
                "", ".txt",
                "TestData"
        );
        long t1 = System.currentTimeMillis();
        preparePaths(param);
        ArrayList<Instance> instances = readInstances(param);
        System.out.println(System.currentTimeMillis()-t1);
        for (Instance instance:instances){
            RoutesMinimizer rtm = new RoutesMinimizer(instance);
            rtm.determineM();
        }
    }
    @NotNull
    static ArrayList<Instance> readInstances(AlgoParam param) throws FileNotFoundException {
        File data = new File(param.path_data);

        Queue<File> que = new LinkedList<>();
        if(data.isDirectory()) {
            que.offer(data);
        }

        ArrayList<File> files = new ArrayList<>();
        while(!que.isEmpty()){
            File folder = que.poll();
            File[] tmpFiles = folder.listFiles();
            for(File file: tmpFiles){
                if(file.isDirectory()){
                    que.offer(file);
                }
                else {
                    String fname = file.getName();
                    if(fname.startsWith(param.teston_prefix) && fname.endsWith(param.teston_extension)){
                        files.add(file);
                    }
                }
            }
        }

        ArrayList<Instance> instances = new ArrayList<>();
        for(File file: files){
            Instance inst = readInstanceVRPTW(file);
            instances.add(inst);
        }
        return instances;
    }
    @NotNull
    public static Instance readInstanceVRPTW(File file) throws FileNotFoundException {
        Scanner cin = new Scanner(file);
        Instance inst = new Instance();
        inst.instName = file.getName();

        String line = cin.nextLine();
        while (!line.startsWith("CUST ")) {
            if (line.startsWith("NUMBER     CAPACITY")) {
                inst.maxVehicles = cin.nextInt();
                inst.Capacity = cin.nextInt();
            }
            line = cin.nextLine();
        }

        ArrayList<Nodes> ls = new ArrayList<>(101);

        while (cin.hasNext()) {
            int id = cin.nextInt();
            int x = cin.nextInt();
            int y = cin.nextInt();
            int demand = cin.nextInt();
            int earlyTime = cin.nextInt();
            int lateTime = cin.nextInt();
            int serviceTime = cin.nextInt();
            Nodes node = new Nodes(id, demand, x, y, serviceTime, earlyTime, lateTime);
            ls.add(node);
            inst.n++;
        }

        inst.nodes = ls.toArray(new Nodes[inst.n]);
        inst.computeDistance();
        return inst;
    }

    static void preparePaths(AlgoParam param) {
        try{
            File dir_result = new File("result");
            if(!dir_result.exists()|| !dir_result.isDirectory()){
                dir_result.mkdir();
            }
            File dir_problem = new File(dir_result, param.problem_name);
            if(!dir_problem.exists() || !dir_problem.isDirectory()){
                dir_problem.mkdir();
            }
            File dir_algo = new File(dir_problem, param.algo_name);
            if(!dir_algo.exists() || !dir_algo.isDirectory()){
                dir_algo.mkdir();
            }

            param.path_result_sol = dir_algo.getAbsolutePath();
            param.path_result_csv = dir_problem.getAbsolutePath() + "/" + param.csv_name();
            param.initial_result_csv();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
