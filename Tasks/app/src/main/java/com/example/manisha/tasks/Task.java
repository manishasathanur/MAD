package com.example.manisha.tasks;

public class Task {
    String taskid,taskName,deadline,status;

    public Task(String taskid, String taskName, String deadline, String status) {
        this.taskid = taskid;
        this.taskName = taskName;
        this.deadline = deadline;
        this.status = status;
    }

    public Task(){

    }
    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
