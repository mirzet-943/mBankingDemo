package com.mirzet.mbanking.interfaces;


import okhttp3.Response;

public interface AccountCallbacks {
    public void onAccountCreated();
    public void onPinSuccess();
    public void signIn();

}

