package com.example.smobileeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class DifficultyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_difficulty, container, false);

        // 각 CardView 가져오기
        CardView cardBronze = view.findViewById(R.id.cardBronze);
        CardView cardSilver = view.findViewById(R.id.cardSilver);
        CardView cardGold = view.findViewById(R.id.cardGold);
        CardView cardPlatinum = view.findViewById(R.id.cardPlatinum);
        CardView cardDiamond = view.findViewById(R.id.cardDiamond);

        // 각 CardView에 클릭 리스너 설정
        cardBronze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("bronze");
            }
        });

        cardSilver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("silver");
            }
        });

        cardGold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("gold");
            }
        });

        cardPlatinum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("platinum");
            }
        });
        cardDiamond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecommendListFragment("diamond");
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

    // CardView 클릭 이벤트 처리 메서드

}
