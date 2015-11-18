package com.miranda.luis.appsupport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class Main extends Activity implements View.OnClickListener {
    private WebView myWebView;
    private String TAG = "Main";
    private String LOCAL_FILE = "file:///android_asset/login.html";
    private String LOCAL_FILE1 = "file:///android_asset/menu.html";
    helpBD aBD;
    SQLiteDatabase db=null;

    Button enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //enviar = (Button) findViewById(R.id.button);
        //enviar.setOnClickListener(this);

        GCMRegistrar.checkDevice(Main.this);
        GCMRegistrar.checkManifest(Main.this);

        //condicion

        GCMRegistrar.register(Main.this, "853205055727");

        if (!verificaConexion(this)) {
            new AlertDialog.Builder(Main.this).setTitle("Error").setMessage("Comprueba tu conexión a Internet!")
                    .setNeutralButton("Ok", null).show();
            this.finish();
        }


        try {

            Bundle bundle = getIntent().getExtras();
            String message = bundle.getString("alerta");


            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        }catch (Exception ex) {}

        myWebView = (WebView) findViewById(R.id.web);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        myWebView.setWebViewClient(new WebViewClient());

        SharedPreferences prefs = getSharedPreferences("LogInn", MODE_PRIVATE);

        Boolean registro = prefs.getBoolean("registro", false);

        if(registro == true) myWebView.loadUrl(LOCAL_FILE1);
        else    myWebView.loadUrl(LOCAL_FILE);

        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(final WebView view, String url) {


                //view.loadUrl("javascript:showSuccessToast();");

            }

        });

        //datosDB();




    }




    public static boolean verificaConexion(Context ctx) {
        boolean bConectado = false;
        ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        for (int i = 0; i < 2; i++) {
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) { bConectado = true; }
        }
        return bConectado;
    }

    @Override
    public void onClick(View v) {
        //if(v.getId() == R.id.button){

        //myWebView.loadUrl("javascript:cambia()");

        //}
    }


    private class WebAppInterface {
        Context mContext;
        int i=0;

        WebAppInterface(Context c) {
            mContext = c;
        }

        // This function can be called in our JS script now
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public String dataiFilter(){

            try{
                aBD=new helpBD(mContext,"data.db",null,1);
                db = aBD.getReadableDatabase();
                if (db!=null) {
                    Cursor cursor = db.rawQuery("SELECT * FROM ifilters ",null);//+num+" and num="+numAleatorio+"",null);
                    int numcol=cursor.getColumnCount();
                    int numren=cursor.getCount();

                    int i=0;

                    while (cursor.moveToNext()){
                        //json.put("obj",new String[]{cursor.getString(1),cursor.getString(2)});
                    }

                    cursor.close();
                    db.close();
                }//if
                else
                    Toast.makeText(mContext, "db fue null :-(", Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                String cad2 = "ERROR " + e.getMessage();
            }


            return "Prueba";
        }


        @JavascriptInterface
        public int verifyLogIn(String user, String pass) {
            String regId = GCMRegistrar.getRegistrationId(Main.this);

            final String datos[]={user.trim(),pass.trim(),regId};

            final httpHandler handler = new httpHandler();
            String result = handler.post("http://flores.rosales.engineer/appSupport/register.php", datos);

            try {
                JSONObject json = new JSONObject(result);
                String answer = json.getString("answer");
                JSONObject data = new JSONObject(json.getString("data"));

                if( answer.equals("true")) {
                    i = 1;
                    String name = data.getString("name");
                    String email = data.getString("email");
                    int userr = Integer.parseInt(data.getString("user"));
                    int status = Integer.parseInt(data.getString("status"));
                    Registro(name,email, userr, status);
                }
                //Toast.makeText(mContext, json.toString(), Toast.LENGTH_LONG).show();



            }catch (Exception ex){}


            return i;

        }


        public String[] verifyUser() {

            SharedPreferences prefs = getSharedPreferences("LogInn", MODE_PRIVATE);
            String registro = prefs.getString("name", "");
            int status = prefs.getInt("status", 0);
            String [] user = {registro, ""+status};

            return user;
        }



    }


    public void Registro(String name, String email, int user, int stat){

        SharedPreferences.Editor editor = getSharedPreferences("LogInn", MODE_PRIVATE).edit();
        editor.putBoolean("registro", true);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putInt("user", user);
        editor.putInt("status", stat);

        editor.commit();

        //Intent intent = new Intent(this, Main.class);
        //startActivity(intent);
        //finish();

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (myWebView.canGoBack()) {
                        myWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }




    public void datosDB(){

        try{
            aBD=new helpBD(this,"data.db",null,1);
            db = aBD.getWritableDatabase();
            if (db!=null) {
                //id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, payload TEXT, from TEXT, to TEXT, priority INT, bytes_size INT, date TIMESTAMP, checksum INT, status INT )";
                db.execSQL("INSERT INTO messages(id, uuid, payload, fromm, too, priority, size, date, checksum, status) VALUES(null,'123456789012','contiene info de ticket y ifilter','soporte1','...',5,120,'12/12/1212',10,0);");
                //"CREATE TABLE ifilter(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, company TEXT, status INTEGER, location TEXT, change TIMESTAMP
                db.execSQL("INSERT INTO ifilter(id, name, company, status, location, change) VALUES(null,'Corporativo',1,9.34234|-82.34245,'12/12/1212');");
                db.execSQL("INSERT INTO ifilter(id, name, company, status, location, change) VALUES(null,'Centro',1,9.34234|-82.34245,'12/12/1212');");


                db.close();
            } else
                Toast.makeText(this, "db fue null :-(", Toast.LENGTH_LONG).show();
        }//try
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        try{
            aBD=new helpBD(this,"data.db",null,1);
            db = aBD.getReadableDatabase();
            if (db!=null) {
                Cursor cursor = db.rawQuery("SELECT * FROM messages ",null);//+num+" and num="+numAleatorio+"",null);
                int numcol=cursor.getColumnCount();
                int numren=cursor.getCount();
                while (cursor.moveToNext()){
                    String cad=cursor.getString(1)+"  "+cursor.getString(2);
                    //Toast.makeText(this, cad, Toast.LENGTH_LONG).show();

                }//while
                //Toast.makeText(getApplicationContext(),"es "+numAleatorio,Toast.LENGTH_SHORT).show();

                cursor.close();
                db.close();
            }//if
            else
                Toast.makeText(this, "db fue null :-(", Toast.LENGTH_LONG).show();
        }//try
        catch (Exception e) {
            String cad2="ERROR "+e.getMessage();
        }//catch





    }







}



/*
*
* try{
            aBD=new Ayudante(this,"mensajes",null,1);
            db = aBD.getWritableDatabase();
            if (db!=null) {
                db.execSQL("insert into mensajes values(null,'General','Puedes donar en vida y después de la muerte',1)");
                db.close();
            } else
                h="db fue null :-(";
        }//try
        catch (Exception e)
        {
            h=e.getMessage()+"\n\n";
        }


        try{
            aBD=new Ayudante(this,"mensajes",null,1);
            db = aBD.getReadableDatabase();
            if (db!=null) {
                Cursor cursor = db.rawQuery("SELECT * FROM mensajes where tematica='"+organo+"' and  num="+numAleatorio,null);//+i+" and num="+g,null);//+num+" and num="+numAleatorio+"",null);
                int numcol=cursor.getColumnCount();
                int numren=cursor.getCount();
                while (cursor.moveToNext()){
                    cad=cursor.getString(2);
                }//while
                //Toast.makeText(getApplicationContext(),"es "+numAleatorio,Toast.LENGTH_SHORT).show();

                cursor.close();
                db.close();
            }//if
            else
                cad="db fue null";
        }//try
        catch (Exception e) {
            String cad2="ERROR "+e.getMessage();
        }//catch
*
*
*
*
*
* */