// DisplayMessageActivity.java
package com.example.marcin.mysecondapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;


public class DisplayMessageActivity extends AppCompatActivity {

    public boolean box;
    public boolean available;
    public Button startButton;
    public Button saveButton;
    public Button exitButton;
    public CheckBox check;
    public Context con;
    public File file;
    public FileOutputStream fos;
    public int signal;
    public int counter=0;
    public int sum=0;
    public int measureCounter=0;
    public int avarage=0;
    public long baseCi;
    public OutputStreamWriter osw;
    private final int interval = 2000; // 2 sekundy
    public String s="";
    public String operator;
    public String type="";
    public String base="";
    public String LOG_TAG="p";
    public String folder="Pomiary";
    public String ref;
    public TextView strengthText;
    public TextView operatorText;
    public TextView standardText;
    public TextView baseText;
    public TelephonyManager TelephonManager;
    public Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Opcje konfiguracyjne
        con = getApplicationContext();
        super.onCreate(savedInstanceState);
        Initialize();
    }

    public void Initialize(){

        // Ustawienie widoku
        setContentView(R.layout.activity_display_message);
        // Deklaracja managera
        TelephonManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        viewComponentsInit();
        pathInit();
        saveButtonInit();
        startStopButtonInit();
        exitButtonInit();
        setOperator();

    }

    public void viewComponentsInit() {
        check = (CheckBox) findViewById(R.id.checkBox);
        operatorText= (TextView) findViewById(R.id.textOperator);
        operatorText.setTextSize(20);
       // operatorText.setTypeface(null, Typeface.BOLD);
        standardText= (TextView) findViewById(R.id.textStandard);
        standardText.setTextSize(20);
       // standardText.setTypeface(null, Typeface.BOLD);
        baseText= (TextView) findViewById(R.id.textBase);
        baseText.setTextSize(20);
        //baseText.setTypeface(null, Typeface.BOLD);
        strengthText=(TextView) findViewById(R.id.textStrength);
        strengthText.setTextSize(25);
        strengthText.setTypeface(null, Typeface.BOLD);
        strengthText.setText("P = " + s + "dBm");
        standardText.setText(type);
        baseText.setText(base);

    }

    public void saveButtonInit(){

        // Deklaracja
        saveButton= (Button) findViewById(R.id.button4);
        //Zapis do pliku
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                box=check.isChecked();
                if(box)
                    ref="Ref.";
                else
                    ref="Pomiar";

                available=isExternalStorageWritable();
                if(!available)
                {
                    Toast.makeText(con, "Nie można otworzyć", Toast.LENGTH_LONG).show();
                }
                else
                {


                    try {
                        //Wysłanie danych
                        fos = new FileOutputStream(file,true);
                        osw = new OutputStreamWriter(fos);
                        osw.append(ref+" "+type+" P="+avarage+"dBm"+"\n");
                        osw.flush();
                        osw.close();
                        // Komunikat
                        Toast.makeText(con, "Zapisano", Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            }
        });


    }

    public void startStopButtonInit(){

        //Rozpoczęcie / zakończenie pomiaru
        startButton = (Button) findViewById(R.id.button2);
        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                counter++;
                if (counter == 1) {
                    startButton.setText("Stop");
                    timer = new Timer();
                    timer.schedule(new Timer1(TelephonManager, con), 400, interval);

                } else {
                    timer.cancel();
                    measureCounter=0;
                    startButton.setText("Start");
                    counter = 0;

                }
            }
        });
    }

    public void exitButtonInit(){
        // Wyjście z okna pomiarowego
        exitButton = (Button) findViewById(R.id.button3);
        exitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                finish();
                timer.cancel();
            }

        });
    }
    public void pathInit(){
        file = new File(getAlbumStorageDir(folder),"/pomiary.txt");
    }



    public void setOperator(){
        // Wczytanie nazwy operatora
        operator= TelephonManager.getNetworkOperatorName();
        operatorText.setText(operator);
    }




    // Czy można zapisać do pamięci
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Utworzenie folderu w katalogu wyjściowym
    public File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }



    // Wykonanie czynności w wątku TimerTask
    final Runnable myRunnable = new Runnable() {

        public void run() {
            strengthText.setText("P = "+s+"dBm");
            standardText.setText(type);
            baseText.setText(base);
           if(measureCounter==10) {
               startButton.performClick();
           }
        }
    };
    // Przechwycenie wiadomości w wątku TimerTask
   public Handler mHandler = new Handler();


    // Klasa wątku pobierającego dane o mocy sygnału i lokalizacji stacji bazowej
    class Timer1 extends TimerTask {

        public Timer1(TelephonyManager tele, Context con) {
            TelephonManager = tele;
            mCContext = con;
        }

        TelephonyManager TelephonManager;
        Context mCContext;
        PhoneStateListener pslistener = new PhoneStateListener();

        public void run() {
            if (measureCounter==0)
                sum=0;

            measureCounter++;
            if(measureCounter==1)
            TelephonManager.listen(pslistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

            try {
                for (final CellInfo info : TelephonManager.getAllCellInfo()) {
                    if (info instanceof CellInfoGsm) {
                        final CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                        final CellIdentityGsm gsmm = ((CellInfoGsm) info).getCellIdentity();
                        signal = gsm.getDbm();
                        if(measureCounter==1)
                        base="Cid: "+gsmm.getCid()+" Lac: "+gsmm.getLac()+"\nMcc: "+gsmm.getMcc()+"           Mnc: "+gsmm.getMnc();
                        s = Integer.toString(signal);
                        type="GSM";

                    } else if (info instanceof CellInfoWcdma) {

                            final CellSignalStrengthWcdma cdma = ((CellInfoWcdma) info).getCellSignalStrength();
                            final CellIdentityWcdma cdm = ((CellInfoWcdma) info).getCellIdentity();
                            if (measureCounter == 1)
                                base = "Cid: " + cdm.getCid() + " Lac: " + cdm.getLac() + "\nMcc: " + cdm.getMcc() + " Mnc: " + cdm.getMnc();
                            signal = cdma.getDbm();
                            s = Integer.toString(signal);
                            type = "3G";
                    System.out.println(signal);

                    } else if (info instanceof CellInfoLte) {
                        final CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                        final CellIdentityLte ltee = ((CellInfoLte) info).getCellIdentity();

                        baseCi=ltee.getCi();
                        if(measureCounter==1)
                        base="Ci: "+baseCi;
                        signal = lte.getDbm();
                        type="LTE";
                        s = Integer.toString(signal);

                    } else {
                        throw new Exception("Unknown type of cell signal!");
                    }

                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            sum=signal+sum;


            if (measureCounter==10)
            {
                avarage=sum/10;
            }

            mHandler.post(myRunnable);
        }


    }


}
