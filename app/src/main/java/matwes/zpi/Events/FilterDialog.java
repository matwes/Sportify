package matwes.zpi.Events;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import matwes.zpi.R;

/**
 * Created by mateu on 18.05.2017.
 */

class FilterDialog extends Dialog
{
    public Button button;
    TextView filterDate;
    private AutoCompleteTextView autoCompleteTextView, autoCompleteTextView2;
    private ArrayAdapter<CharSequence> cityAdapter, sportAdapter;

    FilterDialog(@NonNull Context context, ArrayAdapter<CharSequence> sportAdapter, ArrayAdapter<CharSequence> cityAdapter)
    {
        super(context);
        setTitle(R.string.filter);
        setContentView(R.layout.dialog_filter);
        this.cityAdapter = cityAdapter;
        this.sportAdapter = sportAdapter;
    }



    void update(String selectedSport, String selectedCity, Date minSelectedDate, Date maxSelectedDate)
    {
        LinearLayout layout = (LinearLayout) findViewById(R.id.dialog_filter);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        button = (Button) layout.findViewById(R.id.btnFilter);
        filterDate = (TextView) layout.findViewById(R.id.filterDate);
        filterDate.setText(sdf.format(minSelectedDate)+" - "+sdf.format(maxSelectedDate));



        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(null);
        autoCompleteTextView.setKeyListener(null);
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((AutoCompleteTextView) v).showDropDown();
                return false;
            }
        });
        autoCompleteTextView.setText(selectedSport);
        autoCompleteTextView.setAdapter(sportAdapter);


        autoCompleteTextView2 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
        autoCompleteTextView2.setAdapter(null);
        autoCompleteTextView2.setKeyListener(null);
        autoCompleteTextView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((AutoCompleteTextView) v).showDropDown();
                return false;
            }
        });
        autoCompleteTextView2.setText(selectedCity);
        autoCompleteTextView2.setAdapter(cityAdapter);

    }

    String getSelectedSport()
    {
        return autoCompleteTextView.getText().toString();
    }

    void setSelectedDates(Date minSelectedDate, Date maxSelectedDate)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        filterDate.setText(sdf.format(minSelectedDate)+" - "+sdf.format(maxSelectedDate));
    }

    String getSelectedCity() {
        return autoCompleteTextView2.getText().toString();
    }
}
