/*
Talking Pages
Free Audiobook App
Created By: Soumya Chowdhury
            Makineedi Sai Harsh
            Nagani Vrudant Gopalbhai
            Mukul Bhambari
 */
package com.example.recycleview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.MemoryFile;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Adapter.MyAdapter;
import Model.ListItem;
public class MainActivity extends AppCompatActivity {

    public List<Song> songs = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    public List<ListItem> listItems= new ArrayList<>();
    private MyAdapter ad;
    private MenuItem t;
    private long backPressedTime;
    private Toast backToast;
    public static int k;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check1();
        check();
        k=0;
        fetchSongsFromWeb();

        recyclerView = findViewById(R.id.recycleViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        k=0;
    }

    public void check()
    {
        ConnectivityManager manager= (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork =manager.getActiveNetworkInfo();
        if(activeNetwork==null)
        {
            Intent next = new Intent(this,NoConnection.class);
            startActivity(next);
        }
        return;
    }


    private void fetchSongsFromWeb() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                InputStream inputStream =null;

                try{
                    URL url = new URL("http://2c723b33.ngrok.io/audio/getbook.php");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    int statusCode = urlConnection.getResponseCode();
                    if(statusCode == 200) {
                        inputStream = new BufferedInputStream((urlConnection.getInputStream()));
                        String response = convertInputStreamToString (inputStream);
                        //Log.i("Got Songs!",response);
                        parseIntoSongs(response);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(urlConnection != null)
                        urlConnection.disconnect();
                }
            }
        });
        thread.start();
    }
    private String convertInputStreamToString (InputStream inputStream) throws IOException {
        BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));

        String line="";
        String result="";

        while ((line=bufferedReader.readLine()) != null) {
            result += line;
        }
        if(inputStream != null)
            inputStream.close();
        return result;
    }
    private void parseIntoSongs (String data) {
        String[] dataArray = data.split("/");
        int i=0;
        for(i=0;i<dataArray.length;i++){
            String[] songArray = dataArray[i].split(",");
            String[] s= songArray[4].split("\\.");
            //Log.i("SSS",s[0]);
            Song song = new Song(songArray[0],songArray[1],songArray[2],songArray[3],s[0]);
            songs.add(song);
        }
        for(i=0; i< songs.size();i++){
            Log.i("Got Song",songs.get(i).getSeries());
            ListItem item = new ListItem(songs.get(i).getTitle(),songs.get(i).getAuthor(),songs.get(i).getSeries(),songs.get(i).getGenre());
            listItems.add(item);
        }

      populateListView();
    }

    private void populateListView()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter= new MyAdapter (MainActivity.this,listItems);
                ad = (MyAdapter) adapter;
                recyclerView.setAdapter(ad);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.example_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        t=menu.findItem(R.id.text);
        SearchView searchView = (SearchView) searchItem.getActionView();


            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    ad.getFilter().filter(newText);
                    return false;
                }
            });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            ad.itm(item.getTitle().toString().toLowerCase());
            t.setTitle(item.getTitle().toString());

            return super.onOptionsItemSelected(item);



    }

    @Override
    public void onBackPressed() {

        if(backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }


}
