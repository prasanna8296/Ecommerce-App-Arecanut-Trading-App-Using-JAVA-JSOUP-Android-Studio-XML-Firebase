package com.example.arecanut;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.media.RouteListingPreference;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.ChangeTransform;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SearchView searchView;
    TextView category;
    RecyclerView recyclerView;
    private ImageView imageView;
    MainAdapter mainAdapter;
    AddActivity addActivity;
    MainModel mainModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("https://en.krashimitra.com/arecanut-market-price-karnataka/");

        FloatingActionButton fab1 = findViewById(R.id.sellbutton);
        FloatingActionButton fab2 = findViewById(R.id.floatingActionButton);
        FloatingActionButton fab3 = findViewById(R.id.profilebuttonid);

        fab1.setOnClickListener(view -> {
            String sellerEmail = getIntent().getStringExtra("email");
            Intent addressIntent = new Intent(MainActivity.this, AddActivity.class);
            addressIntent.putExtra("email",sellerEmail);
            startActivity(addressIntent);
        });

        fab2.setOnClickListener(view -> {
            // Trigger the web scraping task
            new Thread(() -> {
                try {
                    WebScraper.scrapeAndStoreData();
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "Web scraping completed", Toast.LENGTH_SHORT).show()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });


        fab3.setOnClickListener(view -> {
            String sellerEmail = getIntent().getStringExtra("email");
            Intent profileIntent = new Intent(MainActivity.this, SellerProfile.class);
            profileIntent.putExtra("email", sellerEmail);
            startActivity(profileIntent);
        });
    }

    private static class ScrapeAndStoreDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                WebScraper.scrapeAndStoreData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
