package com.miranda.luis.appsupport;

/**
 * Created by luis on 20/10/15.
 */
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class httpHandler {

    public String post(String posturl, String datos[]){

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(posturl);

            if(datos != null) {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("user", datos[0]));
                params.add(new BasicNameValuePair("pass", datos[1]));
                params.add(new BasicNameValuePair("regId", datos[2]));
                httppost.setEntity(new UrlEncodedFormEntity(params));

            }

            HttpResponse resp = httpclient.execute(httppost);

            HttpEntity ent = resp.getEntity();
            String text = EntityUtils.toString(ent);
            return text;
        }

        catch(Exception e) { return "error";}

    }

}

