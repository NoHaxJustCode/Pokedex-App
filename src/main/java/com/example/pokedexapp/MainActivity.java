package com.example.pokedexapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.view.View;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import android.widget.ImageView;

import android.widget.NumberPicker;

import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends AppCompatActivity{
    Button addButton;
    Button subtractButton;
    Button moreInfo;
    WebView webView;
    String data="";
    int pokedexNumber=0;
    String finalPokemonName;
    TextView textView;
    ImageView imageView;
    ImageView type1image;
    ImageView type2image;
    Boolean isNormal=true;
    String type1;
    String type2;
    NumberPicker numberPicker;
    private static final int KEY_POKEDEX_NUM = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        addButton = findViewById(R.id.addition);
        subtractButton = findViewById(R.id.subtract);
        imageView = findViewById(R.id.imageView);
        type1image = findViewById(R.id.imageView2);
        type2image = findViewById(R.id.imageView3);
        numberPicker = findViewById(R.id.numbersroller);
        webView = findViewById(R.id.webView);
        webView.setBackgroundColor(Color.TRANSPARENT);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        moreInfo = findViewById(R.id.moreInfo);
        if(savedInstanceState!=null) {
            int placehoilder = savedInstanceState.getInt(String.valueOf(KEY_POKEDEX_NUM));
            pokedexNumber = placehoilder;
            numberPicker.setValue(pokedexNumber);
            try {
                new getPokemonInfo((TextView) findViewById(R.id.textView)).execute(new URL("https://pokeapi.co/api/v2/pokemon/"+pokedexNumber));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(pokedexNumber<889)
                        pokedexNumber+=10;
                    numberPicker.setValue(pokedexNumber);
                    new getPokemonInfo((TextView) findViewById(R.id.textView)).execute(new URL("https://pokeapi.co/api/v2/pokemon/"+pokedexNumber));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        subtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(pokedexNumber>11)
                        pokedexNumber-=10;
                    numberPicker.setValue(pokedexNumber);
                    new getPokemonInfo((TextView) findViewById(R.id.textView)).execute(new URL("https://pokeapi.co/api/v2/pokemon/"+pokedexNumber));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNormal) {
                    new DownloadImageTask().execute("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/"+pokedexNumber+".png");
                    isNormal=false;
                }
                else
                {
                    new DownloadImageTask().execute("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"+pokedexNumber+".png");
                    isNormal=true;
                }
            }
        });
        numberPicker.setMaxValue(898);//total number of pokemon in the api
        numberPicker.setMinValue(0);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                try {
                    pokedexNumber=newVal;
                    if(pokedexNumber!=0) {
                            new getPokemonInfo((TextView) findViewById(R.id.textView)).execute(new URL("https://pokeapi.co/api/v2/pokemon/"+pokedexNumber));
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButton.setVisibility(View.GONE);
                subtractButton.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.setWebViewClient(new WebViewClient());
                String url = "https://api.pokemon.com/us/pokedex/"+pokedexNumber;
                webView.loadUrl(url);
                moreInfo.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(String.valueOf(KEY_POKEDEX_NUM), pokedexNumber);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        savedInstanceState.putInt(String.valueOf(KEY_POKEDEX_NUM), pokedexNumber);
        super.onRestoreInstanceState(savedInstanceState);
    }

    private class getPokemonInfo extends AsyncTask<URL, Void, String>
    {
        TextView t;
        public getPokemonInfo(TextView t)
        {
            this.t=t;
        }
        @Override
        protected String doInBackground(URL... urls) {
            String pokemonName=null;
            data="";
            try
            {
                URLConnection connection = urls[0].openConnection();
                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String input;
                while ((input = reader.readLine()) != null)
                    data += input;
                JSONObject jsonObject = new JSONObject(data);
                JSONArray nameArray = jsonObject.getJSONArray("forms");
                JSONObject name = nameArray.getJSONObject(0);
                pokemonName = name.getString("name");
                if(!pokemonName.contains("-"))
                    pokemonName = pokemonName.substring(0, 1).toUpperCase() + pokemonName.substring(1);
                JSONArray types = jsonObject.getJSONArray("types");
                type1 = types.getJSONObject(0).getJSONObject("type").getString("name");
                try {
                    type2 = types.getJSONObject(1).getJSONObject("type").getString("name");
                }catch(Exception e){ type2 = null;}
            } catch (IOException |JSONException e) {
                e.printStackTrace();
            }
            return pokemonName;
        }
        protected void onPostExecute(String pokemonName) {
            t.setText(pokemonName);
            finalPokemonName=pokemonName;
            pokemonTyping();
            new DownloadImageTask().execute("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"+pokedexNumber+".png");
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) { }
            return bitmap;
        }
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
    public void pokemonTyping()
    {
        switch(type1)
        {
            case "fire":
                type1image.setImageResource(R.drawable.fire);
                break;
            case "ice":
                type1image.setImageResource(R.drawable.ice);
                break;
            case "water":
                type1image.setImageResource(R.drawable.water);
                break;
            case "electric":
                type1image.setImageResource(R.drawable.electric);
                break;
            case "grass":
                type1image.setImageResource(R.drawable.grass);
                break;
            case "ground":
                type1image.setImageResource(R.drawable.ground);
                break;
            case "dragon":
                type1image.setImageResource(R.drawable.dragon);
                break;
            case "fairy":
                type1image.setImageResource(R.drawable.fairy);
                break;
            case "psychic":
                type1image.setImageResource(R.drawable.psychic);
                break;
            case "poison":
                type1image.setImageResource(R.drawable.poison);
                break;
            case "ghost":
                type1image.setImageResource(R.drawable.ghost);
                break;
            case "flying":
                type1image.setImageResource(R.drawable.flying);
                break;
            case "fighting":
                type1image.setImageResource(R.drawable.fighting);
                break;
            case "normal":
                type1image.setImageResource(R.drawable.normal);
                break;
            case "steel":
                type1image.setImageResource(R.drawable.steel);
                break;
            case "rock":
                type1image.setImageResource(R.drawable.rock);
                break;
            case "bug":
                type1image.setImageResource(R.drawable.bug);
                break;
            case "dark":
                type1image.setImageResource(R.drawable.dark);
                break;
        }
        if(type2!=null)
        switch(type2)
        {
            case "fire":
                type2image.setImageResource(R.drawable.fire);
                break;
            case "ice":
                type2image.setImageResource(R.drawable.ice);
                break;
            case "water":
                type2image.setImageResource(R.drawable.water);
                break;
            case "electric":
                type2image.setImageResource(R.drawable.electric);
                break;
            case "grass":
                type2image.setImageResource(R.drawable.grass);
                break;
            case "ground":
                type2image.setImageResource(R.drawable.ground);
                break;
            case "dragon":
                type2image.setImageResource(R.drawable.dragon);
                break;
            case "fairy":
                type2image.setImageResource(R.drawable.fairy);
                break;
            case "psychic":
                type2image.setImageResource(R.drawable.psychic);
                break;
            case "poison":
                type2image.setImageResource(R.drawable.poison);
                break;
            case "ghost":
                type2image.setImageResource(R.drawable.ghost);
                break;
            case "flying":
                type2image.setImageResource(R.drawable.flying);
                break;
            case "fighting":
                type2image.setImageResource(R.drawable.fighting);
                break;
            case "normal":
                type2image.setImageResource(R.drawable.normal);
                break;
            case "steel":
                type2image.setImageResource(R.drawable.steel);
                break;
            case "rock":
                type2image.setImageResource(R.drawable.rock);
                break;
            case "bug":
                type1image.setImageResource(R.drawable.bug);
                break;
            case "dark":
                type1image.setImageResource(R.drawable.dark);
                break;
            default:
                type2image.setImageResource(android.R.color.transparent);
                break;
        }
        else
            type2image.setImageResource(android.R.color.transparent);
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        }
        webView.setVisibility(View.GONE);
        addButton.setVisibility(View.VISIBLE);
        subtractButton.setVisibility(View.VISIBLE);
        moreInfo.setVisibility(View.VISIBLE);
    }
}