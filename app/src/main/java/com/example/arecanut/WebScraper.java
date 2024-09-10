package com.example.arecanut;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebScraper {

    public static void scrapeAndStoreData() {
        try {
            Document document = Jsoup.connect("https://en.krashimitra.com/arecanut-market-price-karnataka/").get();
            Elements rows = document.select("#table_1 tbody tr");

            Log.d("WebScraper", "Number of rows scraped: " + rows.size());

            storeScrapedDataInFirebase(rows);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void storeScrapedDataInFirebase(Elements rows) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference predictionRef = rootRef.child("Prediction");

        for (Element row : rows) {
            Elements cells = row.select("td");

            if (cells.size() >= 6) {
                String category = cells.get(2).text();
                String date = cells.get(1).text();
                double price = Double.parseDouble(cells.get(5).text().replace("₹", "").replace(",", ""));

                storeDataInFirebase(predictionRef, category, date, price);
            } else {
                Log.e("WebScraper", "Insufficient data in the row");
            }
        }
    }

    private static void storeDataInFirebase(DatabaseReference predictionRef, String category, String date, double price) {
        // Get the current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // Store in Firebase under "Prediction" node for the current date
        DatabaseReference entryRef = predictionRef.child(currentDate).push(); // Use push to generate unique keys

        entryRef.child("category").setValue(category);
//       entryRef.child("date").setValue(date);
        entryRef.child("price").setValue(price)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "Data saved successfully");
                    } else {
                        Log.e("Firebase", "Error saving data to Firebase", task.getException());
                    }
                });
    }
}
