package com.lemuel.lemubit.fingerprinttest.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmModel extends RealmObject {
    @PrimaryKey
    private int id;
    private String name;
    private String lastName;
    
    private int rightThumb;
    private int rightIndex;
    private int rightMiddle;
    private int rightRing;
    private int rightPinky;
    
    private int leftThumb;
    private int leftIndex;
    private int leftMiddle;
    private int leftRing;
    private int leftPinky;

    private byte[] photo;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getRightThumb() {
        return rightThumb;
    }

    public void setRightThumb(int rightThumb) {
        this.rightThumb = rightThumb;
    }

    public int getRightIndex() {
        return rightIndex;
    }

    public void setRightIndex(int rightIndex) {
        this.rightIndex = rightIndex;
    }

    public int getRightMiddle() {
        return rightMiddle;
    }

    public void setRightMiddle(int rightMiddle) {
        this.rightMiddle = rightMiddle;
    }

    public int getRightRing() {
        return rightRing;
    }

    public void setRightRing(int rightRing) {
        this.rightRing = rightRing;
    }

    public int getRightPinky() {
        return rightPinky;
    }

    public void setRightPinky(int rightPinky) {
        this.rightPinky = rightPinky;
    }

    public int getLeftThumb() {
        return leftThumb;
    }

    public void setLeftThumb(int leftThumb) {
        this.leftThumb = leftThumb;
    }

    public int getLeftIndex() {
        return leftIndex;
    }

    public void setLeftIndex(int leftIndex) {
        this.leftIndex = leftIndex;
    }

    public int getLeftMiddle() {
        return leftMiddle;
    }

    public void setLeftMiddle(int leftMiddle) {
        this.leftMiddle = leftMiddle;
    }

    public int getLeftRing() {
        return leftRing;
    }

    public void setLeftRing(int leftRing) {
        this.leftRing = leftRing;
    }

    public int getLeftPinky() {
        return leftPinky;
    }

    public void setLeftPinky(int leftPinky) {
        this.leftPinky = leftPinky;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
