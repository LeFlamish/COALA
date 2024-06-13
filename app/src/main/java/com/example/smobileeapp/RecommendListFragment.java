package com.example.smobileeapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecommendListFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    private String type;

    public static RecommendListFragment newInstance(String type) {
        RecommendListFragment fragment = new RecommendListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_list, container, false);
        LinearLayout layout = view.findViewById(R.id.problems);

        // Firebase 데이터베이스 참조 설정
        DatabaseReference mDatabase;

        if (type.equals("bronze") || type.equals("silver") || type.equals("gold") || type.equals("platinum")) {
            mDatabase = FirebaseDatabase.getInstance()
                    .getReference().child("Rproblem").child("difficulty").child(type);
        } else if (type.equals("samsung") || type.equals("kakao") || type.equals("naver") || type.equals("lg")) {
            mDatabase = FirebaseDatabase.getInstance()
                    .getReference().child("Rproblem").child("company").child(type);
        } else if (type.equals("BFS") || type.equals("DFS") || type.equals("DP") || type.equals("Greedy")) {
            mDatabase = FirebaseDatabase.getInstance()
                    .getReference().child("Rproblem").child("Algorithm").child(type);
        } else {
            type=type.toString();
            mDatabase = FirebaseDatabase.getInstance()
                    .getReference().child("Rproblem").child("Custom").child(type);
        }

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                layout.removeAllViews(); // 기존 뷰 삭제

                List<RProblem> problemList = new ArrayList<>();
                for (DataSnapshot problemSnapshot : dataSnapshot.getChildren()) {
                    RProblem problem = problemSnapshot.getValue(RProblem.class);
                    Log.e("111",problemSnapshot.getKey());
                    if (problem != null) {
                        Log.e("222",problem.toString());
                        problemList.add(problem);
                    }
                    else
                        break;
                }
                Context context = requireContext(); // 컨텍스트 가져오기
                // 문제 목록을 정렬 및 추가
                for (RProblem problem : problemList) {
                    Log.e("333",problem.toString());
                    LinearLayout layout_item = new LinearLayout(context);
                    layout_item.setOrientation(LinearLayout.VERTICAL);
                    layout_item.setPadding(20, 10, 20, 10);
                    layout_item.setId(problem.getProblemNum());
                    layout_item.setTag(problem.getProblemNum());

                    TextView tv_problemNum = new TextView(context);
                    tv_problemNum.setText(String.valueOf(problem.getProblemNum()));
                    tv_problemNum.setTextSize(30);
                   // tv_problemNum.setBackgroundColor(getColorForDifficulty(problem.getDifficulty()));
                    tv_problemNum.setBackgroundResource(R.drawable.rounded_background); // 배경 리소스 설정

                    // Drawable을 GradientDrawable로 캐스팅하여 색상을 동적으로 변경
                    GradientDrawable background = (GradientDrawable) tv_problemNum.getBackground();

                    background.setColor(getColorForDifficulty(problem.getDifficulty()));
                    layout_item.addView(tv_problemNum);

                    TextView tv_problemTitle = new TextView(context);
                    tv_problemTitle.setText("문제 제목 : " + problem.getProblemTitle());
                    tv_problemTitle.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
                    layout_item.addView(tv_problemTitle);

                    TextView tv_problemDifficulty = new TextView(context);
                    tv_problemDifficulty.setText("난이도 : " + problem.getDifficulty());
                    tv_problemDifficulty.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
                    layout_item.addView(tv_problemDifficulty);

                    TextView tv_problemType = new TextView(context);
                    tv_problemType.setText("문제 유형 : " + problem.getProblemType());
                    tv_problemType.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
                    layout_item.addView(tv_problemType);

                    layout_item.setOnClickListener(v -> {
                        int problemNum = (int) layout_item.getTag();
                        Intent it = new Intent(getActivity(), RProblemInfo.class);
                        it.putExtra("problemNum", problemNum);
                        it.putExtra("type", type);
                        startActivity(it);
                    });

                    layout.addView(layout_item);



                     // 문제 항목 아래에 선 추가
                    View separator = new View(context);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            3
                    );
                    separator.setLayoutParams(params);
                    separator.setBackgroundColor(ContextCompat.getColor(context, R.color.silver));
                    layout.addView(separator);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리
            }
        });

        return view;
    }

    private int getColorForDifficulty(String difficulty) {
        if (difficulty == null) {
            return ContextCompat.getColor(getActivity(), R.color.default_color);
        }
        if (difficulty.contains("골드")||difficulty.contains("gold")) {
            return ContextCompat.getColor(getActivity(), R.color.gold);
        } else if (difficulty.contains("실버")||difficulty.contains("silver")) {
            return ContextCompat.getColor(getActivity(), R.color.silver);
        } else if (difficulty.contains("브론즈")||difficulty.contains("bronze")) {
            return ContextCompat.getColor(getActivity(), R.color.bronze);
        } else if (difficulty.contains("플래티넘")||difficulty.contains("platinum")) {
            return ContextCompat.getColor(getActivity(), R.color.platinum);
        } else {
            return ContextCompat.getColor(getActivity(), R.color.default_color);
        }
    }

}
