import java.util.ArrayList;

public class SchedulerSRTF extends SchedulerBase implements Scheduler {
    private Platform platform;
    private ArrayList<Process> processes = new ArrayList<>();

    public SchedulerSRTF(Platform platform) {
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

    private int getNext() {
        int min = 0;
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).getTotalTime() - processes.get(i).getElapsedTotal() < processes.get(min).getTotalTime() - processes.get(min).getElapsedTotal()) {
                min = i;
            }
        }
        return min;
    }

    @Override
    public Process update(Process cpu) {
        Process nextProcess = null;
        if (cpu == null) {
            if (processes.size() > 0) {
                this.contextSwitches++;
                nextProcess = processes.remove(getNext());
                platform.log("Scheduled: " + nextProcess.getName());
            }
        } else {
            int currTimeRemaining = cpu.getTotalTime() - cpu.getElapsedTotal();
            if (processes.size() > 0 && processes.get(getNext()).getTotalTime() - processes.get(getNext()).getElapsedTotal() < currTimeRemaining) {
                this.contextSwitches++;
                processes.add(cpu);
                platform.log("Preemptively removed: " + cpu.getName());
                nextProcess = processes.remove(getNext());
                this.contextSwitches++;
                platform.log("Scheduled: " + nextProcess.getName());
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
                        nextProcess = processes.remove(getNext());
                        platform.log("Scheduled: " + nextProcess.getName());
                        this.contextSwitches++;
                    }
                } else {
                    nextProcess = cpu;
                }
            }
        }
        return nextProcess;
    }
}
