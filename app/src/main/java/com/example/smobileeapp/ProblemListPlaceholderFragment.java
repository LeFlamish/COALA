package com.example.smobileeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class ProblemListPlaceholderFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_USER_ID_TOKEN = "user_id_token";
    private static final String TAG = "ProblemListPlaceholder";
    private String userIdToken;

    public ProblemListPlaceholderFragment() {
        // Required empty public constructor
    }

    public static ProblemListPlaceholderFragment newInstance(int sectionNumber, String userIdToken) {
        ProblemListPlaceholderFragment fragment = new ProblemListPlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_USER_ID_TOKEN, userIdToken);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userIdToken = getArguments().getString(ARG_USER_ID_TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_problem_list_placeholder, container, false);
        LinearLayout layout = rootView.findViewById(R.id.problems);

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        userIdToken = getArguments().getString(ARG_USER_ID_TOKEN);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Problems");

        Query query = null;
        switch (sectionNumber) {
            case 1: // 문제 번호 순
                query = mDatabase.orderByChild("problemNum");
                break;
            case 2: // 문제 난이도 순
                query = mDatabase.orderByChild("difficulty");
                break;
            default: // 최신순
                query = mDatabase.orderByChild("timeposted");
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the Fragment is added and context is not null
                if (!isAdded() || getContext() == null) {
                    Log.e(TAG, "Fragment not attached to a context");
                    return;
                }

                layout.removeAllViews();

                List<Problem> problemList = new LinkedList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.getKey().equals(userIdToken)) {
                        for (DataSnapshot problemSnapshot : userSnapshot.getChildren()) {
                            Problem problem = problemSnapshot.getValue(Problem.class);
                            if (problem != null) {
                                problemList.add(problem);
                            }
                        }
                    }
                }

                switch (sectionNumber) {
                    case 1: // 문제 번호 순
                        Collections.sort(problemList, new Comparator<Problem>() {
                            @Override
                            public int compare(Problem p1, Problem p2) {
                                return Integer.compare(p1.getProblemNum(), p2.getProblemNum());
                            }
                        });
                        break;
                    case 2: // 문제 난이도 순
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
                    default: // 최신순
                        Collections.sort(problemList, (p1, p2) -> Long.compare(p2.getTimeposted(), p1.getTimeposted()));
                }

                Context context = requireContext(); // 컨텍스트 가져오기
                for (Problem problem : problemList) {
                    LinearLayout layout_item = new LinearLayout(context);
                    layout_item.setOrientation(LinearLayout.VERTICAL);
                    layout_item.setPadding(20, 10, 20, 10);
                    layout_item.setId(problem.getProblemNum());
                    layout_item.setTag(problem.getProblemNum());

                    TextView tv_problemNum = new TextView(context);
                    tv_problemNum.setText(String.valueOf(problem.getProblemNum()));
                    tv_problemNum.setTextSize(30);
                    tv_problemNum.setBackgroundColor(getColorForDifficulty(problem.getDifficulty()));
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
                        Intent it = new Intent(getActivity(), ProblemInfo.class);
                        it.putExtra("userIdToken", userIdToken);
                        it.putExtra("problemNum", problemNum);
                        startActivity(it);
                    });

                    layout.addView(layout_item);
                }

                if (problemList.isEmpty()) {
                    Log.d(TAG, "No problems found for user: " + userIdToken);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });

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
