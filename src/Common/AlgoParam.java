package Common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

/**
 * AlgoParam
 * - parameters of algorithm
 */

public class AlgoParam {
    public static int seed = 3;
    public static double RC_EPS = 1.0e-6; // Tolerance;

    public String problem_name;
    public String author_id;
    public String author_name;
    public String algo_name;
    public int time_limit;
    public String teston_prefix;
    public String teston_extension;
    public String path_data;
    public String path_result_sol;
    public String path_result_csv;

    public AlgoParam(String problem_name, String author_id, String author_name, String algo_name, int time_limit, String teston_prefix, String teston_extension, String path_data) {
        this.problem_name = problem_name;
        this.author_id = author_id;
        this.author_name = author_name;
        this.algo_name = algo_name;
        this.time_limit = time_limit;
        this.teston_prefix = teston_prefix;
        this.teston_extension = teston_extension;
        this.path_data = path_data;
    }

    public String csv_name() {
        if (teston_prefix.length() > 0) {
            return "result_" + algo_name + "_" + teston_prefix + ".csv";
        } else {
            return "result_" + algo_name + ".csv";
        }
    }

    public void initial_result_csv() {
        try {
            File csv = new File(path_result_csv);
            Log.start(csv, false);
            Log.writeln("instance,N,author_id,author_name,algo,sol,time");
            Log.end();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}