import java.util.ArrayList;

public class SchedulerRR extends SchedulerBase implements Scheduler {
    private Platform platform;
    private int timeQuantum;
    private int progress = 0;
    private ArrayList<Process> processes = new ArrayList<>();

    public SchedulerRR(Platform platform, int timeQuantum) {
        this.timeQuantum = timeQuantum;
        this.platform = platform;
    }

    @Override
    public int getNumberOfContextSwitches() {
        return this.contextSwitches;
    }

    @Override
    public void notifyNewProcess(Process p) {
        processes.add(p);
    }

    @Override
    public Process update(Process cpu) {
        Process nextProcess = null;
        if (cpu == null) {
            if (processes.size() > 0) {
                this.contextSwitches++;
                nextProcess = processes.remove(0);
                platform.log("Scheduled: " + nextProcess.getName());
                progress = 1;
            }
        } else {
            if (cpu.isExecutionComplete()) {
                platform.log("Process " + cpu.getName() + " execution complete");
                this.contextSwitches++;
                if (processes.size() > 0) {
                    this.contextSwitches++;
                    nextProcess = processes.remove(0);
                    platform.log("Scheduled: " + nextProcess.getName());
                    progress = 1;
                }
            } else {
                if (progress == timeQuantum) {
                    platform.log("Time quantum complete for process " + cpu.getName());
                    this.contextSwitches++;
                    processes.add(cpu);
                    this.contextSwitches++;
                    nextProcess = processes.remove(0);
                    platform.log("Scheduled: " + nextProcess.getName());
                    progress = 1;
                } else {
                    nextProcess = cpu;
                    progress++;
                }
            }
        }
        return nextProcess;
    }
}
