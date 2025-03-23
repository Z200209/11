package com.example.app.domain;

public enum IntroductionType {
    TEXT("text"),
    VIDEO("video"),
    IMAGE("image");

    private final String Itype;

    IntroductionType(String Itype) {
        this.Itype = Itype;
    }

    public String getType() {
        return Itype;
    }
}
