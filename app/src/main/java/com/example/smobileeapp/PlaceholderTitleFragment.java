package com.example.smobileeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class PlaceholderTitleFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private String userIdToken;
    private String problemTitle;
    private PlaceholderTitleFragment.ProblemListAdapter adapter;
    private List<Problem> problemList = new ArrayList<>();

    public PlaceholderTitleFragment() {
    }

    public static PlaceholderTitleFragment newInstance(int sectionNumber, String problemTitle, String userIdToken) {
        PlaceholderTitleFragment fragment = new PlaceholderTitleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber); // 올바른 매개변수를 설정해야 함
        args.putString("userIdToken", userIdToken);
        args.putString("problemTitle", problemTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_placeholder_title, container, false);
        ListView listView = rootView.findViewById(R.id.problem_list_view);

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        ProblemListByTitle activity = (ProblemListByTitle) getActivity();
        if (activity != null) {
            userIdToken = activity.getUserIdToken();
            problemTitle = activity.getProblemTitle();

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Problems");
            Query query = null;
            switch (sectionNumber) {
                case 1: // 두 번째 탭: 문제 번호 순
                    query = mDatabase.orderByChild("problemNum");
                    break;
                case 2: // 세 번째 탭: 문제 난이도 순
                    query = mDatabase.orderByChild("difficulty");
                    break;
                default: // 기본값: 최신순
                    query = mDatabase.orderByChild("timeposted");
            }
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    problemList.clear();
                    List<Problem> problemList = new LinkedList<>();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        if (userSnapshot.getKey().equals(userIdToken)) {
                            for (DataSnapshot problemSnapshot : userSnapshot.getChildren()) {
                                Problem problem = problemSnapshot.getValue(Problem.class);
                                if (problem != null && problem.getProblemTitle() != null && problemTitle != null && problem.getProblemTitle().contains(problemTitle)) {
                                    problemList.add(problem);
                                }
                            }
                        }
                    }

                    switch (sectionNumber) {
                        case 1: // 두 번째 탭: 문제 번호 순
                            Collections.sort(problemList, new Comparator<Problem>() {
                                @Override
                                public int compare(Problem p1, Problem p2) {
                                    return Integer.compare(p1.getProblemNum(), p2.getProblemNum());
                                }
                            });
                            break;
                        case 2: // 세 번째 탭: 문제 난이도 순
                            Collections.sort(problemList, new Comparator<Problem>() {
                                @Override
                                public int compare(Problem p1, Problem p2) {
                                    // 먼저 난이도를 비교
                                    int difficultyComparison = Integer.compare(getDifficultyOrder(p1.getDifficulty()), getDifficultyOrder(p2.getDifficulty()));
                                    // 난이도가 같으면 문제 번호로 비교
                                    if (difficultyComparison == 0) {
                                        return Integer.compare(p1.getProblemNum(), p2.getProblemNum());
                                    } else {
                                        return difficultyComparison;
                                    }
                                }

                                private int getDifficultyOrder(String difficulty) {
                                    // 각 난이도에 대한 순서를 정의하여 반환
                                    switch (difficulty) {
                                        case "플래티넘Ⅰ":
                                            return 1;
                                        case "플래티넘Ⅱ":
                                            return 2;
                                        case "플래티넘Ⅲ":
                                            return 3;
                                        case "플래티넘Ⅳ":
                                            return 4;
                                        case "플래티넘Ⅴ":
                                            return 5;
                                        case "골드Ⅰ":
                                            return 6;
                                        case "골드Ⅱ":
                                            return 7;
                                        case "골드Ⅲ":
                                            return 8;
                                        case "골드Ⅳ":
                                            return 9;
                                        case "골드Ⅴ":
                                            return 10;
                                        case "실버Ⅰ":
                                            return 11;
                                        case "실버Ⅱ":
                                            return 12;
                                        case "실버Ⅲ":
                                            return 13;
                                        case "실버Ⅳ":
                                            return 14;
                                        case "실버Ⅴ":
                                            return 15;
                                        case "브론즈Ⅰ":
                                            return 16;
                                        case "브론즈Ⅱ":
                                            return 17;
                                        case "브론즈Ⅲ":
                                            return 18;
                                        case "브론즈Ⅳ":
                                            return 19;
                                        case "브론즈Ⅴ":
                                            return 20;
                                        default:
                                            return Integer.MAX_VALUE; // 그 외의 경우는 가장 큰 값으로 처리하여 가장 뒤로 정렬
                                    }
                                }
                            });
                            break;
                        default: // 기본값: 최신순
                            Collections.sort(problemList, new Comparator<Problem>() {
                                @Override
                                public int compare(Problem p1, Problem p2) {
                                    return Long.compare(p2.getTimeposted(), p1.getTimeposted());
                                }
                            });
                    }

                    PlaceholderTitleFragment.ProblemListAdapter adapter = new PlaceholderTitleFragment.ProblemListAdapter(getActivity(), problemList);
                    listView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        return rootView;
    }

    private class ProblemListAdapter extends ArrayAdapter<Problem> {
        private final List<Problem> problems;

        ProblemListAdapter(Context context, List<Problem> problems) {
            super(context, 0, problems);
            this.problems = problems;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_problem, parent, false);
            }

            Problem problem = getItem(position);

            TextView tv_problemNum = convertView.findViewById(R.id.tv_problem_num);
            TextView tv_problemTitle = convertView.findViewById(R.id.tv_problem_title);
            TextView tv_problemDifficulty = convertView.findViewById(R.id.tv_problem_difficulty);
            TextView tv_problemType = convertView.findViewById(R.id.tv_problem_type);

            if (problem != null) {
                tv_problemNum.setText(String.valueOf(problem.getProblemNum()));
                tv_problemNum.setBackgroundColor(getColorForDifficulty(problem.getDifficulty()));
                tv_problemTitle.setText(problem.getProblemTitle());
                tv_problemDifficulty.setText(problem.getDifficulty());
                tv_problemType.setText(problem.getProblemType());
            }

            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ProblemInfo.class);
                intent.putExtra("userIdToken", userIdToken);
                intent.putExtra("problemNum", problem.getProblemNum());
                startActivity(intent);
            });

            return convertView;
        }
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