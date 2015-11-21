package com.miranda.luis.appsupport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Switch;
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
    private String LOCAL_FILE2 = "file:///android_asset/ifilter.html";
    private String LOCAL_FILE3 = "file:///android_asset/ticket.html";

    helpBD aBD;
    SQLiteDatabase db=null;
    int action;

    Button enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(extras.containsKey("alerta"))
            {
                action = extras.getInt("alerta");
            }
        }


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



        myWebView = (WebView) findViewById(R.id.web);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        myWebView.setWebViewClient(new WebViewClient());

        SharedPreferences prefs = getSharedPreferences("LogInn", MODE_PRIVATE);

        Boolean registro = prefs.getBoolean("registro", false);

        if(registro == true) {

            switch (action){
                //Cargar tickets
                case 1:
                    myWebView.loadUrl(LOCAL_FILE3);
                break;

                //Cargar iFilter
                case 2:
                    myWebView.loadUrl(LOCAL_FILE2);
                break;

                //Elimina registro.
                case 3:
                    myWebView.loadUrl(LOCAL_FILE);
                    //metodo eliminar GCM
                break;

                //Inicio normal
                default:
                    myWebView.loadUrl(LOCAL_FILE1);
                break;
            }



        }
        else {
            myWebView.loadUrl(LOCAL_FILE);
            Ejemplos();
        }

        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(final WebView view, String url) {


                //view.loadUrl("javascript:showSuccessToast();");

            }

        });





    }


    @Override
    public void onNewIntent(Intent intent){
        Bundle extras = intent.getExtras();
        if(extras != null){
            if(extras.containsKey("alerta"))
            {
                String msg = extras.getString("alerta");
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

            }
        }


    }








    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

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


    //Interactua con Javascript

    private class WebAppInterface {
        Context mContext;
        int i=0;

        WebAppInterface(Context c) {
            mContext = c;
        }

        // Pruebas desde Javascript
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
        }

        // Obteber datos de Ticket
        @JavascriptInterface
        public  String dataMessage(){

            JSONArray array = new JSONArray();

            try{
                aBD=new helpBD(mContext,"data.db",null,1);
                db = aBD.getReadableDatabase();
                if (db!=null) {
                    Cursor cursor = db.rawQuery("SELECT * FROM ticket ",null);

                    while (cursor.moveToNext()){

                        JSONObject obj = new JSONObject();
                        obj.put("id", cursor.getString(0));
                        array.put(obj);
                    }
                    cursor.close();
                    db.close();
                }
                else
                    Toast.makeText(mContext, "db fue null :-(", Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                String cad2 = "ERROR " + e.getMessage();
            }

            return array.toString();
        }




        // Obteber datos de Hosts
        @JavascriptInterface
        public  String dataiFilter(){

            JSONArray array = new JSONArray();

            try{
                aBD=new helpBD(mContext,"data.db",null,1);
                db = aBD.getReadableDatabase();
                if (db!=null) {
                    Cursor cursor = db.rawQuery("SELECT  i.id, i.name, i.company, i.location, changeifilters.status FROM ifilters i " +
                            " LEFT JOIN changeifilters ON i.id = changeifilters.ifilter_id AND  changeifilters.id = (SELECT MAX(id) FROM changeifilters WHERE  ifilter_id= i.id ) ;",null);

                    while (cursor.moveToNext()){

                        JSONObject obj = new JSONObject();
                        obj.put("id",cursor.getString(0));
                        obj.put("name",cursor.getString(1));
                        obj.put("company",cursor.getString(2));
                        obj.put("location",cursor.getString(3));
                        if(cursor.getString(4).equals("1")){  obj.put("status","OnLine");}
                        else{ obj.put("status", "OffLine");  }

                        array.put(obj);
                    }
                    cursor.close();
                    db.close();
                }
                else
                    Toast.makeText(mContext, "db fue null :-(", Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                String cad2 = "ERROR " + e.getMessage();
            }

            return array.toString();
        }


        // Obteber datos de Usuarios
        @JavascriptInterface
        public  String dataUsers(){

            JSONArray array = new JSONArray();

            try{
                aBD=new helpBD(mContext,"data.db",null,1);
                db = aBD.getReadableDatabase();
                if (db!=null) {
                    Cursor cursor = db.rawQuery("SELECT * FROM users",null);
                    while (cursor.moveToNext()){
                        JSONObject obj = new JSONObject();

                        obj.put("id",cursor.getString(0));
                        obj.put("name",cursor.getString(1));
                        obj.put("email",cursor.getString(2));
                        obj.put("position",cursor.getString(3));
                        obj.put("status",cursor.getString(4));
                        array.put(obj);
                    }
                    cursor.close();
                    db.close();
                }
                else
                    Toast.makeText(mContext, "db fue null :-(", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                String cad2 = "ERROR " + e.getMessage();
            }

            return array.toString();
        }


        // Obteber datos de usuario con el server con php
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
            }catch (Exception ex){}
            return i;
        }



    }

    //Guardar preferencias de usuario
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
                        myWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }





    public void Ejemplos(){

        try{
            aBD=new helpBD(this,"data.db",null,1);
            db = aBD.getWritableDatabase();
            if (db!=null) {

                //datos de mensajes
                //db.execSQL("INSERT INTO messages(id, uuid, payload, fromm, too, priority, size, date, checksum, status) VALUES(null,'123456789012','contiene info de ticket y ifilter','soporte1','...',5,120,'12/12/1212',10,0);");

                //datos de ifilter
                db.execSQL("INSERT INTO ifilters(id, name, company, location) VALUES(null,'Corporativo','Korporativo','9.34234 -82.34245');");
                db.execSQL("INSERT INTO ifilters(id, name, company, location) VALUES(null,'Centro','Korporativo','9.34234 -82.34245');");
                db.execSQL("INSERT INTO ifilters(id, name, company, location) VALUES(null,'Toluca','Korporativo','9.34234 -82.34245');");
                db.execSQL("INSERT INTO ifilters(id, name, company, location) VALUES(null,'Acapulco','Korporativo','9.34234 -82.34245');");

                db.execSQL("INSERT INTO changeifilters(id, ifilter_id, status, lastchange) VALUES(null,1,1,9342348245);");
                db.execSQL("INSERT INTO changeifilters(id, ifilter_id, status, lastchange) VALUES(null,2,1,9342348245);");
                db.execSQL("INSERT INTO changeifilters(id, ifilter_id, status, lastchange) VALUES(null,3,0,9342348245);");
                db.execSQL("INSERT INTO changeifilters(id, ifilter_id, status, lastchange) VALUES(null,4,1,9342348245);");

                db.execSQL("INSERT INTO changeifilters(id, ifilter_id, status, lastchange) VALUES(null,1,0,9342348245);");
                db.execSQL("INSERT INTO changeifilters(id, ifilter_id, status, lastchange) VALUES(null,2,1,9342348245);");
                db.execSQL("INSERT INTO changeifilters(id, ifilter_id, status, lastchange) VALUES(null,3,0,9342348245);");
                db.execSQL("INSERT INTO changeifilters(id, ifilter_id, status, lastchange) VALUES(null,4,1,9342348245);");

                //datos de user
                //execSQL("INSERT INTO users(id, name, email, position, status) VALUES(null,'Ing Luis Rosales','rosales@techno-world.com','Desarrollo de software',1);");
                //db.execSQL("INSERT INTO users(id, name, email, position, status) VALUES(null,'Luis Flores','flores@techno-world.com','Soporte Tecnico',1);");


                db.close();
            } else
                Toast.makeText(this, "db fue null :-(", Toast.LENGTH_LONG).show();
        }//try
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

}
