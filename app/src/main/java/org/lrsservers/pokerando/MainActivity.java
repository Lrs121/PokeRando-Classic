package org.lrsservers.pokerando;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.anggrayudi.storage.SimpleStorageHelper;
import com.anggrayudi.storage.file.DocumentFileUtils;
import com.anggrayudi.storage.permission.ActivityPermissionRequest;
import com.anggrayudi.storage.permission.PermissionCallback;
import com.anggrayudi.storage.permission.PermissionReport;
import com.anggrayudi.storage.permission.PermissionResult;

import org.jetbrains.annotations.NotNull;
import org.lrsservers.pokerando.upr.Utils;
import org.lrsservers.pokerando.upr.pokemon.GenRestrictions;
import org.lrsservers.pokerando.upr.romhandlers.Gen1RomHandler;
import org.lrsservers.pokerando.upr.romhandlers.Gen2RomHandler;
import org.lrsservers.pokerando.upr.romhandlers.Gen3RomHandler;
import org.lrsservers.pokerando.upr.romhandlers.Gen4RomHandler;
import org.lrsservers.pokerando.upr.romhandlers.Gen5RomHandler;
import org.lrsservers.pokerando.upr.romhandlers.RomHandler;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final SimpleStorageHelper storageHelper = new SimpleStorageHelper(this);
    private final int SAVE_ROM = 10, LOAD_ROM = 15;
    private final ActivityPermissionRequest permissionRequest = new ActivityPermissionRequest.Builder(this)
                  .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                  .withCallback(new PermissionCallback() {
                       @Override
                       public void onPermissionsChecked(@NotNull PermissionResult result, boolean fromSystemDialog) {
                       }
                       @RequiresApi(api = Build.VERSION_CODES.R)
                       @Override
                       public void onShouldRedirectToSystemSettings(@NotNull List<PermissionReport> blockedPermissions) {
                             storageHelper.requestStorageAccess();
                       }
                  }).build();
    private ImageButton ibLoadRom, ibSaveRom;
    private final RomHandler romHandler = null;
    private final GenRestrictions genRestrictions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RomHandler.Factory[] factories = new RomHandler.Factory[]{
                new Gen1RomHandler.Factory(), new Gen2RomHandler.Factory(), new Gen3RomHandler.Factory(), new Gen4RomHandler.Factory(), new Gen5RomHandler.Factory()
        };

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
        storageHelper.getStorage().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        storageHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setupBtnAction() {
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
            try {
                Utils.validateRomFile(DocumentFileUtils.toRawFile(files.get(0), getApplicationContext()));
            } catch (Utils.InvalidROMException e) {
                switch (e.getType()){
                    case LENGTH:
                        Log.e(String.valueOf(R.string.invalid_rom), String.valueOf(R.string.rom_too_short));
                        Toast.makeText(MainActivity.this, String.valueOf(R.string.rom_too_short), Toast.LENGTH_SHORT).show();
                        break;
                    case ZIP_FILE:
                        Log.e(String.valueOf(R.string.invalid_rom), String.valueOf(R.string.zip_file_selected));
                        Toast.makeText(MainActivity.this, String.valueOf(R.string.zip_file_selected), Toast.LENGTH_SHORT).show();
                        break;
                    case RAR_FILE:
                        Log.e(String.valueOf(R.string.invalid_rom), String.valueOf(R.string.rar_file_selected));
                        Toast.makeText(MainActivity.this, String.valueOf(R.string.rar_file_selected), Toast.LENGTH_SHORT).show();
                        break;
                    case IPS_FILE:
                        Log.e(String.valueOf(R.string.invalid_rom), String.valueOf(R.string.ips_file_selected));
                        Toast.makeText(MainActivity.this, String.valueOf(R.string.ips_file_selected), Toast.LENGTH_SHORT).show();
                        break;
                    case UNREADABLE:
                    default:
                        Log.e(String.valueOf(R.string.invalid_rom), String.valueOf(R.string.unknown_file));
                        Toast.makeText(MainActivity.this, String.valueOf(R.string.unknown_file), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        break;
                }
            }
            Toast.makeText(MainActivity.this, files.get(0).toString(), Toast.LENGTH_SHORT).show();
            return null;
        });
    }

}
