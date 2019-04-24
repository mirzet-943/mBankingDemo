package com.mirzet.mbanking.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.mirzet.mbanking.ViewModels.AccountInfo;
import com.mirzet.mbanking.Views.ExpandableListAdapter;
import com.mirzet.mbanking.Helpers.HttpHelper;
import com.mirzet.mbanking.interfaces.IDataHandler;
import com.mirzet.mbanking.Helpers.PreferencesSettings;
import com.mirzet.mbanking.R;
import com.mirzet.mbanking.Views.flipper_item_view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class Bank_Accounts extends AppCompatActivity implements IDataHandler {

    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    private static String accountInfoUri = "http://mobile.asseco-see.hr/builds/ISBD_public/Zadatak_1.json";
    HttpHelper httpHelper;
    private AccountInfo.Acount currentSelected;
    private AccountInfo allAccounts;
    ExpandableListAdapter transactionsAdapter;
    ExpandableListView transactionsView;
    Spinner yearCombo;
    public Context context;
    LinearLayout dataPreview;
    View loadingDialog;
    TextView lblInfo;
    ImageButton fullScreenSwitch;
    ImageButton signOut;

    @Override
    public void setTitle(CharSequence title) {
        TextView header = findViewById(R.id.title);
        if (header != null)
            header.setText(title);
        super.setTitle(title);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_accounts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle(PreferencesSettings.getFullName(this));
        this.context = this;
        mViewFlipper = findViewById(R.id.accounts_flipper);
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        httpHelper = new HttpHelper(this);
        transactionsView = findViewById(R.id.transactionsListView);
        yearCombo = findViewById(R.id.yearsCombo);
        loadingDialog = findViewById(R.id.error_dialog);
        dataPreview = findViewById(R.id.dataPreview);
        lblInfo = findViewById(R.id.status);
        fullScreenSwitch = findViewById(R.id.full_screen);
        signOut = findViewById(R.id.sign_out_btn);
        yearCombo.setOnItemSelectedListener(setYearsAdapter());
        dataPreview.setVisibility(View.GONE);
        httpHelper.HttpRequest_GET(accountInfoUri);
        setupListeners();
    }

    private void SetupFlipView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataPreview.setVisibility(View.VISIBLE);
                loadingDialog.setVisibility(View.GONE);
                if (allAccounts == null) {
                    return;
                }
                if (allAccounts.getAcounts() != null) {
                    for (AccountInfo.Acount acc : allAccounts.getAcounts()) {
                        LinearLayout layout = new flipper_item_view(getApplicationContext(), acc);
                        mViewFlipper.addView(layout);
                    }
                    mViewFlipper.setDisplayedChild(0);
                }
                setupAdapters();
            }
        });
    }

    @Override
    public void onDataReceived(Response response) {
        if (response.isSuccessful()) {
            if (response.body() != null) {
                try {
                    String jsonString = response.body().string();
                    allAccounts = HttpHelper.ParseFromJson(jsonString, AccountInfo.class);
                    SetupFlipView();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        lblInfo.setText("Error while fetching data\n" + response.code());
        loadingDialog.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDataFault(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lblInfo.setText("Error while fetching data\n" + error);
                final ProgressBar bar = findViewById(R.id.progress_circular);
                final Button retry = findViewById(R.id.retry_btn);
                bar.setVisibility(View.GONE);
                retry.setVisibility(View.VISIBLE);
                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        httpHelper.HttpRequest_GET(accountInfoUri);
                        bar.setVisibility(View.VISIBLE);
                        retry.setVisibility(View.GONE);
                    }
                });
                loadingDialog.setVisibility(View.VISIBLE);
            }
        });

    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Swipe left (next)
            if (e1.getX() > e2.getX()) {
                mViewFlipper.setInAnimation(getApplicationContext(), R.anim.slide_in_right);
                mViewFlipper.setOutAnimation(getApplicationContext(), R.anim.slide_out_left);
                mViewFlipper.showNext();
                setupAdapters();
            }
            // Swipe right (previous)
            if (e1.getX() < e2.getX()) {
                mViewFlipper.setInAnimation(getApplicationContext(), android.R.anim.slide_in_left);
                mViewFlipper.setOutAnimation(getApplicationContext(), android.R.anim.slide_out_right);
                mViewFlipper.showPrevious();
                setupAdapters();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            ChooseAccount();
            return true;
        }
    }

    private flipper_item_view current;

    private void setupAdapters() {
        current = (flipper_item_view) mViewFlipper.getCurrentView();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, current.getAccount().getYears());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearCombo.setAdapter(adapter);
        yearCombo.setSelection(0, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void ChooseAccount() {
        if (allAccounts.getAcounts() != null) {
            List<String> accs = new ArrayList<>();
            String[] Accounts = new String[allAccounts.getAcounts().length];
            for (int i = 0; i < allAccounts.getAcounts().length; i++) {
                AccountInfo.Acount acc = allAccounts.getAcounts()[i];
                accs.add(Integer.toString(i + 1) + ".  Currency=" + acc.getCurrency());
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose account");
            builder.setItems(accs.toArray(new String[0]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mViewFlipper.setDisplayedChild(which);
                    setupAdapters();
                }
            });
            builder.show();
        }
    }

    private AdapterView.OnItemSelectedListener setYearsAdapter() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                flipper_item_view current = (flipper_item_view) mViewFlipper.getCurrentView();
                if (view != null) {
                    TextView txt = view.findViewById(android.R.id.text1);
                    if (txt != null) {
                        String value = txt.getText().toString();
                        int Year = Integer.parseInt(value);
                        transactionsAdapter = new ExpandableListAdapter(context, current.getAccount().getSortedTransactions(Year));
                        transactionsView.setAdapter(transactionsAdapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private void setupListeners() {

        mViewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true;
            }

        });
        fullScreenSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = mViewFlipper.getVisibility() == View.VISIBLE;
                if (b) {
                    mViewFlipper.setVisibility(View.GONE);
                } else {
                    mViewFlipper.setVisibility(View.VISIBLE);
                }
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                finish();
            }
        });
    }
}
