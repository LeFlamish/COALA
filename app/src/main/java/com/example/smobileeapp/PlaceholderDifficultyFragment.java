package com.example.smobileeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class PlaceholderDifficultyFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private String userIdToken;
    private String selectedDifficulty;

    public PlaceholderDifficultyFragment() {
    }

    public static PlaceholderDifficultyFragment newInstance(int sectionNumber, String selectedDifficulty, String userIdToken) {
        PlaceholderDifficultyFragment fragment = new PlaceholderDifficultyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString("selectedDifficulty", selectedDifficulty);
        args.putString("userIdToken", userIdToken);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_placeholder_difficulty, container, false);
        LinearLayout layout = rootView.findViewById(R.id.problems);

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        ProblemListByDifficulty activity = (ProblemListByDifficulty) getActivity();

        if (activity != null) {
            selectedDifficulty = activity.getDifficulty();
            userIdToken = activity.getUserIdToken();

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Problems").child(userIdToken);
            Query query = null;
            switch (sectionNumber) {
                case 0: // 등록 시간 순
                    query = mDatabase.orderByChild("timeposted");
                    break;
                case 1: // 문제 번호 순
                    query = mDatabase.orderByChild("problemNum");
                    break;
            }

            if (query != null) {
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("FirebaseQuery", "onDataChange called");
                        layout.removeAllViews();

                        List<Problem> problemList = new LinkedList<>();
                        for (DataSnapshot problemSnapshot : dataSnapshot.getChildren()) {
                            Log.d("FirebaseQuery", "Processing problem snapshot");
                            Problem problem = problemSnapshot.getValue(Problem.class);
                            if (problem != null && problem.getDifficulty().equals(selectedDifficulty)) {
                                problemList.add(problem);
                            }
                        }

                        Log.d("FirebaseQuery", "Problem List Size: " + problemList.size());

                        switch (sectionNumber) {
                            case 0: // 등록 시간 순
                                Collections.sort(problemList, new Comparator<Problem>() {
                                    @Override
                                    public int compare(Problem p1, Problem p2) {
                                        return Long.compare(p2.getTimeposted(), p1.getTimeposted());
                                    }
                                });
                                break;
                            case 1: // 문제 번호 순
                                Collections.sort(problemList, new Comparator<Problem>() {
                                    @Override
                                    public int compare(Problem p1, Problem p2) {
                                        return Integer.compare(p1.getProblemNum(), p2.getProblemNum());
                                    }
                                });
                                break;
                        }

                        for (Problem problem : problemList) {
                            LinearLayout layout_item = new LinearLayout(getActivity());
                            layout_item.setOrientation(LinearLayout.VERTICAL);
                            layout_item.setPadding(20, 10, 20, 10);
                            layout_item.setId(problem.getProblemNum());
                            layout_item.setTag(problem.getProblemNum());

                            TextView tv_problemNum = new TextView(getActivity());
                            tv_problemNum.setText(String.valueOf(problem.getProblemNum()));
                            tv_problemNum.setTextSize(30);
                            tv_problemNum.setBackgroundColor(getColorForDifficulty(problem.getDifficulty()));
                            layout_item.addView(tv_problemNum);

                            TextView tv_problemTitle = new TextView(getActivity());
                            tv_problemTitle.setText("문제 제목 : " + problem.getProblemTitle());
                            tv_problemTitle.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black)); // 수정 필요
                            layout_item.addView(tv_problemTitle);

                            TextView tv_problemDifficulty = new TextView(getActivity());
                            tv_problemDifficulty.setText("난이도 : " + problem.getDifficulty());
                            tv_problemDifficulty.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black)); // 수정 필요
                            layout_item.addView(tv_problemDifficulty);

                            TextView tv_problemType = new TextView(getActivity());
                            tv_problemType.setText("문제 유형 : " + problem.getProblemType());
                            tv_problemType.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black)); // 수정 필요
                            layout_item.addView(tv_problemType);

                            layout_item.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int problemNum = (int) layout_item.getTag();
                                    Intent it = new Intent(getActivity(), ProblemInfo.class);
                                    it.putExtra("userIdToken", userIdToken);
                                    it.putExtra("problemNum", problemNum);
                                    startActivity(it);
                                }
                            });

                            layout.addView(layout_item);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // 취소될 때 처리할 코드를 추가하세요.
                        Toast.makeText(getActivity(), "취소됨", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        return rootView;
    }

    private int getColorForDifficulty(String difficulty) {
        if (difficulty.contains("골드")) {
            return ContextCompat.getColor(getActivity(), R.color.gold);
        } else if (difficulty.contains("실버")) {
            return ContextCompat.getColor(getActivity(), R.color.silver);
        } else if (difficulty.contains("브론즈")) {
            return ContextCompat.getColor(getActivity(), R.color.bronze);
        } else if (difficulty.contains("플래티넘")) {
            return ContextCompat.getColor(getActivity(), R.color.platinum);
        } else {
            return ContextCompat.getColor(getActivity(), R.color.default_color);
        }
    }
}