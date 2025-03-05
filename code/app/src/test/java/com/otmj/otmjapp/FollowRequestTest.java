package com.otmj.otmjapp;

import static org.junit.Assert.assertEquals;

import com.otmj.otmjapp.Models.Follow;
import com.otmj.otmjapp.Models.FollowRequest;

import org.junit.Test;

import java.util.Map;

public class FollowRequestTest {
    private FollowRequest mockFollowRequest(){
        FollowRequest followRequest = new FollowRequest("FF1234", "1234FF");
        return followRequest;
    }

    @Test
    public void TestGetFollowerID(){
        Follow follow = mockFollowRequest();
        assertEquals("FF1234", follow.getFollowerID());
    }

    @Test
    public void TestSetFolloweeID(){
        Follow follow = mockFollowRequest();
        assertEquals("1234FF", follow.getFolloweeID());
    }

    @Test
    public void TestFromMap(){
        Map<String, Object> followData = Map.of(
                "followerID", "1234FF",
                "followeeID", "FF1234"
        );
        Follow follow = Follow.fromMap(followData);
        assertEquals("1234FF", follow.getFollowerID());
        assertEquals("FF1234", follow.getFolloweeID());

    }
}
