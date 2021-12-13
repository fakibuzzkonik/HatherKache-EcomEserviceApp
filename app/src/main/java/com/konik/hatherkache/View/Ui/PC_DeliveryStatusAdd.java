package com.konik.hatherkache.View.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.konik.hatherkache.R;
import com.konik.hatherkache.Service.Model.PC_DeliveryStatusModel;

import java.util.HashMap;
import java.util.Map;

public class PC_DeliveryStatusAdd extends AppCompatActivity {

    private RadioGroup mRadioGroup;
    private RadioButton mRadioBtnPending, mRadioBtnConfirmed, mRadioBtnProcessing,
            mRadioBtnPicked, mRadioBtnContacting, mRadioBtnCompleted, mRadioBtnCancel;
    private Button mPostBtn, mPaymentRecivedBtn;
    private EditText mEditNote;

    //Add Delivery
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_c__delivery_status_add);
        //Initialize
        mRadioGroup = (RadioGroup)findViewById(R.id.delivery_add_radio_group);
        mRadioBtnPending = (RadioButton)findViewById(R.id.delivery_add_radio_btn_pending);
        mRadioBtnConfirmed = (RadioButton)findViewById(R.id.delivery_add_radio_btn_confirmed);
        mRadioBtnProcessing = (RadioButton)findViewById(R.id.delivery_add_radio_btn_processing);
        mRadioBtnPicked = (RadioButton)findViewById(R.id.delivery_add_radio_btn_picked);
        mRadioBtnContacting = (RadioButton)findViewById(R.id.delivery_add_radio_btn_contacting);
        mRadioBtnCompleted = (RadioButton)findViewById(R.id.delivery_add_radio_btn_completed);
        mRadioBtnCancel = (RadioButton)findViewById(R.id.delivery_add_radio_btn_cancel);
        mEditNote = (EditText) findViewById(R.id.delivery_add_edit_note);
        mPostBtn = (Button)findViewById(R.id.delivery_add_post_btn);
        mPaymentRecivedBtn = (Button)findViewById(R.id.delivery_add_payment_paid_btn);


        getIntentMethod();
        mPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dsNote = mEditNote.getText().toString();
                if(!dsDeliveryMode.equals("NO") && !dsNote.equals("")){

                    //.orderBy("time", Query.Direction.ASCENDING).limitToLast(3

                    Map<String, Object> mode = new HashMap<>();
                    long diDate = System.currentTimeMillis();
                    long diDate2 = 1623163000000L;
                    diDate = diDate - diDate2 ;

                    //Server Time Setup
                    FieldValue ddDate =  FieldValue.serverTimestamp();

                    mode.put("ddDate", ddDate);
                    mode.put("dsNote", dsNote);
                    mode.put("dsStatus", dsDeliveryMode);

                    db.collection("HatherKacheApp").document("Sylhet")
                            .collection("Orders").document(dsOrderUID)
                            .collection("DeliveryStatus").add(mode).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            mEditNote.setText("");
                            Toast.makeText(getApplicationContext(),"Successfully Sent",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Failed Sent",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"Plese Select Anyone",Toast.LENGTH_SHORT).show();
                }

            }
        });
        mPaymentRecivedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dsOrderUID.equals("NO"))
                    db.collection("HatherKacheApp").document("Sylhet")
                            .collection("Orders").document(dsOrderUID).update("dsPaymentStatus", "Paid");
            }
        });
    }
    private String dsDeliveryMode = "NO";

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.delivery_add_radio_btn_pending:
                if(checked)
                    dsDeliveryMode = "Pending";
                break;
            case R.id.delivery_add_radio_btn_confirmed:
                if(checked)
                    dsDeliveryMode = "Confirmed";
                break;
            case R.id.delivery_add_radio_btn_processing:
                if(checked)
                    dsDeliveryMode = "Processing";
                break;
            case R.id.delivery_add_radio_btn_picked:
                if(checked)
                    dsDeliveryMode = "Picked";
                break;

            case R.id.delivery_add_radio_btn_contacting:
                if(checked)
                    dsDeliveryMode = "Contacting";
                break;
            case R.id.delivery_add_radio_btn_completed:
                if(checked){
                    dsDeliveryMode = "Completed";
                    mPaymentRecivedBtn.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.delivery_add_radio_btn_cancel:
                if(checked){
                    dsDeliveryMode = "Cancel";
                    mPaymentRecivedBtn.setVisibility(View.INVISIBLE);
                }

                break;

        }
        Toast.makeText(getApplicationContext(), dsDeliveryMode, Toast.LENGTH_SHORT).show();
    }
    private String dsOrderUID = "NO",dsCart_UID = "NO";
    private boolean intentErrorFound = false;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dsOrderUID = intent.getExtras().getString("dsOrderUID");    //Sylhet
            dsCart_UID = intent.getExtras().getString("dsCart_UID");    //Sylhet
            intentErrorFound = CheckIntentMethod(dsOrderUID,intentErrorFound);
            intentErrorFound = CheckIntentMethod(dsCart_UID,intentErrorFound);
            if(!intentErrorFound){  //if intent error found false then do the next work

            }
        }else{
            Toast.makeText(this, "Intent Not Found ", Toast.LENGTH_SHORT).show();
            dsOrderUID = "NO";
            dsCart_UID = "NO";
        }

    }
    private boolean CheckIntentMethod(String dsTestIntent, boolean dbIntentErrorFound ){
        if(!dbIntentErrorFound){
            if (TextUtils.isEmpty(dsTestIntent)) {
                dbIntentErrorFound = true;
                dsTestIntent= "NO";
                Toast.makeText(getApplicationContext(), "intent NULL  " , Toast.LENGTH_SHORT).show();
            }
            if (dsOrderUID.equals("")){
                dbIntentErrorFound = true;
                dsTestIntent= "NO";
                Toast.makeText(getApplicationContext(), "intent 404" , Toast.LENGTH_SHORT).show();
            }
        }

        return dbIntentErrorFound;
    }

}