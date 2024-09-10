package com.example.arecanut;

import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PredictionManager {
    private static final double DEFAULT_PRICE = 35000; // Change the default value


    public interface PredictionCallback {
        void onPredictionReceived(double price);
    }

    public static void predictOptimumPrice(String category, String date, final PredictionCallback callback) {
        DatabaseReference predictionRefToday = FirebaseDatabase.getInstance().getReference("Prediction").child(date);
        DatabaseReference predictionRefYesterday = FirebaseDatabase.getInstance().getReference("Prediction").child(getYesterdayDate());

        predictionRefToday.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final double[] price = {DEFAULT_PRICE}; // Default value

                if (dataSnapshot.exists()) {
                    for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                        String retrievedCategory = uniqueKeySnapshot.child("category").getValue(String.class);
                        Double priceData = uniqueKeySnapshot.child("price").getValue(Double.class);

                        if (retrievedCategory != null && retrievedCategory.equals(category) && priceData != null) {
                            price[0] = priceData;
                            break;
                        }
                    }
                } else {
                    // If data for today's date and category is not available, try fetching from yesterday's date
                    predictionRefYesterday.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                                    String retrievedCategory = uniqueKeySnapshot.child("category").getValue(String.class);
                                    Double priceData = uniqueKeySnapshot.child("price").getValue(Double.class);

                                    if (retrievedCategory != null && retrievedCategory.equals(category) && priceData != null) {
                                        price[0] = priceData;
                                        break;
                                    }
                                }
                            }

                            // Pass the result to the callback
                            callback.onPredictionReceived(price[0]);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("PredictionManager", "DatabaseError: " + databaseError.getMessage());
                            callback.onPredictionReceived(DEFAULT_PRICE);
                        }
                    });
                }

                // Pass the result to the callback
                callback.onPredictionReceived(price[0]);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PredictionManager", "DatabaseError: " + databaseError.getMessage());
                callback.onPredictionReceived(DEFAULT_PRICE);
            }
        });
    }

    // Method to get yesterday's date
    private static String getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1); // Subtract 1 day
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(cal.getTime());
    }





    // New method to get the current date in the required format
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(Calendar.getInstance().getTime());
    }
}

