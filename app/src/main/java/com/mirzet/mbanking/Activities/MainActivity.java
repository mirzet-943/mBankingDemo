package com.mirzet.mbanking.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mirzet.mbanking.interfaces.AccountCallbacks;
import com.mirzet.mbanking.Helpers.AccountUtils;
import com.mirzet.mbanking.Fragments.Home_Fragment;
import com.mirzet.mbanking.Fragments.Pincode_Login;
import com.mirzet.mbanking.Fragments.RegisterFragment;
import com.mirzet.mbanking.R;

public class MainActivity extends AppCompatActivity implements AccountCallbacks {

    private Pincode_Login loginFragment = Pincode_Login.getInstance(this);
    private RegisterFragment registerFragment = RegisterFragment.getInstance(this);
    private Home_Fragment home_fragment = Home_Fragment.getInstance(this);
    private AccountUtils PincodeHelper = new AccountUtils();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_main);
        if (PincodeHelper.isPinCodeEncryptionKeyExist())
            replaceFragment(home_fragment);
        else
            replaceFragment(registerFragment);
    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    @Override
    public void onAccountCreated() {

        replaceFragment(loginFragment);

    }

    @Override
    public void onPinSuccess() {
        Intent intent = new Intent(this, Bank_Accounts.class);
        this.startActivity(intent);
        finish();
    }

    @Override
    public void signIn() {
        replaceFragment(loginFragment);
    }
}
