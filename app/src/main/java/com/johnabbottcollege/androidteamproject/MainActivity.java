package com.johnabbottcollege.androidteamproject;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.auth.api.Auth;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Login And Register";
    public static final String USER_ID = "id";
    public static final String USER_EMAIL ="email";
    public static final String USER_PASSWORD = "password";
    //public static final String RESULT = "latlong result";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin, btnRegister, btn_GoogleLogin, btn_Logout;
    private CheckBox cbRemenberPass;
    private EditText inputNewPassword;
    private static GoogleSignInClient mGoogleSignInClient;
    private String email;
    private String password;
    private Boolean isSigUp;
    FirebaseAuth firebaseAuth;
    private static FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;


    private LocationManager locationManager;
    private LocationListener locationListener;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseUser;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //save in default file (com.johnabbottcollege.passwordlogin
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // save in new file name MyPREFERENCES
        sharedPreferences = getSharedPreferences("MyPREFERENCES",MODE_PRIVATE);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        cbRemenberPass = findViewById(R.id.cb_remember_pass);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
//        tv_Address = (TextView) findViewById(R.id.tvAddressTest);
        firebaseAuth = firebaseAuth.getInstance();
        btn_GoogleLogin = findViewById(R.id.btn_GoogleLogin);
        btn_Logout = findViewById(R.id.btn_Logout);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "account or password can't be empty.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String emailStoreInSharedPreferences = sharedPreferences.getString("email", "");
                    String passwordStoreInSharedPreferences = sharedPreferences.getString("password", "");
                    if ((email.equals(emailStoreInSharedPreferences)) && (password.equals(passwordStoreInSharedPreferences))) {

                        //create database
                        databaseUser = FirebaseDatabase.getInstance().getReference("users");
                        String id = databaseUser.push().getKey();
                        User user = new User(id, email, password);
                        databaseUser.child(id).setValue(user);

                        Intent intent = new Intent(MainActivity.this, PlacesActivity.class);
                        intent.putExtra(USER_ID,user.getUserId());
                        intent.putExtra(USER_EMAIL,user.getEmail());
                        intent.putExtra(USER_PASSWORD,user.getPassword());
                        Toast.makeText(MainActivity.this, "I am here", Toast.LENGTH_LONG).show();
                        //intent.putExtra("latlong", result);
                        //Log.d(TAG,result[0] + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");


                        startActivity(intent);
                        //finish();
                    } else {//email and password is not the same as in the record.
                        if (emailStoreInSharedPreferences.equals("") || passwordStoreInSharedPreferences.equals("")) {
                            Toast.makeText(MainActivity.this, "Please register first", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(MainActivity.this, "Email or Password is not valid", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(),
                        etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful() ) {
                            Toast.makeText(MainActivity.this, "Register successfully",
                                    Toast.LENGTH_SHORT).show();
                            etEmail.setText("");
                            etPassword.setText("");
                        } else {
                            Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                email = etEmail.getText().toString();
//                String password = etPassword.getText().toString();
//                if (email.isEmpty() || password.isEmpty()) {
//                    Toast.makeText(MainActivity.this, "Please enter valid email or password.", Toast.LENGTH_LONG).show();
//                } else {
//                    boolean isValidAccountAndEmail = (validPassword() && validEmail());
//                    if (isValidAccountAndEmail) {
//                        editor = sharedPreferences.edit();
//                        cbRemenberPass.setChecked(true);
//                        if (cbRemenberPass.isChecked()) {
//                            editor.putBoolean("remember_password", true);
//                            editor.putString("email", email);
//                            editor.putString("password", password);
//
//
//                            Toast.makeText(MainActivity.this, "user added", Toast.LENGTH_SHORT).show();
//
//
//                        } else {
//                            editor.clear();
//                        }
//                        editor.apply();
////
////                        Intent intent = new Intent(MainActivity.this, PlacesActivity.class);
////                        startActivity(intent);
////                        //finish();
//
//                    }
//
//                }
//            }
//        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btn_GoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btn_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut(); //get signed out
                btn_Logout.setVisibility(View.GONE);
            }
        });
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this,"you are not able to log in to google",Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

        btn_Logout.setVisibility(View.VISIBLE);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            Toast.makeText(this, "Name of the user :" + personName + " user id is : " + personId, Toast.LENGTH_SHORT).show();

        }
    }


    private boolean validEmail() {
        String emailpattern = "^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$";
        Pattern pattern = Pattern.compile(emailpattern);
        Matcher matcher = pattern.matcher(etEmail.getText().toString());
        boolean b = matcher.matches();
        String str = "True";
        if (b)
            Log.d(TAG, "Email is EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE " + str);
        else
            Log.d(TAG, "Email is invalid EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");


        return b;
    }

    private boolean validPassword() {
        //6 - 16 chartacter with one symbal one uppercase at least
        Pattern pattern = Pattern.compile("((?=.*[a-z])(?=.*\\d)(?=.*[@#$%])(?=.*[A-Z]).{6,16})");
        Matcher mcher = pattern.matcher(etPassword.getText().toString());

        if (mcher.matches()) {

            Log.d(TAG, "Password is true TTTTTTTTTTTTTTTTTTTTTTTTTT");
            return true;
        } else {
            Log.d(TAG, "Password is invalid IIIIIIIIIIIIIIIIIIIIIII");
            return false;
        }
    }

    public void changePassword(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Forgot password?");
        dialog.setMessage("Enter your new password!");
        inputNewPassword = new EditText(this);
        dialog.setView(inputNewPassword);

        dialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = inputNewPassword.getText().toString();
                // editor.putString("password",str);
                // editor.apply();
                // Toast.makeText(MainActivity.this,str,Toast.LENGTH_LONG).show();

                editor = sharedPreferences.edit();
                // if (cbRemenberPass.isChecked()) {
                //  editor.putBoolean("remember_password", true);
                //   editor.putString("email", email);
                editor.putString("password", str);
                editor.apply();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}

