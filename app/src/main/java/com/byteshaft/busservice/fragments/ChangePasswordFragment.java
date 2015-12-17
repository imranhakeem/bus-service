package com.byteshaft.busservice.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.busservice.R;

public class ChangePasswordFragment extends Fragment {

    private View mBaseView;
    EditText editTextPasswordOld;
    EditText editTextPasswordNew;
    EditText editTextPasswordRepeat;
    Button buttonDone;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.layout_change_password, container, false);
        editTextPasswordOld = (EditText) mBaseView.findViewById(R.id.input_password_old);
        editTextPasswordNew = (EditText) mBaseView.findViewById(R.id.input_password_new);
        editTextPasswordRepeat = (EditText) mBaseView.findViewById(R.id.input_password_repeat);
        buttonDone = (Button) mBaseView.findViewById(R.id.btn_change_password);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        return mBaseView;
    }

    public void signup() {
        Log.d("BusService", "PasswordChange");

        if (!validate()) {
            onChangeFailed();
            return;
        }

        buttonDone.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Changing password...");
        progressDialog.show();

        String passwordOld = editTextPasswordOld.getText().toString();
        String passwordNew = editTextPasswordNew.getText().toString();
        String passwordRepeat = editTextPasswordRepeat.getText().toString();

        // TODO: Implement password change logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onChangeSuccess();
                        progressDialog.dismiss();
                    }
                }, 2000);
    }

    public void onChangeSuccess() {
        buttonDone.setEnabled(true);
        getActivity().finish();
    }

    public void onChangeFailed() {
        Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_LONG).show();
        buttonDone.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String passwordOld = editTextPasswordOld.getText().toString();
        String passwordNew = editTextPasswordNew.getText().toString();
        String passwordRepeat = editTextPasswordRepeat.getText().toString();

        if (passwordOld.trim().isEmpty() || passwordOld.length() < 4 ) {
            editTextPasswordOld.setError("at least 4 characters");
            valid = false;
        } else {
            editTextPasswordOld.setError(null);
        }

        if (passwordNew.trim().isEmpty() || passwordNew.length() < 4 ) {
            editTextPasswordNew.setError("at least 4 characters");
            valid = false;
        } else {
            editTextPasswordNew.setError(null);
        }

        if (passwordRepeat.trim().isEmpty() || passwordRepeat.length() < 4) {
            editTextPasswordRepeat.setError("at least 4 characters");
            valid = false;
        } else {
            editTextPasswordRepeat.setError(null);
        }

        if (!passwordNew.equals(passwordRepeat)) {
            editTextPasswordRepeat.setError("password doesn't match");
            valid = false;
        } else {
            editTextPasswordRepeat.setError(null);
        }

        return valid;
    }


}