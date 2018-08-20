package com.example.sucianalf.grouptracking.model;

/**
 * Created by PERSONAL on 6/11/2018.
 */

public class DataGroupMember {
    private String memberName;
    private String memberID;
    private String memberPhone;
    private String memberPhoto;

    public DataGroupMember(){

    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public String getMemberPhoto() {
        return memberPhoto;
    }

    public void setMemberPhoto(String memberPhoto) {
        this.memberPhoto = memberPhoto;
    }

    public String getMemberPhone() {
        return memberPhone;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }
}
