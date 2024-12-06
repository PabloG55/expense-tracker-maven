package org.logic;

import java.util.regex.*;
import java.util.ArrayList;

public class TransactionParser {
    private final ArrayList<Transaction> transactions = new ArrayList<>();


    public TransactionParser(ArrayList<String> transactionLines) {
        //Regex documentation from: https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
        Pattern pattern = Pattern.compile(
    "(\\d{1,2}/\\d{1,2}/\\d{4})\\t(\\d{1,2}/\\d{1,2}/\\d{4})\\t(.+?)\\t(-?\\d+(?:\\.\\d{1,2})?)\\t(.+)"
        );

        for (String line : transactionLines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                if (!matcher.matches()) {
                    System.out.println("Skipped line: " + line); // Or handle the error appropriately.
                }
                String date = matcher.group(1);
                String postedDate = matcher.group(2);
                String description = matcher.group(3);
                double amount = Double.parseDouble(matcher.group(4));
                String category = matcher.group(5);

                Transaction transaction = new Transaction(date, postedDate, description, amount, category, null);
                transactions.add(transaction);
            }
        }
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
}
