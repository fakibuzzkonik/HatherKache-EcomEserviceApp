package com.konik.hatherkache.View.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.konik.hatherkache.R;
import com.konik.hatherkache.RecylerviewClickInterface;
import com.konik.hatherkache.Service.Model.PB_OrderList_Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PB_OrderList_Adapter extends RecyclerView.Adapter<PB_OrderList_Adapter.PB_OrderList_Holder> {
    private Context mContext;
    private List<PB_OrderList_Model> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    public PB_OrderList_Adapter (Context mContext, List<PB_OrderList_Model> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public PB_OrderList_Adapter.PB_OrderList_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_level_order_name,parent,false); //connecting to cardview
        return new PB_OrderList_Adapter.PB_OrderList_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PB_OrderList_Adapter.PB_OrderList_Holder holder, int position) {

        String dsInvoice = mData.get(position).getUid_order();
        Date date = mData.get(position).getDiTime();
        int diPrice = mData.get(position).getDiTotal_Money();
        String dsCompleteLevel = mData.get(position).getDsCompleteLevel();
        String dsPaymentStatus = mData.get(position).getDsPaymentStatus();

        //String dsOrderTimeAgo = TimeAgo(diDate);
        int dsSize = dsInvoice.length();
        int total_value = 0;
        for(int i =0; i<dsSize; i++){
            char character = dsInvoice.charAt(i);
            int ascii = (int) character;
            total_value = total_value + ascii;
        }
            //Long Date to String
            //date=new Date();
            SimpleDateFormat df2 = new SimpleDateFormat("hh:mma  dd/MMM/yy");
            String dateText = df2.format(date);
        long diTime = date.getTime();
        holder.mDateTimeAgoText.setText(TimeAgo(diTime));
        diTime = diTime - -1625464583260L;
        String dsInvoiceID =  String.valueOf(diTime);
        int dsInvoiceIDSize = dsInvoiceID.length();
        holder.mInvoiceText.setText("INVOICE ID: "+ dsInvoiceID.substring(4,7)+" "+dsInvoiceID.substring(8,dsInvoiceIDSize-1));

        holder.mDateText.setText(dateText);
        holder.mPriceText.setText(String.valueOf(diPrice)+"TK");
        if(dsCompleteLevel.equals("1"))
            holder.mProcessingModeText.setText("Pending");
        else if(dsCompleteLevel.equals("Completed")){
            holder.mProcessingModeText.setText(dsCompleteLevel);
            //holder.mProcessingModeText.setBackground(mContext.getResources().getDrawable(R.drawable.style_ripple_textc_paid_round));
            holder.mProcessingModeText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.style_ripple_textc_paid_round));

        }
        else
            holder.mProcessingModeText.setText(dsCompleteLevel);
        holder.mMoneyModeText.setText(dsPaymentStatus);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class PB_OrderList_Holder extends RecyclerView.ViewHolder {

        TextView mInvoiceText;
        TextView mDateText;
        TextView mDateTimeAgoText;
        TextView mPriceText;
        TextView mProcessingModeText, mMoneyModeText;

        public PB_OrderList_Holder(@NonNull View itemView) {
            super(itemView);

            mInvoiceText = (TextView)itemView.findViewById(R.id.order_item_invoice_text);
            mDateText = (TextView)itemView.findViewById(R.id.order_item_date_text);
            mDateTimeAgoText = (TextView)itemView.findViewById(R.id.order_item_time_ago_text);
            mPriceText = (TextView)itemView.findViewById(R.id.order_item_price_text);
            mProcessingModeText = (TextView)itemView.findViewById(R.id.order_item_processing_mode_text);
            mMoneyModeText = (TextView)itemView.findViewById(R.id.order_item_money_mode_text);
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
    private String TimeAgo(long dltime){
        String dstime = " ";
        try
        {
            Long currentTime = dltime;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            Date date = new Date(currentTime);
            String time = simpleDateFormat.format(date);

            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            Date past = format.parse(time);
            Date now = new Date();
            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if(seconds<60)
                dstime = seconds+" seconds ago";
            else if(minutes<60)
                dstime = minutes+" minutes ago";
            else if(hours<24)
                dstime = hours+" hours ago";
            else
                dstime = days+" days ago";


        }
        catch (Exception j){
            j.printStackTrace();
        }

        return dstime;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}