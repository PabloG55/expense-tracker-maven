package org.logic;

import java.util.*;

public class InsightProvider {
    private final ArrayList<Transaction> transactions;

    public InsightProvider(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Map<String, Integer> getCategoryFrequency() {
        Map<String, Integer> categoryFrequency = new HashMap<>();
        for (Transaction transaction : transactions) {
            String category = transaction.getCategory();
            categoryFrequency.put(category, categoryFrequency.getOrDefault(category, 0) + 1);
        }
        return sortByValue(categoryFrequency);
    }

    public Map<String, Double> getCategoryTotalSpending() {
        Map<String, Double> categorySpending = new HashMap<>();
        for (Transaction transaction : transactions) {
            String category = transaction.getCategory();
            double amount = transaction.getAmount();
            categorySpending.put(category, categorySpending.getOrDefault(category, 0.0) + amount);
        }
        return sortByValue(categorySpending);
    }

    public Map<String, Integer> getMerchantFrequency() {
        Map<String, Integer> merchantFrequency = new HashMap<>();
        for (Transaction transaction : transactions) {
            String merchant = transaction.getDescription(); // Assuming description contains merchant name
            merchantFrequency.put(merchant, merchantFrequency.getOrDefault(merchant, 0) + 1);
        }
        return sortByValue(merchantFrequency);
    }

    public Map<String, Double> getMerchantTotalSpending() {
        Map<String, Double> merchantSpending = new HashMap<>();
        for (Transaction transaction : transactions) {
            String merchant = transaction.getDescription();
            double amount = transaction.getAmount();
            merchantSpending.put(merchant, merchantSpending.getOrDefault(merchant, 0.0) + amount);
        }
        return sortByValue(merchantSpending);
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public String getFormattedInsights() {
        StringBuilder insights = new StringBuilder();
        insights.append("Expense Insights:\n\n");

        insights.append("Top 5 Most Frequent Categories:\n");
        insights.append(getTop5Formatted(getCategoryFrequency()));

        insights.append("\nTop 5 Categories by Total Spending:\n");
        insights.append(getTop5Formatted(getCategoryTotalSpending()));

        insights.append("\nTop 5 Most Frequent Merchants:\n");
        insights.append(getTop5Formatted(getMerchantFrequency()));

        insights.append("\nTop 5 Merchants by Total Spending:\n");
        insights.append(getTop5Formatted(getMerchantTotalSpending()));

        return insights.toString();
    }

    private <K, V> String getTop5Formatted(Map<K, V> map) {
        StringBuilder result = new StringBuilder();
        int count = 0;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            result.append(String.format("%s: %s%n", entry.getKey(), entry.getValue()));
            if (++count == 5) break;
        }
        return result.toString();
    }
}