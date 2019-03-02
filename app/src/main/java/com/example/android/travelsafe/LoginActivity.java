package com.example.android.travelsafe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private int counter=5;
    private TextView userRegistration;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Name=(EditText)findViewById(R.id.etName);
        Password=(EditText)findViewById(R.id.etPassword);
        Info= (TextView)findViewById(R.id.tvInfo);
        Login=(Button)findViewById(R.id.btnLogin);
        userRegistration=(TextView)findViewById(R.id.tvRegister);

        Info.setText("No of attempts remaining: 5");
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        //Uncomment to keep current user logged in
       /* FirebaseUser user= firebaseAuth.getCurrentUser();

        if(user!=null){
            finish();
            startActivity(new Intent(LoginActivity.this,SecondActivity.class));
        }*/

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(Name.getText().toString(),Password.getText().toString());
            }
        });

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });
    }

    private void validate(String userName, String userPassword) {

        if (userName.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("Logging you in!");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                        counter--;
                        Info.setText("No of attempts remaining: " + counter);
                        if (counter == 0) {
                            Login.setEnabled(false);
                        }
                    }
                }
            });
        }
    }
}
