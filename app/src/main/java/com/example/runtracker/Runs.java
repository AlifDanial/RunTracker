package com.example.runtracker;

public class Runs {

    //initialization of runs object variables
    private int runID;
    private String runDuration;
    private String runDistance;
    private String runDate;
    private String runElevation;
    private byte[] runMap;

    public Runs(){};

    //Runs constructor
    public Runs(int id, String runDuration, String runDistance, String runDate, String runElevation, byte[] runMap){
        this.runID = id;
        this.runDuration = runDuration;
        this.runDistance = runDistance;
        this.runDate = runDate;
        this.runElevation = runElevation;
        this.runMap = runMap;

    }

    //getter methods for runs class
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

    public byte[] getRunMap() {
        return runMap;
    }

    public String getRunElevation() {
        return runElevation;
    }

}
