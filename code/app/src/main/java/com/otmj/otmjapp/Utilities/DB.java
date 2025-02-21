package com.otmj.otmjapp.Utilities;

import androidx.annotation.NonNull;

import com.otmj.otmjapp.Models.DatabaseObject;

import java.io.Serializable;
import java.util.List;

/**
 * DB is the interface for all database implementations
 * @author Obaloluwa Odelana
 */
public interface DB {

   // CREATE

   /**
    * Inserts some serializable object into the specified location in the database.
    *
    * @param object     Object to add to database
    * @param location   Determines what table/collection to insert into
    * @return           Database object containing original object and extra metadata gotten from
    *                   the database
    */
   DatabaseObject addTo(@NonNull Serializable object, String location);

   /**
    * Adds multiple serializable objects into the specified location in the database.
    * @param objects    Objects to add to database
    * @param location   Determines what table/collection to insert into
    * @return           Database objects containing object plus metadata
    * @see #addTo(Serializable, String)
    */
   List<DatabaseObject> addAllTo(List<Serializable> objects, String location);

   // --------------------

   // READ

   /**
    * Retrieves a single object from the database at the specified location
    * @param id         Primary
    * @param location   Determines what table/collection to insert into
    * @return
    */
   DatabaseObject getFrom(long id, String location);
   List<Serializable> getAllFrom(String location);

   // --------------------

   // UPDATE

   boolean updateIn(long id, Serializable object, String location);

   // --------------------

   // DELETE

   boolean deleteFrom(long id, String location);

   // --------------------
}
