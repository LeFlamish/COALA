package com.example.smobileeapp;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend_custom, container, false);
        LinearLayout layout = view.findViewById(R.id.custom_list);

        // Firebase 데이터베이스 참조 설정
        DatabaseReference mDatabase = FirebaseDatabase.getInstance()
                .getReference().child("Rproblem").child("Custom");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!isAdded()) {
                    return;
                }

                layout.removeAllViews(); // 기존 뷰 삭제

                List<String> customList = new ArrayList<>();
                for (DataSnapshot customSnapshot : dataSnapshot.getChildren()) {
                    String customName = customSnapshot.getKey();
                    if (customName != null) {
                        customList.add(customName);
                    }
                }

                // 목록을 정렬 및 추가
                int i = 0;
                Context context = requireContext(); // 컨텍스트 가져오기
                for (String customEntry : customList) {
                    i++;
                    LinearLayout layout_item = new LinearLayout(context);
                    layout_item.setOrientation(LinearLayout.VERTICAL);
                    layout_item.setPadding(20, 20, 25, 40); // 크기 조정
                    layout_item.setBackground(ContextCompat.getDrawable(context, R.drawable.custom_background)); // 배경 추가

                    // 섹션을 구분하기 위한 여백 추가
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(15, 10, 15, 20); // 위, 아래 여백 설정
                    layout_item.setLayoutParams(params);
                    String orgin = customEntry;
                    String[] parts = customEntry.split("@");
                    String customName = parts.length > 0 ? parts[0] : "Unknown";
                    String creatorName = parts.length > 1 ? parts[1] : "Unknown";

                    TextView tv_customName = new TextView(context);
                    tv_customName.setText(" " + customName);
                    tv_customName.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.black));
                    tv_customName.setTextSize(24); // 텍스트 크기 조정
                    // TextView에 여백 추가
                    LinearLayout.LayoutParams paramsCustomName = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    paramsCustomName.setMargins(15, 30, 35, 35); // 여백 설정
                    tv_customName.setLayoutParams(paramsCustomName);

                    // TextView를 LinearLayout에 추가
                    layout_item.addView(tv_customName);

                    // 새로운 LinearLayout 생성하여 만든 사람을 오른쪽에 정렬
                    LinearLayout creatorLayout = new LinearLayout(context);
                    creatorLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    creatorLayout.setOrientation(LinearLayout.HORIZONTAL);
                    creatorLayout.setGravity(Gravity.END); // 오른쪽 정렬

                    TextView tv_creatorName = new TextView(context);
                    tv_creatorName.setText("만든 사람: " + creatorName + " ");
                    tv_creatorName.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray));
                    tv_creatorName.setTextSize(15); // 텍스트 크기 조정
                    creatorLayout.addView(tv_creatorName);

                    layout_item.addView(creatorLayout);

                    layout_item.setOnClickListener(v -> openCustomProblemListFragment(orgin));

                    layout.addView(layout_item);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리
            }
        });

        return view;
    }

    private void openCustomProblemListFragment(String customName) {
        RecommendListFragment fragment = RecommendListFragment.newInstance(customName);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
