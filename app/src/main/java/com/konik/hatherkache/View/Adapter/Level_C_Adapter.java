package com.konik.hatherkache.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.konik.hatherkache.Service.Model.Level_C_Model;
import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewClickInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Level_C_Adapter extends RecyclerView.Adapter<Level_C_Adapter.Level_C_Holder> {
    private Context mContext;
    private List<Level_C_Model> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    public Level_C_Adapter (Context mContext, List<Level_C_Model> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public Level_C_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_level_c_item,parent,false); //connecting to cardview
        return new Level_C_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Level_C_Holder holder, int position) {
        String dPhotoURL = mData.get(position).getL3PhotoUrl();
        Picasso.get().load(dPhotoURL).into(holder.mL3ItemImageView);
        String dsTitle = mData.get(position).getL3Name();
        String dsBio = mData.get(position).getL3Bio();
        holder.mL3ItemTitleText.setText(dsTitle);
        holder.mL3ItemBioText.setText(dsBio);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class Level_C_Holder extends RecyclerView.ViewHolder {

        ImageView mL3ItemImageView;
        TextView mL3ItemTitleText;
        TextView mL3ItemBioText;

        public Level_C_Holder(@NonNull View itemView) {
            super(itemView);

            mL3ItemImageView = (ImageView) itemView.findViewById(R.id.item_level_c_img_id);
            mL3ItemTitleText = (TextView)itemView.findViewById(R.id.item_level_c_title_id);
            mL3ItemBioText = (TextView)itemView.findViewById(R.id.item_level_c_bio);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface .onItemClick(getAdapterPosition());
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