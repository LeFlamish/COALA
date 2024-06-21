package com.example.smobileeapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private TextView TVtoday;
    private static final Map<String, Integer> dateProblemCount = new HashMap<>();
    private CalendarDay lastSelectedDate;
    private DayViewDecorator selectedDateDecorator;

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userIdToken = currentUser.getUid();
        } else {
            Log.e("CalendarFragment", "Current user is not authenticated.");
            return view;
        }

        calendarView = view.findViewById(R.id.calendarview);
        TVtoday = view.findViewById(R.id.today);
        tvProblemCount = view.findViewById(R.id.tv_problem_count);

        databaseReference = FirebaseDatabase.getInstance().getReference("Problems");

        calendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        calendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));
        calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader);

        calendarView.setTitleFormatter(day -> {
            LocalDate inputText = day.getDate();
            String[] calendarHeaderElements = inputText.toString().split("-");
            return calendarHeaderElements[0] + " " + calendarHeaderElements[1];
        });

        fetchCalendarData();

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (selected) {
                // 이전에 선택한 날짜와 현재 선택한 날짜가 같은 경우 선택을 취소합니다.
                if (lastSelectedDate != null && lastSelectedDate.equals(date)) {
                    widget.clearSelection();
                    tvProblemCount.setText(String.format("해결한 전체 문제 수: %d", dateProblemCount.values().stream().mapToInt(Integer::intValue).sum()));
                    tvProblemCount.setVisibility(View.VISIBLE);
                    lastSelectedDate = null;

                    // 이전 데코레이터를 제거합니다.
                    if (selectedDateDecorator != null) {
                        calendarView.removeDecorator(selectedDateDecorator);
                        selectedDateDecorator = null;
                    }
                    return;
                }

                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", date.getYear(), date.getMonth(), date.getDay());
                int count = dateProblemCount.getOrDefault(selectedDate, 0);
                tvProblemCount.setText(String.format("선택된 날짜에 해결한 문제 수: %d", count));
                tvProblemCount.setVisibility(View.VISIBLE);

                // 새로운 데코레이터 추가
                if (selectedDateDecorator != null) {
                    calendarView.removeDecorator(selectedDateDecorator); // 이전 데코레이터 제거
                }
                selectedDateDecorator = new SelectedDateDecorator(date, requireContext());
                calendarView.addDecorator(selectedDateDecorator);
                lastSelectedDate = date;
            } else {
                // 선택이 취소된 경우 이전 데코레이터를 제거하고 날짜의 상태를 초기화합니다.
                if (selectedDateDecorator != null) {
                    calendarView.removeDecorator(selectedDateDecorator);
                    selectedDateDecorator = null;

                    // 날짜의 상태를 초기화합니다.
                    CalendarDay day = CalendarDay.from(date.getYear(), date.getMonth(), date.getDay());
                    widget.setDateSelected(day, false);
                }
                updateProblemCount(dateProblemCount.values().stream().mapToInt(Integer::intValue).sum());
            }
        });

        return view;
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
            return day.getYear() == Integer.parseInt(dateStr.substring(0, 4)) &&
                    day.getMonth()+1 == Integer.parseInt(dateStr.substring(5, 7)) &&
                    day.getDay() == Integer.parseInt(dateStr.substring(8, 10));
        }

        @Override
        public void decorate(final DayViewFacade view) {
            view.setBackgroundDrawable(new ColorDrawable(color));
            view.setSelectionDrawable(createCircleDrawable(color));
            view.addSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    int count = dateProblemCount.getOrDefault(dateStr, 0);
                    Toast.makeText(widget.getContext(), "선택된 날짜의 푼 문제 수: " + count, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private Drawable createCircleDrawable(int color) {
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(color);
            return drawable;
        }

        private String formatDate(CalendarDay day) {
            return String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    day.getYear(), day.getMonth() + 1, day.getDay());
        }
    }

    public static class SelectedDateDecorator implements DayViewDecorator {

        private final CalendarDay date;
        private final Drawable highlightDrawable;

        public SelectedDateDecorator(CalendarDay date, Context context) {
            this.date = date;
            highlightDrawable = ContextCompat.getDrawable(context, R.drawable.bluecircle); // 초록색 테두리 원을 그리는 Drawable
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.equals(date) && !dateProblemCount.containsKey(date.toString()); // 선택된 날짜이며, 푼 문제가 없는 경우에만 데코레이터를 적용합니다.
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(highlightDrawable); // 푼 문제가 없는 경우 초록색 테두리 원 적용
        }
    }


    private void fetchCalendarData() {
        // 오늘 날짜 가져오기
        Calendar today = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayFormatted = dateFormat.format(today.getTime());
        databaseReference.child(userIdToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 새로운 데이터를 저장할 맵
                Map<String, Integer> newDateProblemCount = new HashMap<>();

                for (DataSnapshot problemSnapshot : dataSnapshot.getChildren()) {
                    Long timePosted = problemSnapshot.child("timeposted").getValue(Long.class);
                    if (timePosted != null) {
                        Date date = new Date(timePosted);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String formattedDate = dateFormat.format(date);
                        // 새로운 데이터를 계산하여 저장
                        int currentCount = newDateProblemCount.getOrDefault(formattedDate, 0) + 1;
                        newDateProblemCount.put(formattedDate, currentCount);
                    }
                }

                int todayCount = dateProblemCount.getOrDefault(todayFormatted, 0);
                if (todayCount == 0) {
                    TVtoday.setText("오늘은 문제를 안 푸셨네요");
                    TVtoday.setVisibility(View.VISIBLE);
                } else {
                    TVtoday.setVisibility(View.GONE);
                }

                // 새로운 데이터로 갱신
                dateProblemCount.clear();
                dateProblemCount.putAll(newDateProblemCount);

                // 기존 데코레이터 제거
                calendarView.removeDecorators();

                // 새로운 데이터를 기반으로 데코레이터 생성
                Set<CalendarDay> problemDays = new HashSet<>();
                Set<DayViewDecorator> decorators = new HashSet<>();

                for (Map.Entry<String, Integer> entry : dateProblemCount.entrySet()) {
                    if (isValidDate(entry.getKey())) {
                        CalendarDay day = CalendarDay.from(
                                Integer.parseInt(entry.getKey().substring(0, 4)),
                                Integer.parseInt(entry.getKey().substring(5, 7)),
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
                        problemDays.add(day);
                    }
                }

                // 기존 데코레이터 추가
                decorators.add(new DefaultBackgroundDecorator(problemDays));

                // 데코레이터 적용
                for (DayViewDecorator decorator : decorators) {
                    calendarView.addDecorator(decorator);
                }

                // 문제 수 업데이트
                int totalCount = dateProblemCount.values().stream().mapToInt(Integer::intValue).sum();
                updateProblemCount(totalCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CalendarFragment", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private boolean isValidDate(String dateStr) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(dateStr, dateFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static class DefaultBackgroundDecorator implements DayViewDecorator {
        private final Set<CalendarDay> problemDays;

        public DefaultBackgroundDecorator(Set<CalendarDay> problemDays) {
            this.problemDays = problemDays;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return !problemDays.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateProblemCount(int count) {
        tvProblemCount.setText(String.format("해결한 전체 문제 수: %d", count));
        tvProblemCount.setVisibility(View.VISIBLE);
        Log.d("ProblemCount", "Problem count: " + count);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCalendarData();
    }

    @Override
    public void onPause() {
        super.onPause();
        fetchCalendarData();
    }
}