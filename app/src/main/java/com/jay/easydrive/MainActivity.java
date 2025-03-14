package com.jay.easydrive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import com.jay.easydrive.R;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;;


import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2;
    private  NetHttpTransport transport ;
    private static final GsonFactory JSON_FACTORY = new GsonFactory();
    private DriveServiceHelper driveServiceHelper;
    private PreferedServiceHelper preferedServiceHelper;
     Button update_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        update_button = findViewById(R.id.btnupload);
       preferedServiceHelper = new PreferedServiceHelper(this);
        transport = new NetHttpTransport();

        launchsignInIntent();
        updateDriveFile();

//        uploadFileToDrive();
    }



    public void launchsignInIntent(){
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.GET_ACCOUNTS) !=
                        PackageManager.PERMISSION_GRANTED

        ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);

        }else {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                    .build();

            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }

    }



//### 4. Handle the Sign-In Result
//**a. Override onActivityResult:**

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK){

                handleSignInResult(data);
            }

        }
    }


    private void handleSignInResult(Intent data) {

        GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener(googleSignInAccount -> {
            GoogleAccountCredential credential = GoogleAccountCredential
                    .usingOAuth2(MainActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccount(googleSignInAccount.getAccount());
            Drive driveService = new Drive.Builder(transport, JSON_FACTORY, credential)
                    .setApplicationName("drive tutu")
                    .build();

            driveServiceHelper = new DriveServiceHelper(driveService, preferedServiceHelper);
            String drive_file_id = preferedServiceHelper.getDriveSession();
            if (drive_file_id.length() == 0){
                uploadFileToDrive();
            }

        }).addOnFailureListener(e -> {

        });

    }
 //### end 4. Handle the Sign-In Result

//    ### 5. Use the Signed-In Account to Access Google Drive
//    Once signed in, you can use the authenticated account to make API calls to Google Drive:
//    private HttpRequestInitializer getGoogleAccountCredential( GoogleSignInAccount account) {
//
//        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
//                this, Collections.singleton(DriveScopes.DRIVE_FILE));
//        credential.setSelectedAccount(account.getAccount());
//
//        return credential;
//    }

    private void uploadFileToDrive()  {
        String monfichier = new java.io.File("/storage/emulated/0/", "gestioncredit.db").getPath();
        driveServiceHelper.createFile(monfichier)
                .addOnSuccessListener(s -> {
                    Log.d("uploadFileToDrivesuccess", "upload File To Drive succed ");
                    Log.d("uploadFileToDrivesuccess", "upload File id "+s.getId());
                    preferedServiceHelper.saveDriveSession(s.getId());
                }).addOnFailureListener(e -> Log.d("uploadFileToDrivefailled", "upload File To Drive failled "));

    }


    private void updateDriveFile()  {

        update_button.setOnClickListener(v -> {
            String monfichier = new java.io.File("/storage/emulated/0/", "gestioncredit.db").getPath();
            driveServiceHelper.updateFile(monfichier)
                    .addOnSuccessListener(s -> {
                        Log.d("updateFilesuccess", "update File To Drive succed ");
                        Log.d("updateFilesuccess", "upload File id "+s.getId());
//                        preferedServiceHelper.saveDriveSession(s.getId());
                    }).addOnFailureListener(e -> Log.d("updateFilefailled", "update File To Drive failled "));

        });


    }


//    ### end 5. Use the Signed-In Account to Access Google Drive



}