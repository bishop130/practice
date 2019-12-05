package com.suji.lj.myapplication.StepperForm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.suji.lj.myapplication.R;
import com.google.gson.GsonBuilder;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import ernestoyaquello.com.verticalstepperform.Step;

public class MakeDateStep extends Step<String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEE, d MMM yyyy");
    private MaterialCalendarView materialCalendarView;
    private String days_result;
    private StringBuffer sb = null;
    private String con;
    private int count = 0;
    private SimpleDateFormat date_sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    TextView clear_selection;


    public MakeDateStep(String title) {
        this(title, "");
    }

    public MakeDateStep(String title, String subtitle) {
        super(title, subtitle);
    }

    @Override
    public String getStepData() {

        return String.valueOf(count);
    }

    @Override
    protected View createStepContentLayout() {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.step_date_pick, null, false);
        materialCalendarView = view.findViewById(R.id.material_calendarView);

        Calendar min = Calendar.getInstance();

        materialCalendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .commit();


        //materialCalendarView.addDecorator(new SundayDecorator());


        materialCalendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay calendarDay) {

                StringBuffer buffer = new StringBuffer();
                int yearOne = calendarDay.getYear();
                int monthOne = calendarDay.getMonth();
                buffer.append(yearOne).append("년  ").append(monthOne).append("월");
                return buffer;
            }
        });
        //materialCalendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        //materialCalendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));
        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);



        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {

            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean selected) {

                con = new GsonBuilder().setPrettyPrinting().create().toJson(materialCalendarView.getSelectedDates());
                if (selected == true) {
                    count++;

                } else {
                    count--;

                }
                markAsCompletedOrUncompleted(true);
                Log.d("날짜열", con);
            }
        });

        clear_selection = view.findViewById(R.id.clear_date);
        clear_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialCalendarView.clearSelection();
                con = null;
                count = 0;
                markAsCompletedOrUncompleted(true);
            }
        });


        return view;

    }


    @Override
    public String getStepDataAsHumanReadableString() {
        int maxYear = 0;
        int maxMonth = 0;
        int maxDay = 0;
        int minYear = 2100;
        int minMonth = 13;
        int minDay = 31;
        String minDate = null;
        String maxDate = null;
        List<String> date_array = new ArrayList<>();

        if (con != null) {
            sb = new StringBuffer();
            try {
                JSONArray jsonArr = new JSONArray(con);
                String[] date_arr = new String[jsonArr.length()];
                for (int i = 0; i < jsonArr.length(); i++) {
                    Log.d("입장2", "입장2");
                    JSONObject object = jsonArr.getJSONObject(i);
                    JSONObject object2 = object.getJSONObject("date");
                    String day = object2.getString("day");
                    String month = object2.getString("month");
                    String year = object2.getString("year");
                    String sum = year + "-" + month + "-" + day;
                    String date_for_server = date_sdf.format(date_sdf.parse(sum));
                    sb.append(date_for_server);
                    sb.append(",");
                    date_array.add(sum);
                    date_arr[i] = sum;

                    if (minYear == Integer.valueOf(year)) {
                        if (minMonth == Integer.valueOf(month)) {
                            if (minDay > Integer.valueOf(day)) {
                                minDay = Integer.valueOf(day);
                            }
                        } else if (minMonth > Integer.valueOf(month)) {
                            minMonth = Integer.valueOf(month);
                            minDay = Integer.valueOf(day);
                        }
                    } else if (minYear > Integer.valueOf(year)) {
                        minYear = Integer.valueOf(year);
                        minMonth = Integer.valueOf(month);
                        minDay = Integer.valueOf(day);
                    }
                    if (maxYear == Integer.valueOf(year)) {
                        if (maxMonth == Integer.valueOf(month)) {
                            if (maxDay < Integer.valueOf(day)) {
                                maxDay = Integer.valueOf(day);

                            }
                        } else if (maxMonth < Integer.valueOf(month)) {
                            maxMonth = Integer.valueOf(month);
                            maxDay = Integer.valueOf(day);
                        }
                    } else if (maxYear < Integer.valueOf(year)) {
                        maxYear = Integer.valueOf(year);
                        maxMonth = Integer.valueOf(month);
                        maxDay = Integer.valueOf(day);
                    }

                    minDate = minYear + "-" + minMonth + "-" + minDay + " ";
                    maxDate = " " + maxYear + "-" + maxMonth + "-" + maxDay;
                }

                String result = sb.toString();
                int last = result.length() - 1;
                if (last > 0 && result.charAt(last) == ',') {
                    result = result.substring(0, last);
                }
                String result2 = Arrays.toString(date_arr);
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("sFile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("date_array_server", con);
                editor.putString("date_array", result);
                editor.putString("initiate_date", minDate);
                editor.putString("date_array_valid", Arrays.toString(date_arr));
                editor.apply();
                editor.commit();
                Log.d("서버날짜", result);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return minDate + "~" + maxDate + "\n" + "총" + count + "일";


        } else {

            return "날짜를 선택하세요";
        }
    }

    @Override
    public void restoreStepData(String data) {


    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {

        if (Integer.valueOf(stepData) == 0) {
            return new IsDataValid(false, "최소 하루를 선택해야합니다.");
        } else {
            return new IsDataValid(true);
        }


    }


    @Override
    protected void onStepOpened(boolean animated) {

    }

    @Override
    protected void onStepClosed(boolean animated) {

    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
    }

/*
    public class SundayDecorator implements DayViewDecorator {
        public SundayDecorator() {

        }

        @Override
        public boolean shouldDecorate(final CalendarDay day) {
            final DayOfWeek weekDay = day.getDate().getDayOfWeek();
            return weekDay == DayOfWeek.SUNDAY;
        }

        @Override
        public void decorate(final DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }
    }
    */

}
