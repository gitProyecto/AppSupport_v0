package com.miranda.luis.appsupport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.app.Activity;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import org.json.JSONObject;


public class Main extends Activity {
    private WebView myWebView;
    private String TAG = "Main";
    private String LOCAL_FILE = "file:///android_asset/login.html";
    private String LOCAL_FILE1 = "file:///android_asset/menu.html";


    boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GCMRegistrar.checkDevice(Main.this);
        GCMRegistrar.checkManifest(Main.this);
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

}