package org.lrsservers.pokerando;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anggrayudi.storage.SimpleStorageHelper;
import com.anggrayudi.storage.file.DocumentFileUtils;
import com.anggrayudi.storage.permission.ActivityPermissionRequest;
import com.anggrayudi.storage.permission.PermissionCallback;
import com.anggrayudi.storage.permission.PermissionReport;
import com.anggrayudi.storage.permission.PermissionResult;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.lrsservers.pokerando.upr.RandomSource;
import org.lrsservers.pokerando.upr.Utils;
import org.lrsservers.pokerando.upr.pokemon.GenRestrictions;
import org.lrsservers.pokerando.upr.romhandlers.Gen1RomHandler;
import org.lrsservers.pokerando.upr.romhandlers.Gen2RomHandler;
import org.lrsservers.pokerando.upr.romhandlers.Gen3RomHandler;
import org.lrsservers.pokerando.upr.romhandlers.Gen4RomHandler;
import org.lrsservers.pokerando.upr.romhandlers.Gen5RomHandler;
import org.lrsservers.pokerando.upr.romhandlers.RomHandler;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final SimpleStorageHelper storageHelper = new SimpleStorageHelper(this);
    private final int SAVE_ROM = 10, LOAD_ROM = 15;
    private final ActivityPermissionRequest permissionRequest= new ActivityPermissionRequest.Builder(this)
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
    private final GenRestrictions genRestrictions = null;
    private ImageButton ibLoadRom, ibSaveRom;
    private RomHandler romHandler = null;
    private RomHandler.Factory[] factories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        factories = new RomHandler.Factory[]{
                new Gen1RomHandler.Factory(), new Gen2RomHandler.Factory(), new Gen3RomHandler.Factory(), new Gen4RomHandler.Factory(), new Gen5RomHandler.Factory()
        };

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupSimpleStorage(savedInstanceState);
        setupBtnAction();
        initialState();

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
                switch (e.getType()) {
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
                        Log.e(String.valueOf(R.string.invalid_rom), String.valueOf(R.string.unknown_file));
                        Toast.makeText(MainActivity.this, String.valueOf(R.string.unknown_file), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        break;
                    default:
                        for (RomHandler.Factory factory : factories) {
                            if (factory.isLoadable(DocumentFileUtils.getBaseName(files.get(0)))) {
                                this.romHandler = factory.create(RandomSource.instance());
                                TextView romName = findViewById(R.id.txtRomName);
                                TextView romCode = findViewById(R.id.txtRomCode);
                                TextView romFile = findViewById(R.id.txtFileName);
                                TextView supportStat = findViewById(R.id.txtRomSupport);
                                romFile.setText(DocumentFileUtils.getBaseName(files.get(0)));
                                romName.setText(this.romHandler.getROMName());
                                romCode.setText(this.romHandler.getROMCode());
                                supportStat.setText(this.romHandler.getSupportLevel());
                                enableUI(DocumentFileUtils.toRawFile(files.get(0), getApplicationContext()));


                            }else{
                                Snackbar.make(MainActivity.this, findViewById(R.id.topLayout), String.valueOf(R.string.invalid_rom), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                        break;
                }
            }
            Toast.makeText(MainActivity.this, files.get(0).toString(), Toast.LENGTH_SHORT).show();

            return null;
        });
    }

    private void initialState() {
        ScrollView mainView = findViewById(R.id.clMain);
        RadioGroup rgBase = findViewById(R.id.rbgpBaseStats), rgAbility = findViewById(R.id.rgPokeAbility), rgTypes = findViewById(R.id.rgPokeTypes), rgEvo = findViewById(R.id.rgPokeEvoRand), rgStarter = findViewById(R.id.rgStarterPoke), rgMovesets = findViewById(R.id.rgMoveSets), rgTrainerRand = findViewById(R.id.rgTrainerRand), rgWildsPoke = findViewById(R.id.rgWildsRandSetting), rgWildsRules = findViewById(R.id.rgWildsAddRules), rgTMRand = findViewById(R.id.rgTMRando), rgTMHMComp = findViewById(R.id.rgTMHMCompats), rgTutorMoves = findViewById(R.id.rgTutorMoveRand), rgTutorCompat = findViewById(R.id.rgTutorCompat), rgTrades = findViewById(R.id.rgTradeRandom), rgFieldItems = findViewById(R.id.rgFieldItemsRand);
        RadioGroup[] radioGroups = new RadioGroup[]{rgBase, rgAbility, rgTypes, rgEvo, rgStarter, rgMovesets, rgTrainerRand, rgWildsPoke, rgWildsRules, rgTMRand, rgTMHMComp, rgTutorCompat, rgTutorMoves, rgTrades, rgFieldItems};
        ImageView boxArt = findViewById(R.id.imgBoxArt);

        for (int i = 0; i < mainView.getChildCount(); i++) {
            View child = mainView.getChildAt(i);
            child.setEnabled(false);
        }
        for (RadioGroup radioGroup : radioGroups) {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                View child = radioGroup.getChildAt(j);
                child.setEnabled(false);
            }
        }
        boxArt.setVisibility(View.INVISIBLE);
    }

    private void enableUI(){
        ScrollView mainView = findViewById(R.id.clMain);
        RadioGroup rgBase = findViewById(R.id.rbgpBaseStats), rgAbility = findViewById(R.id.rgPokeAbility), rgTypes = findViewById(R.id.rgPokeTypes), rgEvo = findViewById(R.id.rgPokeEvoRand), rgStarter = findViewById(R.id.rgStarterPoke), rgMovesets = findViewById(R.id.rgMoveSets), rgTrainerRand = findViewById(R.id.rgTrainerRand), rgWildsPoke = findViewById(R.id.rgWildsRandSetting), rgWildsRules = findViewById(R.id.rgWildsAddRules), rgTMRand = findViewById(R.id.rgTMRando), rgTMHMComp = findViewById(R.id.rgTMHMCompats), rgTutorMoves = findViewById(R.id.rgTutorMoveRand), rgTutorCompat = findViewById(R.id.rgTutorCompat), rgTrades = findViewById(R.id.rgTradeRandom), rgFieldItems = findViewById(R.id.rgFieldItemsRand);
        RadioGroup[] radioGroups = new RadioGroup[]{rgBase, rgAbility, rgTypes, rgEvo, rgStarter, rgMovesets, rgTrainerRand, rgWildsPoke, rgWildsRules, rgTMRand, rgTMHMComp, rgTutorCompat, rgTutorMoves, rgTrades, rgFieldItems};
        ImageView boxArt = findViewById(R.id.imgBoxArt);

        for (int i = 0; i < mainView.getChildCount(); i++) {
            View child = mainView.getChildAt(i);
            child.setEnabled(true);
        }
        boxArt.setEnabled(false);
        for (RadioGroup radioGroup : radioGroups) {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                View child = radioGroup.getChildAt(j);
                child.setEnabled(true);
            }
        }
        if(this.romHandler.generationOfPokemon() < 6){

        }
    }

    private void setBoxArt(String romCode){
        ImageView boxArt = findViewById(R.id.imgBoxArt);
        switch (romCode){
            case "BPRE0":
                break;
            default:
                break;
        }
    }

}
