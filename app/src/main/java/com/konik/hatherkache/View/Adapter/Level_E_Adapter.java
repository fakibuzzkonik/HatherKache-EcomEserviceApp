package com.konik.hatherkache.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.konik.hatherkache.Service.Model.Level_E_Model;
import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewClickInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Level_E_Adapter extends RecyclerView.Adapter<Level_E_Adapter.Level_E_Holder> {
    private Context mContext;
    private List<Level_E_Model> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    private String dsLevel2_Name = "N0";

    public Level_E_Adapter(Context mContext, List<Level_E_Model> mData, RecylerviewClickInterface recylerviewClickInterface, String dsLevel2_Name) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
        this.dsLevel2_Name = dsLevel2_Name;
    }

    @NonNull
    @Override
    public Level_E_Adapter.Level_E_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_level_e_item, parent, false); //connecting to cardview
        return new Level_E_Adapter.Level_E_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Level_E_Adapter.Level_E_Holder holder, int position) {
        String dPhotoURL = mData.get(position).getL5PhotoUrl().get(0);
        Picasso.get().load(dPhotoURL).into(holder.mL5ItemImageView);
        String dsTitle = mData.get(position).getL5Name();
        String dsFrom = mData.get(position).getL5From();
        int diPrice = mData.get(position).getL5iPrice();
        int diViews = mData.get(position).getL5iViews();
        int diOrders = mData.get(position).getL5iOrders();
        int diRatingCount = mData.get(position).getL5iRating();
        float dfRatingCount = (float)diRatingCount;
        holder.mL5ItemTitleText.setText(dsTitle);
        holder.mL5ItemFromText.setText(dsFrom);
        holder.mL5ItemRatingText.setText(String.valueOf(diRatingCount)+".0");
        holder.mL5ItemViewsText.setText(String.valueOf(diViews) + "+");
        holder.mL5ItemOrdersText.setText(String.valueOf(diOrders) + "+ orders");
        try {
            holder.mL5ItemRatingBar.setRating(dfRatingCount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dsLevel2_Name.equals("FoodDelivery") ){
            holder.mL5ItemFromText.setVisibility(View.GONE);
        }else if(dsLevel2_Name.equals("GroceryShopping") ){
            holder.mL5ItemFromText.setText(String.valueOf(diPrice)+" TK");
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class Level_E_Holder extends RecyclerView.ViewHolder {

        ImageView mL5ItemImageView;
        TextView mL5ItemTitleText;
        TextView mL5ItemFromText;
        TextView mL5ItemViewsText;
        TextView mL5ItemOrdersText;
        TextView mL5ItemRatingText;
        RatingBar mL5ItemRatingBar;

        public Level_E_Holder(@NonNull View itemView) {
            super(itemView);

            mL5ItemImageView = (ImageView) itemView.findViewById(R.id.level_e_item_img);
            mL5ItemTitleText = (TextView) itemView.findViewById(R.id.level_e_title_id);
            mL5ItemFromText = (TextView) itemView.findViewById(R.id.level_e_from);
            mL5ItemViewsText = (TextView) itemView.findViewById(R.id.level_e_total_view);
            mL5ItemOrdersText = (TextView) itemView.findViewById(R.id.level_e_total_orders);
            mL5ItemRatingText = (TextView) itemView.findViewById(R.id.level_e_rating_text);
            mL5ItemRatingBar = (RatingBar) itemView.findViewById(R.id.level_e_ratingbar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface.onItemClick(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recylerviewClickInterface.onItemLongCLick(getAdapterPosition());
                    return false;
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
