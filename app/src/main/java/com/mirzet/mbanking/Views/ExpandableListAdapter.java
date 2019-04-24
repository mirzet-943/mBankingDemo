package com.mirzet.mbanking.Views;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.mirzet.mbanking.ViewModels.AccountInfo;
import com.mirzet.mbanking.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private TreeMap<String, List<AccountInfo.Transaction>> _listDataChild;

    public ExpandableListAdapter(Context context,
                                 TreeMap<String, List<AccountInfo.Transaction>> listChildData) {
        this._context = context;
        this._listDataHeader = new ArrayList<>(listChildData.keySet());
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final AccountInfo.Transaction transaction = (AccountInfo.Transaction) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.transaction_sample, null);
        }
        TextView description = (TextView) convertView
                .findViewById(R.id.lblDescription);
        TextView date = (TextView) convertView
                .findViewById(R.id.lblDate);
        TextView amount = (TextView) convertView
                .findViewById(R.id.lblAmount);

        description.setText(transaction.getDescription());
        amount.setText(transaction.getAmount());
        SimpleDateFormat format = new SimpleDateFormat("dd MMM,yyyy");
        String dateString = format.format(transaction.getDate());
        date.setText(dateString);
        if (transaction.getAmountDouble() > 0)
            amount.setTextColor(_context.getResources().getColor(R.color.Green));
        else
            amount.setTextColor(_context.getResources().getColor(R.color.Red));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        int month = Integer.parseInt(headerTitle);
        headerTitle = new DateFormatSymbols().getMonths()[month-1];
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.transaction_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}