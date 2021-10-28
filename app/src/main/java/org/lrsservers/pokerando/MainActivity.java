package org.lrsservers.pokerando;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;

import com.anggrayudi.storage.SimpleStorageHelper;
import com.anggrayudi.storage.file.DocumentFileUtils;
import com.anggrayudi.storage.permission.ActivityPermissionRequest;
import com.anggrayudi.storage.permission.PermissionCallback;
import com.anggrayudi.storage.permission.PermissionReport;
import com.anggrayudi.storage.permission.PermissionResult;
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
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final SimpleStorageHelper storageHelper = new SimpleStorageHelper(MainActivity.this);
    private final int SAVE_ROM = 10, LOAD_ROM = 15;
    private final ActivityPermissionRequest permissionRequest = new ActivityPermissionRequest.Builder(
            MainActivity.this)
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
    private GenRestrictions genRestrictions;
    private ImageButton ibLoadRom, ibSaveRom;
    private RomHandler romHandler = null;
    private RomHandler.Factory[] factories;
    private TextView romName, romCode, romFile, supportStat;
    private ImageView boxArt;
    private RadioGroup rgPokeEvoRand, rgPokeTypes, rgPokeAbilities, rgBaseStats, rgStarterPoke,
            rgMoveSets, rgTrainerRand, rgWildsPoke, rgWildsAdditionalRules, rgTMRando, rgTMHMCompat,
            rgTutorMoves, rgTutorCompat, rgPokeTrade, rgFieldItems;
    private RadioGroup[] radioGroups;
    private Group gpGeneralSettings, gpPokeBase, gpPokeAbilities, gpPokeTypes, gpPokeEvo,
            gpStarterPoke, gpMoveData, gpMoveSets, gpTrainerPoke, gpWildPoke, gpTMHM, gpMoveTutor,
            gpPokeTrades, gpFieldItems, gpMisc;
    private Group[] groups;
    private Spinner spStarterFirst, spStarterSecond, spStarterThird;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //app init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        //variable init
        genRestrictions = null;
        factories = new RomHandler.Factory[]{
                new Gen1RomHandler.Factory(), new Gen2RomHandler.Factory(),
                new Gen3RomHandler.Factory(), new Gen4RomHandler.Factory(),
                new Gen5RomHandler.Factory()
        };
        groups = new Group[]{gpGeneralSettings = findViewById(R.id.gpGeneralSettings),
                gpPokeBase = findViewById(R.id.gpPokeBase), gpPokeAbilities = findViewById(R.id.gpPokeAbilities),
                gpPokeTypes = findViewById(R.id.gpPokeTypes), gpPokeEvo = findViewById(R.id.gpPokeEvo),
                gpStarterPoke = findViewById(R.id.gpStarterPoke), gpMoveData = findViewById(R.id.gpMoveData),
                gpMoveSets = findViewById(R.id.gpMoveSets), gpTrainerPoke = findViewById(R.id.gpTrainerPoke),
                gpWildPoke = findViewById(R.id.gpWildPoke), gpTMHM = findViewById(R.id.gpTMHM),
                gpMoveTutor = findViewById(R.id.gpMoveTutor), gpPokeTrades = findViewById(R.id.gpPokeTrades),
                gpFieldItems = findViewById(R.id.gpFieldItems), gpMisc = findViewById(R.id.gpMisc)
        };
        radioGroups = new RadioGroup[]{rgBaseStats = findViewById(R.id.rgBaseStats),
                rgStarterPoke = findViewById(R.id.rgStarterPoke), rgFieldItems = findViewById(R.id.rgFieldItemsRand),
                rgMoveSets = findViewById(R.id.rgMoveSets), rgPokeAbilities = findViewById(R.id.rgPokeAbility),
                rgPokeEvoRand = findViewById(R.id.rgPokeEvoRand), rgPokeTrade = findViewById(R.id.rgTradeRandom),
                rgPokeTypes = findViewById(R.id.rgPokeTypes), rgTMHMCompat = findViewById(R.id.rgTMHMCompats),
                rgTMRando = findViewById(R.id.rgTMRando), rgTrainerRand = findViewById(R.id.rgTrainerRand),
                rgTutorCompat = findViewById(R.id.rgTutorCompat), rgTutorMoves = findViewById(R.id.rgTutorMoveRand),
                rgWildsAdditionalRules = findViewById(R.id.rgWildsAddRules), rgWildsPoke = findViewById(R.id.rgWildsRandSetting)
        };
        spStarterFirst = findViewById(R.id.spStarterFirst);
        spStarterSecond = findViewById(R.id.spStarterSecond);
        spStarterThird = findViewById(R.id.spStarterThird);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
                R.array.pokemon_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStarterFirst.setAdapter(adapter);
        spStarterSecond.setAdapter(adapter);
        spStarterThird.setAdapter(adapter);
        ibLoadRom = findViewById(R.id.imgbLoadRom);
        ibSaveRom = findViewById(R.id.imgbSaveRom);
        romName = findViewById(R.id.txtRomName);
        romCode = findViewById(R.id.txtRomCode);
        romFile = findViewById(R.id.txtFileName);
        supportStat = findViewById(R.id.txtRomSupport);
        boxArt = findViewById(R.id.imgBoxArt);

        //begin personal code
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
        ibLoadRom.setOnClickListener(view -> {
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
                Utils.validateRomFile(new File(DocumentFileUtils.getAbsolutePath(files.get(0), getApplicationContext()).toString().trim()));
            } catch (Utils.InvalidROMException e) {
                switch (e.getType()) {
                    case LENGTH:
                        Log.e(String.valueOf(R.string.invalid_rom), getString(R.string.rom_too_short));
                        Toast.makeText(MainActivity.this, String.valueOf(R.string.rom_too_short), Toast.LENGTH_SHORT).show();
                        return null;
                    case ZIP_FILE:
                        Log.e(String.valueOf(R.string.invalid_rom), getString(R.string.zip_file_selected));
                        Toast.makeText(MainActivity.this, String.valueOf(R.string.zip_file_selected), Toast.LENGTH_SHORT).show();
                        return null;
                    case RAR_FILE:
                        Log.e(String.valueOf(R.string.invalid_rom), getString(R.string.rar_file_selected));
                        Toast.makeText(MainActivity.this, String.valueOf(R.string.rar_file_selected), Toast.LENGTH_SHORT).show();
                        return null;
                    case IPS_FILE:
                        Log.e(String.valueOf(R.string.invalid_rom), getString(R.string.ips_file_selected));
                        Toast.makeText(MainActivity.this, String.valueOf(R.string.ips_file_selected), Toast.LENGTH_SHORT).show();
                        return null;
                    case UNREADABLE:
                    default:
                        Log.e(String.valueOf(R.string.invalid_rom), getString(R.string.unknown_file));
                        Toast.makeText(MainActivity.this, R.string.unknown_file, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        return null;
                }
            }

            for (RomHandler.Factory factory : factories) {
                if (factory.isLoadable(DocumentFileUtils.getAbsolutePath(files.get(0), getApplicationContext()).toString().trim())) {
                    this.romHandler = factory.create(RandomSource.instance());
                    this.romFile.setText(DocumentFileUtils.getBaseName(files.get(0)));
                    this.romName.setText(this.romHandler.getROMName());
                    this.romCode.setText(this.romHandler.getROMCode());
                    this.supportStat.setText(this.romHandler.getSupportLevel());
                    setBoxArt(this.romHandler.getROMCode());
                    newRomUI();

                } else {
                    Snackbar.make(MainActivity.this, findViewById(R.id.topLayout),
                            getString(R.string.unsupported_game), Snackbar.LENGTH_SHORT).show();
                }
            }
            Toast.makeText(MainActivity.this, DocumentFileUtils.getBaseName(files.get(0)),
                    Toast.LENGTH_SHORT).show();

            return null;
        });
    }

    private void initialState() {
        for (Group group : this.groups) {
            for (int i = 0; i < Arrays.stream(group.getReferencedIds()).count(); i++) {
                int[] ids = group.getReferencedIds();
                findViewById(ids[i]).setEnabled(false);
            }
        }
        for (RadioGroup radioGroup : this.radioGroups) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                radioGroup.getChildAt(i).setEnabled(false);
            }
        }

    }

    private void newRomUI() {
        for (Group group : this.groups) {
            for (int i = 0; i < Arrays.stream(group.getReferencedIds()).count(); i++) {
                int[] ids = group.getReferencedIds();
                findViewById(ids[i]).setEnabled(true);
                findViewById(ids[i]).setSelected(false);
            }
        }
        for (RadioGroup radioGroup : this.radioGroups) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                radioGroup.getChildAt(i).setEnabled(true);
                if (i == 0){
                    radioGroup.getChildAt(i).setSelected(true);
                }
                radioGroup.getChildAt(i).
            }
        }
        spStarterFirst.setEnabled(false);
        spStarterSecond.setEnabled(false);
        spStarterThird.setEnabled(false);

    }

    private void setBoxArt(String romCode) {
        boxArt.setBackgroundColor(Color.TRANSPARENT);
        switch (romCode) {
            case "POKEMON RED":
                boxArt.setImageResource(R.drawable.red);
                break;
            case "POKEMON BLUE":
                boxArt.setImageResource(R.drawable.blue);
                break;
            case "POKEMON YELLOW":
                boxArt.setImageResource(R.drawable.yellow);
                break;
            case "POKEMON GREEN":
                boxArt.setImageResource(R.drawable.green);
                break;
            case "AAUE":
            case "AAUJ":
            case "AAUF":
            case "AAUD":
            case "AAUS":
            case "AAUI":
                boxArt.setImageResource(R.drawable.gold);
                break;
            case "AAXJ":
            case "AAXE":
            case "AAXF":
            case "AAXD":
            case "AAXS":
            case "AAXI":
                //this is silver
                boxArt.setImageResource(R.drawable.silver);
                break;
            case "KAPB":
            case "BYTE":
            case "BXTJ":
            case "BYTF":
            case "BYTD":
            case "BYTS":
            case "BYTI":
                //this is crystal
                boxArt.setImageResource(R.drawable.crystal);
                break;
            case "AXVE":
            case "AXVF":
            case "AXVD":
            case "AXVS":
            case "AXVI":
            case "AXVJ":
                //ruby
                boxArt.setImageResource(R.drawable.ruby);
                break;
            case "AXPE":
            case "AXPF":
            case "AXPD":
            case "AXPS":
            case "AXPI":
            case "AXPJ":
                //sapphire
                boxArt.setImageResource(R.drawable.sapphire);
                break;
            case "BPEE":
            case "BPEF":
            case "BPED":
            case "BPEI":
            case "BPES":
            case "BPET":
            case "BPEJ":
                //emerald
                boxArt.setImageResource(R.drawable.emerald);
                break;
            case "BPRE":
            case "BPRF":
            case "BPRD":
            case "BPRS":
            case "BPRI":
            case "BPRJ":
                //firered
                boxArt.setImageResource(R.drawable.firered);
                break;
            case "BPGE":
            case "BPGF":
            case "BPGD":
            case "BPGS":
            case "BPGI":
            case "BPGJ":
                //leafgreen
                boxArt.setImageResource(R.drawable.leafgreen);
                break;
            case "ADAE":
            case "ADAJ":
            case "ADAD":
            case "ADAS":
            case "ADAI":
            case "ADAF":
            case "ADAK":
                //diamond
                boxArt.setImageResource(R.drawable.diamond);
                break;
            case "APAK":
            case "APAF":
            case "APAI":
            case "APAS":
            case "APAE":
            case "APAD":
            case "APAJ":
                //perl
                boxArt.setImageResource(R.drawable.pearl);
                break;
            case "CPUE":
            case "CPUJ":
            case "CPUD":
            case "CPUF":
            case "CPUS":
            case "CPUI":
            case "CPUK":
                //platinum
                boxArt.setImageResource(R.drawable.platinum);
                break;
            case "IPKJ":
            case "IPKE":
            case "IPKK":
            case "IPKF":
            case "IPKD":
            case "IPKS":
            case "IPKI":
                //heartgold
                boxArt.setImageResource(R.drawable.heartgold);
                break;
            case "IPGI":
            case "IPGS":
            case "IPGD":
            case "IPGF":
            case "IPGK":
            case "IPGE":
            case "IPGJ":
                //soulsilver
                boxArt.setImageResource(R.drawable.soulsilver);
                break;
            case "IRBO":
            case "IRBF":
            case "IRBD":
            case "IRBS":
            case "IRBI":
            case "IRBJ":
            case "IRBK":
                //black
                boxArt.setImageResource(R.drawable.black);
                break;
            case "IRAF":
            case "IRAO":
            case "IRAD":
            case "IRAS":
            case "IRAI":
            case "IRAJ":
            case "IRAK":
                //white
                boxArt.setImageResource(R.drawable.white);
                break;
            case "IREO":
            case "IREF":
            case "IRED":
            case "IREI":
            case "IRES":
            case "IREJ":
            case "IREK":
                //black2
                boxArt.setImageResource(R.drawable.black2);
                break;
            case "IRDO":
            case "IRDF":
            case "IRDD":
            case "IRDI":
            case "IRDS":
            case "IRDJ":
            case "IRDK":
                //white2
                boxArt.setImageResource(R.drawable.white2);
                break;
            default:
                boxArt.setImageResource(R.drawable.ic_error);
                break;
        }
    }

}
