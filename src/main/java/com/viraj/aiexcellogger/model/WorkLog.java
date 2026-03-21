package com.viraj.aiexcellogger.model;

import lombok.Data;

@Data
public class WorkLog {

    private String date;
    private String projectName;
    private String taskSummary;
    private String description;
    private double hours;
    private String nextAction;
    private String inProgress;
    private String dueDate;
    private String reviewedBy;
    private String remark;
}