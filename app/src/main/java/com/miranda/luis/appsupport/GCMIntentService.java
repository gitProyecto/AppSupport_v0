package com.miranda.luis.appsupport;

/**
 * Created by luis on 22/10/15.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;

import org.json.JSONObject;

public class GCMIntentService extends GCMBaseIntentService {

    public GCMIntentService() {
        super("853205055727");
    }

    @Override
    protected void onError(Context context, String errorId) {
        Log.d("GCMTest", "REGISTRATION: Error -> " + errorId);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {

        String msg =  intent.getExtras().getString("msg");



        try {
            mostrarNotificacion(context, msg);

        }catch (Exception ex){  Log.d("GCMTest", "Error: " + ex.getMessage());     }
    }

    @Override
    protected void onRegistered(Context context, String regId) {
        Log.d("GCMTest", "REGISTRATION: Registrado OK.");


    }

    @Override
    protected void onUnregistered(Context context, String regId) {
        Log.d("GCMTest", "REGISTRATION: Desregistrado OK.");
    }




    private void mostrarNotificacion(Context context, String message){

        String uuid="", command="", payload="";
        int indi=0;

        String id="", status="" , lastchange="";

        try {

            JSONObject json = new JSONObject(message);
            JSONObject jsonI = json.getJSONObject("iFilter");

            id = jsonI.getString("id");
            status = jsonI.getString("status");
            lastchange = jsonI.getString("change");

             insertData(id,status,lastchange);


            //JSONObject json = new JSONObject(message);
            //JSONObject jsonM = json.getJSONObject("Message");

            //uuid = jsonM.getString("UUID");
            //command = jsonM.getString("command");

            //if(command.equals("tic")){}

        }catch (Exception ex){}


        Intent notificationIntent = new Intent(context, Main.class);
        notificationIntent.putExtra("alerta", indi);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.notice)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.notice))
                .setTicker("Nueva Alerta")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Alerta # " + uuid)
                .setContentText(message);



        long[] pattern = {500,500,500,500,500,500,500,500,500};
        builder.setVibrate(pattern);

        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tono);

        builder.setSound(sound);

        builder.setLights(Color.RED, 3000, 3000);


        Notification n = builder.build();



        //n.defaults |= Notification.DEFAULT_SOUND;
        nm.notify(1, n);

    }


    public void insertData(String id, String status, String lastchange){

        helpBD aBD;   SQLiteDatabase db=null;

        try{
            aBD=new helpBD(this,"data.db",null,1);
            db = aBD.getWritableDatabase();
            if (db!=null) {

                db.execSQL("INSERT INTO changeifilters(id, ifilter_id, status, lastchange) VALUES(null," + id + "," + status + "," + lastchange + ");");

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