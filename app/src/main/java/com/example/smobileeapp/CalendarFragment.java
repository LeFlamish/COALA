package com.example.smobileeapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CalendarFragment extends Fragment {

    private String userIdToken;
    private MaterialCalendarView calendarView;
    private DatabaseReference databaseReference;
    private TextView tvProblemCount;
    private final Map<String, Integer> dateProblemCount = new HashMap<>();
    private CalendarDay lastSelectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // FirebaseAuth 인스턴스를 가져옵니다.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // 현재 인증된 사용자를 가져와서 UID를 확인합니다.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userIdToken = currentUser.getUid();
        } else {
            // 현재 사용자가 인증되지 않았다면 로그를 남기고 리턴합니다.
            Log.e("CalendarFragment", "Current user is not authenticated.");
            return view;
        }

        calendarView = view.findViewById(R.id.calendarview);
        tvProblemCount = view.findViewById(R.id.tv_problem_count);

        databaseReference = FirebaseDatabase.getInstance().getReference("Problems");

        // 월, 요일을 한글로 보이게 설정 (MonthArrayTitleFormatter의 작동을 확인하려면 밑의 setTitleFormatter()를 지운다)
        calendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        calendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));

        // 좌우 화살표 사이 연, 월의 폰트 스타일 설정
        calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader);

        // 좌우 화살표 가운데의 연/월이 보이는 방식 커스텀
        calendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                LocalDate inputText = day.getDate();
                String[] calendarHeaderElements = inputText.toString().split("-");
                return calendarHeaderElements[0] + " " + calendarHeaderElements[1];
            }
        });

        fetchCalendarData();

        // 날짜 선택 리스너 추가
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    // 이전에 선택된 날짜와 현재 선택된 날짜가 동일한 경우 선택을 취소합니다.
                    if (lastSelectedDate != null && lastSelectedDate.equals(date)) {
                        widget.clearSelection();
                        tvProblemCount.setText(String.format("해결한 전체 문제 수: %d", dateProblemCount.values().stream().mapToInt(Integer::intValue).sum()));
                        tvProblemCount.setVisibility(View.VISIBLE);
                        lastSelectedDate = null;
                        return;
                    }

                    // 선택된 경우 해당 날짜에 푼 문제 수를 출력합니다.
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            date.getYear(), date.getMonth() + 1, date.getDay());
                    int count = dateProblemCount.getOrDefault(selectedDate, 0);
                    tvProblemCount.setText(String.format("선택된 날짜에 해결한 문제 수: %d", count));
                    tvProblemCount.setVisibility(View.VISIBLE);

                    // 마지막으로 선택된 날짜를 저장합니다.
                    lastSelectedDate = date;
                } else {
                    // 선택되지 않은 경우 전체 문제 수를 출력합니다.
                    updateProblemCount(dateProblemCount.values().stream().mapToInt(Integer::intValue).sum());
                }
            }
        });

        return view;
    }

    private void fetchCalendarData() {
        databaseReference.child(userIdToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("FirebaseData", "DataSnapshot received: " + dataSnapshot.toString());

                for (DataSnapshot problemSnapshot : dataSnapshot.getChildren()) {
                    Log.d("FirebaseData", "ProblemId: " + problemSnapshot.getKey());
                    Long timePosted = problemSnapshot.child("timeposted").getValue(Long.class);
                    Log.d("FirebaseData", "Time posted: " + timePosted);

                    if (timePosted != null) {
                        Date date = new Date(timePosted);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String formattedDate = dateFormat.format(date);
                        Log.d("FirebaseData", "Formatted date: " + formattedDate);

                        int currentCount = dateProblemCount.getOrDefault(formattedDate, 0) + 1;
                        dateProblemCount.put(formattedDate, currentCount);
                        Log.d("FirebaseData", "Updated count for " + formattedDate + ": " + currentCount);
                    }
                }

                Set<CalendarDay> problemDays = new HashSet<>();
                for (String date : dateProblemCount.keySet()) {
                    if (isValidDate(date)) {
                        CalendarDay day = CalendarDay.from(
                                Integer.parseInt(date.substring(0, 4)),
                                Integer.parseInt(date.substring(5, 7)),
                                Integer.parseInt(date.substring(8, 10))
                        );
                        problemDays.add(day);
                    }
                }

                Set<DayViewDecorator> decorators = new HashSet<>();
                decorators.add(new DefaultBackgroundDecorator(problemDays));

                for (Map.Entry<String, Integer> entry : dateProblemCount.entrySet()) {
                    if (isValidDate(entry.getKey())) {
                        CalendarDay day = CalendarDay.from(
                                Integer.parseInt(entry.getKey().substring(0, 4)),
                                Integer.parseInt(entry.getKey().substring(5, 7)), // 월에 1을 더함
                                Integer.parseInt(entry.getKey().substring(8, 10))
                        );
                        int count = entry.getValue();
                        int color;
                        if (count >= 11) {
                            color = Color.parseColor("#C03379");
                        } else if (count >= 7) {
                            color = Color.parseColor("#CC678E");
                        } else if (count >= 3) {
                            color = Color.parseColor("#CE777A");
                        } else {
                            color = Color.parseColor("#E6BCB9");
                        }
                        decorators.add(new CombinedDecorator(color, day, dateProblemCount));
                    }
                }

                for (DayViewDecorator decorator : decorators) {
                    calendarView.addDecorator(decorator);
                }

                // 초기 문제 수 표시 업데이트
                int totalCount = dateProblemCount.values().stream().mapToInt(Integer::intValue).sum();
                updateProblemCount(totalCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CalendarFragment", "Failed to read value.", databaseError.toException());
            }
        });
    }

    // 날짜 유효성을 검증하는 메서드
    private boolean isValidDate(String dateStr) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(dateStr, dateFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // 기본 배경색 데코레이터 클래스
    private static class DefaultBackgroundDecorator implements DayViewDecorator {
        private final Set<CalendarDay> problemDays;

        public DefaultBackgroundDecorator(Set<CalendarDay> problemDays) {
            this.problemDays = problemDays;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return !problemDays.contains(day); // 문제를 풀지 않은 날짜에만 적용
        }

        @Override
        public void decorate(DayViewFacade view) {
            // 아무런 변경 없음
        }
    }

    public static class CombinedDecorator implements DayViewDecorator {
        private final int color;
        private final String dateStr;
        private final Map<String, Integer> dateProblemCount;

        public CombinedDecorator(int color, CalendarDay day, Map<String, Integer> dateProblemCount) {
            this.color = color;
            this.dateStr = formatDate(day);
            this.dateProblemCount = dateProblemCount;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            // 여기서 월은 0부터 시작하는 인덱스이므로 1을 더해줍니다.
            return day.getYear() == Integer.parseInt(dateStr.substring(0, 4)) &&
                    day.getMonth() + 1 == Integer.parseInt(dateStr.substring(5, 7)) &&
                    day.getDay() == Integer.parseInt(dateStr.substring(8, 10));
        }

        @Override
        public void decorate(final DayViewFacade view) {
            // 배경색과 함께 동그라미를 적용합니다.
            view.setBackgroundDrawable(new ColorDrawable(color));
            view.setSelectionDrawable(createCircleDrawable(color));

            // 클릭 이벤트 추가
            view.addSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // 선택된 날짜에 해당하는 문제 수를 출력합니다.
                    int count = dateProblemCount.getOrDefault(dateStr, 0);
                    Toast.makeText(widget.getContext(), "선택된 날짜의 푼 문제 수: " + count, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private Drawable createCircleDrawable(int color) {
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(color); // 동그라미 색상
            return drawable;
        }

        private String formatDate(CalendarDay day) {
            return String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    day.getYear(), day.getMonth() + 1, day.getDay());
        }
    }


    @SuppressLint("DefaultLocale")
    private void updateProblemCount(int count) {
        tvProblemCount.setText(String.format("해결한 전체 문제 수: %d", count));
        tvProblemCount.setVisibility(View.VISIBLE);
        Log.d("ProblemCount", "Problem count: " + count); // 현재 count 로그 출력
    }
}

