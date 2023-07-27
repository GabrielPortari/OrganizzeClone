package com.example.organizzeclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizzeclone.R;
import com.example.organizzeclone.config.ConfigFirebase;
import com.example.organizzeclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {
    /* Declaração de váriaveis */
    private EditText editEmail, editSenha;
    private Button buttonEntrar;
    private Usuario user;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Definição dos Ids de cada item do layout
        getSupportActionBar().setTitle("Login");
        setContentView(R.layout.activity_login);
        editEmail = findViewById(R.id.editEmail_login);
        editSenha = findViewById(R.id.editSenha_login);
        buttonEntrar = findViewById(R.id.buttonEntrar_login);

        //Listener do button Entrar
        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Recupera o email e senha digitados para uma string
                String textEmail = editEmail.getText().toString();
                String textSenha = editSenha.getText().toString();
                //Validação dos campos
                if(!textEmail.isEmpty()){
                    if(!textSenha.isEmpty()){
                        //Cria o objeto user com o email e senha digitado para fazer a validação do login
                        user = new Usuario();
                        user.setEmail(textEmail);
                        user.setSenha(textSenha);
                        validarLogin();
                    }else{
                        Toast.makeText(LoginActivity.this,"Preencha a senha!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this,"Preencha o email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /*Método responsável pela validação do login do usuário*/
    public void validarLogin(){
        auth = ConfigFirebase.getFirebaseAuth();
        auth.signInWithEmailAndPassword(user.getEmail(), user.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Caso a autenticação seja concluida, chama a tela principal
                    abrirTelaPrincipal();

                }else{
                    //Caso contrário mostra uma mensagem de erro.
                    String exception = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        exception = "Usuário não cadastrado!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        exception = "Email e senha não correspondem a um usuário válido!";
                    }catch (Exception e){
                        exception = "Erro ao cadastrar usuario: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this,exception, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /*Este método apenas chama a Principal Activity*/
    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}