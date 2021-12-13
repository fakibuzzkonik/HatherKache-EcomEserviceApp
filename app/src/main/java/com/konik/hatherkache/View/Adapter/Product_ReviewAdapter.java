package com.konik.hatherkache.View.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.konik.hatherkache.Service.Model.Produt_UserReviewModel;
import com.konik.hatherkache.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.GONE;

public class Product_ReviewAdapter extends FirestoreRecyclerAdapter<Produt_UserReviewModel, Product_ReviewAdapter.BookReviewHolder> {
    public boolean ddUserisAuthor = false;
    public Product_ReviewAdapter(@NonNull FirestoreRecyclerOptions<Produt_UserReviewModel> options, boolean dUserIsAuthor) {
        super(options);
        if(dUserIsAuthor == true){
            ddUserisAuthor = true;
        }else{
            ddUserisAuthor = false;
        }
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference user_data_ref =  db.collection("USER_DATA").document("REGISTER");


    @Override
    public BookReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_product_review_per_item, parent,false);
        return new BookReviewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final BookReviewHolder holder, int position, @NonNull Produt_UserReviewModel model) {
        holder.mBookReviewUserText.setText(model.getUser_review());
        String dUserUID = model.getUser_uid();
        String dAdminUID =  model.getAdmin_id();
        Double dRate = model.getUser_rating();
        Float ddRate = dRate.floatValue();

        Date ddPublishDate = model.getUser_time();
        if(ddPublishDate != null){
            Long currentTime = ddPublishDate.getTime();
            SimpleDateFormat df2 = new SimpleDateFormat("hh:mma  dd/MM/yy");
            String dateText = df2.format(ddPublishDate);
            holder.mBookReviewUserTime.setText(dateText);
        }else
            holder.mBookReviewUserTime.setText("1 seconds ago");


        holder.mBookReviewUserRating.setRating(ddRate);
        holder.mBookReviewAdminText.setText(model.getAdmin_reply());

        user_data_ref =  db.collection("HatherKacheApp").document("REGISTER");
        user_data_ref.collection("NORMAL_USER").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){

                    String dUserName = documentSnapshot.getString("name");
                    String dUserPhotoURL = documentSnapshot.getString("photoURL");

                    holder.mBookReviewUserName.setText(dUserName);
                    if(dUserPhotoURL.equals("NO")){

                    }else if(dUserPhotoURL == null){

                    }else{
                        Picasso.get().load(dUserPhotoURL).into(holder.mBookReviewUserImg);
                    }


                }else{
                    holder.mBookReviewUserName.setText("User NOT FOUND");
                }
            }
        });

        if(dAdminUID.equals("NO")){
            holder.mBookReviewAdminName.setVisibility(GONE);
            holder.mBookReviewAdminImg.setVisibility(GONE);
            holder.mBookReviewAdminType.setVisibility(GONE);
            holder.mBookReviewAdminText.setVisibility(GONE);
            if(ddUserisAuthor == true){
                holder.mBookReviewAdminReplyBtn.setVisibility(View.VISIBLE);
                holder.mBookReviewAdminReplyEditText.setVisibility(View.VISIBLE);
            }
        }else{
            holder.mBookReviewAdminName.setVisibility(View.VISIBLE);
            holder.mBookReviewAdminImg.setVisibility(View.VISIBLE);
            holder.mBookReviewAdminType.setVisibility(View.VISIBLE);
            holder.mBookReviewAdminText.setVisibility(View.VISIBLE);


            db.collection("HatherKacheApp").document("REGISTER").collection("NORMAL_USER").document(dAdminUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        String UserName = documentSnapshot.getString("name");
                        String UserPhotoUrl = documentSnapshot.getString("photoURL");
                        holder.mBookReviewAdminName.setText(UserName);
                        if(UserPhotoUrl.equals("NO")){

                        }else if(UserPhotoUrl == null){

                        }else{
                            Picasso.get().load(UserPhotoUrl).into(holder.mBookReviewAdminImg);
                        }

                    }else{
                        holder.mBookReviewAdminName.setText("Admin Not FOUND");
                    }
                }
            });
        }
    }

    class BookReviewHolder extends RecyclerView.ViewHolder{
        ImageView mBookReviewUserImg, mBookReviewAdminImg;
        TextView mBookReviewUserName, mBookReviewUserText, mBookReviewUserTime;
        TextView mBookReviewAdminName, mBookReviewAdminText, mBookReviewAdminType;
        EditText mBookReviewAdminReplyEditText; Button mBookReviewAdminReplyBtn;
        RatingBar mBookReviewUserRating;
        public BookReviewHolder(@NonNull View itemView){
            super(itemView);
            mBookReviewUserImg = (ImageView)itemView.findViewById(R.id.book_review_user_img);
            mBookReviewAdminImg = (ImageView)itemView.findViewById(R.id.book_review_admin_img);

            mBookReviewUserName = (TextView)itemView.findViewById(R.id.book_review_user_name);
            mBookReviewAdminName = (TextView)itemView.findViewById(R.id.book_review_admin_name);
            mBookReviewUserText = (TextView)itemView.findViewById(R.id.book_review_text);
            mBookReviewAdminText = (TextView)itemView.findViewById(R.id.book_review_admin_reply);
            mBookReviewUserTime = (TextView)itemView.findViewById(R.id.book_review_time);
            mBookReviewAdminType = (TextView)itemView.findViewById(R.id.book_review_user_typp);
            mBookReviewUserRating = (RatingBar)itemView.findViewById(R.id.book_review_rating);
            mBookReviewAdminReplyEditText = (EditText) itemView.findViewById(R.id.book_review_admin_reply_edit);
            mBookReviewAdminReplyBtn = (Button)itemView.findViewById(R.id.book_review_admin_reply_btn);

            mBookReviewAdminReplyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dReplyEditText = mBookReviewAdminReplyEditText.getText().toString();
                    int postion = getAdapterPosition();
                    if(postion != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(postion), postion,dReplyEditText);
                        mBookReviewAdminReplyBtn.setVisibility(GONE);
                        mBookReviewAdminReplyEditText.setVisibility(GONE);
                    }
                }
            });

        }
    }
    private ClickListenerPackage listener;
    private ClickListenerPackage listener2;

    public interface ClickListenerPackage {
        void onItemClick(DocumentSnapshot documentSnapshot, int postion, String dReplyEditText);
    }
    public void replyBtnClickListener(Product_ReviewAdapter.ClickListenerPackage listener){
        this.listener = listener;
    }
}
