package com.teste.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;

import com.teste.myapplication.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
{
    ActivityMainBinding binding;
    ArrayList<String> lista_names;
    Handler mainhandler = new Handler();
    ProgressDialog progressDialog;
    ArrayAdapter<String> listadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeList();
        binding.Acionar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new fetchData().start();
            }
        });


    }

    private void initializeList()
    {
        lista_names = new ArrayList<>();
        listadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lista_names);
        binding.listview.setAdapter(listadapter);
    }

    private class fetchData extends Thread
    {

        String data = "";
        StringBuilder sb = new StringBuilder();

        @Override
        public void run()
        {
            mainhandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Buscando...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });

            try
            {
                URL url = new URL("http://192.168.1.14/FetchData/selectNames.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

               //propriedades da conexão
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);


                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String response;
                    response = bufferedReader.readLine();
                    System.out.println("SAIDA RESPONSE");
                    System.out.println(response);

                    //em vez de while, use if
                    if (response != null)
                    {
                        sb.append(response);
                    }
                }

                String data = sb.toString();
                System.out.println("SAIDA DATA");
                System.out.println(data);

                if(!(data.isEmpty()))
                {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsNames = jsonObject.getJSONArray("Names");
                    lista_names.clear();
                    for (int i = 0; i < jsNames.length(); i++)
                    {
                        JSONObject namesJSONObject = jsNames.getJSONObject(i);
                        String name = namesJSONObject.getString("Name");
                        lista_names.add(name);
                    }
                }

            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            mainhandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                    listadapter.notifyDataSetChanged();
                }
            });

        }
    }




}