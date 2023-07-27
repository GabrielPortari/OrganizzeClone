package com.example.organizzeclone.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfigFirebase {
    private static FirebaseAuth auth;
    private static DatabaseReference firebaseRef;

    /*Retorna a instancia da autenticação caso haja uma*/
    public static FirebaseAuth getFirebaseAuth(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }
    /*Retorna a instancia do banco de dados caso haja uma*/
    public static DatabaseReference getFirebaseDatabase(){
        if(firebaseRef == null){
            firebaseRef = FirebaseDatabase.getInstance().getReference();
        }
        return firebaseRef;
    }
}
