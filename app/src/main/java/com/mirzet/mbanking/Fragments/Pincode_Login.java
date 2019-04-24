package com.mirzet.mbanking.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mirzet.mbanking.interfaces.AccountCallbacks;
import com.mirzet.mbanking.Helpers.AccountUtils;
import com.mirzet.mbanking.Helpers.PreferencesSettings;
import com.mirzet.mbanking.R;

public class Pincode_Login extends Fragment {

    private AccountUtils PincodeHelper = new AccountUtils();
    private View FragmentView;
    AccountCallbacks callback;
    public static Pincode_Login getInstance(AccountCallbacks callback){
        Pincode_Login fragment = new Pincode_Login();
        fragment.callback = callback;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setup_pincode, container, false);
        FragmentView = view;
        mPincodeView = FragmentView.findViewById(R.id.pincode_text);
        mInfoTextView = FragmentView.findViewById(R.id.info_text);
        mPincodeView.setShowSoftInputOnFocus(false);
        if (PincodeHelper.isPinCodeEncryptionKeyExist())
            mInfoTextView.setText("Please enter your PIN");
        setupKeyboard();
        return FragmentView;
    }

    private boolean creatingPincode = false;
    private TextView mInfoTextView;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private String mPassword;
    private EditText mPincodeView;
    // UI references.

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    public void attemptLogin() {
        mPincodeView.setError(null);
        if (creatingPincode) {
            if (!setupPincode(mPassword)){
                mInfoTextView.setText("Pincode incorrect\nCreate new pincode");
                creatingPincode = false;
                mPincodeView.setText("");
            }
            return;
        }
        mPassword = mPincodeView.getText().toString();
        if (mPassword.length() < 4) {
            mPincodeView.setError(getString(R.string.error_field_required));
            return;
        }

        if (PincodeHelper.isPinCodeEncryptionKeyExist()) {
            String mCurrentPincode = PreferencesSettings.getCode(this.getContext());
            if (PincodeHelper.checkPin(this.getContext(), mCurrentPincode, mPassword)) {
                Toast.makeText(this.getContext(), "Login Success", Toast.LENGTH_SHORT).show();
                callback.onPinSuccess();
            }
            else{
                final Animation animShake = AnimationUtils.loadAnimation(FragmentView.getContext(), R.anim.shake);
                FragmentView.startAnimation(animShake);
                mPincodeView.setText("");
                mInfoTextView.setText(getString(R.string.incorrect_pincode));
            }
        } else {
            creatingPincode = true;
            mPincodeView.setText("");
            mInfoTextView.setText(getString(R.string.retype_pincode));
        }
    }

    public boolean setupPincode(String pin) {
        String repeatedPincode = mPincodeView.getText().toString();
        if (mPassword.length() < 4) {
            mPincodeView.setError(getString(R.string.error_field_required));
            return false;
        }
        if (!PincodeHelper.isPinCodeEncryptionKeyExist() && repeatedPincode.equals(mPassword)) {
            boolean valid = PincodeHelper.encodePin(this.getContext(), repeatedPincode);
            if (valid) {
                callback.onPinSuccess();
            }
        }
        return false;
    }

    private void setupKeyboard() {
        for (int i = 0; i < 10; i++) {
            int id = this.getResources().getIdentifier("pad_" + i, "id", FragmentView.getContext().getPackageName());
            Button btn = FragmentView.findViewById(id);
            final int keyCode = i + 7;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPincodeView.dispatchKeyEvent(new KeyEvent(15, 0, KeyEvent.ACTION_DOWN,
                            keyCode, 0));
                    mPincodeView.dispatchKeyEvent(new KeyEvent(15, 0, KeyEvent.ACTION_UP,
                            keyCode, 0));
                }
            });
        }
        Button btnDelete = FragmentView.findViewById(R.id.btn_delete);
        Button btnSubmit = FragmentView.findViewById(R.id.btn_submit);
        btnDelete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mPincodeView.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DEL, 0));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mPincodeView.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP,
                            KeyEvent.KEYCODE_DEL, 0));
                }
                return true;
            }
        });
        btnDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mPincodeView.setText("");
                return false;
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }
}
