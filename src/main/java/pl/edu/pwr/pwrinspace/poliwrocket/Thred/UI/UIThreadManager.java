package pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.IMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.ParsingResultStatus;

import java.util.LinkedList;
import java.util.Queue;

public class UIThreadManager implements InvalidationListener {
    private final Queue<Runnable> activeRunnableQueue = new LinkedList<>();
    private UIRunnable waitingNormalRunnable = new UIRunnable();
    private UIRunnable waitingImmediateRunnable = new UIRunnable();
    private int normalTasksLimit = 0;

    private static final UIThreadManager instance = new UIThreadManager();

    private UIThreadManager() {
    }

    public static UIThreadManager getInstance() {
        return instance;
    }

    private synchronized void run() {
        while (!activeRunnableQueue.isEmpty()) {
            Platform.runLater(activeRunnableQueue.poll());
        }
    }

    public void addNormal(InterfaceUpdateTask task) {
        waitingNormalRunnable.addTask(task);
        if(waitingNormalRunnable.waitingTasks() == normalTasksLimit) {
            activeRunnable(waitingNormalRunnable);
            waitingNormalRunnable = new UIRunnable();
        }
    }
    public void addImmediate(InterfaceUpdateTask task) {
        waitingImmediateRunnable.addTask(task);
    }

    public void addActiveSensor() {
        normalTasksLimit++;
    }

    private void activeRunnable(UIRunnable uiRunnable) {
        activeRunnableQueue.add(uiRunnable);
        run();
    }

    @Override
    public void invalidated(Observable observable) {
        if(observable instanceof IMessageParser && ((IMessageParser) observable).getParsingStatus() == ParsingResultStatus.OK) {
            activeRunnable(waitingImmediateRunnable);
            waitingImmediateRunnable = new UIRunnable();
        }
    }
}
