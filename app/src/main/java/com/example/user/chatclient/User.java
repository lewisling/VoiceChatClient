package com.example.user.chatclient;
import java.io.Serializable;
import java.util.ArrayList;


public class User implements Serializable{
    private static final long serialVersionUID = 0L;
    public String email,firstName,lastName,Age,Permission,nickName,lang;
    public ArrayList<String> chatWith;
    public User(String email, String firstName, String lastName, String Age, String Permission, String nickName,String lang)
    {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.Age = Age;
        this.Permission = Permission;
        this.nickName = nickName;
        this.lang = lang;
        this.chatWith = new ArrayList<String>();
    }
}

