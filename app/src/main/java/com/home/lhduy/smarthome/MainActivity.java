package com.home.lhduy.smarthome;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView txvResult;
    Switch switchLed,switchFan;
    TextView statusLed,statusFan;
    ImageView imagLed,imagFan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchLed = (Switch) findViewById(R.id.swt1);
        switchFan = (Switch) findViewById(R.id.swt2);
        statusLed = (TextView) findViewById(R.id.sttLed);
        statusFan = (TextView) findViewById(R.id.sttFan);
        imagLed = (ImageView) findViewById(R.id.ledView);
        imagFan = (ImageView) findViewById(R.id.fanView);

        final DatabaseReference mData;
        mData = FirebaseDatabase.getInstance().getReference();

        //Hien thi ket qua noi tai text view
        txvResult = (TextView) findViewById(R.id.txvResult);

     //---------------------------------Cap nhat trang thai thiet bi tu database----------------------
        mData.child("LED").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue().toString() == "1"){
                    statusLed.setText("ON");
                    switchLed.setChecked(true);
                    imagLed.setImageResource(R.drawable.light_on);
                }else{
                    statusLed.setText("OFF");
                    switchLed.setChecked(false);
                    imagLed.setImageResource(R.drawable.light_off);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mData.child("FAN").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString() == "1"){
                    statusFan.setText("ON");
                    switchFan.setChecked(true);
                    imagFan.setImageResource(R.drawable.fan_on);
                }else{
                    statusFan.setText("OFF");
                    switchFan.setChecked(false);
                    imagFan.setImageResource(R.drawable.fan_off);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
     //------------------------------------------Điều khiển đèn------------------------------------------
        switchLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    mData.child("LED").setValue(1, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError == null){

                                statusLed.setText("ON");
                            }
                            else{

                            }
                        }
                    });
                }else{

                    mData.child("LED").setValue(0, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError == null){`1

                                statusLed.setText("OFF");
                            }
                            else{

                            }
                        }
                    });
                }
            }
        });

     //----------------------------------------------Điều khiển quạt-------------------------------------------
        switchFan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mData.child("FAN").setValue(1, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError == null){

                                statusFan.setText("ON");
                            }
                            else{

                            }
                        }
                    });

                }else{
                    mData.child("FAN").setValue(0, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError == null){

                                statusFan.setText("OFF");
                            }
                            else{

                            }
                        }
                    });
                }
            }
        });




    }

    //---------------------------------PHAN DIEU KHIEN BANG GIONG NOI-----------------------------

    //Nhan lenh tu giong noi
    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        //Co the thay doi ngon ngu tại Locale ||vi du:Locale.ENGLISH
        //O day dung ngon ngu mat dinh cua thiet bi
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Khai bao bien tham chieu den database cua firebase
        final DatabaseReference mData;
        mData = FirebaseDatabase.getInstance().getReference();

        //LED,FAN = 1 TURN ON
        //LED,FAN = 0 TURN OFF
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {

                    //Truyen ket qua xu ly tu server cho mang result
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    //Hien ket qua nhan duoc
                    txvResult.setText(result.get(0));

                    //Control LED
                    if (result.get(0).toLowerCase().contains("mở đèn")) {
                        mData.child("LED").setValue(1);
                    }
                    if (result.get(0).toLowerCase().contains("tắt đèn")) {
                        mData.child("LED").setValue(0);
                    }

                    //Control FAN
                    if (result.get(0).toLowerCase().contains("mở quạt")) {
                        mData.child("FAN").setValue(1);
                    }
                    if (result.get(0).toLowerCase().contains("tắt quạt")) {
                        mData.child("FAN").setValue(0);
                    }
                }
                break;
        }
    }
 //-----------------------------------------------------------------------------------------
}
