package com.example.smobileeapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RadarChartFragment extends Fragment {

    private RadarChart radarChart;
    private DatabaseReference databaseReference;
    private String userIdToken;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_radar_chart, container, false);
        radarChart = rootView.findViewById(R.id.radarChart);

        // 레이아웃 파라미터를 설정하여 크기 조정
        radarChart.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams layoutParams = radarChart.getLayoutParams();
                layoutParams.width = 1200;  // 원하는 너비 (픽셀 단위)
                layoutParams.height = 1200;  // 원하는 높이 (픽셀 단위)
                radarChart.setLayoutParams(layoutParams);
            }
        });

        // userIdToken을 가져오는 코드
        Intent intent = getActivity().getIntent();
        userIdToken = intent.getStringExtra("userIdToken");

        // AsyncTask를 사용하여 데이터베이스 호출 처리
        new FetchDataAsyncTask().execute();

        return rootView;
    }

    // 데이터베이스 호출을 처리하기 위한 AsyncTask
    private class FetchDataAsyncTask extends AsyncTask<Void, Void, Map<String, Integer>> {
        @Override
        protected Map<String, Integer> doInBackground(Void... voids) {
            Map<String, Integer> algorithmCount = new HashMap<>();
            initializeAlgorithmCount(algorithmCount);

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Problems").child(userIdToken);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // 알고리즘 별로 문제 개수 세기
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String problemTypes = snapshot.child("problemType").getValue(String.class);
                            if (problemTypes != null) {
                                String[] types = problemTypes.split(", ");
                                for (String type : types) {
                                    if (algorithmCount.containsKey(type)) {
                                        algorithmCount.put(type, algorithmCount.get(type) + 1);
                                    }
                                }
                            }
                        }

                        // 그래프 그리기
                        drawRadarChart(algorithmCount);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 데이터 가져오기가 취소된 경우
                    Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            });

            return algorithmCount;
        }

        @Override
        protected void onPostExecute(Map<String, Integer> algorithmCount) {
            // AsyncTask가 완료되면 여기에서 아무것도 할 필요가 없습니다.
            // 데이터 처리 및 레이더 차트 그리기는 doInBackground 메서드에서 수행됩니다.
        }
    }

    // 알고리즘 카운트 맵 초기화
    private void initializeAlgorithmCount(Map<String, Integer> algorithmCount) {
        algorithmCount.put("브루트포스", 0);
        algorithmCount.put("DFS", 0);
        algorithmCount.put("BFS", 0);
        algorithmCount.put("구현", 0);
        algorithmCount.put("그래프 이론", 0);
        // algorithmCount.put("그리디", 0);
        algorithmCount.put("수학", 0);
        algorithmCount.put("백트래킹", 0);
        algorithmCount.put("DP", 0);
    }

    // 레이더 차트 그리기
    private void drawRadarChart(Map<String, Integer> algorithmCount) {
        // 데이터 생성
        ArrayList<String> labels = new ArrayList<>();
        List<RadarEntry> radarEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : algorithmCount.entrySet()) {
            radarEntries.add(new RadarEntry(entry.getValue()));
            labels.add(entry.getKey());
        }

        // 데이터 설정
        RadarDataSet dataSet = new RadarDataSet(radarEntries, "Algorithm Distribution");
        dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
        dataSet.setFillColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
        dataSet.setValueTextSize(12f);

        RadarData data = new RadarData(dataSet);
        radarChart.setData(data);

        // 레이블 설정
        radarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

        // 그래프 갱신
        radarChart.invalidate();
    }
}