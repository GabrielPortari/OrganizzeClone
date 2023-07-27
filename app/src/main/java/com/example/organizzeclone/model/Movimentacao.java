package com.example.organizzeclone.model;

import com.example.organizzeclone.config.ConfigFirebase;
import com.example.organizzeclone.helper.Base64Custom;
import com.example.organizzeclone.helper.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Movimentacao {
    private String data;
    private String categoria;
    private String descricao;
    private String tipo;
    private String chave;
    private double valor;

    public Movimentacao() {
    }

    public void salvarFireBase(String data){

        FirebaseAuth auth = ConfigFirebase.getFirebaseAuth(); // recupera o email do usuario e converte base64
        String idUsuario = Base64Custom.codeBase64(auth.getCurrentUser().getEmail());
        String dataFormatada = DateCustom.mesAnoDataEscolhida(data); // Formata a data para salvar no firebase em mesano = 072023
        DatabaseReference firebase = ConfigFirebase.getFirebaseDatabase();
        firebase.child("movimentacao")
                    .child(idUsuario)
                        .child(dataFormatada)
                            .push() //utiliza-se o push para gerar um id unico do proprio firebase
                                .setValue(this);
    }
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
