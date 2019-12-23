package com.example.runtracker;

public class Runs {

    private int runID;
    private String runDuration;
    private String runDistance;
    private String runDate;
    private String runElevation;
    private byte[] runMap;

    public Runs(){};

    //recipe constructor
    public Runs(int id, String runDuration, String runDistance, String runDate, String runElevation, byte[] runMap){
        this.runID = id;
        this.runDuration = runDuration;
        this.runDistance = runDistance;
        this.runDate = runDate;
        this.runElevation = runElevation;
        this.runMap = runMap;

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

    public byte[] getRunMap() {
        return runMap;
    }

    public void setRunMap(byte[] runMap) {
        this.runMap = runMap;
    }

    public String getRunElevation() {
        return runElevation;
    }

    public void setRunElevation(String runElevation) {
        this.runElevation = runElevation;
    }
}
