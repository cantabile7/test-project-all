package com.example.cantabile.flagviewer;

public class Item {
    String skillName;
    int skillImage;
    int skillDetail;

    public Item(String skillName,int skillImage){
        this.skillImage=skillImage;
        this.skillName=skillName;
    }
    public Item(String skillName,int skillImage,int skillDetail){
        this.skillImage=skillImage;
        this.skillName=skillName;
        this.skillDetail=skillDetail;
    }
    public String getSkillName(){
        return skillName;
    }
    public int getSkillImage(){
        return skillImage;
    }
    public int getSkillDetail(){
        return skillDetail;
    }
}
