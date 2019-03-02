package com.example.android.travelsafe;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class FeedbackActivity extends AppCompatActivity {

    CheckBox cb1, cb2, cb3, cb4, cb5, cb6;
    RatingBar ratingBar;
    Button Submit;
    Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        cb1=(CheckBox)findViewById(R.id.checkBox1);
        cb2=(CheckBox)findViewById(R.id.checkBox2);
        cb3=(CheckBox)findViewById(R.id.checkBox3);
        cb4=(CheckBox)findViewById(R.id.checkBox4);
        cb5=(CheckBox)findViewById(R.id.checkBox5);
        cb6=(CheckBox)findViewById(R.id.checkBox6);
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);
        Submit=(Button)findViewById(R.id.btnSubmit);
       Firebase.setAndroidContext(this);
       String UniqueID= Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
       firebase=new Firebase("https://travel-safe-30e9e.firebaseio.com/Users"+ UniqueID);
      //  database = FirebaseDatabase.getInstance();
        //myRef = database.getReference("message");


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String field1="0", field2="0", field3="0", field4="0", field5="0", field6="0";
                if(cb1.isChecked()) field1="1";
                if(cb2.isChecked()) field2="1";
                if(cb3.isChecked()) field3="1";
                if(cb4.isChecked()) field4="1";
                if(cb5.isChecked()) field5="1";
                if(cb6.isChecked()) field6="1";
                final String rating = Float.toString(ratingBar.getRating());

                Firebase f1=firebase.child("Field1");
                f1.setValue(field1);
                Firebase f2=firebase.child("Field2");
                f2.setValue(field2);
                Firebase f3=firebase.child("Field3");
                f3.setValue(field3);
                Firebase f4=firebase.child("Field4");
                f4.setValue(field4);
                Firebase f5=firebase.child("Field5");
                f5.setValue(field5);
                Firebase f6=firebase.child("Field6");
                f6.setValue(field6);
                Firebase child_rating=firebase.child("Rating");
                child_rating.setValue(rating);

                Toast.makeText(getApplicationContext(),"Feedback Sent!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(FeedbackActivity.this,SecondActivity.class));
            }
        });
    }
}
