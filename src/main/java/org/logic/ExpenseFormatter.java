package org.logic;

import java.util.ArrayList;

public class ExpenseFormatter {
    private final StringBuilder personalExpenses;
    private final StringBuilder generalExpenses;

    public ExpenseFormatter() {
        personalExpenses = new StringBuilder("Personal Expenses:\n");
        generalExpenses = new StringBuilder("General Expenses:\n");
    }

    public void formatExpenses(ArrayList<Transaction> transactions, ExpenseCalculator expenseCalculator) {
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                continue;
            }

            if ("Personal".equalsIgnoreCase(transaction.getType())) {
                personalExpenses.append(transaction).append("\n");
            } else if ("General".equalsIgnoreCase(transaction.getType())) {
                generalExpenses.append(transaction).append("\n");
            } else if (transaction instanceof SplitTransaction splitTransaction) {

                generalExpenses.append(String.format("Date: %s, Posted: %s, Description: %s, Amount: %.2f, Category: %s (Split - General)\n",
                        splitTransaction.date, splitTransaction.postedDate, splitTransaction.description,
                        splitTransaction.getGeneralAmount(), splitTransaction.category));

                personalExpenses.append(String.format("Date: %s, Posted: %s, Description: %s, Amount: %.2f, Category: %s (Split - Personal)\n",
                        splitTransaction.getDate(), splitTransaction.postedDate, splitTransaction.description,
                        splitTransaction.getSplitAmount(), splitTransaction.category));
            }
        }

        personalExpenses.append("\nTotal Personal Expenses: ").append(String.format("%.2f", expenseCalculator.getPersonalTotal())).append("\n");
        generalExpenses.append("\nTotal General Expenses: ").append(String.format("%.2f", expenseCalculator.getGeneralTotal())).append("\n");
    }

    public String getPersonalExpenses() {
        return personalExpenses.toString();
    }

    public String getGeneralExpenses() {
        return generalExpenses.toString();
    }
}