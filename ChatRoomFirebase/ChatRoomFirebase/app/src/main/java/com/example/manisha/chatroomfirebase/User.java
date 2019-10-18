package com.example.manisha.chatroomfirebase;

import java.util.HashMap;
import java.util.Map;

public class User {

    String firstName, lastName, userId, userKey;

    public User() {
    }

    public User(String firstName, String lastName, String userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("userID", userId);
        result.put("userKey", userKey);
        return result;
    }
}