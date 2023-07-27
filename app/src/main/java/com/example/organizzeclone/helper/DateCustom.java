package com.example.organizzeclone.helper;

import java.text.SimpleDateFormat;

public class DateCustom {
    /*Método criado para retornar a data atual no formado dd/MM/yyyy*/
    public static String dataAtual(){
        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataAtual = simpleDateFormat.format(data);
        return dataAtual;
    }
    /*Método criado para retornar o mes e ano de uma data, ex janeiro de 2001 -> 012001*/
    public static String mesAnoDataEscolhida(String data){
        String dataSplit[] = data.split("/");
        return dataSplit[1]+dataSplit[2];
    }
}
