package com.km.ao3reader;

public class Work {
    private String workName;
    private int numOfChapters;

    public Work(String workName, int numOfChapters) {
        this.workName = workName;
        this.numOfChapters = numOfChapters;
    }

    public String getWorkName() {
        return workName;
    }

    public int getNumOfChapters() {
        return numOfChapters;
    }
}
