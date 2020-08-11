package com.example.quizadmin;

import java.util.List;

public class CategoryModel {
    private String name;
    private List<String> sets;
    private String key;

    public CategoryModel() {
    }

    public CategoryModel(String name, List<String> sets, String key) {
        this.name = name;
        this.sets = sets;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSets() {
        return sets;
    }

    public void setSets(List<String> sets) {
        this.sets = sets;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
