package com.example.projectclip;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout textViewsContainer;
    private List<String> clipboardContents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewsContainer = findViewById(R.id.textViewsContainer);
        ScrollView scrollView = findViewById(R.id.scrollView);

        // Get the system clipboard manager
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        // Create a handler to poll the clipboard periodically
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Get the current text from the clipboard
                ClipData clipData = clipboardManager.getPrimaryClip();
                if (clipData != null && clipData.getItemCount() > 0) {
                    String copiedText = clipData.getItemAt(0).getText().toString();
                    if (!clipboardContents.contains(copiedText)) {
                        addCopiedText(copiedText, clipboardManager);
                        clipboardContents.add(copiedText);
                    }
                }
                handler.postDelayed(this, 1000); // Polling interval in milliseconds (e.g., 1000 = 1 second)
            }
        };

        // Start polling the clipboard
        handler.post(runnable);
    }

    private void addCopiedText(String copiedText, ClipboardManager clipboardManager) {
        // Check if the copied text already exists in the container
        if (clipboardContents.contains(copiedText)) {
            return; // Skip adding duplicate text
        }

        // Inflate the textview_layout.xml
        View textViewLayout = LayoutInflater.from(MainActivity.this).inflate(R.layout.textview_layout, null);
        TextView textView = textViewLayout.findViewById(R.id.textView);
        ImageView copyIcon = textViewLayout.findViewById(R.id.copyIcon);
        ImageView deleteIcon = textViewLayout.findViewById(R.id.deleteIcon);

        // Set the copied text
        textView.setText(copiedText);

        // Set a click listener for the TextView
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Copy the text from the TextView to the clipboard
                ClipData clipData = ClipData.newPlainText("copied_text", copiedText);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(MainActivity.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        };
        textView.setOnClickListener(clickListener);
        copyIcon.setOnClickListener(clickListener);

        // Set a click listener for the delete icon
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the item from the container and the stored list
                int index = textViewsContainer.indexOfChild(textViewLayout);
                textViewsContainer.removeView(textViewLayout);
                textViewsContainer.removeViewAt(index); // Remove the separating line
                clipboardContents.remove(copiedText);
            }
        });

        // Add the TextView, the copy icon, and the delete icon to the container
        textViewsContainer.addView(textViewLayout);

        // Add the horizontal separating line
        View line = new View(MainActivity.this);
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        );
        int margin = dpToPx(8); // Define your desired margin in dp
        lineParams.setMargins(margin, 0, margin, 0);
        line.setLayoutParams(lineParams);
        line.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray));

        // Add the separating line to the container
        textViewsContainer.addView(line);

        // Add the copied text to the clipboard contents list
        clipboardContents.add(copiedText);
    }


    // Convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
