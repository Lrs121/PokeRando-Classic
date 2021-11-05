package org.lrsservers.pokerando;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;

import com.anggrayudi.storage.SimpleStorageHelper;
import com.anggrayudi.storage.file.DocumentFileUtils;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final SimpleStorageHelper storageHelper = new SimpleStorageHelper(MainActivity.this);
    private final int SAVE_ROM = 10, LOAD_ROM = 15;
    List<String> extensions = new ArrayList<String>(Arrays.asList("gb", "sgb", "gbc", "gba", "nds"));
    private GenRestrictions genRestrictions;
    private ImageButton ibLoadRom, ibSaveRom;
    private RomHandler romHandler = null;
    private RomHandler.Factory[] factories;
    private TextView romName, romCode, romFile, supportStat;
    private ImageView boxArt;
    private RadioButton rbBaseUnchange, rbBaseShuffle, rbBaseRand, rbAbilityRand, rbEvoRand,
            rbStarterCustom, rbMovesetUnchange, rbMovesetRandSameType, rbMovesetRandComplete,
            rbMovesetMetronome, rbTrainerUnchanged, rbTrainerRand, rbTrainerRandType, rbWildRand,
            rbWildArea1to1, rbWildGlobal1to1, rbWildUnchanged, rbWildAddNone, rbTMRandom,
            rbTutorRand, rbTradeUnchanged, rbFieldItemRand;
    private RadioGroup rgPokeEvoRand, rgPokeTypes, rgPokeAbilities, rgBaseStats, rgStarterPoke,
            rgMoveSets, rgTrainerRand, rgWildsPoke, rgWildsAdditionalRules, rgTMRando, rgTMHMCompat,
            rgTutorMoves, rgTutorCompat, rgPokeTrade, rgFieldItems, rgStaticPoke;
    private RadioGroup[] radioGroups;
    private Group gpGeneralSettings, gpPokeBase, gpPokeAbilities, gpPokeTypes, gpPokeEvo,
            gpStarterPoke, gpMoveData, gpMoveSets, gpTrainerPoke, gpWildPoke, gpTMHM, gpMoveTutor,
            gpPokeTrades, gpFieldItems, gpMisc, gpStaticPoke;
    private Group[] groups;
    private Button btnLimitPkmn;
    private Spinner spStarterFirst, spStarterSecond, spStarterThird;
    private SwitchMaterial swBaseEvos, swAbilityWG, swAbilityEvos, swAbilityTraping, swAbilityNegative,
            swEvoStrength, swEvoTyping, swEvo3Stage, swEvoForce, swStarterBadItem, swMoveDataLegacy,
            swMoveSet4Moves, swMoveSetReorder, swMoveSetPctGood, swTrainerRivalStarter, swTrainerSimilarStrength,
            swTrainerWeightTypes, swTrainerNoLegendaries, swTrainerNoEarlyWG, swTrainerFullEvo,
            swTrainerPctLevel, swWildLegendaries, swWildBanBadItems, swTMLevelMoveSanity, swTMKeepFieldMove,
            swTMPctGoodMove, swTutorMoveLevelSanity, swTutorKeepField, swTutorPctGoodMoves, swTradeNicknames,
            swTradeRandOT, swTradeRandIV, swTradeRandItem, swFieldBadItem, swGeneralLimitPkmn,
            swStarterHeldItem, swMoveDataUpdate, swWildMinCatch, swWildTimeEncount, swWildRandItem;
    private Slider slideMoveSetPct, slideTrainerFullEvo, slideTrainerLevelPct, slideWildCatchRate,
            slideTMMovePct, slideTutorPctMoves;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //app init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        //begin personal code
        initVariables();
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

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void setupBtnAction() {
        ibLoadRom.setOnClickListener(view -> {
            if (!Environment.isExternalStorageManager()) {
                //request for the permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            } else {
                storageHelper.openFilePicker(LOAD_ROM);
            }
        });

        swGeneralLimitPkmn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                btnLimitPkmn.setEnabled(true);

            } else {
                btnLimitPkmn.setEnabled(false);
            }
        });

        rbBaseUnchange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swBaseEvos.setChecked(false);
                    swBaseEvos.setEnabled(false);
                }
            }
        });
        rbBaseShuffle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !swBaseEvos.isEnabled()) {
                    swBaseEvos.setEnabled(true);
                } else if (isChecked && swBaseEvos.isEnabled()) {
                    swBaseEvos.setEnabled(true);

                } else {
                    swBaseEvos.setChecked(false);
                }
            }
        });
        rbBaseRand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !swBaseEvos.isEnabled()) {
                    swBaseEvos.setEnabled(true);
                } else if (isChecked && swBaseEvos.isEnabled()) {
                    swBaseEvos.setEnabled(true);

                } else {
                    swBaseEvos.setChecked(false);
                }
            }
        });
        rbAbilityRand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swAbilityWG.setEnabled(true);
                    swAbilityEvos.setEnabled(true);
                    swAbilityTraping.setEnabled(true);
                    swAbilityNegative.setEnabled(true);
                } else {
                    swAbilityWG.setChecked(false);
                    swAbilityEvos.setChecked(false);
                    swAbilityTraping.setChecked(false);
                    swAbilityNegative.setChecked(false);
                    swAbilityWG.setEnabled(false);
                    swAbilityEvos.setEnabled(false);
                    swAbilityTraping.setEnabled(false);
                    swAbilityNegative.setEnabled(false);
                }
            }
        });
        rbEvoRand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swEvoStrength.setEnabled(true);
                    swEvoTyping.setEnabled(true);
                    swEvo3Stage.setEnabled(true);
                    swEvoForce.setEnabled(true);

                } else {
                    swEvoStrength.setChecked(false);
                    swEvoTyping.setChecked(false);
                    swEvo3Stage.setChecked(false);
                    swEvoForce.setChecked(false);
                    swEvoStrength.setEnabled(false);
                    swEvoTyping.setEnabled(false);
                    swEvo3Stage.setEnabled(false);
                    swEvoForce.setEnabled(false);
                }
            }
        });
        rbStarterCustom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spStarterFirst.setEnabled(true);
                    spStarterSecond.setEnabled(true);
                    spStarterThird.setEnabled(true);
                } else {
                    spStarterFirst.setSelection(0);
                    spStarterSecond.setSelection(0);
                    spStarterThird.setSelection(0);
                    spStarterFirst.setEnabled(false);
                    spStarterSecond.setEnabled(false);
                    spStarterThird.setEnabled(false);
                }
            }
        });
        swStarterHeldItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swStarterBadItem.setEnabled(true);
                } else {
                    swStarterBadItem.setChecked(false);
                    swStarterBadItem.setEnabled(false);
                }
            }
        });
        swMoveDataUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swMoveDataLegacy.setEnabled(true);
                } else {
                    swMoveDataLegacy.setChecked(false);
                    swMoveDataLegacy.setEnabled(false);
                }
            }
        });
        rbMovesetUnchange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swMoveSet4Moves.setChecked(false);
                    swMoveSetReorder.setChecked(false);
                    swMoveSetPctGood.setChecked(false);
                    swMoveSet4Moves.setEnabled(false);
                    swMoveSetReorder.setEnabled(false);
                    swMoveSetPctGood.setEnabled(false);
                    slideMoveSetPct.setValue(0);
                    slideMoveSetPct.setEnabled(false);
                }
            }
        });
        rbMovesetMetronome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swMoveSet4Moves.setChecked(false);
                    swMoveSetReorder.setChecked(false);
                    swMoveSetPctGood.setChecked(false);
                    swMoveSet4Moves.setEnabled(false);
                    swMoveSetReorder.setEnabled(false);
                    swMoveSetPctGood.setEnabled(false);
                    slideMoveSetPct.setValue(0);
                    slideMoveSetPct.setEnabled(false);
                }
            }
        });
        rbMovesetRandSameType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swMoveSetPctGood.setChecked(false);
                    swMoveSet4Moves.setChecked(false);
                    swMoveSetReorder.setChecked(false);
                    slideMoveSetPct.setValue(0);
                    swMoveSet4Moves.setEnabled(true);
                    swMoveSetReorder.setEnabled(true);
                    swMoveSetPctGood.setEnabled(true);
                }
            }
        });
        rbMovesetRandComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swMoveSetPctGood.setChecked(false);
                    swMoveSet4Moves.setChecked(false);
                    swMoveSetReorder.setChecked(false);
                    slideMoveSetPct.setValue(0);
                    swMoveSet4Moves.setEnabled(true);
                    swMoveSetReorder.setEnabled(true);
                    swMoveSetPctGood.setEnabled(true);
                }
            }
        });
        swMoveSetPctGood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && rbMovesetRandSameType.isChecked()) {
                    slideMoveSetPct.setEnabled(true);
                } else if (isChecked && rbMovesetRandComplete.isChecked()) {
                    slideMoveSetPct.setEnabled(true);
                } else {
                    slideMoveSetPct.setValue(0);
                    slideMoveSetPct.setEnabled(false);
                }
            }
        });
        rbTrainerUnchanged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swTrainerRivalStarter.setChecked(false);
                    swTrainerRivalStarter.setEnabled(false);
                    swTrainerSimilarStrength.setChecked(false);
                    swTrainerSimilarStrength.setEnabled(false);
                    swTrainerWeightTypes.setEnabled(false);
                    swTrainerWeightTypes.setChecked(false);
                    swTrainerNoLegendaries.setChecked(false);
                    swTrainerNoLegendaries.setEnabled(false);
                    swTrainerNoEarlyWG.setChecked(false);
                    swTrainerNoEarlyWG.setEnabled(false);
                    swTrainerFullEvo.setEnabled(false);
                    swTrainerFullEvo.setChecked(false);
                    slideTrainerFullEvo.setEnabled(false);
                    slideTrainerFullEvo.setValue(30);
                    swTrainerPctLevel.setChecked(false);
                    swTrainerPctLevel.setEnabled(false);
                    slideTrainerLevelPct.setValue(0);
                    slideTrainerLevelPct.setEnabled(false);
                }
            }
        });
        rbTrainerRand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swTrainerRivalStarter.setEnabled(true);
                    swTrainerSimilarStrength.setEnabled(true);
                    swTrainerWeightTypes.setEnabled(false);
                    swTrainerNoLegendaries.setEnabled(true);
                    swTrainerNoEarlyWG.setEnabled(true);
                    swTrainerFullEvo.setEnabled(true);
                    slideTrainerFullEvo.setEnabled(false);
                    swTrainerPctLevel.setEnabled(true);
                    slideTrainerLevelPct.setEnabled(false);
                }
            }
        });
        rbTrainerRandType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swTrainerRivalStarter.setEnabled(true);
                    swTrainerSimilarStrength.setEnabled(true);
                    swTrainerWeightTypes.setEnabled(true);
                    swTrainerNoLegendaries.setEnabled(true);
                    swTrainerNoEarlyWG.setEnabled(true);
                    swTrainerFullEvo.setEnabled(true);
                    slideTrainerFullEvo.setEnabled(false);
                    swTrainerPctLevel.setEnabled(true);
                    slideTrainerLevelPct.setEnabled(false);
                } else {
                    swTrainerWeightTypes.setChecked(false);
                    swTrainerWeightTypes.setEnabled(false);
                }
            }
        });
        swTrainerFullEvo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    slideTrainerFullEvo.setEnabled(true);
                } else {
                    slideTrainerFullEvo.setEnabled(false);
                }
                slideTrainerFullEvo.setValue(30);
            }
        });
        swTrainerPctLevel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    slideTrainerLevelPct.setEnabled(true);
                    slideTrainerLevelPct.setValue(0);
                } else {
                    slideTrainerLevelPct.setEnabled(false);
                    slideTrainerLevelPct.setValue(0);
                }
            }
        });
        swWildMinCatch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    slideWildCatchRate.setValue(1);
                    slideWildCatchRate.setEnabled(true);
                } else {
                    slideWildCatchRate.setEnabled(false);
                    slideWildCatchRate.setValue(1);
                }
            }
        });
        swWildRandItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swWildBanBadItems.setChecked(false);
                    swWildBanBadItems.setEnabled(true);
                } else {
                    swWildBanBadItems.setChecked(false);
                    swWildBanBadItems.setEnabled(false);
                }
            }
        });
        rbWildUnchanged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < rgWildsAdditionalRules.getChildCount(); i++) {
                        rgWildsAdditionalRules.getChildAt(i).setEnabled(false);
                    }
                    rbWildAddNone.setChecked(true);
                    swWildTimeEncount.setChecked(false);
                    swWildTimeEncount.setEnabled(false);
                    swWildLegendaries.setChecked(false);
                    swWildLegendaries.setEnabled(false);
                }

            }
        });
        rbWildRand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < rgWildsAdditionalRules.getChildCount(); i++) {
                        rgWildsAdditionalRules.getChildAt(i).setEnabled(true);
                    }
                    rbWildAddNone.setChecked(true);
                    swWildTimeEncount.setChecked(false);
                    swWildTimeEncount.setEnabled(true);
                    swWildLegendaries.setChecked(false);
                    swWildLegendaries.setEnabled(true);
                }
            }
        });
        rbWildArea1to1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < rgWildsAdditionalRules.getChildCount(); i++) {
                        rgWildsAdditionalRules.getChildAt(i).setEnabled(true);
                    }
                    rbWildAddNone.setChecked(true);
                    swWildTimeEncount.setChecked(false);
                    swWildTimeEncount.setEnabled(true);
                    swWildLegendaries.setChecked(false);
                    swWildLegendaries.setEnabled(true);
                }
            }
        });
        rbWildGlobal1to1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < rgWildsAdditionalRules.getChildCount(); i++) {
                        rgWildsAdditionalRules.getChildAt(i).setEnabled(false);
                    }
                    for (int i = 0; i < rgWildsAdditionalRules.getChildCount() - 2; i++) {
                        rgWildsAdditionalRules.getChildAt(i).setEnabled(true);
                    }
                    rbWildAddNone.setChecked(true);
                    swWildTimeEncount.setChecked(false);
                    swWildTimeEncount.setEnabled(true);
                    swWildLegendaries.setChecked(false);
                    swWildLegendaries.setEnabled(true);
                }
            }
        });
        rbTMRandom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swTMLevelMoveSanity.setChecked(false);
                    swTMLevelMoveSanity.setEnabled(true);
                    swTMKeepFieldMove.setChecked(false);
                    swTMKeepFieldMove.setEnabled(true);
                    swTMPctGoodMove.setChecked(false);
                    swTMPctGoodMove.setEnabled(true);
                    slideTMMovePct.setEnabled(false);

                } else {
                    swTMLevelMoveSanity.setChecked(false);
                    swTMLevelMoveSanity.setEnabled(false);
                    swTMKeepFieldMove.setChecked(false);
                    swTMKeepFieldMove.setEnabled(false);
                    swTMPctGoodMove.setChecked(false);
                    swTMPctGoodMove.setEnabled(false);
                    slideTMMovePct.setEnabled(false);
                }
                slideTMMovePct.setValue(0);
            }
        });
        swTMPctGoodMove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    slideTMMovePct.setEnabled(true);
                } else {
                    slideTMMovePct.setEnabled(false);
                }
                slideTMMovePct.setValue(0);
            }
        });
        rbTutorRand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swTutorMoveLevelSanity.setChecked(false);
                    swTutorMoveLevelSanity.setEnabled(true);
                    swTutorKeepField.setChecked(false);
                    swTutorKeepField.setEnabled(true);
                    swTutorPctGoodMoves.setChecked(false);
                    swTutorPctGoodMoves.setEnabled(true);
                    slideTutorPctMoves.setEnabled(false);

                } else {
                    swTutorMoveLevelSanity.setChecked(false);
                    swTutorMoveLevelSanity.setEnabled(false);
                    swTutorKeepField.setChecked(false);
                    swTutorKeepField.setEnabled(false);
                    swTutorPctGoodMoves.setChecked(false);
                    swTutorPctGoodMoves.setEnabled(false);
                    slideTutorPctMoves.setEnabled(false);
                }
                slideTutorPctMoves.setValue(0);
            }
        });
        swTutorPctGoodMoves.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    slideTutorPctMoves.setEnabled(true);
                } else {
                    slideTutorPctMoves.setEnabled(false);
                }
                slideTutorPctMoves.setValue(0);
            }
        });
        rbTradeUnchanged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swTradeNicknames.setChecked(false);
                    swTradeNicknames.setEnabled(false);
                    swTradeRandOT.setChecked(false);
                    swTradeRandOT.setEnabled(false);
                    swTradeRandIV.setChecked(false);
                    swTradeRandIV.setEnabled(false);
                    swTradeRandItem.setChecked(false);
                    swTradeRandItem.setEnabled(false);
                } else {
                    swTradeNicknames.setChecked(false);
                    swTradeNicknames.setEnabled(true);
                    swTradeRandOT.setChecked(false);
                    swTradeRandOT.setEnabled(true);
                    swTradeRandIV.setChecked(false);
                    swTradeRandIV.setEnabled(true);
                    swTradeRandItem.setChecked(false);
                    swTradeRandItem.setEnabled(true);
                }
            }
        });
        rbFieldItemRand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swFieldBadItem.setChecked(false);
                    swFieldBadItem.setEnabled(true);
                } else {
                    swFieldBadItem.setChecked(false);
                    swFieldBadItem.setEnabled(false);
                }
            }
        });

        ibSaveRom.setOnClickListener(v -> Toast.makeText(MainActivity.this, "nope", Toast.LENGTH_SHORT).show());
        btnLimitPkmn.setOnClickListener(v -> Toast.makeText(MainActivity.this, "nope", Toast.LENGTH_SHORT).show());

    }

    private void setupSimpleStorage(Bundle savedState) {
        if (savedState != null) {
            storageHelper.onRestoreInstanceState(savedState);
        }
        storageHelper.setOnFileSelected((requestCode, files) -> {
            try {
                Utils.validateRomFile(new File(DocumentFileUtils.getAbsolutePath(files.get(0), getApplicationContext()).trim()));
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
            boolean loadSuccess = false;
            for (RomHandler.Factory factory : factories) {
                if (factory.isLoadable(DocumentFileUtils.getAbsolutePath(files.get(0), getApplicationContext()).trim())) {
                    this.romHandler = factory.create(RandomSource.instance());
                    loadSuccess = true;

                }
            }
            if (loadSuccess) {
                this.romHandler.loadRom(DocumentFileUtils.getAbsolutePath(files.get(0), getApplicationContext()).trim());
                this.romFile.setText(DocumentFileUtils.getBaseName(files.get(0)));
                this.romName.setText(this.romHandler.getROMName());
                this.romCode.setText(this.romHandler.getROMCode());
                this.supportStat.setText(this.romHandler.getSupportLevel());
                setBoxArt();
                newRomUI();
            } else {
                Toast.makeText(MainActivity.this, R.string.unknown_file, Toast.LENGTH_SHORT).show();
            }

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

            }
        }
        reDisable();
        tweaks();

    }

    private void setBoxArt() {
        boxArt.setBackgroundColor(Color.TRANSPARENT);
        switch (this.romHandler.getROMCode()) {
            case "POKEMON RED":
            case "POKEMON RED (0/1)":
            case "POKEMON RED (1/1)":
                boxArt.setImageResource(R.drawable.red);
                break;
            case "POKEMON BLUE":
            case "POKEMON BLUE (0/1)":
            case "POKEMON BLUE (1/1)":
                boxArt.setImageResource(R.drawable.blue);
                break;
            case "POKEMON YELLOW":
            case "POKEMON YELLOW (0/1)":
            case "POKEMON YELLOW (1/1)":
                boxArt.setImageResource(R.drawable.yellow);
                break;
            case "POKEMON GREEN":
            case "POKEMON GREEN (0/1)":
            case "POKEMON GREEN (1/1)":
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

    private void tweaks() {
        for (int i = 0; i < Arrays.stream(gpMisc.getReferencedIds()).count(); i++) {
            int[] items = gpMisc.getReferencedIds();
            findViewById(items[i]).setEnabled(false);
        }
        switch (this.romHandler.getROMCode()) {
            case "POKEMON RED":
            case "POKEMON RED (0/1)":
            case "POKEMON RED (1/1)":

                break;
            case "POKEMON BLUE":
            case "POKEMON BLUE (0/1)":
            case "POKEMON BLUE (1/1)":
                break;
            case "POKEMON YELLOW":
            case "POKEMON YELLOW (0/1)":
            case "POKEMON YELLOW (1/1)":
                break;
            case "POKEMON GREEN":
            case "POKEMON GREEN (0/1)":
            case "POKEMON GREEN (1/1)":
                break;
            case "AAUE":
            case "AAUJ":
            case "AAUF":
            case "AAUD":
            case "AAUS":
            case "AAUI":
                // gold
                break;
            case "AAXJ":
            case "AAXE":
            case "AAXF":
            case "AAXD":
            case "AAXS":
            case "AAXI":
                //this is silver
                break;
            case "KAPB":
            case "BYTE":
            case "BXTJ":
            case "BYTF":
            case "BYTD":
            case "BYTS":
            case "BYTI":
                //this is crystal
                break;
            case "AXVE":
            case "AXVF":
            case "AXVD":
            case "AXVS":
            case "AXVI":
            case "AXVJ":
                //ruby
                break;
            case "AXPE":
            case "AXPF":
            case "AXPD":
            case "AXPS":
            case "AXPI":
            case "AXPJ":
                //sapphire
                break;
            case "BPEE":
            case "BPEF":
            case "BPED":
            case "BPEI":
            case "BPES":
            case "BPET":
            case "BPEJ":
                //emerald
                break;
            case "BPRE":
            case "BPRF":
            case "BPRD":
            case "BPRS":
            case "BPRI":
            case "BPRJ":
                //firered
                break;
            case "BPGE":
            case "BPGF":
            case "BPGD":
            case "BPGS":
            case "BPGI":
            case "BPGJ":
                //leafgreen
                break;
            case "ADAE":
            case "ADAJ":
            case "ADAD":
            case "ADAS":
            case "ADAI":
            case "ADAF":
            case "ADAK":
                //diamond
                break;
            case "APAK":
            case "APAF":
            case "APAI":
            case "APAS":
            case "APAE":
            case "APAD":
            case "APAJ":
                //perl
                break;
            case "CPUE":
            case "CPUJ":
            case "CPUD":
            case "CPUF":
            case "CPUS":
            case "CPUI":
            case "CPUK":
                //platinum
                break;
            case "IPKJ":
            case "IPKE":
            case "IPKK":
            case "IPKF":
            case "IPKD":
            case "IPKS":
            case "IPKI":
                //heartgold
                break;
            case "IPGI":
            case "IPGS":
            case "IPGD":
            case "IPGF":
            case "IPGK":
            case "IPGE":
            case "IPGJ":
                //soulsilver
                break;
            case "IRBO":
            case "IRBF":
            case "IRBD":
            case "IRBS":
            case "IRBI":
            case "IRBJ":
            case "IRBK":
                //black
                break;
            case "IRAF":
            case "IRAO":
            case "IRAD":
            case "IRAS":
            case "IRAI":
            case "IRAJ":
            case "IRAK":
                //white
                break;
            case "IREO":
            case "IREF":
            case "IRED":
            case "IREI":
            case "IRES":
            case "IREJ":
            case "IREK":
                //black2
                break;
            case "IRDO":
            case "IRDF":
            case "IRDD":
            case "IRDI":
            case "IRDS":
            case "IRDJ":
            case "IRDK":
                //white2
                break;
            default:

                break;
        }

    }

    private void reDisable() {
        //make disabled by default
        swWildTimeEncount.setEnabled(false);
        swBaseEvos.setEnabled(false);
        swAbilityTraping.setEnabled(false);
        swAbilityWG.setEnabled(false);
        swAbilityNegative.setEnabled(false);
        swAbilityEvos.setEnabled(false);
        spStarterFirst.setEnabled(false);
        spStarterSecond.setEnabled(false);
        spStarterThird.setEnabled(false);
        swEvoTyping.setEnabled(false);
        swEvo3Stage.setEnabled(false);
        swEvoStrength.setEnabled(false);
        swEvoForce.setEnabled(false);
        swStarterBadItem.setEnabled(false);
        btnLimitPkmn.setEnabled(false);
        swMoveDataLegacy.setEnabled(false);
        swMoveSetReorder.setEnabled(false);
        swMoveSetPctGood.setEnabled(false);
        swMoveSet4Moves.setEnabled(false);
        slideMoveSetPct.setEnabled(false);
        slideTrainerFullEvo.setEnabled(false);
        slideTrainerLevelPct.setEnabled(false);
        swTrainerWeightTypes.setEnabled(false);
        swTrainerFullEvo.setEnabled(false);
        swTrainerSimilarStrength.setEnabled(false);
        swTrainerRivalStarter.setEnabled(false);
        swTrainerPctLevel.setEnabled(false);
        swTrainerNoLegendaries.setEnabled(false);
        swTrainerNoEarlyWG.setEnabled(false);
        for (int i = 0; i < rgWildsAdditionalRules.getChildCount(); i++) {
            rgWildsAdditionalRules.getChildAt(i).setEnabled(false);
        }
        swWildLegendaries.setEnabled(false);
        swWildBanBadItems.setEnabled(false);
        slideWildCatchRate.setEnabled(false);
        swTMPctGoodMove.setEnabled(false);
        swTMLevelMoveSanity.setEnabled(false);
        swTMKeepFieldMove.setEnabled(false);
        slideTMMovePct.setEnabled(false);
        swTutorPctGoodMoves.setEnabled(false);
        swTutorMoveLevelSanity.setEnabled(false);
        swTutorKeepField.setEnabled(false);
        slideTutorPctMoves.setEnabled(false);
        swTradeRandOT.setEnabled(false);
        swTradeNicknames.setEnabled(false);
        swTradeRandIV.setEnabled(false);
        swTradeRandItem.setEnabled(false);
        swFieldBadItem.setEnabled(false);

    }

    private void initVariables() {
        //romhandlers
        genRestrictions = null;
        factories = new RomHandler.Factory[]{
                new Gen1RomHandler.Factory(), new Gen2RomHandler.Factory(),
                new Gen3RomHandler.Factory(), new Gen4RomHandler.Factory(),
                new Gen5RomHandler.Factory()
        };

        //groups
        groups = new Group[]{gpGeneralSettings = findViewById(R.id.gpGeneralSettings),
                gpPokeBase = findViewById(R.id.gpPokeBase), gpPokeAbilities = findViewById(R.id.gpPokeAbilities),
                gpPokeTypes = findViewById(R.id.gpPokeTypes), gpPokeEvo = findViewById(R.id.gpPokeEvo),
                gpStarterPoke = findViewById(R.id.gpStarterPoke), gpMoveData = findViewById(R.id.gpMoveData),
                gpMoveSets = findViewById(R.id.gpMoveSets), gpTrainerPoke = findViewById(R.id.gpTrainerPoke),
                gpWildPoke = findViewById(R.id.gpWildPoke), gpTMHM = findViewById(R.id.gpTMHM),
                gpMoveTutor = findViewById(R.id.gpMoveTutor), gpPokeTrades = findViewById(R.id.gpPokeTrades),
                gpFieldItems = findViewById(R.id.gpFieldItems), gpMisc = findViewById(R.id.gpMisc),
                gpStaticPoke = findViewById(R.id.gpStaticPoke)
        };
        radioGroups = new RadioGroup[]{rgBaseStats = findViewById(R.id.rgBaseStats),
                rgStarterPoke = findViewById(R.id.rgStarterPoke), rgFieldItems = findViewById(R.id.rgFieldItemsRand),
                rgMoveSets = findViewById(R.id.rgMoveSets), rgPokeAbilities = findViewById(R.id.rgPokeAbility),
                rgPokeEvoRand = findViewById(R.id.rgPokeEvoRand), rgPokeTrade = findViewById(R.id.rgTradeRandom),
                rgPokeTypes = findViewById(R.id.rgPokeTypes), rgTMHMCompat = findViewById(R.id.rgTMHMCompats),
                rgTMRando = findViewById(R.id.rgTMRando), rgTrainerRand = findViewById(R.id.rgTrainerRand),
                rgTutorCompat = findViewById(R.id.rgTutorCompat), rgTutorMoves = findViewById(R.id.rgTutorMoveRand),
                rgWildsAdditionalRules = findViewById(R.id.rgWildsAddRules), rgWildsPoke = findViewById(R.id.rgWildsRandSetting),
                rgStaticPoke = findViewById(R.id.rgStaticPoke)
        };

        //spinners
        spStarterFirst = findViewById(R.id.spStarterFirst);
        spStarterSecond = findViewById(R.id.spStarterSecond);
        spStarterThird = findViewById(R.id.spStarterThird);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
                R.array.pokemon_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStarterFirst.setAdapter(adapter);
        spStarterSecond.setAdapter(adapter);
        spStarterThird.setAdapter(adapter);

        //open and save buttons
        ibLoadRom = findViewById(R.id.imgbLoadRom);
        ibSaveRom = findViewById(R.id.imgbSaveRom);
        btnLimitPkmn = findViewById(R.id.btnLimitPoke);

        //general rom info
        romName = findViewById(R.id.txtRomName);
        romCode = findViewById(R.id.txtRomCode);
        romFile = findViewById(R.id.txtFileName);
        supportStat = findViewById(R.id.txtRomSupport);
        boxArt = findViewById(R.id.imgBoxArt);

        //switches
        swBaseEvos = findViewById(R.id.swBaseFollowEvo);
        swAbilityEvos = findViewById(R.id.swAbilityFollowEvo);
        swAbilityWG = findViewById(R.id.swAbilityWG);
        swAbilityNegative = findViewById(R.id.swAbilityNegative);
        swAbilityTraping = findViewById(R.id.swAbilityTrapping);
        swEvo3Stage = findViewById(R.id.swEvoLimitStage);
        swEvoForce = findViewById(R.id.swEvoForceChange);
        swEvoStrength = findViewById(R.id.swEvoStrength);
        swEvoTyping = findViewById(R.id.swEvoType);
        swStarterBadItem = findViewById(R.id.swStarterBanItem);
        swMoveDataLegacy = findViewById(R.id.swMoveDataUpdateLegacy);
        swMoveSet4Moves = findViewById(R.id.swMoveSetStartFourMove);
        swMoveSetPctGood = findViewById(R.id.swMoveSetGoodDmgPct);
        swMoveSetReorder = findViewById(R.id.swMoveSetReorder);
        swTrainerFullEvo = findViewById(R.id.swTrainerForceFullEvo);
        swTrainerNoEarlyWG = findViewById(R.id.swTrainerEarlyWG);
        swTrainerNoLegendaries = findViewById(R.id.swTrainerUseLegend);
        swTrainerPctLevel = findViewById(R.id.swTrainerLevelModifier);
        swTrainerRivalStarter = findViewById(R.id.swTrainerRivalStarter);
        swTrainerSimilarStrength = findViewById(R.id.swTrainerSimilarStrgh);
        swTrainerWeightTypes = findViewById(R.id.swTrainerWeightTypes);
        swWildBanBadItems = findViewById(R.id.swWildsBanItem);
        swWildLegendaries = findViewById(R.id.swWildsLegends);
        swTMKeepFieldMove = findViewById(R.id.swTMKeepField);
        swTMLevelMoveSanity = findViewById(R.id.swTMLevelUpSanity);
        swTMPctGoodMove = findViewById(R.id.swTMGoodMoves);
        swTutorKeepField = findViewById(R.id.swTutorKeepField);
        swTutorMoveLevelSanity = findViewById(R.id.swTutorLevelSanity);
        swTutorPctGoodMoves = findViewById(R.id.swTutorPctGoodMove);
        swTradeNicknames = findViewById(R.id.swTradeRandNicks);
        swTradeRandItem = findViewById(R.id.swTradeRandItems);
        swTradeRandIV = findViewById(R.id.swTradeRandIV);
        swTradeRandOT = findViewById(R.id.swTradeRandOT);
        swFieldBadItem = findViewById(R.id.swFieldItemBad);
        swGeneralLimitPkmn = findViewById(R.id.swLimitPoke);
        swStarterHeldItem = findViewById(R.id.swStarterRandItem);
        swMoveDataUpdate = findViewById(R.id.swMoveDataUpdate);
        swWildMinCatch = findViewById(R.id.swWildsMinCatch);
        swWildTimeEncount = findViewById(R.id.swWildsTimeEncounters);
        swWildRandItem = findViewById(R.id.swWildsHeldItem);

        //sliders
        slideMoveSetPct = findViewById(R.id.slideGoodMovePct);
        slideTrainerLevelPct = findViewById(R.id.slideTrainerLevelPct);
        slideTrainerLevelPct.setValueFrom(-50);
        slideTrainerLevelPct.setValueTo(50);
        slideTrainerFullEvo = findViewById(R.id.slideTrainerFullEvo);
        slideTrainerFullEvo.setValueFrom(30);
        slideTrainerFullEvo.setValueTo(65);
        slideWildCatchRate = findViewById(R.id.slideWildsCatchRate);
        slideTMMovePct = findViewById(R.id.slideTMGoodPct);
        slideTutorPctMoves = findViewById(R.id.slideTutorPctGood);


        //radiobuttons
        rbBaseUnchange = findViewById(R.id.rbBaseUnchange);
        rbBaseRand = findViewById(R.id.rbBaseRandom);
        rbBaseShuffle = findViewById(R.id.rbBaseShuffle);
        rbAbilityRand = findViewById(R.id.rbAbilityRandom);
        rbEvoRand = findViewById(R.id.rbPokeEvoRandom);
        rbStarterCustom = findViewById(R.id.rbStarterCustom);
        rbMovesetMetronome = findViewById(R.id.rbMoveSetMetronome);
        rbMovesetRandComplete = findViewById(R.id.rbMoveSetCompRand);
        rbMovesetRandSameType = findViewById(R.id.rbMoveSetRandSameType);
        rbMovesetUnchange = findViewById(R.id.rbMoveSetUnchanged);
        rbTrainerRand = findViewById(R.id.rbTrainerRandom);
        rbTrainerRandType = findViewById(R.id.rbTrainerThemed);
        rbTrainerUnchanged = findViewById(R.id.rbTrainerUnchanged);
        rbWildArea1to1 = findViewById(R.id.rbWildsAreaMap);
        rbWildUnchanged = findViewById(R.id.rbWildsUnchanged);
        rbWildGlobal1to1 = findViewById(R.id.rbWildsGlobal);
        rbWildRand = findViewById(R.id.rbWildsRandom);
        rbWildAddNone = findViewById(R.id.rbWildsAddNone);
        rbTMRandom = findViewById(R.id.rbTMRandom);
        rbTutorRand = findViewById(R.id.rbTutorRand);
        rbTradeUnchanged = findViewById(R.id.rbTradeUnchanged);
        rbFieldItemRand = findViewById(R.id.rbFieldItemCompRand);

    }

}
