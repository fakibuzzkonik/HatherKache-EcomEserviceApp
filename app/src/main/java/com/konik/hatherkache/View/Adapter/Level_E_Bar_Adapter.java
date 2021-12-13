package com.konik.hatherkache.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewBarBtnClickInterface;

import java.util.List;

public class Level_E_Bar_Adapter extends RecyclerView.Adapter<Level_E_Bar_Adapter.Level_E_Bar_Holder> {
    private Context mContext;
    private List<String> mData;
    private RecylerviewBarBtnClickInterface recylerviewBarBtnClickInterface;
    private String dsLevel2_Name = "N0";

    public Level_E_Bar_Adapter(Context mContext, List<String> mData, RecylerviewBarBtnClickInterface recylerviewBarBtnClickInterface, String dsLevel2_Name) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewBarBtnClickInterface = recylerviewBarBtnClickInterface;
        //this.dsLevel2_Name = dsLevel2_Name;
    }

    @NonNull
    @Override
    public Level_E_Bar_Adapter.Level_E_Bar_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_level_e_bar_item, parent, false); //connecting to cardview
        return new Level_E_Bar_Adapter.Level_E_Bar_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Level_E_Bar_Adapter.Level_E_Bar_Holder holder, int position) {
        String dsL5ItemText = mData.get(position).toString();
        holder.mL5BarText.setText(dsL5ItemText);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class Level_E_Bar_Holder extends RecyclerView.ViewHolder {

        TextView mL5BarText;
        public Level_E_Bar_Holder(@NonNull View itemView) {
            super(itemView);
            mL5BarText = (TextView) itemView.findViewById(R.id.level_e_bar_item_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    recylerviewBarBtnClickInterface.onBarItemClick(pos);
                }
            });

        }
    }

}