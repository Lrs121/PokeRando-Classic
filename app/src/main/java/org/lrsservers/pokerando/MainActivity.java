package org.lrsservers.pokerando;

import static android.widget.Toast.*;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private final int STORAGE_PERMISSION_CODE = 1;
    private ImageButton ibLoadRom;
    private ImageButton ibSaveRom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ibSaveRom = findViewById(R.id.imgbSaveRom);
        ibLoadRom = findViewById(R.id.imgbLoadRom);

        ibSaveRom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this, "perm req", LENGTH_SHORT).show();
                    requestStoragePermission();
                }
            }
        });

        ibLoadRom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestStoragePermission();
                }
            }
        });

    }

    private void requestStoragePermission(){
        Toast.makeText(MainActivity.this, "start req", LENGTH_SHORT).show();
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this, "show reason Dialog", LENGTH_SHORT).show();
            new AlertDialog.Builder(MainActivity.this).setTitle("Permissions Needed").setMessage("Storage access needed inorder to load and save Rom files.").setPositiveButton("Grant ", (dialog, which) -> {
                Toast.makeText(MainActivity.this, "requesting permission", LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.MANAGE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "close dialog", LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            Toast.makeText()
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.MANAGE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

        }
        Toast.makeText(MainActivity.this, "exit req", LENGTH_SHORT).show();

    }

}