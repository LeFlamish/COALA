package com.example.smobileeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class DifficultyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_difficulty, container, false);

        Button btnBronze = view.findViewById(R.id.btnBronze);
        Button btnSilver = view.findViewById(R.id.btnSilver);
        Button btnGold = view.findViewById(R.id.btnGold);
        Button btnPlatinum = view.findViewById(R.id.btnPlatinum);

        btnBronze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("bronze");
            }
        });

        btnSilver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("silver");
            }
        });

        btnGold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("gold");
            }
        });

        btnPlatinum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("platinum");
            }
        });

        return view;
    }

    private void openRecommendListFragment(String difficulty) {
        RecommendListFragment fragment = RecommendListFragment.newInstance(difficulty);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
