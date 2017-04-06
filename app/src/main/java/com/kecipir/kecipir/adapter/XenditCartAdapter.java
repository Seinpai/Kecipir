package com.kecipir.kecipir.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kecipir.kecipir.R;
import com.kecipir.kecipir.SessionManager;
import com.kecipir.kecipir.data.ClickListener;
import com.kecipir.kecipir.data.ShoppingCart;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Kecipir-Dev on 10/02/2017.
 */

public class XenditCartAdapter  extends RecyclerView.Adapter<XenditCartAdapter.XenditCartViewHolder>{

    List<ShoppingCart> data;
    private LayoutInflater inflater;
    private Context context;


    TextView jmlCart;
    ShoppingCart cart;

    ClickListener clickListener;

    public XenditCartAdapter(Context context, List<ShoppingCart> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public XenditCartAdapter.XenditCartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate( R.layout.custom_xendit_cart, parent,false);
        XenditCartAdapter.XenditCartViewHolder holder = new XenditCartAdapter.XenditCartViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(XenditCartAdapter.XenditCartViewHolder holder, int position) {
        ShoppingCart current = data.get(position);
        holder.namaBarangCart.setText(current.getNama_barang());
        holder.hargaBarangCart.setText(current.getHarga_jual());
        holder.tglPanenCart.setText(current.getTgl_panen());
        holder.petaniCart.setText(current.getNama_petani());
        Glide.with(context).load(current.getFoto()).into(holder.imgCart);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnClickListener (ClickListener clickListener){
        this.clickListener  = clickListener;
    }

    class XenditCartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imgCart;
        TextView namaBarangCart;
        TextView hargaBarangCart;
        TextView tglPanenCart;
        TextView petaniCart;
        String id_user;
        String email;

        SessionManager sessionManager;

        ShoppingCart current;

        public XenditCartViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            imgCart = (ImageView) itemView.findViewById(R.id.img_cart);
            namaBarangCart = (TextView) itemView.findViewById(R.id.namabarang_cart);
            hargaBarangCart = (TextView) itemView.findViewById(R.id.hargabarang_cart);
            tglPanenCart = (TextView) itemView.findViewById(R.id.tglpanen_cart);
            petaniCart = (TextView) itemView.findViewById(R.id.petani_cart);

            sessionManager = new SessionManager(context);
            HashMap<String, String> user = sessionManager.getUser();
            id_user = user.get("uid");
            email = user.get("email");

        }

        @Override
        public void onClick(View view) {
            if (clickListener!=null){
                clickListener.itemClicked(view, getLayoutPosition());
            }

        }

    }

}
