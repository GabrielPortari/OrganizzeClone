package com.example.organizzeclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizzeclone.R;
import com.example.organizzeclone.config.ConfigFirebase;
import com.example.organizzeclone.helper.Base64Custom;
import com.example.organizzeclone.helper.DateCustom;
import com.example.organizzeclone.model.Movimentacao;
import com.example.organizzeclone.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ReceitaActivity extends AppCompatActivity {
    /*Declaração de váriaveis */
    private TextInputEditText editData, editCategoria, editDescricao;
    private EditText editValor;
    private FloatingActionButton fabAddReceita;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
    private FirebaseAuth auth = ConfigFirebase.getFirebaseAuth();
    private double receitaTotal;
    private double receitaAtualizada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita);
        //Chama o método para recuperar a receita total do usuário
        recuperarReceitaTotal();

        //Definição dos Ids de cada item do layout
        fabAddReceita = findViewById(R.id.fab_add_receita);
        editValor = findViewById(R.id.editValor_receita);
        editData = findViewById(R.id.editData_receita);
        editCategoria = findViewById(R.id.editCategoria_receita);
        editDescricao = findViewById(R.id.editDescricao_receita);
        //Seta o campo da data com a data atual
        editData.setText(DateCustom.dataAtual());

        //Listener para o botao de adicionar despesa
        fabAddReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarReceita(v);
            }
        });

    }
    /*Método responsável por salvar uma despesa no banco de dados*/
    public void salvarReceita(View view){
        if(validarCampos()){
            movimentacao = new Movimentacao();
            //Recupera os valores dos editText
            String data = editData.getText().toString();
            double valorGerado = Double.parseDouble(editValor.getText().toString());
            //Seta os valores no objeto movimentacao
            movimentacao.setValor(valorGerado);
            movimentacao.setData(data);
            movimentacao.setCategoria(editCategoria.getText().toString());
            movimentacao.setDescricao(editDescricao.getText().toString());
            movimentacao.setTipo("r");
            //Atualiza a receita total do usuário no firebase
            receitaAtualizada = valorGerado + receitaTotal;
            //Salva a nova movimentação no firebase
            atualizarReceitaTotal(receitaAtualizada);
            movimentacao.salvarFireBase(data);
            finish();
        }
    }
    /*Método utilizado apenas para validar os campos*/
    public Boolean validarCampos(){
        String valor = editValor.getText().toString();
        String data = editData.getText().toString();
        String categoria = editCategoria.getText().toString();
        String descricao = editDescricao.getText().toString();

        if(!valor.isEmpty()){
            if(!data.isEmpty()){
                if(!categoria.isEmpty()){
                    if(!descricao.isEmpty()){
                        return true;
                    }else{
                        Toast.makeText(this, "Preencha todos os campos antes de continuar.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }else{
                    Toast.makeText(this, "Preencha todos os campos antes de continuar.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(this, "Preencha todos os campos antes de continuar.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(this, "Preencha todos os campos antes de continuar.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    /*Método utilizado para recuperar a despesa total do usuário no firebase*/
    public void recuperarReceitaTotal(){
        //Recupera o id unico do usuário, para recuperar sua receita total.
        String emailUser = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(emailUser);
        DatabaseReference userRef = firebaseRef.child("usuarios").child(idUser);

        //Adiciona um listener no usuário para que seus dados sejam recuperados quando sua despesa total mudar
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    /*Método que atualiza a despesa total do usuário quando uma nova for adicionada*/
    public void atualizarReceitaTotal(double receitaAtualizada){
        String emailUser = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(emailUser);
        DatabaseReference userRef = firebaseRef.child("usuarios").child(idUser);
        //Sempre que uma movimentação é adicionada, o valor da despesa total do usuário é atualizada
        userRef.child("receitaTotal").setValue(receitaAtualizada);
    }
}