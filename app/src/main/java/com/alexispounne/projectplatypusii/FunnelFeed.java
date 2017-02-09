package com.alexispounne.projectplatypusii;

public class FunnelFeed {
    private int id;
    private String firstName;
    private String lastName;
    private short birthYear;

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public short getBirthYear() {
        return birthYear;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthYear(short birthYear) {
        this.birthYear = birthYear;
    }
}
