// AccountInfo.java

package com.mirzet.mbanking.ViewModels;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

public class AccountInfo {
    private String userID;
    private Acount[] acounts;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String value) {
        this.userID = value;
    }

    public Acount[] getAcounts() {
        return acounts;
    }

    public void setAcounts(Acount[] value) {
        this.acounts = value;
    }

    //Should be Account but in
    public class Acount {
        private String id;
        private String IBAN;
        private String amount;
        private String currency;
        private Transaction[] transactions;

        public String getID() {
            return id;
        }

        public void setID(String value) {
            this.id = value;
        }

        public String getIban() {
            return IBAN;
        }

        public void setIban(String value) {
            this.IBAN = value;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String value) {
            this.amount = value;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String value) {
            this.currency = value;
        }

        public Transaction[] getTransactions() {
            return transactions;
        }

        public List<Transaction> getTransactions(int year) {
            List<AccountInfo.Transaction> trans = new ArrayList<>();
            for (AccountInfo.Transaction transaction : transactions) {
                if (getYear(transaction.getDate()) != year)
                    continue;
                trans.add(transaction);
            }
            return trans;
        }

        public TreeMap<String, List<AccountInfo.Transaction>> getSortedTransactions(int year) {
            List<AccountInfo.Transaction> trans = getTransactions(year);
            Collections.reverse(trans);
            TreeMap<String, List<AccountInfo.Transaction>> values = new TreeMap<>(Collections.reverseOrder());
            for (int i = 0; i < trans.size(); i++) {
                AccountInfo.Transaction transaction = trans.get(i);
                if (getYear(transaction.getDate()) != year)
                    continue;
                SimpleDateFormat month_date = new SimpleDateFormat("MM");
                String month_name = month_date.format(transaction.getDate());
                addNewValue(values, month_name, transaction);
            }
            return values;
        }

        private TreeMap<String, List<Transaction>> sortTreeMap(TreeMap<String, List<Transaction>> listTreeMap) {
            TreeMap<String, List<Transaction>> sorted = new TreeMap<>();
            List<String> keys = new ArrayList<>(listTreeMap.keySet());
            Collections.reverse(keys);

            for (int i = 0; i < keys.size(); i++) {
                String item = keys.get(i);
                sorted.put(item, listTreeMap.get(item));
            }
            return sorted;
        }

        public void addNewValue(TreeMap<String, List<AccountInfo.Transaction>> TreeMap, String key, AccountInfo.Transaction transaction) {
            if (TreeMap.containsKey(key)) {
                List<AccountInfo.Transaction> transactions = TreeMap.get(key);
                transactions.add(transaction);
                TreeMap.put(key, transactions);
            } else {
                List<AccountInfo.Transaction> transactions = new ArrayList<AccountInfo.Transaction>();
                transactions.add(transaction);
                TreeMap.put(key, transactions);
            }
        }

        public String[] getYears() {
            List<String> years = new ArrayList<>();
            for (Transaction trans : getTransactions()) {
                String year = Integer.toString(getYear(trans.getDate()));
                if (!years.contains(year))
                    years.add(year);
            }
            Collections.sort(years);
            return years.toArray(new String[years.size()]);
        }

        public void setTransactions(Transaction[] value) {
            this.transactions = value;
        }
    }

    public class Transaction implements Comparable<Transaction> {
        private String id;
        private String date;
        private String description;
        private String amount;
        private String type;

        public String getID() {
            return id;
        }

        public void setID(String value) {
            this.id = value;
        }

        public String getDateString() {
            return date;
        }

        public Date getDate() {
            return stringToDate(getDateString(), "dd.MM.yyyy");
        }

        public void setDate(String value) {
            this.date = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String value) {
            this.description = value;
        }

        public String getAmount() {
            return amount;
        }

        public double getAmountDouble() {
            return Double.parseDouble(getAmount().replace(".", "").replace(",", ".").split(" ")[0]);
        }

        public void setAmount(String value) {
            this.amount = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String value) {
            this.type = value;
        }

        @Override
        public int compareTo(Transaction o) {
            return getDate().compareTo(o.getDate());
        }


    }

    public static Date stringToDate(String aDate, String aFormat) {

        if (aDate == null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;
    }

    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

}
