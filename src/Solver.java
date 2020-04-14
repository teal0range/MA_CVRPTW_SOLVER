import Algorithm.Core;
import Common.AlgoParam;
import Common.Instance;
import Common.Nodes;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Solver {
    public static void main(String[] args) throws FileNotFoundException {
        AlgoParam param = new AlgoParam(
                "CVRPTW",
                "181870063",
                "SouloStar",
                "EAMA",
                600,
                200,
                "", ".txt",
                "TestData"
        );
        preparePaths(param);
        ArrayList<Instance> instances = readInstances(param);
        System.out.println(instances.size() + " instances" + " > ");
        for (Instance instance : instances) {
            System.out.println("\t> " + instance.instName);
        }
        for (Instance instance : instances) {
            System.out.print(instance.instName + " > ");
            Core core = new Core(20000 / (instance.nodes.length - 1), 20,
                    instance, param.time_limit, param.genTime, param);
            core.run();
        }
    }

    @NotNull
    static ArrayList<Instance> readInstances(@NotNull AlgoParam param) throws FileNotFoundException {
        File data = new File(param.path_data);

        Queue<File> que = new LinkedList<>();
        if (data.isDirectory()) {
            que.offer(data);
        }

        ArrayList<File> files = new ArrayList<>();
        while (!que.isEmpty()) {
            File folder = que.poll();
            File[] tmpFiles = folder.listFiles();
            assert tmpFiles != null;
            for (File file : tmpFiles) {
                if (file.isDirectory()) {
                    que.offer(file);
                } else {
                    String fname = file.getName();
                    if (fname.matches("^" + param.teston_prefix + ".*" + param.teston_extension + "$")) {
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
            if (!dir_result.exists() || !dir_result.isDirectory()) dir_result.mkdir();
            File dir_problem = new File(dir_result, param.problem_name);
            if (!dir_problem.exists() || !dir_problem.isDirectory()) dir_problem.mkdir();
            File dir_algo = new File(dir_problem, param.algo_name);
            if (!dir_algo.exists() || !dir_algo.isDirectory()) dir_algo.mkdir();

            param.path_result_sol = dir_algo.getAbsolutePath();
            param.path_result_csv = dir_problem.getAbsolutePath() + "/" + param.csv_name();
            param.initial_result_csv();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
