package com.example.smobileeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PieChartFragment extends Fragment {
    private static final String TAG = "PieChartFragment";
    private PieChart pieChart;
    private DatabaseReference databaseReference;
    private String userIdToken;

    private static final String[] DIFFICULTY_LEVELS = {
            "PⅠ", "PⅡ", "PⅢ", "PⅣ", "PⅤ",
            "GⅠ", "GⅡ", "GⅢ", "GⅣ", "GⅤ",
            "SⅠ", "SⅡ", "SⅢ", "SⅣ", "SⅤ",
            "BⅠ", "BⅡ", "BⅢ", "BⅣ", "BⅤ"
    };

    private static final Map<String, String> DIFFICULTY_MAPPING = new HashMap<>();
    static {
        DIFFICULTY_MAPPING.put("플래티넘Ⅰ", "PⅠ");
        DIFFICULTY_MAPPING.put("골드Ⅰ", "GⅠ");
        DIFFICULTY_MAPPING.put("실버Ⅰ", "SⅠ");
        DIFFICULTY_MAPPING.put("브론즈Ⅰ", "BⅠ");
        DIFFICULTY_MAPPING.put("플래티넘Ⅱ", "PⅡ");
        DIFFICULTY_MAPPING.put("골드Ⅱ", "GⅡ");
        DIFFICULTY_MAPPING.put("실버Ⅱ", "SⅡ");
        DIFFICULTY_MAPPING.put("브론즈Ⅱ", "BⅡ");
        DIFFICULTY_MAPPING.put("플래티넘Ⅲ", "PⅢ");
        DIFFICULTY_MAPPING.put("골드Ⅲ", "GⅢ");
        DIFFICULTY_MAPPING.put("실버Ⅲ", "SⅢ");
        DIFFICULTY_MAPPING.put("브론즈Ⅲ", "BⅢ");
        DIFFICULTY_MAPPING.put("플래티넘Ⅳ", "PⅣ");
        DIFFICULTY_MAPPING.put("골드Ⅳ", "GⅣ");
        DIFFICULTY_MAPPING.put("실버Ⅳ", "SⅣ");
        DIFFICULTY_MAPPING.put("브론즈Ⅳ", "BⅣ");
        DIFFICULTY_MAPPING.put("플래티넘Ⅴ", "PⅤ");
        DIFFICULTY_MAPPING.put("골드Ⅴ", "GⅤ");
        DIFFICULTY_MAPPING.put("실버Ⅴ", "SⅤ");
        DIFFICULTY_MAPPING.put("브론즈Ⅴ", "BⅤ");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        pieChart = view.findViewById(R.id.pieChart);
        Intent intent = getActivity().getIntent();
        userIdToken = intent.getStringExtra("userIdToken");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Problems").child(userIdToken);

        fetchDataFromFirebase();

        return view;
    }

    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Integer> difficultyCount = initializeDifficultyCount();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String difficulty = snapshot.child("difficulty").getValue(String.class);
                        if (difficulty != null) {
                            String shortenedDifficulty = DIFFICULTY_MAPPING.getOrDefault(difficulty, "기타");
                            difficultyCount.put(shortenedDifficulty, difficultyCount.get(shortenedDifficulty) + 1);
                        }
                    }
                    updatePieChart(difficultyCount);
                } else {
                    Log.w(TAG, "No data found for user: " + userIdToken);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage(), databaseError.toException());
            }
        });
    }

    private Map<String, Integer> initializeDifficultyCount() {
        Map<String, Integer> difficultyCount = new HashMap<>();
        for (String level : DIFFICULTY_LEVELS) {
            difficultyCount.put(level, 0);
        }
        difficultyCount.put("기타", 0);
        return difficultyCount;
    }

    private void updatePieChart(Map<String, Integer> difficultyCount) {
        List<PieEntry> pieEntries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (String difficulty : DIFFICULTY_LEVELS) {
            if (difficultyCount.get(difficulty) > 0) {
                pieEntries.add(new PieEntry(difficultyCount.get(difficulty), difficulty));
                colors.add(getColorForDifficulty(difficulty));
            }
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "Difficulty Distribution");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setText("Distribution of Problems by Difficulty");
        pieChart.invalidate();
    }

    private int getColorForDifficulty(String difficulty) {
        switch (difficulty) {
            case "PⅠ": return ContextCompat.getColor(getActivity(), R.color.platinum1);
            case "GⅠ": return ContextCompat.getColor(getActivity(), R.color.gold1);
            case "SⅠ": return ContextCompat.getColor(getActivity(), R.color.silver1);
            case "BⅠ": return ContextCompat.getColor(getActivity(), R.color.bronze1);
            case "PⅡ": return ContextCompat.getColor(getActivity(), R.color.platinum2);
            case "GⅡ": return ContextCompat.getColor(getActivity(), R.color.gold2);
            case "SⅡ": return ContextCompat.getColor(getActivity(), R.color.silver2);
            case "BⅡ": return ContextCompat.getColor(getActivity(), R.color.bronze2);
            case "PⅢ": return ContextCompat.getColor(getActivity(), R.color.platinum3);
            case "GⅢ": return ContextCompat.getColor(getActivity(), R.color.gold3);
            case "SⅢ": return ContextCompat.getColor(getActivity(), R.color.silver3);
            case "BⅢ": return ContextCompat.getColor(getActivity(), R.color.bronze3);
            case "PⅣ": return ContextCompat.getColor(getActivity(), R.color.platinum4);
            case "GⅣ": return ContextCompat.getColor(getActivity(), R.color.gold4);
            case "SⅣ": return ContextCompat.getColor(getActivity(), R.color.silver4);
            case "BⅣ": return ContextCompat.getColor(getActivity(), R.color.bronze4);
            case "PⅤ": return ContextCompat.getColor(getActivity(), R.color.platinum5);
            case "GⅤ": return ContextCompat.getColor(getActivity(), R.color.gold5);
            case "SⅤ": return ContextCompat.getColor(getActivity(), R.color.silver5);
            case "BⅤ": return ContextCompat.getColor(getActivity(), R.color.bronze5);
            default: return ContextCompat.getColor(getActivity(), R.color.default_color);
        }
    }
}
