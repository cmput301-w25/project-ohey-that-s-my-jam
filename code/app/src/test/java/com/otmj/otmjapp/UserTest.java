package com.otmj.otmjapp;

import static org.junit.Assert.assertEquals;

import com.otmj.otmjapp.Models.User;

import org.junit.Test;

import java.util.Map;

public class UserTest {
    private User mockUser(){
        User user = new User("Kai","Kaiiscool@gmail.com","123456","http://example.com/profile.jpg");
        return user;
    }

    @Test
    public void testGetUsername(){
        User user = mockUser();
        assertEquals("Kai", user.getUsername());
    }

    @Test
    public void testSetUsername(){
        User user = mockUser();
        user.setUsername("Ava");
        assertEquals("Ava", user.getUsername());
    }

    @Test
    public void TestGetEmailAddress(){
        User user = mockUser();
        assertEquals("Kaiiscool@gmail.com", user.getEmailAddress());
    }

    @Test
    public void TestSetEmailAddress(){
        User user = mockUser();
        user.setEmailAddress("Kaiisnotcool@gmail.com");
        assertEquals("Kaiisnotcool@gmail.com", user.getEmailAddress());
    }

    @Test
    public void TestGetPassword(){
        User user = mockUser();
        assertEquals("123456", user.getPassword());
    }

    @Test
    public void TestSetPassword(){
        User user = mockUser();
        user.setPassword("123456","654321");
        assertEquals("654321", user.getPassword());
    }

    @Test
    public void TestIsPassword(){
        User user = mockUser();
        assertEquals(true, user.isPassword("123456"));
        assertEquals(false, user.isPassword("654321"));
    }

    @Test
    public void TestGetProfilePictureLink(){
        User user = mockUser();
        assertEquals("http://example.com/profile.jpg", user.getProfilePictureLink());
    }

    @Test
    public void TestSetProfilePictureLink(){
        User user = mockUser();
        user.setProfilePictureLink("http://example.com/profile.png");
        assertEquals("http://example.com/profile.png", user.getProfilePictureLink());
    }

//    @Test
//    public void TestFromMap(){
//        Map<String, Object> userData = Map.of(
//                "username", "john",
//                "emailAddress", "john@example.com",
//                "password", "123456",
//                "profilePictureLink", "http://example.com/profile.jpg"
//        );
//
//        User user = User.fromMap(userData);
//        assertEquals("john", user.getUsername());
//        assertEquals("john@example.com", user.getEmailAddress());
//        assertEquals("123456", user.getPassword());
//        assertEquals("http://example.com/profile.jpg", user.getProfilePictureLink());
//    }

}
