package matwes.zpi.profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import matwes.zpi.AsyncTaskCompleteListener;
import matwes.zpi.Common;
import matwes.zpi.GetMethodAPI;
import matwes.zpi.R;
import matwes.zpi.RequestAPI;
import matwes.zpi.domain.User;
import matwes.zpi.utils.CustomDialog;

/**
 * Created by Mateusz Wesołowski
 */

public class MyProfileFragment extends Fragment {
    private static final String UPDATE_PROFILE = Common.URL + "/users/";
    private EditText etFirstName, etLastName, etBirthDay, etDescription;
    private AutoCompleteTextView actvSex;
    private ArrayAdapter<CharSequence> adapter;
    private String sDate;
    private long userId;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.my_profile_label));


        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etBirthDay = view.findViewById(R.id.etBirthDate);
        etDescription = view.findViewById(R.id.etDescription);
        actvSex = view.findViewById(R.id.actvSex);

        adapter = ArrayAdapter.createFromResource(getContext(), R.array.sex_list, R.layout.spinner_item);
        actvSex.setAdapter(null);
        actvSex.setKeyListener(null);
        actvSex.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((AutoCompleteTextView) v).showDropDown();
                return false;
            }
        });

        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                sDate = String.format("%02d-%02d-%02d", year, month + 1, day);
                etBirthDay.setText(day + "-" + (month + 1) + "-" + year);
            }
        };

        etBirthDay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    new DatePickerDialog(getContext(), datePicker, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        userId = Common.getCurrentUserId(getContext());

        view.findViewById(R.id.btnSaveProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEvent();
            }
        });

        view.findViewById(R.id.btnChangePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                intent.putExtra("userId", userId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });

        getProfile();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.filter).setVisible(false);
        menu.findItem(R.id.change_view).setVisible(false);
        menu.findItem(R.id.refresh).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private String getCodeSex(String sex) {
        String result = "NOT_SPECIFIED";
        switch (sex) {
            case "Kobieta":
                result = "FEMALE";
                break;
            case "Mężczyzna":
                result = "MALE";
                break;
        }
        return result;
    }

    private void updateEvent() {
        JSONObject json = new JSONObject();
        try {
            json.put("birthday", sDate);
            json.put("description", etDescription.getText().toString());
            json.put("firstName", etFirstName.getText().toString());
            json.put("lastName", etLastName.getText().toString());
            json.put("sex", getCodeSex(actvSex.getText().toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new RequestAPI(getContext(), "PATCH", json.toString(), new AsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskComplete(String result) {
                if (result.equals("200")) {
                    CustomDialog.showInfo(getContext(), getString(R.string.error_message));
                } else {
                    CustomDialog.showError(getContext(), getString(R.string.error_message));
                }
            }
        }, true).execute(UPDATE_PROFILE + userId);
    }

    private void getProfile() {
        new GetMethodAPI(getContext(), new AsyncTaskCompleteListener<String>() {
            @Override
            public void onTaskComplete(String result) {
                Gson gson = new GsonBuilder().create();
                User user = gson.fromJson(result, User.class);
                etFirstName.setText(user.getFirstName());
                etLastName.setText(user.getLastName());
                etBirthDay.setText(user.getBirthday());
                sDate = user.getBirthday();
                etDescription.setText(user.getDescription());
                actvSex.setText(Common.getPolishSex(user.getSex()));
                actvSex.setAdapter(adapter);
            }
        }, true).execute(UPDATE_PROFILE + userId);
    }
}
