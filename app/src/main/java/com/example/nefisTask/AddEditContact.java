package com.example.nefisTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditContact extends Activity {

    enum Validation {CORRECT, NAME_ERROR, BIRTHDAY_ERROR, PHONE_ERROR, EMAIL_ERROR}

    private long rowID;
    private EditText nameEditText;
    private EditText birthdayEditText;
    private RadioButton genderMaleRadioButton;
    private EditText phoneEditText;
    private EditText emailEditText;
    private final static Calendar CALENDAR = Calendar.getInstance();

    OnClickListener saveContactButtonClicked = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Validation valid = validation(nameEditText,
                    phoneEditText, emailEditText);
            if (valid == Validation.CORRECT) {
                AsyncTask<Object, Object, Object> saveContactTask =
                        new AsyncTask<Object, Object, Object>() {
                            @Override
                            protected Object doInBackground(Object... params) {
                                saveContact(); // save contact to the database
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object result) {
                                finish(); // return to the previous Activity
                            }
                        };
                saveContactTask.execute((Object[]) null);
            } else {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(AddEditContact.this);

                // choice of error message
                builder.setTitle(R.string.errorTitle);
                switch (valid) {
                    case NAME_ERROR:
                        builder.setMessage(R.string.errorNameMessage);
                        break;
                    case BIRTHDAY_ERROR:
                        builder.setMessage(R.string.errorBirthdayMessage);
                        break;
                    case PHONE_ERROR:
                        builder.setMessage(R.string.errorPhoneMessage);
                        break;
                    case EMAIL_ERROR:
                        builder.setMessage(R.string.errorEmailMessage);
                        break;
                }
                builder.setPositiveButton(R.string.errorButton, null);
                builder.show();
            }
        } // end method onClick
    };

    // called when the Activity is first started
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        birthdayEditText = (EditText) findViewById(R.id.birthdayEditText);
        genderMaleRadioButton = ((RadioButton) findViewById(R.id.genderMaleRadioButton));
        RadioButton genderFemaleRadioButton = ((RadioButton) findViewById(R.id.genderFemaleRadioButton));
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            rowID = extras.getLong("row_id");
            nameEditText.setText(extras.getString("name"));
            birthdayEditText.setText(extras.getString("birthday"));
            if (extras.getString("gender").equals(getResources().getString(R.string.gender_male)))
                genderMaleRadioButton.setChecked(true);
            else
                genderFemaleRadioButton.setChecked(true);

            phoneEditText.setText(extras.getString("phone"));
            emailEditText.setText(extras.getString("email"));
        }
        Button saveContactButton =
                (Button) findViewById(R.id.saveContactButton);
        saveContactButton.setOnClickListener(saveContactButtonClicked);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                CALENDAR.set(Calendar.YEAR, year);
                CALENDAR.set(Calendar.MONTH, monthOfYear);
                CALENDAR.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        final DatePickerDialog datePickerDialog = new DatePickerDialog(AddEditContact.this, date,
                CALENDAR.get(Calendar.YEAR), CALENDAR.get(Calendar.MONTH),
                CALENDAR.get(Calendar.DAY_OF_MONTH));


        birthdayEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    datePickerDialog.show();
                    birthdayEditText.clearFocus();
                    phoneEditText.requestFocus();
                }
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        birthdayEditText.setText(sdf.format(CALENDAR.getTime()));
    }

    // saves contact information to the database
    private void saveContact() {
        DatabaseConnector databaseConnector = new DatabaseConnector(this);

        if (getIntent().getExtras() == null) {
            databaseConnector.insertContact(
                    nameEditText.getText().toString(),
                    birthdayEditText.getText().toString(),
                    genderMaleRadioButton.isChecked() ?
                            getResources().getString(R.string.gender_male) :
                            getResources().getString(R.string.gender_female),
                    phoneEditText.getText().toString(),
                    emailEditText.getText().toString());
        } else {
            databaseConnector.updateContact(rowID,
                    nameEditText.getText().toString(),
                    birthdayEditText.getText().toString(),
                    genderMaleRadioButton.isChecked() ?
                            getResources().getString(R.string.gender_male) :
                            getResources().getString(R.string.gender_female),
                    phoneEditText.getText().toString(),
                    emailEditText.getText().toString());
        }
    }

    private static Validation validation(EditText name, EditText phone, EditText email) {
        if (!isValidName(name.getText().toString()))
            return Validation.NAME_ERROR;
        if (!isValidPhone(phone.getText().toString()))
            return Validation.PHONE_ERROR;
        if (!isValidEmail(email.getText().toString()))
            return Validation.EMAIL_ERROR;

        return Validation.CORRECT;
    }

    private static boolean isValidName(String target) {
        return target != null && target.length() != 0;
    } // this field cannot be empty

    private static boolean isValidPhone(String target) {
        return target != null && (target.length() == 0 || Patterns.PHONE.matcher(target).matches());
    } // this field can be empty

    private static boolean isValidEmail(String target) {
        return target != null && (target.length() == 0 ||
                Patterns.EMAIL_ADDRESS.matcher(target).matches());
    } // this field can be empty
}
