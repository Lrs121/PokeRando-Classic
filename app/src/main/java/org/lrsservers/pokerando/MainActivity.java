package org.lrsservers.pokerando;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import com.anggrayudi.storage.SimpleStorageHelper;
import com.anggrayudi.storage.file.DocumentFileUtils;
import com.anggrayudi.storage.permission.ActivityPermissionRequest;
import com.anggrayudi.storage.permission.PermissionCallback;
import com.anggrayudi.storage.permission.PermissionReport;
import com.anggrayudi.storage.permission.PermissionResult;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final SimpleStorageHelper storageHelper = new SimpleStorageHelper(this);
    private final int SAVE_ROM = 10, LOAD_ROM = 15;
    private ImageButton ibLoadRom, ibSaveRom;
    private String romUri;

    private final ActivityPermissionRequest permissionRequest = new ActivityPermissionRequest.Builder(this)
            .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            .withCallback(new PermissionCallback() {
                @Override
                public void onPermissionsChecked(@NotNull PermissionResult result, boolean fromSystemDialog) {
                    String grantStatus = result.getAreAllPermissionsGranted() ? "granted" : "denied";
                  //  Toast.makeText(getBaseContext(), "Storage permissions are " + grantStatus, Toast.LENGTH_SHORT).show();
                }

                @RequiresApi(api = Build.VERSION_CODES.R)
                @Override
                public void onShouldRedirectToSystemSettings(@NotNull List<PermissionReport> blockedPermissions) {
                    storageHelper.requestStorageAccess();
                }
            })
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupSimpleStorage(savedInstanceState);
        setupBtnAction();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        storageHelper.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        storageHelper.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        storageHelper.getStorage().onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        storageHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setupBtnAction(){
        findViewById(R.id.imgbLoadRom).setOnClickListener(view -> {
           permissionRequest.check();
           storageHelper.openFilePicker(LOAD_ROM);
        });
    }

    private void setupSimpleStorage(Bundle savedState) {
        if (savedState != null) {
            storageHelper.onRestoreInstanceState(savedState);
        }
        storageHelper.setOnStorageAccessGranted((requestCode, root) -> {
            String absolutePath = DocumentFileUtils.getAbsolutePath(root, getBaseContext());
            Toast.makeText(
                    getBaseContext(),
                    getString(R.string.ss_selecting_root_path_success_without_open_folder_picker, absolutePath),
                    Toast.LENGTH_SHORT
            ).show();
            return null;
        });
        storageHelper.setOnFileSelected((requestCode, files) -> {
            String message = "File selected: " + DocumentFileUtils.getFullName(files.get(0));
            //Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            romUri = DocumentFileUtils.getAbsolutePath((DocumentFile) files, getBaseContext());
            Toast.makeText(getBaseContext(), romUri, Toast.LENGTH_SHORT).show();
            return null;
        });
        storageHelper.setOnFileCreated((requestCode, file) -> {
            String message = "File created: " + file.getName();
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            return null;
        });
    }
}
