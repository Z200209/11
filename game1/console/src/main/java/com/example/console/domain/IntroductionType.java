package com.example.console.domain;

public enum IntroductionType {
    TEXT("text"),
    VIDEO("video"),
    IMAGE("image");

    private final String type;

    IntroductionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
