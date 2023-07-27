package com.example.organizzeclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.organizzeclone.R;
import com.example.organizzeclone.activity.CadastroActivity;
import com.example.organizzeclone.activity.LoginActivity;
import com.example.organizzeclone.config.ConfigFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        /* Criação dos slides mostrados quando não há um usuário logado */

        //Desativa os botões de anterior e proximo
        setButtonBackVisible(false);
        setButtonNextVisible(false);
        //Criaçao do slide 1
        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.intro_1)
                .canGoBackward(false)
                .build());
        //Criaçao do slide 2
        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.intro_2)
                .build());
        //Criaçao do slide 3
        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.intro_3)
                .build());
        //Criaçao do slide 4
        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.intro_4)
                .build());
        //Criaçao do ultimo slide, a tela de cadastro / login
        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.intro_cadastro)
                .canGoForward(false)
                .build());
    }

    @Override
    /*Método onStart, que sempre verifica se há um usuário logado quando o app é iniciado*/
    protected void onStart() {
        super.onStart();
        verificarUserLogado();
    }
    /*Método para chamar a activity de Login caso seja selecionado "Já possuo uma conta"*/
    public void buttonEntrar(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }
    /*Método para chamar a activity de cadastro caso seja selecionado "Cadastre-se"*/
    public void buttonCadastro(View view){
        startActivity(new Intent(this, CadastroActivity.class));
    }
    /*Metodo que verifica se já existe um usuário logado, para que não seja mostrados os slides/telas iniciais*/
    public void verificarUserLogado(){
        auth = ConfigFirebase.getFirebaseAuth();
        if(auth.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }
    /*Método que chama a tela principal do aplicativo*/
    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
    }
}