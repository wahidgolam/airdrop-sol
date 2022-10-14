package com.zingit.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import club.cred.neopop.PopFrameLayout;

public class Login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    GoogleSignInAccount account;
    StudentUser studentUser;
    LoadingDialog loadingDialog;
    PopFrameLayout login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.primary));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();

                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }

        db = FirebaseFirestore.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_web_client))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        mAuth = FirebaseAuth.getInstance();
        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInPressed(view);
            }
        });
    }
    public void signInPressed(View view){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadingDialog = new LoadingDialog(Login.this, "Logging you in");
        loadingDialog.startLoadingDialog();
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        if(completedTask.isSuccessful()){
            try {
                account = completedTask.getResult(ApiException.class);
                if(account!=null){
                    //Firebase Auth with Google Credentials
                    String idToken = account.getIdToken();
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            firebaseAuthWithGoogle(idToken);
                        }
                    }, 100);
                }
            } catch (ApiException e) {
                Log.d("Google Authentication Error", "signInResult:failed code=" + e.getStatusCode());
                //Display failure information to user
                Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Log.d("Google Authentication Unsuccessful", "Sign-in unsuccessful");
            //Display failure information to user
            Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
        }

    }
    public void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("FirebaseAuth", "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    DocumentReference userConcerned = db.collection("studentUser").document(user.getUid());
                    userConcerned.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    //set Data-holder
                                    StudentUser studentUser = document.toObject(StudentUser.class);
                                    Dataholder.studentUser = studentUser;
                                    Dataholder.cart = new Cart();
                                    loadingDialog.dismissDialog();
                                    Intent intent = new Intent(getApplicationContext(), Onboarding1.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.d("Firestore access", "No such document");
                                    createUser();
                                }
                            } else {
                                Log.d("Firestore access", "Got failed with ", task.getException());
                            }
                        }
                    });
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("FirebaseAuth", "signInWithCredential:failure", task.getException());
                    //Display failure information to user
                    Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismissDialog();
                }
            }
        });
    }
    public void createUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String email = user.getEmail();
        String firstName = account.getGivenName();
        String fullName = account.getDisplayName();
        String id = user.getUid();
        String userImage = Objects.requireNonNull(account.getPhotoUrl()).toString();
        //assign campusID
        String campusID = "undef";
        Toast.makeText(this, "Logged in as "+ email, Toast.LENGTH_SHORT).show();
        //pushing campusID to sharedPreference
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("campusID", campusID);
        myEdit.commit();
        //updating db
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                studentUser = new StudentUser(campusID, email, firstName, fullName, id, userImage);
                //adding user data to Data-holder
                Dataholder.studentUser = studentUser;
                //updating database
                db.collection("studentUser").document(id).set(studentUser);
                //navigate to homepage
                Dataholder.cart = new Cart();
                loadingDialog.dismissDialog();
                Intent intent = new Intent(getApplicationContext(), ChooseCampus.class);
                startActivity(intent);
                finish();
            }
        }, 100);
    }
    public void openPrivacy(View view){
        Intent httpIntent = new Intent(Intent.ACTION_VIEW);
        httpIntent.setData(Uri.parse("https://zingnow.in/privacy.html"));

        startActivity(httpIntent);
    }
    public void openTerms(View view){
        Intent httpIntent = new Intent(Intent.ACTION_VIEW);
        httpIntent.setData(Uri.parse("https://zingnow.in/toc.html"));

        startActivity(httpIntent);
    }

    @Override
    public void onBackPressed() {
        loadingDialog.dismissDialog();
        finishAffinity();
        super.onBackPressed();
    }
}