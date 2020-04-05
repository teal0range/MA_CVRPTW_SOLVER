package Algorithm;

public class Timer extends Thread{
    private long total_time;
    private Core core;
    private long startTime;
    public Timer(Core core){
        this(core,60);
    }

    public Timer(Core core, long total_time) {
        this.core = core;
        this.total_time = total_time;
    }

    @Override
    public void run() {
        core.start();
        startTime = System.currentTimeMillis();
        try {
            Thread.sleep(1000*total_time);
        }
        catch (Exception e){
            System.exit(0);
        }
        core.interrupt();
        core.res.output();
    }

}
