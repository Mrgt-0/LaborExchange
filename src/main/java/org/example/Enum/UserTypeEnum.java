package org.example.Enum;

public enum UserTypeEnum {
    CANDIDATE,
    EMPLOYER,
    ADMIN;
    @Override
    public String toString() {
        return name();
    }
}
