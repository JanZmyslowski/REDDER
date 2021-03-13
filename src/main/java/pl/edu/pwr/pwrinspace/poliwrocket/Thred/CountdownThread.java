package pl.edu.pwr.pwrinspace.poliwrocket.Thred;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.List;

public class CountdownThread implements Observable, Runnable {

    private long countdownTime = 10000;

    private String formattedTimeResult = "";

    private List<InvalidationListener> observers = new ArrayList<>();

    private boolean canRun = false;

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        observers.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        observers.remove(invalidationListener);
    }

    public void update(long time) {
        formattedTimeResult = formattedTime(time);
        notifyObserver();
    }

    private void notifyObserver() {
        for (InvalidationListener obs : observers) {
            obs.invalidated(this);
        }
    }

    private String formattedTime(long milliseconds) {
        String formattedTime = "";
        if (milliseconds > 0) {
            formattedTime += "-";
        } else {
            formattedTime += "+";
        }
        milliseconds = Math.abs(milliseconds);
        long hours = milliseconds / (60 * 60 * 1000);
        milliseconds = milliseconds % (60 * 60 * 1000);
        long minutes = milliseconds / (60 * 1000);
        milliseconds = milliseconds % (60 * 1000);
        long seconds = milliseconds / 1000;
        milliseconds = milliseconds % 1000;

        formattedTime += (hours < 10 ? "0" + hours : hours) + ":";
        formattedTime += (minutes < 10 ? "0" + minutes : minutes) + ":";
        formattedTime += (seconds < 10 ? "0" + seconds : seconds) + ":";
        if(milliseconds < 10){
            formattedTime += "00" + milliseconds;
        } else {
            formattedTime += milliseconds < 100 ? "0" + milliseconds : milliseconds;
        }
        return formattedTime;
    }

    public boolean isCanRun() {
        return canRun;
    }

    public void resetCountdown() {
        canRun = false;
        countdownTime = 10000;
        formattedTimeResult = formattedTime(countdownTime);
        notifyObserver();
    }

    @Override
    public void run() {
        try {
            canRun = true;
            Thread.sleep(500);
            while (canRun) {

                this.countdownTime-=100;
                update(this.countdownTime);
                if (this.countdownTime == Long.MAX_VALUE) {
                    break;
                }
                Thread.sleep(100);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public long getCountdownTime() {
        return countdownTime;
    }

    public String getFormattedTimeResult() {
        return formattedTimeResult;
    }
}
