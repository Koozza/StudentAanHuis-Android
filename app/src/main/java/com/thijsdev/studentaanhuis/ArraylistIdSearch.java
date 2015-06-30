package com.thijsdev.studentaanhuis;

import com.thijsdev.studentaanhuis.Database.DatabaseObject;

import java.util.ArrayList;

public class ArraylistIdSearch {
    public static boolean compare(ArrayList<DatabaseObject> _arrayList, DatabaseObject _databaseObject) {
        boolean match = false;

        for(DatabaseObject databaseObject : _arrayList) {
            if (databaseObject.getId() == _databaseObject.getId()) {
                match = true;
                break;
            }
        }

        return match;
    }
}
