package org.lrsservers.pokerando;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.anggrayudi.storage.SimpleStorageHelper;
import com.anggrayudi.storage.permission.ActivityPermissionRequest;
import com.anggrayudi.storage.permission.PermissionCallback;
import com.anggrayudi.storage.permission.PermissionReport;
import com.anggrayudi.storage.permission.PermissionResult;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SimpleStorageHelper simpleStorageHelper = new SimpleStorageHelper(this);
    private final int SAVE_ROM = 10, LOAD_ROM = 15;
    private ImageButton ibLoadRom, ibSaveRom;

/*    private final ActivityPermissionRequest permissionRequest = new ActivityPermissionRequest.Builder(this)
            .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            .withCallback(new PermissionCallback() {
                @Override
                public void onPermissionsChecked(@NotNull PermissionResult result, boolean fromSystemDialog) {
                    String grantStatus = result.getAreAllPermissionsGranted() ? "granted" : "denied";
                    Toast.makeText(getBaseContext(), "Storage permissions are " + grantStatus, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onShouldRedirectToSystemSettings(@NotNull List<PermissionReport> blockedPermissions) {
                    SimpleStorageHelper.redirectToSystemSettings(JavaActivity.this);
                }
            })
            .build();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ibLoadRom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    simpleStorageHelper.requestStorageAccess();
                }
                simpleStorageHelper.openFolderPicker();
            }
        });

        ibSaveRom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    simpleStorageHelper.requestStorageAccess();
                }
//                simpleStorageHelper.
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        simpleStorageHelper.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        simpleStorageHelper.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        simpleStorageHelper.getStorage().onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        simpleStorageHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}