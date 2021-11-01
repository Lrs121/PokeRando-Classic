package org.lrsservers.pokerando.upr.thenewpoketext;

/*----------------------------------------------------------------------------*/
/*--  UnicodeParser.java - maintains the poke<->unicode text table          --*/
/*--  Code loosely derived from "thenewpoketext", copyright (C) loadingNOW  --*/
/*--  Ported to Java and customized by Dabomstew                            --*/
/*----------------------------------------------------------------------------*/

import androidx.appcompat.app.AppCompatActivity;

import org.lrsservers.pokerando.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UnicodeParser extends AppCompatActivity {

    public static String[] tb = new String[65536];
    public static Map<String, Integer> d = new HashMap<String, Integer>();

    {
        Scanner sc = new Scanner(getResources().openRawResource(R.raw.generation4), "UTF-8");
        while (sc.hasNextLine()) {
            String q = sc.nextLine();
            if (!q.trim().isEmpty()) {
                String[] r = q.split("=", 2);
                if (r[1].endsWith("\r\n")) {
                    r[1] = r[1].substring(0, r[1].length() - 2);
                }
                tb[Integer.parseInt(r[0], 16)] = r[1];
                d.put(r[1], Integer.parseInt(r[0], 16));
            }
        }
        sc.close();
    }

}
