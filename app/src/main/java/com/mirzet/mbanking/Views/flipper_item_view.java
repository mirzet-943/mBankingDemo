package com.mirzet.mbanking.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mirzet.mbanking.ViewModels.AccountInfo;
import com.mirzet.mbanking.R;

/**
 * TODO: document your custom view class.
 */
public class flipper_item_view extends LinearLayout {


    private TextView mAmountText, mIBANText, mCurrencyText;
    private AccountInfo.Acount Account ;

    public flipper_item_view(Context context, AccountInfo.Acount account) {
        super(context, null);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.flipper_item_account, this, true);
        mAmountText = view.findViewById(R.id.amountTextview);
        mIBANText = view.findViewById(R.id.ibanTextView);
        mCurrencyText = view.findViewById(R.id.currencyTextView);
        this.Account = account;
        setData(account.getIban(),account.getAmount(), account.getCurrency());
    }
    public void setData(String IBAN, String amount, String currency) {
        mAmountText.setText(amount);
        mIBANText .setText("IBAN: " + IBAN);
        mCurrencyText.setText(currency);
    }
    public AccountInfo.Acount getAccount(){
        return Account;
    }

}
