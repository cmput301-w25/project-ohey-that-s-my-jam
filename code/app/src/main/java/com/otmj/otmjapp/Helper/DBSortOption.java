package com.otmj.otmjapp.Helper;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Query;

/**
 * Helper class for specifying how to order results from a Firestore query.
 */
public class DBSortOption {
    /**
     * Field/property to order by.
     */
    public final String fieldName;
    /**
     * Specifies whether to order in ascending or descending order.
     */
    public final Query.Direction direction;

    /**
     * Constructor to create a sorting option based on a field and order direction.
     *
     * @param fieldName The field to order by.
     * @param descending Whether to sort in descending order (true) or ascending (false).
     */
    public DBSortOption(@NonNull String fieldName, boolean descending) {
        this.fieldName = fieldName;
        if (descending) {
            direction = Query.Direction.DESCENDING;
        } else {
            direction = Query.Direction.ASCENDING;
        }
    }

    /**
     * Returns the query with the sorting applied.
     *
     * @param ref The query reference to apply sorting to.
     * @return The query with the applied sorting.
     */
    public Query getSorting(Query ref) {
        return ref.orderBy(fieldName, direction);
    }
}
