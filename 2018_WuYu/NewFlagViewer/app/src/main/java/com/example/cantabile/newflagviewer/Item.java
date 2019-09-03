package com.example.cantabile.newflagviewer;

public class Item {
    String skillName;
    int skillImage;

    public Item(String skillName,int skillImage){
        this.skillImage=skillImage;
        this.skillName=skillName;
    }
    public String getSkillName(){
        return skillName;
    }
    public int getSkillImage(){
        return skillImage;
    }
}