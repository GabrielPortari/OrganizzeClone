package com.example.organizzeclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.organizzeclone.R;
import com.example.organizzeclone.adapter.AdapterMovimentacao;
import com.example.organizzeclone.config.ConfigFirebase;
import com.example.organizzeclone.helper.Base64Custom;
import com.example.organizzeclone.model.Movimentacao;
import com.example.organizzeclone.model.Usuario;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {
    /* Declaração de váriaveis */
    private FloatingActionButton fabReceita, fabDespesa;
    private MaterialCalendarView materialCalendarView;
    private TextView textSaudacao, textSaldo;

    //Método da classe ConfigFirebase utilizado para selecionar a instancia da autenticação e banco de dados
    private FirebaseAuth auth = ConfigFirebase.getFirebaseAuth();
    private DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();

    private DatabaseReference userRef, movimentacaoRef;
    private ValueEventListener valueEventListenerUsuario, valueEventListenerMovimentacoes;
    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private Movimentacao movimentacao;
    private double despesaTotal = 0.0;
    private double receitaTotal = 0.0;
    private double resumoUsuario = 0.0;
    private String mesAnoSelecionado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        // Definição dos Ids de cada item do layout
        getSupportActionBar().show();
        getSupportActionBar().setElevation(0);
        fabReceita = findViewById(R.id.fab_receita);
        fabDespesa = findViewById(R.id.fab_despesa);
        materialCalendarView = findViewById(R.id.calendarView);
        textSaudacao = findViewById(R.id.text_saudacao);
        textSaldo = findViewById(R.id.text_saldo);
        recyclerView = findViewById(R.id.recyclerView);

        // Métodos para configurar a biblioteca Material Calendar, e definição do swipe no recycler view
        configCalendar();
        swipe();

        // Configuração do clicklistener dos FAB do FAB Menu
        fabReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarReceita(v);
            }
        });
        fabDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarDespesa(v);
            }
        });

        // Configuração do adapter e recycler view
        adapterMovimentacao = new AdapterMovimentacao(movimentacoes, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapterMovimentacao);
    }
    /*Método que configura o swipe para o recycler view*/
    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlag = ItemTouchHelper.ACTION_STATE_IDLE; // drag faz menção a mover o item da recycler para qualquer lado
                int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END; // swipe faz menção para arrastar para os lados, neste caso vai para direita/esquerda
                return makeMovementFlags(dragFlag, swipeFlag); // retorna drag = inativo, swipe = para esquerda e direita
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirMovimentacao(viewHolder); // ao usar swipe na horizontal, chama o método excluirMovimentacao para o viewHolder que foi swipado
            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }
    /*Método responsável por excluir as movimentações de um usuário, tanto do aplicativo (recyclerview), quanto do banco de dados do firebase*/
    public void excluirMovimentacao(RecyclerView.ViewHolder viewHolder){
        //Configuração do AlertDialog, que pede a confirmação do usuário para excluir ou não uma movimentação do app/firebase
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Exclusão");
        alertDialog.setMessage("Deseja excluir essa movimentação da sua conta?");
        alertDialog.setCancelable(false);
        //Configuração para caso o usuário deseja confirmar a exclusão
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Recupera a posição do viewHolder swipado no adapter;
                int posicao = viewHolder.getAdapterPosition();
                //Apos recuperar a posição do viewHolder, recupera a posição da movimentação na lista
                movimentacao = movimentacoes.get(posicao);

                //Recupera o email do usuário e converte para base64 para obter o identificador unico
                String emailUser = auth.getCurrentUser().getEmail();
                String idUser = Base64Custom.codeBase64(emailUser);

                //Utilizando o id do usuario, percorre o banco de dados em movimentacao -> id usuario -> mes selecionado
                movimentacaoRef = firebaseRef.child("movimentacao")
                        .child(idUser)
                        .child(mesAnoSelecionado);
                //É recuperado a chave (identificador unico do firebase), e excluido essa tabela.
                movimentacaoRef.child(movimentacao.getChave()).removeValue();
                //Notifica o adapter para atualizar com o item removido
                adapterMovimentacao.notifyItemRemoved(posicao);
                //Chama o método atualizar saldo para atualizar os dados no firebase
                atualizarSaldo();
            }
        });
        //Configuração caso o usuário cancele a exclusão
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Apenas é mostrado um toast e o item swipado retorna para a tela
                Toast.makeText(PrincipalActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                adapterMovimentacao.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }
    /*Método utilizado para atualizar o saldo após um item ser excluido*/
    public void atualizarSaldo(){
        //Recupera o valor total de uma receita/despesa, e subtrai o valor que foi excluido
        if(movimentacao.getTipo().equals("r")){
            receitaTotal = receitaTotal - movimentacao.getValor();
            userRef.child("receitaTotal").setValue(receitaTotal);
        }
        if(movimentacao.getTipo().equals("d")){
            despesaTotal = despesaTotal - movimentacao.getValor();
            userRef.child("despesaTotal").setValue(receitaTotal);
        }
    }
    /*Método utilizado para abrir a activity de adicionar receita utilizado pelo FAB*/
    public void adicionarReceita(View view){
        startActivity(new Intent(this, ReceitaActivity.class));
    }
    /*Método utilizado para abrir a activity de adicionar despesa utilizado pelo FAB*/
    public void adicionarDespesa(View view){
        startActivity(new Intent(this, DespesaActivity.class));
    }
    /*Método utilizado para configurar o Material Calendar*/
    public void configCalendar(){
        //Muda o titulo dos meses para portugues
        CharSequence meses[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        materialCalendarView.setTitleMonths(meses);

        //Recupera a data atual e formata para ser utilizado no firebase ex, janeiro de 2001 -> 012001
        CalendarDay dataAtual = materialCalendarView.getCurrentDate();
        String mesFormatado = String.format("%02d", dataAtual.getMonth());
        mesAnoSelecionado = mesFormatado + "" + dataAtual.getYear();

        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                //No método onMonthChanged, sempre que o usuario trocar o mes na tela principal, esse método será chamado
                //Desta forma sempre que o mês for trocado, será recuperado as movimentações daquele mês
                String mesFormatado = String.format("%02d", date.getMonth());
                mesAnoSelecionado = mesFormatado + "" + date.getYear();
                //Remove o eventlistener para que ele seja reiniciado no método recuperarMovimentacoes
                movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
                recuperarMovimentacoes();
            }
        });
    }
    /*Método responsável por recuperar e atualizar as mensagens na tela inicial*/
    public void recuperarResumo(){
        //Recupera o usuário que está utilizando o app a partir de seu id unico
        String emailUser = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(emailUser);
        userRef = firebaseRef.child("usuarios").child(idUser);

        //eventListener para que sempre que algo for mudado, atualizar as informações na tela principal
        valueEventListenerUsuario = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Recupera o objeto usuário salvo no firebase
                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
                receitaTotal = usuario.getReceitaTotal();
                resumoUsuario = receitaTotal-despesaTotal;

                //Formatação do texto que será mostrado na tela R$ 0.00
                DecimalFormat dF = new DecimalFormat("0.##");
                //setText do nome do usuario e seu saldo na tela principal
                textSaudacao.setText("Olá " + usuario.getNome());
                textSaldo.setText("R$ " + dF.format(resumoUsuario));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    /*Método responsável por recuperar e atualizar as movimentações de cada mês na tela*/
    public void recuperarMovimentacoes(){
        //Recupera o usuário e seu id unico
        String emailUser = auth.getCurrentUser().getEmail();
        String idUser = Base64Custom.codeBase64(emailUser);

        //Recupera a referencia ao mes e ano selecionado que foi configurado no metodo configCalendar
        movimentacaoRef = firebaseRef.child("movimentacao")
                                    .child(idUser)
                                    .child(mesAnoSelecionado);
        //Adiciona um listener para a referencia de movimentacoes para que caso algo seja mudado, atualizar o firebase/recyclerview
        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Limpa a lista de movimentações, para que possa ser adicionada novamente do seu respectivo mês e ano
                movimentacoes.clear();
                for(DataSnapshot dados : snapshot.getChildren()){
                    //adiciona na lista de movimentacoes, cada movimentacao que existe naquele mês e ano selecionado
                    movimentacao = dados.getValue(Movimentacao.class);
                    movimentacao.setChave(dados.getKey());
                    movimentacoes.add(movimentacao);
                }
                //Notifica o Adapter para atualizar na tela
                adapterMovimentacao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    /*Método que configura o menu da tela*/
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    /*Método que configura os itens do menu*/
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Caso o item Sair seja selecionado, realiza o logout do usuario
        if(item.getItemId() == R.id.menuSair){
            auth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        Log.i("onOptionsItemSelected", "Logout efetuado");
        return super.onOptionsItemSelected(item);
    }
    @Override
    /*Método onStart é chamado toda vez que essa activity é aberta*/
    protected void onStart() {
        super.onStart();
        //Sempre que essa activity for chamada, atualiza o usuario e suas movimentações
        recuperarResumo();
        recuperarMovimentacoes();
        Log.i("onStart", "Resumo e movimentacoes recuperados");
    }
    @Override
    /*Método onStop é chamado sempre que o app ficar em segundo plano*/
    protected void onStop() {
        super.onStop();
        Log.i("onStop", "EventListener removido");
        //Remove os listeners do usuario e movimentações para que não haja consumo desnecessário de memória
        userRef.removeEventListener(valueEventListenerUsuario);
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
    }
}