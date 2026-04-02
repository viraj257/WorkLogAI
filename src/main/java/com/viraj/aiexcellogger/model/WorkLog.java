package com.viraj.aiexcellogger.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class WorkLog {

    private String date;
    private String projectName;

    @JsonProperty("tasks")
    private String taskSummary;

    private String description;
    private double hours;
    private String nextAction;
    private boolean inProgress;
    private String dueDate;
    private String reviewedBy;
    private String remark;
}