package com.example.exam.api.dto;

public class BlankAnswer {
    private int index;
    private String value;

    public BlankAnswer() {
    }

    public BlankAnswer(int index, String value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
