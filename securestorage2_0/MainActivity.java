package com.example.securestorage2_0;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import com.example.securestorage2_0.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanks.passcodeview.PasscodeView;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {
    PasscodeView passcodeView;
    String password;
    boolean changePass;
    boolean f;
    // Used to load the 'securestorage2_0' library on application startup.
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference passRef = database.getReference("pass");

    private ActivityMainBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // binding = ActivityMainBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());

        SetPermissions();

        Bundle arg = getIntent().getExtras();

        if(arg!=null)
        {
            String arg_name = arg.get("menuItem").toString();
            passRef.removeValue();
        }
        else
        {   changePass = true;
            //passRef.removeValue();
        }


        passcodeView = findViewById(R.id.passcodeview);
        checkPass();

    }



    boolean checkPass() {

        /*passRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
                    @Override
                    public void onFail(String wrongNumber) {
                        Toast.makeText(MainActivity.this, wrongNumber, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String number) {
                        Intent intent_passcode = new Intent(MainActivity.this, Storage.class);
                        password = number;
                        passRef.setValue(password);

                        try {
                            startActivity(intent_passcode);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });*/

        passRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue() == null)//задаем новый пароль
                {
                    passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
                        @Override
                        public void onFail(String wrongNumber) {
                            Toast.makeText(MainActivity.this, wrongNumber, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(String number) {
                            Intent intent_passcode = new Intent(MainActivity.this, Storage.class);
                            password = number;
                            Crypt crypt = new Crypt("heL13@w0");
                            String encr_pass= null;
                            try {
                                encr_pass = crypt.EncryptPass(password);
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            } catch (IllegalBlockSizeException e) {
                                e.printStackTrace();
                            } catch (BadPaddingException e) {
                                e.printStackTrace();
                            }
                            passRef.setValue(encr_pass);

                            try {
                                startActivity(intent_passcode);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {


                        String encr_Pass = snapshot.getValue(String.class);
                        Crypt crypt = new Crypt("heL13@w0");
                        String getPass = null;
                        try {
                            getPass = crypt.DecryptPass(encr_Pass);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        }
                        passcodeView.setPasscodeLength(5).setLocalPasscode(getPass).setIsAutoClear(true).setListener(new PasscodeView.PasscodeViewListener() {
                            @Override
                            public void onFail(String wrongNumber) {
                                Toast.makeText(MainActivity.this, wrongNumber, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(String number) {
                                Intent intent_passcode = new Intent(MainActivity.this, Storage.class);
                                startActivity(intent_passcode);
                            }
                        });
                    }
                }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    void SetPermissions() {
        if (!checkPerm(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.MANAGE_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);


        }
        if (!Environment.isExternalStorageManager()) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(intent);
        }
    }

    private boolean checkPerm(String perm) {
        int permCheck = ContextCompat.checkSelfPermission(this, perm);
        return (permCheck == PackageManager.PERMISSION_GRANTED);
    }


}