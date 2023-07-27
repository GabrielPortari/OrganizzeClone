package com.example.organizzeclone.helper;

import android.util.Base64;

public class Base64Custom {
    /*Método custom criado apenas para codificar o email do usuário em base64
    para ser utilizado como identificador unico*/
    public static String codeBase64(String s){
        return Base64.encodeToString(s.getBytes(), Base64.DEFAULT).replaceAll("\\n|\\r", "");
    }
    public static String decodeBase64(String s){
        return new String (Base64.decode(s, Base64.DEFAULT));
    }
}
