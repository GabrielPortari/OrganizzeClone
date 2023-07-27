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

public class DespesaActivity extends AppCompatActivity {
    /*Declaração de váriaveis */
    private TextInputEditText editData, editCategoria, editDescricao;
    private EditText editValor;
    private FloatingActionButton fabAddDespesa;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
    private FirebaseAuth auth = ConfigFirebase.getFirebaseAuth();
    private double despesaTotal;
    private double despesaAtualizada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa);
        //Chama o método para recuperar a despesa total do usuário
        recuperarDespesaTotal();

        //Definição dos Ids de cada item do layout
        fabAddDespesa = findViewById(R.id.fab_add_despesa);
        editValor = findViewById(R.id.editValor_despesa);
        editData = findViewById(R.id.editData_despesa);
        editCategoria = findViewById(R.id.editCategoria_despesa);
        editDescricao = findViewById(R.id.editDescricao_despesa);
        //Seta o campo da data com a data atual
        editData.setText(DateCustom.dataAtual());

        //Listener para o botao de adicionar despesa
        fabAddDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarDespesa(v);
            }
        });

    }
    /*Método responsável por salvar uma despesa no banco de dados*/
    public void salvarDespesa(View view){
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
            movimentacao.setTipo("d");
            despesaAtualizada = valorGerado + despesaTotal;
            //Atualiza a despesa total do usuário no firebase
            atualizarDespesaTotal(despesaAtualizada);
            //Salva a nova movimentação no firebase
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
    public void recuperarDespesaTotal(){
        //Recupera o id unico do usuário, para recuperar sua despesa total.
        String emailUser = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(emailUser);
        DatabaseReference userRef = firebaseRef.child("usuarios").child(idUser);

        //Adiciona um listener no usuário para que seus dados sejam recuperados quando sua despesa total mudar
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    /*Método que atualiza a despesa total do usuário quando uma nova for adicionada*/
    public void atualizarDespesaTotal(double despesaAtualizada){
        String emailUser = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(emailUser);
        DatabaseReference userRef = firebaseRef.child("usuarios").child(idUser);
        //Sempre que uma movimentação é adicionada, o valor da despesa total do usuário é atualizada
        userRef.child("despesaTotal").setValue(despesaAtualizada);
    }
}