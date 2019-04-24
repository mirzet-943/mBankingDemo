package com.mirzet.mbanking.interfaces;

import okhttp3.Response;

public interface IDataHandler {
    public void onDataReceived(Response response);
    public void onDataFault(String error);
}
