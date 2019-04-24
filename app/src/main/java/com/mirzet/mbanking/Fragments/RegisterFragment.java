package com.mirzet.mbanking.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mirzet.mbanking.interfaces.AccountCallbacks;
import com.mirzet.mbanking.Helpers.PreferencesSettings;
import com.mirzet.mbanking.R;

public class RegisterFragment extends Fragment {

    View FragmentView;
    EditText mFirstName;
    EditText mLastname;
    Button mRegisterButton;
    public AccountCallbacks mCreatedCallback;

    public static RegisterFragment getInstance(AccountCallbacks callback){
        RegisterFragment fragment = new RegisterFragment();
        fragment.mCreatedCallback = callback;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register, container, false);
        FragmentView = view;
        mFirstName = FragmentView.findViewById(R.id.input_firstname);
        mLastname = FragmentView.findViewById(R.id.input_lastname);
        mRegisterButton = FragmentView.findViewById(R.id.btn_signup);
        mRegisterButton.setOnClickListener(submitListener());
        return FragmentView;
    }
    private View.OnClickListener submitListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        };
    }
    private void attemptRegister() {
        mFirstName.setError(null);
        mLastname.setError(null);
        String firstName = mFirstName.getText().toString();
        String lastName = mLastname.getText().toString();
        boolean cancel = false;
        if (firstName.length() <= 1)
        {
            mFirstName.setError("Field can't be blank");
            cancel = true;
        }
        if (lastName.length() <= 1)
        {
            mLastname.setError("Field can't be blank");
            cancel = true;
        }
        if (!cancel){
            String fullName = firstName + " " + lastName;
            mCreatedCallback.onAccountCreated();
            PreferencesSettings.saveToPref(FragmentView.getContext(), "Full_Name", fullName );
        }
    }
}
