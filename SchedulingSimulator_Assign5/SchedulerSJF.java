import java.util.ArrayList;

public class SchedulerSJF extends SchedulerBase implements Scheduler {
    private Platform platform;
    private ArrayList<Process> processes = new ArrayList<>();

    public SchedulerSJF(Platform platform) {
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
        int min = 0;
        if (cpu == null) {
            if (processes.size() > 0) {
                this.contextSwitches++;
                for (int i = 0; i < processes.size(); i++) {
                    if (processes.get(i).getTotalTime() < processes.get(min).getTotalTime()) {
                        min = i;
                    }
                }
                nextProcess = processes.remove(min);
                platform.log("Scheduled: " + nextProcess.getName());
            }
        } else {
            if (cpu.isBurstComplete()) {
                this.contextSwitches++;
                platform.log("Process " + cpu.getName() + " burst complete");
                if (!cpu.isExecutionComplete()) {
                    processes.add(cpu);
                } else {
                    platform.log("Process " + cpu.getName() + " execution complete");
                }
                if (processes.size() > 0) {
                    for (int i = 0; i < processes.size(); i++) {
                        if (processes.get(i).getTotalTime() < processes.get(min).getTotalTime()) {
                            min = i;
                        }
                    }
                    nextProcess = processes.remove(min);
                    platform.log("Scheduled: " + nextProcess.getName());
                    this.contextSwitches++;

                }
            } else {
                nextProcess = cpu;
            }
        }
        return nextProcess;
    }
}
