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
    import androidx.cardview.widget.CardView;
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
                        String orgin = customEntry;
                        String[] parts = customEntry.split("@");
                        String customName = parts.length > 0 ? parts[0] : "Unknown";
                        String creatorName = parts.length > 1 ? parts[1] : "Unknown";

                        // CardView 생성
                        CardView cardView = new CardView(context);
                        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        cardParams.setMargins(20, 12, 20, 20); // CardView 사이 여백 설정
                        cardView.setLayoutParams(cardParams);
                        cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                        cardView.setRadius(15); // CardView 모서리 둥글기 설정
                        cardView.setContentPadding(30, 20, 20, 30); // 내부 여백 설정

// CardView 안에 들어갈 레이아웃 생성
                        LinearLayout innerLayout = new LinearLayout(context);
                        innerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        innerLayout.setOrientation(LinearLayout.VERTICAL);

// TextView (Custom Name)
                        TextView tv_customName = new TextView(context);
                        tv_customName.setText(customName);
                        tv_customName.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                        tv_customName.setTextSize(24); // 텍스트 크기 조정
                        LinearLayout.LayoutParams customNameParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        customNameParams.setMargins(0, 30, 0, 30); // 여백 설정
                        tv_customName.setLayoutParams(customNameParams);
                        innerLayout.addView(tv_customName);
// 가로 공백을 추가할 빈 TextView
                        TextView tv_horizontalSpacer = new TextView(context);
                        tv_horizontalSpacer.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                20 // 공백의 높이를 조절하세요
                        ));
                        innerLayout.addView(tv_horizontalSpacer);
// TextView (Creator Name)
                        TextView tv_creatorName = new TextView(context);
                        tv_creatorName.setText("만든 사람: " + creatorName);
                        tv_creatorName.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
                        tv_creatorName.setTextSize(15); // 텍스트 크기 조정
                        tv_creatorName.setGravity(Gravity.END); // 오른쪽 정렬
                        LinearLayout.LayoutParams creatorNameParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        creatorNameParams.setMargins(0, 20, 0, 0); // 여백 설정
                        tv_creatorName.setLayoutParams(creatorNameParams);
                        innerLayout.addView(tv_creatorName);

// innerLayout을 cardView에 추가
                        cardView.addView(innerLayout);

// CardView에 OnClickListener 설정
                        cardView.setOnClickListener(v -> openCustomProblemListFragment(orgin));

// layout에 cardView 추가
                        layout.addView(cardView);

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
