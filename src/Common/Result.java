package Common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;

public class Result {
    String instname;
    public AlgoParam param;
    int n;
    public Solution sol;
    public double time;

    public Result(Instance inst, AlgoParam param) {
        this.instname = inst.instName;
        this.n = inst.n;
        this.param = param;
    }

    public String toCsvString() {
        return instname + "," + n + "," + param.author_id + "," +
                param.author_name + "," + param.algo_name + "," +
                sol.distance + "," + sol.routes.size() + "," + time;
    }

    public String toJSonString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(new OutClass(instname, param, n, time, sol.toString()));
    }

    public void output(Instance inst) {
        Log.start(new File(param.path_result_csv), true);
        Log.writeln(toCsvString());
        Log.end();
        FileInputStream fis = null;

        Log.start(new File(param.path_result_sol + "/result_" + instname + ".json"), false);
        Log.writeln(toJSonString());
        Log.end();

        Log.start(new File(param.path_result_sol + "/plot_sol_" + instname + ".txt"), false);
        Log.write(sol.toPlotString(inst));
        Log.end();
    }
}
