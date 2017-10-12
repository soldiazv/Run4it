package com.example.adm.pantallas;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.view.*;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import static com.example.adm.pantallas.R.raw.duracion;

public class salida extends AppCompatActivity {
    MediaPlayer reproductor;
    MediaPlayer bgmusic;
    int maxVolume=100;
    int volume = 5;
    int nextAudio = 10, alertType = 10;
    int nextAudioB = 10;
    int runTime = 100;
    int bTot, spareTime, a;
    boolean alert=false;
    String nextAudioLevel="a_";
    int durationSeg;
    HashMap<Integer,Integer> durationA = new HashMap<Integer, Integer>();
    HashMap<Integer,Integer> durationB = new HashMap<Integer, Integer>();
    HashMap<Integer,Integer> durationC = new HashMap<Integer, Integer>();
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salida);
        Context ctx = getBaseContext();
        reproductor = MediaPlayer.create(this, R.raw.a_10);
        bgmusic = MediaPlayer.create(this, R.raw.bgmusic);
        float log1 = (float) (Math.log(maxVolume - volume) / Math.log(maxVolume));
        bgmusic.setVolume(1 - log1, 1 - log1);
        bgmusic.start();
        bgmusic.setLooping(true);
        obtenerDuracion();
        reproducirAudio();
    }
    protected void reproducirAudio(){
        reproductor.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer reproductor) {
                volume = 5;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        elegirAudio();
                    }
                }, spareTime/(bTot-10+a+1-10)*1000);   //5 seconds
                volume=50;
            }
        });
    }
    protected void elegirAudio(){
        reproductor.stop();
        reproductor.reset();
        if (alert=false){
        if (runTime>0) {
            try {

                if ((bTot - 10) > 0 && nextAudioLevel == "a_") {
                    nextAudioLevel = "b_";
                    String filename = "android.resource://" + getBaseContext().getPackageName() + "/raw/" + nextAudioLevel + nextAudioB;
                    reproductor.setDataSource(getBaseContext(), Uri.parse(filename));
                    reproductor.prepare();
                    runTime -= durationB.get(nextAudioB);
                    nextAudioB++;
                    bTot--;
                } else {
                    nextAudioLevel = "a_";
                    String filename = "android.resource://" + getBaseContext().getPackageName() + "/raw/" + nextAudioLevel + nextAudio;
                    reproductor.setDataSource(getBaseContext(), Uri.parse(filename));
                    reproductor.prepare();
                    runTime -= durationA.get(nextAudio);
                    nextAudio++;
                }
                Toast.makeText(getApplicationContext(),"Audio numero " + (nextAudio-10), Toast.LENGTH_LONG).show();
                reproductor.start();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "NO MORE AUDIOS SIR, "+spareTime, Toast.LENGTH_LONG).show();
                reproductor.stop();
                reproductor.release();
            }
        }else{
            Toast.makeText(getApplicationContext(),"YOU FINISHED", Toast.LENGTH_LONG).show();
            }
        }else{
            try {
                alert=false;
                String filename = "android.resource://" + getBaseContext().getPackageName() + "/raw/c_" + alertType;
                reproductor.setDataSource(getBaseContext(), Uri.parse(filename));
                reproductor.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void Alert(View v){
        alert=true;
    }
    public void SaludarOnClick(View v) {
        /*
        EditText Cajatexto = (EditText) findViewById(R.id.ET_Nombre);
        String nombre = Cajatexto.getText().toString();
        Toast.makeText(this,"Hola "+ nombre, Toast.LENGTH_LONG).show();*/
        reproductor.start();
        nextAudio++;
        float duration = reproductor.getDuration();
        float getPos = reproductor.getCurrentPosition();
    }
    protected void obtenerDuracion(){
        Context ctx = getBaseContext();
        InputStream is = ctx.getResources().openRawResource(duracion);
        Scanner sc = new Scanner(is);
        String linea = sc.next();
        durationSeg = sc.nextInt();
        String level="";
        int durA=0, durB=0, durC=0, aDurTotal=0, bDurTotal=0, cDurTotal=0, c = 10, b = 10, durRest=0;
        a = 10;
        while (sc.hasNext()){
            level = sc.next();
            switch (level.charAt(0)){
                case 'a':                           //29 seg
                    durA = sc.nextInt();
                    aDurTotal+=durA;
                    durationA.put(a,durA);
                    a++;
                    break;
                case 'b':
                    durB=sc.nextInt();
                    bDurTotal+=durB;
                    durationB.put(b,durB);
                    b++;
                    break;
                case 'c':
                    durC=sc.nextInt();
                    cDurTotal+=durC;
                    durationC.put(c,durC);
                    c++;
                    break;
                default:
                    break;
            }
        }
        sc.close();
        //Toast.makeText(getApplicationContext(),String.valueOf(aDurTotal), Toast.LENGTH_LONG).show();

        durRest=runTime-aDurTotal;
        bTot=10;

            for (int i = 10; durRest>0; i++){
                if (durationB.containsKey(i)) {
                    durRest -= durationB.get(i);
                    bTot++;
                }else{
                    spareTime++;
                    durRest--;
                }
            }

       // bTot-=1;
        Toast.makeText(getApplicationContext(),"Entran los "+(bTot-10)+" primeros audios de la categoria B :)", Toast.LENGTH_LONG).show();
    }
}
