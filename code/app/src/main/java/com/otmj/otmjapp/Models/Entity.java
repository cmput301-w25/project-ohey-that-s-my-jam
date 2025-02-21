package com.otmj.otmjapp.Models;

import java.util.Map;

/**
 * Abstract class for working with data retrieved from database.
 * Every model class that is put in a database should subclass
 * this class and implement the `fromMap` method.
 */
public abstract class Entity {
    /**
     * Converts a string map of values to a class.
     * @param map   Map containing (string, object) pairs which would coincide
     *              with the object's properties.
     * @return      Object that is formed from matching its properties with the
     *              values provided in the map
     */
     public static Entity fromMap(Map<String, Object> map) {
         throw new UnsupportedOperationException("Not implemented yet");
     }
}
