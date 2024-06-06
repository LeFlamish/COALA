package com.example.smobileeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smobileeapp.Problem;
import com.example.smobileeapp.ProblemInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private static final String ARG_SELECTED_DIFFICULTY = "problemDifficulty";
    private static final String TAG = "PlaceholderDifficulty";

    private FirebaseAuth mAuth;
    private String selectedDifficulty;
    private ProblemListAdapter adapter;
    private List<Problem> problemList = new LinkedList<>();

    public PlaceholderDifficultyFragment() {
    }

    public static PlaceholderDifficultyFragment newInstance(int sectionNumber, String selectedDifficulty) {
        PlaceholderDifficultyFragment fragment = new PlaceholderDifficultyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_SELECTED_DIFFICULTY, selectedDifficulty);
        fragment.setArguments(args);
        return fragment;
    }

    // 수정된 부분 시작
    // 수정된 부분 시작
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            selectedDifficulty = getArguments().getString(ARG_SELECTED_DIFFICULTY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_placeholder_difficulty, container, false);
        ListView listView = rootView.findViewById(R.id.problem_list_view);

        adapter = new ProblemListAdapter(getActivity(), problemList);
        listView.setAdapter(adapter);

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Problems").child(currentUser.getUid());
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
                        Log.d(TAG, "onDataChange called");
                        problemList.clear();

                        for (DataSnapshot problemSnapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, "Processing problem snapshot");
                            Problem problem = problemSnapshot.getValue(Problem.class);
                            if (problem != null && problem.getDifficulty().equals(selectedDifficulty)) {
                                problemList.add(problem);
                            }
                        }

                        Log.d(TAG, "Problem List Size: " + problemList.size());

                        switch (sectionNumber) {
                            case 0: // 등록 시간 순
                                Collections.sort(problemList, Comparator.comparingLong(Problem::getTimeposted).reversed());
                                break;
                            case 1: // 문제 번호 순
                                Collections.sort(problemList, Comparator.comparingInt(Problem::getProblemNum));
                                break;
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "취소됨: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                intent.putExtra("userIdToken", mAuth.getCurrentUser().getUid());
                intent.putExtra("problemNum", problem.getProblemNum());
                startActivity(intent);
            });

            return convertView;
        }
    }
}
