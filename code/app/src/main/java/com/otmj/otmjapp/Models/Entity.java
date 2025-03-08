package com.otmj.otmjapp.Models;

import java.util.Map;

public class Entity {
    public String ID;
    public final Map<String, Object> objectMap;

    public Entity(String ID, Map<String, Object> objectMap) {
        this.ID = ID;
        this.objectMap = objectMap;
    }
}
