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

public class AlgorithmFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_algorithm, container, false);

        Button btnBFS = view.findViewById(R.id.btnBFS);
        Button btnDFS = view.findViewById(R.id.btnDFS);
        Button btnDP = view.findViewById(R.id.btnDP);
        Button btnGreedy = view.findViewById(R.id.btnGreedy);

        btnBFS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("BFS");
            }
        });

        btnDFS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("DFS");
            }
        });

        btnDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("DP");
            }
        });

        btnGreedy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("Greedy");
            }
        });

        return view;
    }

    private void openRecommendListFragment(String algorithm) {
        RecommendListFragment fragment = RecommendListFragment.newInstance(algorithm);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
}
