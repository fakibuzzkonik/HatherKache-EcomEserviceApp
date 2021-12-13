package com.konik.hatherkache.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.konik.hatherkache.Service.Model.PC_DeliveryStatusModel;
import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewClickInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PC_DeliveryStatusAdapter extends RecyclerView.Adapter<PC_DeliveryStatusAdapter.PC_DeliveryStatusHolder> {
    private Context mContext;
    private List<PC_DeliveryStatusModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    private int dicount = 0;
    public PC_DeliveryStatusAdapter(int count, Context mContext, List<PC_DeliveryStatusModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
        dicount = count;
    }

    @NonNull
    @Override
    public PC_DeliveryStatusAdapter.PC_DeliveryStatusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_pc_order_delivery_status, parent, false); //connecting to cardview
        return new PC_DeliveryStatusAdapter.PC_DeliveryStatusHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PC_DeliveryStatusAdapter.PC_DeliveryStatusHolder holder, int position) {
        String diLastCheck = mData.get(position).getDsStatusUID();
        

            Date date = mData.get(position).getDdDate();
            SimpleDateFormat df2 = new SimpleDateFormat("dd MMM\nhh:mm a");
            String dateText = df2.format(date);
            holder.mcStatusTime.setText(dateText);




        String dsMode = mData.get(position).getDsStatus();
        String dsNote = mData.get(position).getDsNote();
        holder.mcStatusMode.setText(dsMode);
        holder.mcStatusNote.setText(dsNote);
        if(diLastCheck.equals("FIRST")){
            holder.mcLinearBar.setVisibility(View.VISIBLE);
            holder.mcLinearRoundBottomBall.setVisibility(View.VISIBLE);
        }else if(diLastCheck.equals("LAST")){
            holder.mcLinearBar.setVisibility(View.INVISIBLE);
            holder.mcLinearRoundBottomBall.setVisibility(View.GONE);
        }else if(diLastCheck.equals("2nd")){
            holder.mcLinearBar.setVisibility(View.VISIBLE);
            holder.mcLinearRoundBottomBall.setVisibility(View.GONE);
        }else{
            holder.mcLinearBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class PC_DeliveryStatusHolder extends RecyclerView.ViewHolder {

        LinearLayout mcLinearRoundBall,mcLinearRoundBottomBall, mcLinearBar;
        TextView mcStatusTime;
        TextView mcStatusMode;
        TextView mcStatusNote;

        public PC_DeliveryStatusHolder(@NonNull View itemView) {
            super(itemView);

            mcLinearRoundBall = (LinearLayout) itemView.findViewById(R.id.delivery_status_round_icon);
            mcLinearRoundBottomBall = (LinearLayout) itemView.findViewById(R.id.delivery_status_round_icon_bottom);
            mcLinearBar = (LinearLayout) itemView.findViewById(R.id.delivery_status_bar_icon);
            mcStatusTime = (TextView)itemView.findViewById(R.id.delivery_status_date_text); 
            mcStatusMode = (TextView)itemView.findViewById(R.id.delivery_status_text); 
            mcStatusNote = (TextView)itemView.findViewById(R.id.delivery_status_note); 
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