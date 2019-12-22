package com.example.runtracker;

public class Runs {

    private int runID;
    private String runDuration;
    private String runDistance;
    private String runDate;
    private String runStartTime;
    private String runStopTime;

    public Runs(){};

    //recipe constructor
    public Runs(int id, String runDuration, String runDistance, String runDate, String runStartTime, String runStopTime){
        this.runID = id;
        this.runDuration = runDuration;
        this.runDistance = runDistance;
        this.runDate = runDate;
        this.runStartTime = runStartTime;
        this.runStopTime = runStopTime;

    }

    public int getRunID() {
        return runID;
    }

    public String getRunDuration() {
        return runDuration;
    }

    public String getRunDistance() {
        return runDistance;
    }

    public String getRunDate() {
        return runDate;
    }

    public String getRunStartTime() {
        return runStartTime;
    }

    public String getRunStopTime() {
        return runStopTime;
    }

    public void setRunID(int runID) {
        this.runID = runID;
    }

    public void setRunDuration(String runDuration) {
        this.runDuration = runDuration;
    }

    public void setRunDistance(String runDistance) {
        this.runDistance = runDistance;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public void setRunStartTime(String runStartTime) {
        this.runStartTime = runStartTime;
    }

    public void setRunStopTime(String runStopTime) {
        this.runStopTime = runStopTime;
    }
}
