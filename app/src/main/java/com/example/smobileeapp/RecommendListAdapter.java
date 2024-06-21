package com.example.smobileeapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecommendListAdapter extends ArrayAdapter<RProblem> {
    private final List<RProblem> problems;
    private final String type;
    private final Set<Integer> userProblemNums = new HashSet<>();
    private final Context context;
    private final FirebaseAuth mAuth;

    public RecommendListAdapter(Context context, List<RProblem> problems, String type) {
        super(context, 0, problems);
        this.context = context;
        this.problems = problems;
        this.type = type;
        this.mAuth = FirebaseAuth.getInstance();
        fetchUserProblems();
    }

    private void fetchUserProblems() {
        String userToken = mAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Problems").child(userToken);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userProblemNums.clear(); // 기존 데이터를 지우고 새로운 데이터로 채움
                for (DataSnapshot problemSnapshot : snapshot.getChildren()) {
                    try {
                        // 각 문제 번호를 정수로 가져오기 시도
                        Integer problemNum = Integer.valueOf(problemSnapshot.getKey());
                        if (problemNum != null) {
                            userProblemNums.add(problemNum);
                        }
                    } catch (NumberFormatException e) {
                        // 문제 번호가 정수가 아닌 경우 처리
                        Log.e("RecommendListAdapter", "Problem number is not an integer: " + problemSnapshot.getKey());
                    }
                }
                notifyDataSetChanged();  // 데이터가 변경되었음을 알림
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
                Log.e("RecommendListAdapter", "Database error: " + error.getMessage());
            }
        });
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_recommend_problem, parent, false);
        }

        RProblem problem = getItem(position);

        TextView tv_problemNum = convertView.findViewById(R.id.tv_problem_num);
        TextView tv_problemTitle = convertView.findViewById(R.id.tv_problem_title);
        TextView tv_problemDifficulty = convertView.findViewById(R.id.tv_problem_difficulty);
        TextView tv_problemType = convertView.findViewById(R.id.tv_problem_type);
        ImageView successImage = convertView.findViewById(R.id.success_image);

        if (problem != null) {
            tv_problemNum.setText(String.valueOf(problem.getProblemNum()));
            tv_problemNum.setBackgroundResource(R.drawable.rounded_background);

            GradientDrawable background = (GradientDrawable) tv_problemNum.getBackground();
            background.setColor(getColorForDifficulty(problem.getDifficulty()));

            tv_problemTitle.setText(problem.getProblemTitle());
            tv_problemDifficulty.setText(problem.getDifficulty());
            tv_problemType.setText(problem.getProblemType());

            // 성공 여부를 설정합니다.
            if (userProblemNums.contains(problem.getProblemNum())) {
                successImage.setVisibility(View.VISIBLE);
            } else {
                successImage.setVisibility(View.GONE);
            }
        }

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RProblemInfo.class);
            intent.putExtra("problemNum", problem.getProblemNum());
            intent.putExtra("type", type);
            getContext().startActivity(intent);
        });

        return convertView;
    }

    private int getColorForDifficulty(String difficulty) {
        if (difficulty == null) {
            return ContextCompat.getColor(getContext(), R.color.default_color);
        }
        if (difficulty.contains("골드") || difficulty.contains("gold")) {
            return ContextCompat.getColor(getContext(), R.color.gold);
        } else if (difficulty.contains("실버") || difficulty.contains("silver")) {
            return ContextCompat.getColor(getContext(), R.color.silver);
        } else if (difficulty.contains("브론즈") || difficulty.contains("bronze")) {
            return ContextCompat.getColor(getContext(), R.color.bronze);
        } else if (difficulty.contains("플래티넘") || difficulty.contains("platinum")) {
            return ContextCompat.getColor(getContext(), R.color.platinum);
        }
        else if (difficulty.contains("다이아몬드") || difficulty.contains("diamond")) {
            return ContextCompat.getColor(getContext(), R.color.diamond);
        } else {
            return ContextCompat.getColor(getContext(), R.color.default_color);
        }
    }
}