package com.example.exam.api.dto;

import java.util.List;

/**
 * Normalized answer content used for both request payloads and attempt snapshots.
 */
public class AnswerContent {
    private List<String> choice;
    private List<BlankAnswer> blanks;
    private String text;

    public List<String> getChoice() {
        return choice;
    }

    public void setChoice(List<String> choice) {
        this.choice = choice;
    }

    public List<BlankAnswer> getBlanks() {
        return blanks;
    }

    public void setBlanks(List<BlankAnswer> blanks) {
        this.blanks = blanks;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
