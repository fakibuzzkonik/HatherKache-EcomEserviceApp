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

import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewClickInterface;
import com.konik.hatherkache.Service.Model.PB_Cart_Model;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PB_OrderDetails_ItemAdapter extends RecyclerView.Adapter<PB_OrderDetails_ItemAdapter.PB_OrderDetails_ItemHolder> {
    private Context mContext;
    private List<PB_Cart_Model> mData;
    private RecylerviewClickInterface recylerviewClickInterface;

    public PB_OrderDetails_ItemAdapter(Context mContext, List<PB_Cart_Model> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public PB_OrderDetails_ItemAdapter.PB_OrderDetails_ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_pb_cart_b_items, parent, false); //connecting to cardview
        return new PB_OrderDetails_ItemAdapter.PB_OrderDetails_ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PB_OrderDetails_ItemAdapter.PB_OrderDetails_ItemHolder holder, int position) {
        String dPhotoURL = mData.get(position).getProductPHOTO();
        Picasso.get().load(dPhotoURL).into(holder.mcProductImage);
        String dsName = mData.get(position).getProductName();
        int diPrice = mData.get(position).getProductPrice();
        int diQuantity = mData.get(position).getProductQunatity();

        holder.mcProductName.setText(dsName);
        holder.mcProductPrice.setText("TK "+diPrice+" X ");
        int totalprice = diPrice*diQuantity;
        holder.mcProductTotalPrice.setText("TK "+totalprice);
        holder.mcProdcutQuantity.setText(String.valueOf(diQuantity));


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class PB_OrderDetails_ItemHolder extends RecyclerView.ViewHolder {

        ImageView mcProductImage;
        TextView mcProductName;
        TextView mcProductPrice,mcProductTotalPrice;
        TextView mcProdcutQuantity;
        Button mcProductQuantityAddBtn;
        Button mcProductQuantityMinusBtn;
        Button mcProductDelete;

        public PB_OrderDetails_ItemHolder(@NonNull View itemView) {
            super(itemView);

            mcProductImage = (ImageView) itemView.findViewById(R.id.pb_cart_b_tem_img);
            mcProductName = (TextView) itemView.findViewById(R.id.pb_cart_b_product_name);
            mcProductPrice = (TextView) itemView.findViewById(R.id.pb_cart_b_product_price);
            mcProductTotalPrice = (TextView) itemView.findViewById(R.id.pb_cart_b_product_price_total);
            mcProdcutQuantity = (TextView) itemView.findViewById(R.id.pb_cart_b_product_quantity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface.onItemClick(getAdapterPosition());
                }
            });
            /*mCategoryBtn = (Button) itemView.findViewById(R.id.card_category_btn);

            mCategoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postion = getAdapterPosition();
                    if (postion != RecyclerView.NO_POSITION && listener1 != null) {
                        listener1.onItemClick(getSnapshots().getSnapshot(postion), postion);

                    }
                }
            });*/
        }
    }
}