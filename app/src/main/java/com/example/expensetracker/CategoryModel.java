package com.example.expensetracker;

public class CategoryModel {
    private String id;
    private String name;
    private String dailyBudget;
    private String weeklyBudget;
    private String monthlyBudget;
    private String yearlyBudget;
    private String categoryViewNote;

    // Default constructor required for calls to DataSnapshot.getValue(CategoryModel.class)
    public CategoryModel() {
    }

    // Getters and setters for each field
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

    public String getDailyBudget() {
        return dailyBudget;
    }

    public void setDailyBudget(String dailyBudget) {
        this.dailyBudget = dailyBudget;
    }

    public String getWeeklyBudget() {
        return weeklyBudget;
    }

    public void setWeeklyBudget(String weeklyBudget) {
        this.weeklyBudget = weeklyBudget;
    }

    public String getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(String monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public String getYearlyBudget() {
        return yearlyBudget;
    }

    public void setYearlyBudget(String yearlyBudget) {
        this.yearlyBudget = yearlyBudget;
    }

    public String getCategoryViewNote() {
        return categoryViewNote;
    }

    public void setCategoryViewNote(String categoryViewNote) {
        this.categoryViewNote = categoryViewNote;
    }

}
