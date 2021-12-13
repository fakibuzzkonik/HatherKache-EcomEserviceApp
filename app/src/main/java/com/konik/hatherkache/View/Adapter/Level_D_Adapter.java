package com.konik.hatherkache.View.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.konik.hatherkache.Service.Model.Level_D_Model;
import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewClickInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Level_D_Adapter extends RecyclerView.Adapter<Level_D_Adapter.Level_D_Holder> {
private Context mContext;
private List<Level_D_Model> mData;
private RecylerviewClickInterface recylerviewClickInterface;
public Level_D_Adapter (Context mContext, List<Level_D_Model> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
        }

@NonNull
@Override
public Level_D_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_level_d_item,parent,false); //connecting to cardview
        return new Level_D_Holder(view);
        }

@Override
public void onBindViewHolder(@NonNull Level_D_Holder holder, int position) {
        String dPhotoURL = mData.get(position).getL4PhotoUrl();
        Picasso.get().load(dPhotoURL).into(holder.mL4ItemImageView);
        String dsTitle = mData.get(position).getL4Name();
        int diViews = mData.get(position).getL4iViewCount();
        String dsBio = mData.get(position).getL4Bio();
        holder.mL4ItemTitleText.setText(dsTitle);
        holder.mL4ItemBioText.setText(dsBio);
        holder.mL4ItemViewsText.setText(String.valueOf(diViews));
        }

@Override
public int getItemCount() {
        return mData.size();
        }


class Level_D_Holder extends RecyclerView.ViewHolder {

    ImageView mL4ItemImageView;
    TextView mL4ItemTitleText;
    TextView mL4ItemViewsText;
    TextView mL4ItemBioText;

    public Level_D_Holder(@NonNull View itemView) {
        super(itemView);

        mL4ItemImageView = (ImageView) itemView.findViewById(R.id.level_d_item_img);
        mL4ItemTitleText = (TextView)itemView.findViewById(R.id.level_d_title_id);
        mL4ItemViewsText = (TextView)itemView.findViewById(R.id.level_d_views);
        mL4ItemBioText = (TextView)itemView.findViewById(R.id.level_d_bio_text);
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