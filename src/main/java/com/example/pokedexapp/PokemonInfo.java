package com.example.pokedexapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PokemonInfo extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pokemon_info, container, false);
        ImageView pokemonImage = v.findViewById(R.id.fragment_imageView);
        TextView textView = v.findViewById(R.id.textView3);
        textView.setText(getArguments().getString("Pokemon Name"));
        return v;
    }

}