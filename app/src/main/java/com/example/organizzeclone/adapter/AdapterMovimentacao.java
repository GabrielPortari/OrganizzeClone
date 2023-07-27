package com.example.organizzeclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.organizzeclone.R;
import com.example.organizzeclone.model.Movimentacao;

import java.util.List;

public class AdapterMovimentacao extends RecyclerView.Adapter<AdapterMovimentacao.MyViewHolder> {
    /*Construtor do adapter, recebe como parametro uma lista de movimentações, e o context*/
    List<Movimentacao> movimentacaoList;
    Context context;
    public AdapterMovimentacao(List<Movimentacao> movimentacaoList, Context c) {
        this.movimentacaoList = movimentacaoList;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Retorna um myviewholder para cada item da lista
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Recupera a movimentação a partir da lista criada pelo construtor, e mostra seus dados
        Movimentacao movimentacao = movimentacaoList.get(position);
        String valorMov = "R$" + movimentacao.getValor();
        holder.categoria.setText(movimentacao.getCategoria());
        holder.descricao.setText(movimentacao.getDescricao());
        holder.valor.setText(valorMov);
        //Caso a movimentação seja uma despesa, mostra em vermelho, caso seja uma receita em verde
        if(movimentacao.getTipo() == "d" || movimentacao.getTipo().equals("d")){
            holder.valor.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDespesa));
            holder.valor.setText(valorMov);
        }else{
            holder.valor.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryReceita));
            holder.valor.setText(valorMov);
        }
    }

    @Override
    public int getItemCount() {
        //Retorna o tamanho da lista
        return movimentacaoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView categoria, descricao, valor;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //Definição do id dos itens do layout
            categoria = itemView.findViewById(R.id.textCategoria_recycler);
            descricao = itemView.findViewById(R.id.textDescricao_recycler);
            valor = itemView.findViewById(R.id.textValor_recycler);
        }
    }
}
