package com.example.organizzeclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizzeclone.R;
import com.example.organizzeclone.config.ConfigFirebase;
import com.example.organizzeclone.helper.Base64Custom;
import com.example.organizzeclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {
    /*Declaração de váriaveis*/
    private EditText editNome, editSenha, editEmail;
    private Button botao_cadastro;
    private FirebaseAuth auth;
    Usuario user = new Usuario();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Declaração dos ids de cada item do layout
        getSupportActionBar().setTitle("Cadastro");
        setContentView(R.layout.activity_cadastro);
        editNome = findViewById(R.id.editNome_cadastro);
        editEmail = findViewById(R.id.editEmail_cadastro);
        editSenha = findViewById(R.id.editSenha_cadastro);
        botao_cadastro = findViewById(R.id.button_cadastro);

        //Listener do botão cadastro
        botao_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Recupera os textos digitados e converte pra strings
                String textNome = editNome.getText().toString();
                String textEmail = editEmail.getText().toString();
                String textSenha = editSenha.getText().toString();
                //Verifica se todos os campos foram preenchidos
                if(!textNome.isEmpty()){
                    if(!textEmail.isEmpty()){
                        if(!textSenha.isEmpty()){
                            //Cria-se o objeto usuário e chama o método cadastrarUsuário
                            user.setNome(textNome);
                            user.setEmail(textEmail);
                            user.setSenha(textSenha);
                            cadastrarUsuario();
                        }else{
                            Toast.makeText(CadastroActivity.this,"Preencha a senha!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(CadastroActivity.this,"Preencha o email!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CadastroActivity.this,"Preencha o nome!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /*Método responsável por cdastrar o usuário*/
    public void cadastrarUsuario(){
        //Recupera a referencia da autenticação
        auth = ConfigFirebase.getFirebaseAuth();

        //Cria-se um novo usuário utilizando email e senha -> user.getEmail() e user.getSenha()
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getSenha())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Salva a autenticação no firebase, e cadastra o usuário no banco de dados chamando o método user.salvarFirebase()
                            String idUser = Base64Custom.codeBase64(user.getEmail());
                            user.setIdUsuario(idUser);
                            user.salvarFireBase();
                            finish();
                        }else{
                            //Caso haja algum problema, mostra um Toast mostrando qual foi
                            String exception = "";
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                exception = "Digite uma senha mais forte!";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                exception = "Digite um email válido!";
                            }catch (FirebaseAuthUserCollisionException e){
                                exception = "Essa conta já foi cadastrada!";
                                e.printStackTrace();
                            }catch (Exception e){
                                exception += "Erro ao cadastrar usuario: " + e.getMessage();
                            }
                            Toast.makeText(CadastroActivity.this,exception, Toast.LENGTH_SHORT).show();
                        }
                    }
        });
    }
}