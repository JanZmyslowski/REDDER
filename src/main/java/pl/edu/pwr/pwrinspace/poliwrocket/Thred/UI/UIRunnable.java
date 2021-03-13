package pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI;

import java.util.LinkedList;

public class UIRunnable implements Runnable {

    LinkedList<InterfaceUpdateTask> updateTasks = new LinkedList<>();

    @Override
    public void run(){
        updateTasks.forEach(InterfaceUpdateTask::execute);
    }

    public void addTask(InterfaceUpdateTask task) {
        updateTasks.add(task);
    }

    public int waitingTasks() {
        return  updateTasks.size();
    }
}
