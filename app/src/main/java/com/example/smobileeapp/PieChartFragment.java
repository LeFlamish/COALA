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
    private TextView tvAverageDifficulty; // Average difficulty TextView

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

    private static final Map<String, Integer> DIFFICULTY_SCORES = new HashMap<>();

    static {
        DIFFICULTY_SCORES.put("PⅠ", 20);
        DIFFICULTY_SCORES.put("PⅡ", 19);
        DIFFICULTY_SCORES.put("PⅢ", 18);
        DIFFICULTY_SCORES.put("PⅣ", 17);
        DIFFICULTY_SCORES.put("PⅤ", 16);
        DIFFICULTY_SCORES.put("GⅠ", 15);
        DIFFICULTY_SCORES.put("GⅡ", 14);
        DIFFICULTY_SCORES.put("GⅢ", 13);
        DIFFICULTY_SCORES.put("GⅣ", 12);
        DIFFICULTY_SCORES.put("GⅤ", 11);
        DIFFICULTY_SCORES.put("SⅠ", 10);
        DIFFICULTY_SCORES.put("SⅡ", 9);
        DIFFICULTY_SCORES.put("SⅢ", 8);
        DIFFICULTY_SCORES.put("SⅣ", 7);
        DIFFICULTY_SCORES.put("SⅤ", 6);
        DIFFICULTY_SCORES.put("BⅠ", 5);
        DIFFICULTY_SCORES.put("BⅡ", 4);
        DIFFICULTY_SCORES.put("BⅢ", 3);
        DIFFICULTY_SCORES.put("BⅣ", 2);
        DIFFICULTY_SCORES.put("BⅤ", 1);
    }

    private static final Map<Integer, String> SCORE_TO_DIFFICULTY = new HashMap<>();

    static {
        for (Map.Entry<String, Integer> entry : DIFFICULTY_SCORES.entrySet()) {
            SCORE_TO_DIFFICULTY.put(entry.getValue(), entry.getKey());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pie_chart, container, false); // view 객체 초기화

        pieChart = view.findViewById(R.id.pieChart); // Ensure this ID matches your layout
        tvRecommendation = view.findViewById(R.id.tv_recommendation); // Initialize recommendation TextView
        tvAverageDifficulty = view.findViewById(R.id.tv_average_difficulty); // Initialize average difficulty TextView

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
                    int totalScore = 0;
                    int problemCount = 0;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String difficulty = snapshot.child("difficulty").getValue(String.class);
                        if (difficulty != null) {
                            String shortenedDifficulty = DIFFICULTY_MAPPING.getOrDefault(difficulty, "기타");
                            difficultyCount.put(shortenedDifficulty, difficultyCount.get(shortenedDifficulty) + 1);

                            // 난이도 점수를 합산
                            Integer score = DIFFICULTY_SCORES.get(shortenedDifficulty);
                            if (score != null) {
                                totalScore += score;
                                problemCount++;
                            }
                        }
                    }
                    Log.d(TAG, "Difficulty Count: " + difficultyCount.toString()); // 로그 추가
                    updatePieChart(difficultyCount); // 데이터를 받은 후 차트 업데이트
                    updateTable(difficultyCount); // 데이터를 받은 후 표 업데이트
                    updateRecommendation(difficultyCount, totalScore, problemCount); // 데이터를 받은 후 추천 업데이트

                    // 평균 난이도 계산 및 업데이트
                    if (problemCount > 0) {
                        double averageDifficulty = (double) totalScore / problemCount;
                        int roundedDifficulty = (int) Math.round(averageDifficulty);
                        String difficultyString = SCORE_TO_DIFFICULTY.get(roundedDifficulty);

                        if (difficultyString != null) {
                            switch (difficultyString) {
                                case "PⅠ":
                                    difficultyString = "플래티넘Ⅰ";
                                    break;
                                case "PⅡ":
                                    difficultyString = "플래티넘Ⅱ";
                                    break;
                                case "PⅢ":
                                    difficultyString = "플래티넘Ⅲ";
                                    break;
                                case "PⅣ":
                                    difficultyString = "플래티넘Ⅳ";
                                    break;
                                case "PⅤ":
                                    difficultyString = "플래티넘Ⅴ";
                                    break;
                                case "GⅠ":
                                    difficultyString = "골드Ⅰ";
                                    break;
                                case "GⅡ":
                                    difficultyString = "골드Ⅱ";
                                    break;
                                case "GⅢ":
                                    difficultyString = "골드Ⅲ";
                                    break;
                                case "GⅣ":
                                    difficultyString = "골드Ⅳ";
                                    break;
                                case "GⅤ":
                                    difficultyString = "골드Ⅴ";
                                    break;
                                case "SⅠ":
                                    difficultyString = "실버Ⅰ";
                                    break;
                                case "SⅡ":
                                    difficultyString = "실버Ⅱ";
                                    break;
                                case "SⅢ":
                                    difficultyString = "실버Ⅲ";
                                    break;
                                case "SⅣ":
                                    difficultyString = "실버Ⅳ";
                                    break;
                                case "SⅤ":
                                    difficultyString = "실버Ⅴ";
                                    break;
                                case "BⅠ":
                                    difficultyString = "브론즈Ⅰ";
                                    break;
                                case "BⅡ":
                                    difficultyString = "브론즈Ⅱ";
                                    break;
                                case "BⅢ":
                                    difficultyString = "브론즈Ⅲ";
                                    break;
                                case "BⅣ":
                                    difficultyString = "브론즈Ⅳ";
                                    break;
                                case "BⅤ":
                                    difficultyString = "브론즈Ⅴ";
                                    break;
                            }
                            tvAverageDifficulty.setText("평균 난이도 : " + difficultyString);
                        }
                    }
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

    private void updateRecommendation(Map<String, Integer> difficultyCount, int totalScore, int problemCount) {
        String recommendation = getRecommendation(difficultyCount, totalScore, problemCount);
        tvRecommendation.setText(recommendation);
    }

    private String getRecommendation(Map<String, Integer> difficultyCount, int totalScore, int problemCount) {
        if (problemCount == 0) {
            return "추천 문제 난이도 : 없음 (풀이된 문제가 없습니다)";
        }

        double averageScore = (double) totalScore / problemCount;
        String recommendationDifficulty;

        if (averageScore >= 19) {
            recommendationDifficulty = "플래티넘Ⅰ";
        } else if (averageScore >= 18) {
            recommendationDifficulty = "플래티넘Ⅱ";
        } else if (averageScore >= 17) {
            recommendationDifficulty = "플래티넘Ⅲ";
        } else if (averageScore >= 16) {
            recommendationDifficulty = "플래티넘Ⅳ";
        } else if (averageScore >= 15) {
            recommendationDifficulty = "플래티넘Ⅴ";
        } else if (averageScore >= 14) {
            recommendationDifficulty = "골드Ⅰ";
        } else if (averageScore >= 13) {
            recommendationDifficulty = "골드Ⅱ";
        } else if (averageScore >= 12) {
            recommendationDifficulty = "골드Ⅲ";
        } else if (averageScore >= 11) {
            recommendationDifficulty = "골드Ⅳ";
        } else if (averageScore >= 10) {
            recommendationDifficulty = "골드Ⅴ";
        } else if (averageScore >= 9) {
            recommendationDifficulty = "실버Ⅰ";
        } else if (averageScore >= 8) {
            recommendationDifficulty = "실버Ⅱ";
        } else if (averageScore >= 7) {
            recommendationDifficulty = "실버Ⅲ";
        } else if (averageScore >= 6) {
            recommendationDifficulty = "실버Ⅳ";
        } else if (averageScore >= 5) {
            recommendationDifficulty = "실버Ⅴ";
        } else if (averageScore >= 4) {
            recommendationDifficulty = "브론즈Ⅰ";
        } else if (averageScore >= 3) {
            recommendationDifficulty = "브론즈Ⅱ";
        } else if (averageScore >= 2) {
            recommendationDifficulty = "브론즈Ⅲ";
        } else if (averageScore >= 1) {
            recommendationDifficulty = "브론즈Ⅳ";
        } else {
            recommendationDifficulty = "브론즈Ⅴ";
        }

        return "추천 문제 난이도 : " + recommendationDifficulty;
    }
}
