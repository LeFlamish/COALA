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

public class CompanyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_company, container, false);

        Button btnSamsung = view.findViewById(R.id.btnSamsung);
        Button btnKakao = view.findViewById(R.id.btnKakao);
        Button btnNaver = view.findViewById(R.id.btnNaver);
        Button btnLG = view.findViewById(R.id.btnLG);

        btnSamsung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("samsung");
            }
        });

        btnKakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("kakao");
            }
        });

        btnNaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("naver");
            }
        });

        btnLG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("lg");
            }
        });

        return view;
    }

    private void openRecommendListFragment(String company) {
        RecommendListFragment fragment = RecommendListFragment.newInstance(company);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
}
