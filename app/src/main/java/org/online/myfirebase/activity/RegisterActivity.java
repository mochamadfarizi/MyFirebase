package org.online.myfirebase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.online.myfirebase.R;
import org.online.myfirebase.model.user;


public class RegisterActivity extends AppCompatActivity {
    private Spinner spinnerRole;
    private EditText nama;
    private EditText password;
    private EditText confirmPassword;
    private DatabaseReference database;
    private Button regis;
    private FirebaseAuth mAuth;
    private TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //INISIALISASI VIEW
        setContentView(R.layout.activity_register);
        //INISIALISASI BUTTON
        nama = (EditText) findViewById(R.id.textInputEditTextName);
        password = (EditText) findViewById(R.id.textInputEditTextPassword);
        login=(TextView) findViewById(R.id.appCompatTextViewLoginLink);
        confirmPassword = (EditText) findViewById(R.id.textInputEditTextConfirmPassword);
        spinnerRole = (Spinner) findViewById(R.id.listRoles);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        regis = (Button) findViewById(R.id.appCompatButtonRegister);
        regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              registerUser();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }
    //Proses Submit register
    private void registerUser(){
        //inisialisasi layout
        String namaa = nama.getText().toString().trim();
        String pass= password.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

        //validation
        if(namaa.isEmpty()){
            nama.setError("Please email required");
            nama.requestFocus();
            return;
        }
        if(pass.isEmpty()){
            password.setError("Please password required");
            password.requestFocus();
            return;
        }
       if(pass.length()<6){
           password.setError("Password must be 6 character!");
           password.requestFocus();
           return;
       }
       //auntetikasi user
       mAuth.createUserWithEmailAndPassword(namaa,pass)
               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           //membuat inputan pada database
                           user User= new user(namaa,pass,role);
                           //inisialisasi firebase database
                           FirebaseDatabase.getInstance().getReference("user")
                                   //auntetikasi menampilkan data user
                                   .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                   .setValue(User).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       Toast.makeText(RegisterActivity.this,"Succes! Enjoy ur account! ",
                                               Toast.LENGTH_SHORT).show();
                                   }else{
                                       Toast.makeText(RegisterActivity.this,"Failed to regis ",
                                               Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
                       }else{
                           Toast.makeText(RegisterActivity.this,"Failed to regis ",Toast.LENGTH_SHORT).show();
                       }
                   }
               });
    }
}



