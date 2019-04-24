package com.mirzet.mbanking.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mirzet.mbanking.interfaces.AccountCallbacks;
import com.mirzet.mbanking.Helpers.PreferencesSettings;
import com.mirzet.mbanking.R;

public class Home_Fragment extends Fragment {

    public static Home_Fragment getInstance(AccountCallbacks callback){
        Home_Fragment fragment = new Home_Fragment();
        fragment.mSignInCallback = callback;
        return fragment;
    }

    public AccountCallbacks mSignInCallback;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button signInButton = view.findViewById(R.id.signIn);
        TextView mTitle = view.findViewById(R.id.title_text);
        mTitle.setText("Welcome\n" + PreferencesSettings.getFullName(this.getContext()));
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignInCallback.signIn();
            }
        });
        return view;
    }
}
