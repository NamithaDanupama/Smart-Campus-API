package com.smartcampus.model;

public class ErrorMessage {
    private String message;
    private int statusCode;
    private String documentation;

    public ErrorMessage() {}

    public ErrorMessage(String message, int statusCode, String documentation) {
        this.message = message;
        this.statusCode = statusCode;
        this.documentation = documentation;
    }

    public String getMessage() { return message; }
    public int getStatusCode() { return statusCode; }
    public String getDocumentation() { return documentation; }

    public void setMessage(String message) { this.message = message; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public void setDocumentation(String documentation) { this.documentation = documentation; }
}