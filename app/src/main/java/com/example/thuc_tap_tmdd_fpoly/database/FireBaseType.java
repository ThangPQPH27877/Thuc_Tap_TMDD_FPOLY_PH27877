package com.example.thuc_tap_tmdd_fpoly.database;

import com.google.firebase.database.DataSnapshot;

public class FireBaseType {
    public static Boolean isAdmin(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            Boolean isAdmin = dataSnapshot.child("user_type").getValue(Boolean.class);
            return isAdmin != null && isAdmin;
        }
        return false;
    }
}
