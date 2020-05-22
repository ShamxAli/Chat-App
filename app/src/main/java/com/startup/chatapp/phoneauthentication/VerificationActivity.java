package com.startup.chatapp.phoneauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.startup.chatapp.image_account.InfoActivity;
import com.startup.chatapp.model.Person;
import com.startup.chatapp.R;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {
    // Views
    private EditText otp;
    private TextView resend;

    // Firebase
    private MKLoader loader;
    private FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;

    // Global
    private String number, id, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        loader = findViewById(R.id.loader);
        number = getIntent().getStringExtra("number");
        otp = findViewById(R.id.otp);
        resend = findViewById(R.id.resend);


        // Firebase init
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Users");
        mAuth = FirebaseAuth.getInstance();


        sendVerificationCode();
        generateToken();

    }

    // Button Submit
    public void btnSubmit(View view) {
        if (TextUtils.isEmpty(otp.getText().toString())) {
            Toast.makeText(VerificationActivity.this, "Enter Otp", Toast.LENGTH_SHORT).show();
        } else if (otp.getText().toString().replace(" ", "").length() != 6) {
            Toast.makeText(VerificationActivity.this, "Enter right otp", Toast.LENGTH_SHORT).show();
        } else {
            loader.setVisibility(View.VISIBLE);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, otp.getText().toString().replace(" ", ""));
            signInWithPhoneAuthCredential(credential);
        }
    }

    // Button resend(tv)
    public void btnResend(View view) {
        sendVerificationCode();
    }


    // Resend verification code
    private void sendVerificationCode() {
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                resend.setText("" + l / 1000);
                resend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resend.setText(" Resend");
                resend.setEnabled(true);
            }
        }.start();


        // Verify phone number automatically
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        VerificationActivity.this.id = id;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(VerificationActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });        // OnVerificationStateChangedCallbacks


    }

    public void generateToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {

                    token = task.getResult().getToken();
                }
            }
        });
    }

    /*Verify and store data...*/
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loader.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            Person person = new Person(mAuth.getUid(), number, token);
                            Log.d("lol", "onComplete: " + person.getUid() + " " + person.getPhoneNumber());
                            mRef.child(person.getUid()).setValue(person);

                            Intent intent = new Intent(VerificationActivity.this, InfoActivity.class);
                            startActivity(intent);
                            finish();
                            // ...
                        } else {
                            Toast.makeText(VerificationActivity.this, "Verification Filed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
