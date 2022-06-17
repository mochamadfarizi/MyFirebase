package org.online.myfirebase.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.online.myfirebase.R;
import org.online.myfirebase.activity.buyer.BuyerHomeActivity;
import org.online.myfirebase.activity.seller.SellerHomeActivity;

public class LoginActivity2 extends AppCompatActivity {
    private final AppCompatActivity activity = LoginActivity2.this;
    private TextView textViewLink;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private Button login;
    private SignInButton btn_sigIn;
    private EditText nama;
    private EditText pass;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mGetReference ;
     GoogleSignInClient mGoogleSignInClient;
     GoogleSignInOptions gso;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login2);
        //inisialisasi view
        nama = (EditText) findViewById(R.id.textInputEditTextUsername);
        pass = (EditText) findViewById(R.id.textInputEditTextPassword);
        login = (Button) findViewById(R.id.appCompatButtonLogin);
        textViewLink = (TextView) findViewById(R.id.textViewLinkRegister);
        btn_sigIn =(SignInButton) findViewById(R.id.btn_google);
        //inisialiasasi file firebase aplikasi
        FirebaseApp.initializeApp(this);
        //inisialisasi firebase databse
        mDatabase=FirebaseDatabase.getInstance();
        mGetReference=mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

       gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               .requestIdToken("743435323502-5fd3ns1fjndss1vukl6s7s779r1mpenr.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

        //activity ketika button login di click
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("user").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       userLogin();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        textViewLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity2.this, RegisterActivity.class);
                startActivity(intent);
            }

        });
        btn_sigIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });
    }

    

    private void userLogin() {
        String nama1 = nama.getText().toString().trim();
        String pass1 = pass.getText().toString().trim();
        DatabaseReference role = mGetReference.child("role");
        if (nama1.isEmpty()) {
            nama.setError("Email required!");
            nama.requestFocus();
            return;
        }   
        if (pass1.isEmpty()) {
            pass.setError("Password required!");
            pass.requestFocus();
            return;
        }
        if (pass1.length() < 6) {
            pass.setError("Password wrong!");
            pass.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(nama1, pass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                        Intent buyer = new Intent(activity, BuyerHomeActivity.class);
                        buyer.putExtra("Username", nama.getText().toString().trim());
                        startActivity(buyer);
                        Toast.makeText(getApplicationContext(), "Welcome here " + nama.getText().toString(), Toast.LENGTH_LONG).show();
                        emptyInputEditText();

                } else {
                    Toast.makeText(LoginActivity2.this, "Login Failed,Please Try Again Dude", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void emptyInputEditText() {
        nama.setText(null);
        pass.setText(null);
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                finish();
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {

                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity2.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Intent intent= new Intent(activity, SellerHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Welcome here " , Toast.LENGTH_LONG).show();
    }

}




