package com.konik.hatherkache.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.konik.hatherkache.Service.Model.PB_Cart_Model;
import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewCartClickInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PB_Cart_Adapter extends RecyclerView.Adapter<PB_Cart_Adapter.PB_Cart_Holder> {
    private Context mContext;
    private List<PB_Cart_Model> mData;
    private RecylerviewCartClickInterface recylerviewCartClickInterface;

    public PB_Cart_Adapter(Context mContext, List<PB_Cart_Model> mData, RecylerviewCartClickInterface recylerviewCartClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewCartClickInterface = recylerviewCartClickInterface;
    }

    @NonNull
    @Override
    public PB_Cart_Adapter.PB_Cart_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_pb_cart_items, parent, false); //connecting to cardview
        return new PB_Cart_Adapter.PB_Cart_Holder(view);
    }
    int diPrice = 0;
    int diQuantity = 0;
    @Override
    public void onBindViewHolder(@NonNull PB_Cart_Adapter.PB_Cart_Holder holder, int position) {
        String dPhotoURL = mData.get(position).getProductPHOTO();
        Picasso.get().load(dPhotoURL).into(holder.mcProductImage);
        String dsName = mData.get(position).getProductName();
        diPrice = mData.get(position).getProductPrice();
        diQuantity = mData.get(position).getProductQunatity();
        holder.mcProductName.setText(dsName);
        holder.mcProductPrice.setText("TK "+diPrice+" X ");
        holder.mcProdcutQuantity.setText(String.valueOf(diQuantity));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class PB_Cart_Holder extends RecyclerView.ViewHolder {

        ImageView mcProductImage;
        TextView mcProductName;
        TextView mcProductPrice;
        TextView mcProdcutQuantity;
        Button mcProductQuantityAddBtn;
        Button mcProductQuantityMinusBtn;
        Button mcProductDelete;

        public PB_Cart_Holder(@NonNull View itemView) {
            super(itemView);

            mcProductImage = (ImageView) itemView.findViewById(R.id.pb_cart_tem_img);
            mcProductName = (TextView) itemView.findViewById(R.id.pb_cart_product_name);
            mcProductPrice = (TextView) itemView.findViewById(R.id.pb_cart_product_price);
            mcProdcutQuantity = (TextView) itemView.findViewById(R.id.pb_cart_product_quantity);
            mcProductQuantityAddBtn = (Button) itemView.findViewById(R.id.pb_cart_product_quantity_add);
            mcProductQuantityMinusBtn = (Button) itemView.findViewById(R.id.pb_cart_product_quantity_minus);
            mcProductDelete = (Button) itemView.findViewById(R.id.pb_cart_product_delete);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewCartClickInterface.onWholeItemClick(getAdapterPosition());
                }
            });

            mcProductQuantityAddBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    diQuantity = mData.get(position).getProductQunatity();;
                    diPrice = mData.get(position).getProductPrice();
                    diQuantity++;
                    mData.get(position).setProductQunatity(diQuantity);
                    mcProdcutQuantity.setText(String.valueOf(diQuantity));
                    recylerviewCartClickInterface.onPlusBtnClick(getAdapterPosition(),diQuantity,diPrice);
                }
            });
            mcProductQuantityMinusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    diQuantity = mData.get(position).getProductQunatity();;
                    diPrice = mData.get(position).getProductPrice();
                    if(diQuantity > 1){
                        diQuantity--;
                        mData.get(position).setProductQunatity(diQuantity);
                        mData.get(position).setProductPrice(diPrice);
                        mcProdcutQuantity.setText(String.valueOf(diQuantity));
                        recylerviewCartClickInterface.onMinusBtnClick(getAdapterPosition(),diQuantity,diPrice, false);
                    }else{
                        recylerviewCartClickInterface.onMinusBtnClick(getAdapterPosition(),diQuantity,diPrice, true);
                    }
                }
            });
            mcProductDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    diQuantity = mData.get(position).getProductQunatity();;
                    diPrice = mData.get(position).getProductPrice();
                    recylerviewCartClickInterface.onDeleteItemClick(position,diQuantity,diPrice);
                }
            });


        }
    }


}


