package com.example.smobileeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private Context context;
    private TextView tvRecommendation;  // Recommendation TextView

    private View view; // 프래그먼트 뷰 변수 추가

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private static final String[] DIFFICULTY_LEVELS = {
            "PⅠ", "PⅡ", "PⅢ", "PⅣ", "PⅤ",
            "GⅠ", "GⅡ", "GⅢ", "GⅣ", "GⅤ",
            "SⅠ", "SⅡ", "SⅢ", "SⅣ", "SⅤ",
            "BⅠ", "BⅡ", "BⅢ", "BⅣ", "BⅤ"
    };

    private static final Map<String, String> DIFFICULTY_MAPPING = new HashMap<>();

    static {
        DIFFICULTY_MAPPING.put("플래티넘Ⅰ", "PⅠ");
        DIFFICULTY_MAPPING.put("플래티넘Ⅱ", "PⅡ");
        DIFFICULTY_MAPPING.put("플래티넘Ⅲ", "PⅢ");
        DIFFICULTY_MAPPING.put("플래티넘Ⅳ", "PⅣ");
        DIFFICULTY_MAPPING.put("플래티넘Ⅴ", "PⅤ");
        DIFFICULTY_MAPPING.put("골드Ⅰ", "GⅠ");
        DIFFICULTY_MAPPING.put("골드Ⅱ", "GⅡ");
        DIFFICULTY_MAPPING.put("골드Ⅲ", "GⅢ");
        DIFFICULTY_MAPPING.put("골드Ⅳ", "GⅣ");
        DIFFICULTY_MAPPING.put("골드Ⅴ", "GⅤ");
        DIFFICULTY_MAPPING.put("실버Ⅰ", "SⅠ");
        DIFFICULTY_MAPPING.put("실버Ⅱ", "SⅡ");
        DIFFICULTY_MAPPING.put("실버Ⅲ", "SⅢ");
        DIFFICULTY_MAPPING.put("실버Ⅳ", "SⅣ");
        DIFFICULTY_MAPPING.put("실버Ⅴ", "SⅤ");
        DIFFICULTY_MAPPING.put("브론즈Ⅰ", "BⅠ");
        DIFFICULTY_MAPPING.put("브론즈Ⅱ", "BⅡ");
        DIFFICULTY_MAPPING.put("브론즈Ⅲ", "BⅢ");
        DIFFICULTY_MAPPING.put("브론즈Ⅳ", "BⅣ");
        DIFFICULTY_MAPPING.put("브론즈Ⅴ", "BⅤ");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pie_chart, container, false); // view 객체 초기화

        pieChart = view.findViewById(R.id.pieChart); // Ensure this ID matches your layout
        tvRecommendation = view.findViewById(R.id.tv_recommendation); // Initialize recommendation TextView

        Intent intent = getActivity().getIntent();
        userIdToken = intent.getStringExtra("userIdToken");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Problems").child(userIdToken);

        pieChart.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams layoutParams = pieChart.getLayoutParams();
                layoutParams.width = 1000;  // 원하는 너비 (픽셀 단위)
                layoutParams.height = 1000;  // 원하는 높이 (픽셀 단위)
                pieChart.setLayoutParams(layoutParams);
            }
        });

        fetchDataFromFirebase(); // 데이터 가져오기 메서드 호출

        return view;
    }

    private void fetchDataFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    Log.d(TAG, "Difficulty Count: " + difficultyCount.toString()); // 로그 추가
                    updatePieChart(difficultyCount); // 데이터를 받은 후 차트 업데이트
                    updateTable(difficultyCount); // 데이터를 받은 후 표 업데이트
                    updateRecommendation(difficultyCount); // 데이터를 받은 후 추천 업데이트
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
                colors.add(getColorForDifficulty(requireContext(), difficulty));
            }
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "난이도 목록");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setText("난이도 별 문제 푼 수");
        pieChart.invalidate();
    }

    private int getColorForDifficulty(Context context, String difficulty) {
        if (context != null) {
            switch (difficulty) {
                case "PⅠ":
                    return ContextCompat.getColor(context, R.color.platinum1);
                case "GⅠ":
                    return ContextCompat.getColor(context, R.color.gold1);
                case "SⅠ":
                    return ContextCompat.getColor(context, R.color.silver1);
                case "BⅠ":
                    return ContextCompat.getColor(context, R.color.bronze1);
                case "PⅡ":
                    return ContextCompat.getColor(context, R.color.platinum2);
                case "GⅡ":
                    return ContextCompat.getColor(context, R.color.gold2);
                case "SⅡ":
                    return ContextCompat.getColor(context, R.color.silver2);
                case "BⅡ":
                    return ContextCompat.getColor(context, R.color.bronze2);
                case "PⅢ":
                    return ContextCompat.getColor(context, R.color.platinum3);
                case "GⅢ":
                    return ContextCompat.getColor(context, R.color.gold3);
                case "SⅢ":
                    return ContextCompat.getColor(context, R.color.silver3);
                case "BⅢ":
                    return ContextCompat.getColor(context, R.color.bronze3);
                case "PⅣ":
                    return ContextCompat.getColor(context, R.color.platinum4);
                case "GⅣ":
                    return ContextCompat.getColor(context, R.color.gold4);
                case "SⅣ":
                    return ContextCompat.getColor(context, R.color.silver4);
                case "BⅣ":
                    return ContextCompat.getColor(context, R.color.bronze4);
                case "PⅤ":
                    return ContextCompat.getColor(context, R.color.platinum5);
                case "GⅤ":
                    return ContextCompat.getColor(context, R.color.gold5);
                case "SⅤ":
                    return ContextCompat.getColor(context, R.color.silver5);
                case "BⅤ":
                    return ContextCompat.getColor(context, R.color.bronze5);
                default:
                    return ContextCompat.getColor(context, R.color.default_color);
            }
        } else {
            // Log a message or handle the null context case appropriately
            Log.e(TAG, "Context is null");
            // Return a default color or handle the null context case appropriately
            return R.color.default_color; // For example, return black color as default
        }
    }

    private static final Map<String, Integer> TEXT_VIEW_IDS = new HashMap<>();

    static {
        TEXT_VIEW_IDS.put("PⅠ", R.id.platinum1);
        TEXT_VIEW_IDS.put("PⅡ", R.id.platinum2);
        TEXT_VIEW_IDS.put("PⅢ", R.id.platinum3);
        TEXT_VIEW_IDS.put("PⅣ", R.id.platinum4);
        TEXT_VIEW_IDS.put("PⅤ", R.id.platinum5);
        TEXT_VIEW_IDS.put("GⅠ", R.id.gold1);
        TEXT_VIEW_IDS.put("GⅡ", R.id.gold2);
        TEXT_VIEW_IDS.put("GⅢ", R.id.gold3);
        TEXT_VIEW_IDS.put("GⅣ", R.id.gold4);
        TEXT_VIEW_IDS.put("GⅤ", R.id.gold5);
        TEXT_VIEW_IDS.put("SⅠ", R.id.silver1);
        TEXT_VIEW_IDS.put("SⅡ", R.id.silver2);
        TEXT_VIEW_IDS.put("SⅢ", R.id.silver3);
        TEXT_VIEW_IDS.put("SⅣ", R.id.silver4);
        TEXT_VIEW_IDS.put("SⅤ", R.id.silver5);
        TEXT_VIEW_IDS.put("BⅠ", R.id.bronze1);
        TEXT_VIEW_IDS.put("BⅡ", R.id.bronze2);
        TEXT_VIEW_IDS.put("BⅢ", R.id.bronze3);
        TEXT_VIEW_IDS.put("BⅣ", R.id.bronze4);
        TEXT_VIEW_IDS.put("BⅤ", R.id.bronze5);
    }

    private void updateTable(Map<String, Integer> difficultyCount) {
        for (Map.Entry<String, Integer> entry : difficultyCount.entrySet()) {
            String level = entry.getKey();
            int count = entry.getValue();

            Integer textViewId = TEXT_VIEW_IDS.get(level);
            if (textViewId != null) {
                TextView textView = view.findViewById(textViewId);
                textView.setText(String.valueOf(count));
            }
        }
    }

    private void updateRecommendation(Map<String, Integer> difficultyCount) {
        String recommendation = getRecommendation(difficultyCount);
        tvRecommendation.setText(recommendation);
    }

    private String getRecommendation(Map<String, Integer> difficultyCount) {
        int bronzeCount = difficultyCount.get("BⅠ") + difficultyCount.get("BⅡ") + difficultyCount.get("BⅢ") +
                difficultyCount.get("BⅣ") + difficultyCount.get("BⅤ");
        int silverCount = difficultyCount.get("SⅠ") + difficultyCount.get("SⅡ") + difficultyCount.get("SⅢ") +
                difficultyCount.get("SⅣ") + difficultyCount.get("SⅤ");
        int goldCount = difficultyCount.get("GⅠ") + difficultyCount.get("GⅡ") + difficultyCount.get("GⅢ") +
                difficultyCount.get("GⅣ") + difficultyCount.get("GⅤ");
        int platinumCount = difficultyCount.get("PⅠ") + difficultyCount.get("PⅡ") + difficultyCount.get("PⅢ") +
                difficultyCount.get("PⅣ") + difficultyCount.get("PⅤ");

        if (bronzeCount < 20) {
            return "브론즈 문제를 풀어보세요!";
        } else if (silverCount < 20) {
            return "실버 문제를 풀어보세요!";
        } else if (goldCount < 20) {
            return "골드 문제를 풀어보세요!";
        } else {
            return "플래티넘 문제를 풀어보세요!";
        }
    }
}