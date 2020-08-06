package com.suji.lj.myapplication.Fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.suji.lj.myapplication.Adapters.RecyclerDateTimeAdapter;
import com.suji.lj.myapplication.Items.ItemForDateTime;
import com.suji.lj.myapplication.Items.MissionCartItem;
import com.suji.lj.myapplication.R;
import com.suji.lj.myapplication.SingleModeActivity;
import com.suji.lj.myapplication.Utils.DateTimeFormatter;
import com.suji.lj.myapplication.Utils.DateTimeUtils;

import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class CalendarSelectFragment extends Fragment implements RecyclerDateTimeAdapter.OnTimeSetListener {

    MaterialCalendarView materialCalendarView;
    RecyclerDateTimeAdapter recyclerDateTimeAdapter;
    RecyclerView date_time_recyclerView;
    RealmList<ItemForDateTime> list;
    Realm realm;
    LinearLayout ly_date_error;
    TextView wrong_time_tv;
    TextView common_time;


    Calendar calendar = Calendar.getInstance();
    int selected_year = calendar.get(Calendar.YEAR);
    int selected_month = calendar.get(Calendar.MONTH) + 1;
    int selected_day = calendar.get(Calendar.DATE);
    String min_date = selected_year + "-" + selected_month + "-" + selected_day;
    OnResetAmountFromCalendarListener onResetAmountListener;


    public CalendarSelectFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_calendar_select, container, false);


        materialCalendarView = view.findViewById(R.id.material_calendarView);
        date_time_recyclerView = view.findViewById(R.id.recycler_date_time);
        ly_date_error = view.findViewById(R.id.ly_date_error);
        common_time = view.findViewById(R.id.common_time);
        realm = Realm.getDefaultInstance();
        MissionCartItem item = realm.where(MissionCartItem.class).findFirst();
        list = item.getCalendarDayList();


        common_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonTimeSet();
            }
        });


        materialCalendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .commit();


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

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int year = date.getYear();
                int month = date.getMonth();
                int day = date.getDay();


                if (selected) {
                    Log.d("사이즈", "선택");

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            MissionCartItem cart = realm.where(MissionCartItem.class).findFirst();
                            //ItemForDateTime item = realm.createObject(ItemForDateTime.class);

                            ItemForDateTime item = new ItemForDateTime();
                            item.setDate(DateTimeUtils.makeDateForServer(year, month, day));
                            item.setTime(DateTimeUtils.getCurrentHourMin());
                            item.setYear(year);
                            item.setMonth(month);
                            item.setDay(day);
                            item.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                            item.setMin(calendar.get(Calendar.MINUTE));
/*
                            list.add(item);

                            Log.d("날짜", list.size() + "");
                            Collections.sort(list, new Comparator<ItemForDateTime>() {
                                @Override
                                public int compare(ItemForDateTime o1, ItemForDateTime o2) {
                                    return o1.getDate().compareTo(o2.getDate());
                                }
                            });
                            recyclerDateTimeAdapter.notifyDataSetChanged();
                            */

                            long time = DateTimeFormatter.dateParser(item.getDate(), "yyyyMMdd").getTime();


                            if (list.size() == 0) {
                                Log.d("사이즈0", list.size() + "");
                                list.add(item);
                                recyclerDateTimeAdapter.notifyItemInserted(0);
                            } else if (list.size() == 1) {
                                Log.d("사이즈1", list.size() + "");
                                if (DateTimeFormatter.dateParser(list.get(0).getDate(), "yyyyMMdd").getTime() > time) {
                                    list.add(0, item);
                                    recyclerDateTimeAdapter.notifyItemInserted(0);

                                } else {
                                    list.add(1, item);
                                    recyclerDateTimeAdapter.notifyItemInserted(1);
                                }
                            } else {
                                Log.d("사이즈2", list.size() + "");
                                for (int i = 1; i < list.size(); i++) {
                                    Log.d("사이즈2", "for");
                                    if (time > DateTimeFormatter.dateParser(list.get(i - 1).getDate(), "yyyyMMdd").getTime()) {
                                        if (time < DateTimeFormatter.dateParser(list.get(i).getDate(), "yyyyMMdd").getTime()) {
                                            Log.d("사이즈2", "문제가되는곳1");
                                            list.add(i, item);
                                            recyclerDateTimeAdapter.notifyItemInserted(i);
                                            break;
                                        } else if (i == list.size() - 1) {
                                            Log.d("사이즈2", "문제가되는곳2");
                                            list.add(i + 1, item);
                                            recyclerDateTimeAdapter.notifyItemInserted(i + 1);
                                            break;
                                        }

                                    } else {
                                        Log.d("사이즈2", "문제가되는곳3");
                                        list.add(i - 1, item);
                                        recyclerDateTimeAdapter.notifyItemInserted(i - 1);
                                        break;

                                    }
                                }

                            }

                        }

                    });
                    onResetAmountListener.onResetAmountFromCalendar();//날짜를 선택할때마다 총 금액을 업데이트한다
                } else {
                    for (int i = 0; i < list.size(); i++) {

                        if (list.get(i).getDate().equals(DateTimeUtils.makeDateForServer(year, month, day))) {

                            Log.d("솔약국", i + "remove");
                            final int position = i;
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    list.remove(position);
                                }
                            });
                            recyclerDateTimeAdapter.notifyItemRemoved(i);
                            recyclerDateTimeAdapter.notifyItemRangeChanged(i, list.size());
                        }
                    }
                    onResetAmountListener.onResetAmountFromCalendar();//날짜를 선택할때마다 총 금액을 업데이트한다
                }
                if (materialCalendarView.getSelectedDates().size() == 0) {
                    ly_date_error.setVisibility(View.VISIBLE);
                } else {
                    ly_date_error.setVisibility(View.GONE);
                }

            }
        });


        setSelectedDate();


        return view;
    }

    private void setDateTimeRecyclerView(List<ItemForDateTime> list) {


        //Log.d("담쟁이2","넘어가"+stringList.size());
        date_time_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        date_time_recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerDateTimeAdapter = new RecyclerDateTimeAdapter(getContext(), list, realm, this);
        date_time_recyclerView.setAdapter(recyclerDateTimeAdapter);

    }

    private void commonTimeSet() {
        Log.d("다이소", "여기맞아?");

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).setHour(hourOfDay);
                            list.get(i).setMin(minute);

                            list.get(i).setTime(DateTimeUtils.makeTimeForServer(hourOfDay, minute));
                        }
                        recyclerDateTimeAdapter.notifyDataSetChanged();
                    }
                });


            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();


    }

    private void setSelectedDate() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < list.size(); i++) {

                    int year = list.get(i).getYear();
                    int month = list.get(i).getMonth();
                    int day = list.get(i).getDay();
                    Log.d("번들 날", year + " " + month + " " + day);
                    String date = DateTimeUtils.makeDateForServer(year, month, day);
                    long selected_date = DateTimeFormatter.dateParser(date, "yyyyMMdd").getTime();
                    long today = DateTimeFormatter.dateParser(DateTimeUtils.getToday(), "yyyyMMdd").getTime();

                    if (selected_date < today) {//어제 이전 모든 날짜
                        list.get(i).deleteFromRealm();
                    }
                    materialCalendarView.setDateSelected(CalendarDay.from(year, month, day), true);
                }
                setDateTimeRecyclerView(list);
            }

        });

    }

    @Override
    public void onTimeSet(int hour, int min, int position) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                list.get(position).setTime(DateTimeUtils.makeTimeForServer(hour, min));
                list.get(position).setHour(hour);
                list.get(position).setMin(min);
                recyclerDateTimeAdapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onResetAmountListener = (OnResetAmountFromCalendarListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TextClicked");
        }
    }

    @Override
    public void onDetach() {
        onResetAmountListener = null; // => avoid leaking, thanks @Deepscorn
        super.onDetach();
    }

    public interface OnResetAmountFromCalendarListener {
        void onResetAmountFromCalendar();


    }

}
