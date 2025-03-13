package com.otmj.otmjapp.Helper;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Query;

/**
 * Helper class for specifying how to order results from a database
 */
public class DBSortOption {
    /**
     * Field/property to order by
     */
    public final String fieldName;
    /**
     * Specifies whether to order in ascending or descending order
     */
    public final Query.Direction direction;

    public DBSortOption(@NonNull String fieldName, boolean descending) {
        this.fieldName = fieldName;
        if (descending) {
            direction = Query.Direction.DESCENDING;
        } else {
            direction = Query.Direction.ASCENDING;
        }
    }

    public Query getSorting(Query ref) {
        return ref.orderBy(fieldName, direction);
    }
}
