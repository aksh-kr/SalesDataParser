package org.example.hms;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SalesDataAnalyzer {

    public static void main(String[] args) {
        String fileName = "C:\\Users\\91850\\dev\\sales-data.txt"; // Replace with your file path

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            List<Sale> salesData = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length != 5) {
                    System.err.println("Invalid data format: " + line);
                    continue;
                }

                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = dateFormat.parse(fields[0]);

                    salesData.add(new Sale(
                            date,
                            fields[1],
                            Double.parseDouble(fields[2]),
                            Integer.parseInt(fields[3]),
                            Double.parseDouble(fields[4])
                    ));
                } catch (ParseException e) {
                    System.err.println("Invalid date format: " + fields[0]);
                    continue;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid numeric value: " + line);
                    continue;
                }
            }

            // Process and analyze the sales data
            calculateTotalSales(salesData);
            calculateMonthlySales(salesData);
            findMostPopularItemPerMonth(salesData);
            findRevenueGeneratingItems(salesData);
            findMostPopularItemStats(salesData);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public static void calculateTotalSales(List<Sale> salesData) {
        double totalSales = 0;
        for (Sale sale : salesData) {
            totalSales += sale.getTotalPrice();
        }
        System.out.println("Total Sales: " + totalSales);
    }

    public static void calculateMonthlySales(List<Sale> salesData) {
        Map<String, Double> monthlySales = new HashMap<>();
        for (Sale sale : salesData) {
            String month = new SimpleDateFormat("yyyy-MM").format(sale.getDate());
            monthlySales.putIfAbsent(month, 0.0);
            monthlySales.put(month, monthlySales.get(month) + sale.getTotalPrice());
        }

        System.out.println("Monthly Sales:");
        for (Map.Entry<String, Double> entry : monthlySales.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public static void findMostPopularItemPerMonth(List<Sale> salesData) {
        Map<String, Map<String, Integer>> itemPopularity = new HashMap<>();

        for (Sale sale : salesData) {
            String month = new SimpleDateFormat("yyyy-MM").format(sale.getDate());

            // Check if the month exists in the itemPopularity map
            Map<String, Integer> monthMap = itemPopularity.get(month);
            if (monthMap == null) {
                monthMap = new HashMap<>();
                itemPopularity.put(month, monthMap);
            }

            // Check if the SKU exists in the monthMap
            Integer quantity = monthMap.get(sale.getSku());
            if (quantity == null) {
                monthMap.put(sale.getSku(), sale.getQuantity());
            } else {
                monthMap.put(sale.getSku(), quantity + sale.getQuantity());
            }
        }

        System.out.println("Most Popular Item Per Month:");
        for (Map.Entry<String, Map<String, Integer>> entry : itemPopularity.entrySet()) {
            String mostPopularItem = null;
            int maxQuantity = 0;
            for (Map.Entry<String, Integer> itemEntry : entry.getValue().entrySet()) {
                if (itemEntry.getValue() > maxQuantity) {
                    mostPopularItem = itemEntry.getKey();
                    maxQuantity = itemEntry.getValue();
                }
            }
            System.out.println(entry.getKey() + ": " + mostPopularItem);
        }
    }

    public static void findRevenueGeneratingItems(List<Sale> salesData) {
        Map<String, Map<String, Double>> itemRevenue = new HashMap<>();

        // Iterate over sales data
        for (Sale sale : salesData) {
            String month = new SimpleDateFormat("yyyy-MM").format(sale.getDate());

            // Check if the month exists in the itemRevenue map
            Map<String, Double> monthMap = itemRevenue.get(month);
            if (monthMap == null) {
                monthMap = new HashMap<>();
                itemRevenue.put(month, monthMap);
            }

            // Check if the SKU exists in the monthMap
            Double revenue = monthMap.get(sale.getSku());
            if (revenue == null) {
                monthMap.put(sale.getSku(), sale.getTotalPrice());
            } else {
                monthMap.put(sale.getSku(), revenue + sale.getTotalPrice());
            }
        }

        System.out.println("Revenue Generating Items Per Month:");
        for (Map.Entry<String, Map<String, Double>> entry : itemRevenue.entrySet()) {
            String mostRevenueItem = null;
            double maxRevenue = 0;

            // Find the most revenue-generating item for the month
            for (Map.Entry<String, Double> itemEntry : entry.getValue().entrySet()) {
                if (itemEntry.getValue() > maxRevenue) {
                    mostRevenueItem = itemEntry.getKey();
                    maxRevenue = itemEntry.getValue();
                }
            }

            System.out.println(entry.getKey() + ": " + mostRevenueItem);
        }
    }

    public static void findMostPopularItemStats(List<Sale> salesData) {
        Map<String, List<Integer>> itemOrders = new HashMap<>();

        // Iterate over sales data
        for (Sale sale : salesData) {
            // Check if the SKU exists in itemOrders map
            List<Integer> orders = itemOrders.get(sale.getSku());
            if (orders == null) {
                orders = new ArrayList<>();
                itemOrders.put(sale.getSku(), orders);
            }
            orders.add(sale.getQuantity());
        }

        String mostPopularItem = null;
        int maxTotalQuantity = 0;

        // Calculate the most popular item by total quantity
        for (Map.Entry<String, List<Integer>> entry : itemOrders.entrySet()) {
            int totalQuantity = 0;
            for (int quantity : entry.getValue()) {
                totalQuantity += quantity;
            }

            if (totalQuantity > maxTotalQuantity) {
                mostPopularItem = entry.getKey();
                maxTotalQuantity = totalQuantity;
            }
        }

        // Output most popular item stats
        System.out.println("Most Popular Item Stats:");
        System.out.println("Item: " + mostPopularItem);

        for (Map.Entry<String, List<Integer>> entry : itemOrders.entrySet()) {
            if (entry.getKey().equals(mostPopularItem)) {
                List<Integer> orders = entry.getValue();
                System.out.println("Min Orders: " + Collections.min(orders));
                System.out.println("Max Orders: " + Collections.max(orders));

                // Calculate average manually
                int sum = 0;
                for (int order : orders) {
                    sum += order;
                }
                double average = (double) sum / orders.size();
                System.out.println("Avg Orders: " + average);
            }
        }
    }
}



