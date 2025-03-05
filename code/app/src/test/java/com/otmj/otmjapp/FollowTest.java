package com.otmj.otmjapp;

import static org.junit.Assert.assertEquals;

import com.otmj.otmjapp.Models.Follow;

import org.junit.Test;

import java.util.Map;

public class FollowTest {
    private Follow mockFollow(){
        Follow follow = new Follow("FF1234","1234FF");
        return follow;
    }

    @Test
    public void TestGetFollowerID(){
        Follow follow = mockFollow();
        assertEquals("FF1234", follow.getFollowerID());
    }

    @Test
    public void TestSetFolloweeID(){
        Follow follow = mockFollow();
        assertEquals("1234FF", follow.getFolloweeID());
    }

    @Test
    public void TestFromMap(){
        Map <String, Object> followData = Map.of(
                "followerID", "1234FF",
                "followeeID", "FF1234"
        );
        Follow follow = Follow.fromMap(followData);
        assertEquals("1234FF", follow.getFollowerID());
        assertEquals("FF1234", follow.getFolloweeID());

    }
}
