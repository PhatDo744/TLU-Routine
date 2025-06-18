package com.example.tlu_routine.model;

import java.time.LocalDate;
import java.util.List;

public class Event {
    private String id;
    private String name;
    private String startTime;
    private String endTime;
    private String duration;
    private LocalDate date;
    private String repeatType; // "once", "daily", "selected_days"
    private List<String> selectedDays;
    private boolean notificationEnabled;
    private String tag;
    private String location;
    private long createdAt;
    private boolean completed; // Trạng thái hoàn thành

    public Event() {
        this.id = String.valueOf(System.currentTimeMillis());
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public List<String> getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(List<String> selectedDays) {
        this.selectedDays = selectedDays;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}