package com.dentalcheck.themedapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private EditText userName, inputEmail, inputPassword, userdob,speciality;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressDialog progressDialog;
    RadioGroup radioGroup ;
    RadioButton male;
    int selectedId ;
    private FirebaseAuth auth;
    private DatabaseReference storeref;
    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressDialog = new ProgressDialog(RegisterActivity.this);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            finish();
        }
        radioGroup = (RadioGroup) findViewById(R.id.radioGrp);
         selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton genderChoice = findViewById(selectedId);

        btnSignIn =  findViewById(R.id.sign_in_button);
        btnSignUp =  findViewById(R.id.sign_up_button);
        userName =   findViewById(R.id.name);
        inputEmail = findViewById(R.id.email);
        userdob =    findViewById(R.id.DOB);
        inputPassword = findViewById(R.id.password);
        speciality = findViewById(R.id.title);

        final String responseText=genderChoice.getText().toString();



        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = userName.getText().toString().trim();
                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                gender = responseText;
                final String dob = userdob.getText().toString();
                final String image = "default_image";
                final String Speciality = speciality.getText().toString().trim();


                if (TextUtils.isEmpty(name)) {
                    userName.setError("Enter email your Full Name!");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    //Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    inputEmail.setError("Enter email address!");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    //Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    inputPassword.setError("Enter Password");
                    return;
                }

                if (password.length() < 6) {
                   // Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    inputPassword.setError(getString(R.string.minimum_password));
                    return;
                }


                progressDialog.setMessage("registering....");
                progressDialog.setTitle("Register");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(RegisterActivity.this, "Createing User was:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (task.isSuccessful()) {
                                    String DeviceToken = FirebaseInstanceId.getInstance().getToken();
                                    user usr = new user(
                                            name,
                                            email,
                                            gender,
                                            dob,
                                            image,
                                            Speciality,
                                            DeviceToken
                                    );

                                    String userId = auth.getCurrentUser().getUid();
                                    storeref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                                    storeref.keepSynced(true);

                                    storeref.setValue(usr).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this, "Details Added" + task.getException(),
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Register Error." + task.getException(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private String identifierName(String name) {
        String newName = name.substring(0,4);
        return newName;
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.dismiss();
    }
}