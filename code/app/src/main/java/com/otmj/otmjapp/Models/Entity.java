package com.otmj.otmjapp.Models;

import java.io.Serializable;
import java.util.Map;

/**
 * Abstract class for working with data retrieved from database.
 * Every model class that is put in a database should subclass
 * this class and implement the `fromMap` method.
 */
public abstract class Entity implements Serializable {
    /**
     * Converts a string map of values to a object.
     * @param map   Map containing (string, object) pairs which would coincide
     *              with the object's properties.
     * @return      Object that is formed from matching its properties with the
     *              values provided in the map
     */
     public static Entity fromMap(Map<String, Object> map) {
         throw new UnsupportedOperationException("Only call this method on subclasses of Entity!");
     }

    /**
     * Converts an object to string map of its values.
     * @return  Map containing (string, object) pairs which would coincide
     *          with the object's properties.
     */
    public Map<String, Object> toMap() {
         throw new UnsupportedOperationException("Only call this method on subclasses of Entity!");
     }
}
