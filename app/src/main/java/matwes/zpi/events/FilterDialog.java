package matwes.zpi.events;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import matwes.zpi.R;

/**
 * Created by Mateusz Wesołowski
 */

class FilterDialog extends Dialog {
    public Button button;
    TextView filterDate;
    private TextInputEditText autoCompleteTextView2;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> eventsAdatper;
    private AppCompatCheckBox nameCheckBox, priceCheckBox, dateCheckBox;
    private Context viewContext;
    FilterDialog(@NonNull Context context, ArrayAdapter<String> eventsAdatper) {
        super(context);
        this.viewContext = context;
        setTitle(R.string.filter);
        setContentView(R.layout.dialog_filter);
        this.eventsAdatper = eventsAdatper;
    }

    void update(String selectedName, String selectedPrice, Date minSelectedDate, Date maxSelectedDate, List<Boolean> selectedCheckBoxes) {
        LinearLayout layout = findViewById(R.id.dialog_filter);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputManager = (InputMethodManager) viewContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                return false;
            }
        });
        button = layout.findViewById(R.id.btnFilter);
        filterDate = layout.findViewById(R.id.filterDate);

        if (maxSelectedDate == null || minSelectedDate == null) {
            filterDate.setHint("Wybierz przedział");
            filterDate.setText("");
        }else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
            filterDate.setText(String.format("%s - %s", sdf.format(minSelectedDate), sdf.format(maxSelectedDate)));
            CharSequence ss = filterDate.getText();
        }
        nameCheckBox = findViewById(R.id.checkButtonEventName);
        priceCheckBox = findViewById(R.id.checkButtonPrice);
        dateCheckBox  = findViewById(R.id.checkButtonDate);

        nameCheckBox.setChecked(selectedCheckBoxes.get(0));
        priceCheckBox.setChecked(selectedCheckBoxes.get(1));
        dateCheckBox.setChecked(selectedCheckBoxes.get(2));

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView2);
//        autoCompleteTextView.setAdapter(null);
//        autoCompleteTextView.setKeyListener(null);
//        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                ((AutoCompleteTextView) v).showDropDown();
//                return false;
//            }
//        });
        autoCompleteTextView.setText(selectedName);
        autoCompleteTextView.setAdapter(eventsAdatper);

        autoCompleteTextView2 = findViewById(R.id.maxPriceEditText);
        autoCompleteTextView2.setText(selectedPrice);
    }

    String getSelectedEventName() {
        return autoCompleteTextView.getText().toString();
    }

    void setSelectedDates(Date minSelectedDate, Date maxSelectedDate) {
        if (maxSelectedDate == null || minSelectedDate == null) {
            filterDate.setHint("Wybierz przedział");
            filterDate.setText("");
        }else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
            filterDate.setText(String.format("%s - %s", sdf.format(minSelectedDate), sdf.format(maxSelectedDate)));
        }
    }

    String getSelectedPrice() {
        return autoCompleteTextView2.getText().toString();
    }

    List<Boolean> getSelectedCheckboxes() {
        List<Boolean> selectedCheckboxes = new ArrayList<>();
        selectedCheckboxes.add(this.nameCheckBox.isChecked());
        selectedCheckboxes.add(this.priceCheckBox.isChecked());
        selectedCheckboxes.add(this.dateCheckBox.isChecked());
        return selectedCheckboxes;
    }
}
