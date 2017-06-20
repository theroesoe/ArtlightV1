package com.example.felix.artlightv1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    ArtNetClient artnet;

    byte[] buffer = new byte[512];

    public void setup(View view) {

        try {

            NetworkInterface ni = NetworkInterface.getByName("wlan0");
            if (ni == null) {
                throw new SocketException("wlan0 interface not found");
            }
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            InetAddress adr;
            // passende IP-Adresse wählen (ipv4)
            while ((adr = addresses.nextElement()) != null) {
                Log.d("Fehler", adr.toString());
                if (adr.toString().length() < 16) {
                    break;
                }
            }

            artnet = new ArtNetClient();
            // Artnet Socket mit passender IP-Adresse öffnen
            artnet.open(adr, null);

            // IP-Adresse für Artnet-Server aus Textfeld bekommen
            String ip;
            EditText input;
            input = (EditText) findViewById(R.id.ip);
            ip = input.getText().toString();

            // Receiver-IP setzen
            artnet.setReceiver(ip);
            Toast errorToast = Toast.makeText(getApplicationContext(), "Socket geöffnet", Toast.LENGTH_SHORT);
            errorToast.show();

        } catch (SocketException e) {
            e.printStackTrace();
            Toast errorToast = Toast.makeText(getApplicationContext(), "Socket konnte nicht geöffnet werden\n" + e.getMessage(), Toast.LENGTH_SHORT);
            errorToast.show();
        }
    }

    public void send(View view) {
        // Auslesen der Textfelder
        EditText von = (EditText) findViewById(R.id.von);
        EditText bis = (EditText) findViewById(R.id.bis);
        EditText wert = (EditText) findViewById(R.id.wert);

        int vonnum = Integer.parseInt(von.getText().toString());
        int bisnum = 0;

        // Verhindern, dass bis leer ist
        try {
            bisnum = Integer.parseInt(bis.getText().toString());
        } catch (Exception e) {
        }

        int wertnum = Integer.parseInt(wert.getText().toString());

        int j;

        if (bisnum >= vonnum) {
            j = bisnum;
        } else {
            j = vonnum;
        }

        // Alle angegeben Adressen ablaufen und Wert setzten
        for (int i = vonnum - 1; i < j; i++) {
            // wertnum zu Datentyp byte casten
            buffer[i] = (byte) wertnum;
        }

        // Senden im Hintergrund-Thread ausführen
        new MyAsyncTask().execute();
    }


    void stop() {
        artnet.close();
    }


    class MyAsyncTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            artnet.send(0, buffer);
            return null;
        }
    }
}

