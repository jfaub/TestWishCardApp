package com.example.username.testwishcard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private static final int NUMBER_OF_OPTIONS = 5;

    private static final String TEXT_MSG_KEY = "TEXT_MSG";
    private static final String TEXT_SIZE_KEY = "TEXT_SIZE";
    private static final String TEXT_SIZE_PROGRESS_KEY = "TEXT_SIZE_PROGRESS";
    private static final String TEXT_ROTATION_KEY = "TEXT_ROTATION";
    private static final String TEXT_ROTATION_PROGRESS_KEY = "TEXT_ROTATION_PROGRESS";
    private static final String CARD_COLOR_KEY = "CARD_COLOR";
    private static final String TEXT_COLOR_KEY = "TEXT_COLOR";

    // Begin by 0 index because "boolean[] optionsEnabled" relies on it (or use a Map?)
    private static final int TEXT_OPTIONS = 0;
    private static final int CARD_COLOR_OPTIONS = 1;
    private static final int TEXT_COLOR_OPTIONS = 2;

    private static final int DEFAULT_TEXT_SIZE = 16;
    private static final int DEFAULT_TEXT_SIZE_PROGRESS = 25;
    private static final float TEXT_SIZE_PROGRESS_STEP = 5.0f;

    private static final int DEFAULT_TEXT_ROTATION = 0;
    private static final int DEFAULT_TEXT_ROTATION_PROGRESS = 0;

    SharedPreferences sharedPref;

    private boolean[] optionsEnabled;

    private ViewGroup mainViewGroup;

    private LinearLayout cardView;
    private EditText textView;
    private SeekBar textRotationSeekBar;
    private SeekBar textSizeSeekBar;

    private int textSizeProgress;
    private int textRotationProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        optionsEnabled = new boolean[NUMBER_OF_OPTIONS];
        for (int i = 0; i < optionsEnabled.length; i++) {
            optionsEnabled[i] = false;
        }

        textSizeProgress = DEFAULT_TEXT_SIZE_PROGRESS;
        textRotationProgress = DEFAULT_TEXT_ROTATION_PROGRESS;

        mainViewGroup = (ViewGroup) findViewById(R.id.activity_main);
        cardView = (LinearLayout) findViewById(R.id.card);
        textView = (EditText) findViewById(R.id.text);

        ImageButton textOptionsBtn = (ImageButton) findViewById(R.id.option_text_btn);
        textOptionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!optionsEnabled[TEXT_OPTIONS]) {
                    enableOption(TEXT_OPTIONS);
                } else {
                    disableOption(TEXT_OPTIONS);
                }
            }
        });

        ImageButton cardColorOptionsBtn = (ImageButton) findViewById(R.id.option_card_color_btn);
        cardColorOptionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!optionsEnabled[CARD_COLOR_OPTIONS]) {
                    enableOption(CARD_COLOR_OPTIONS);
                } else {
                    disableOption(CARD_COLOR_OPTIONS);
                }
            }
        });

        ImageButton textColorOptionsBtn = (ImageButton) findViewById(R.id.option_text_color_btn);
        textColorOptionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!optionsEnabled[TEXT_COLOR_OPTIONS]) {
                    enableOption(TEXT_COLOR_OPTIONS);
                } else {
                    disableOption(TEXT_COLOR_OPTIONS);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "onResume()");
        super.onResume();

        String textMsg = sharedPref.getString(TEXT_MSG_KEY, "");
        textView.setText(textMsg);
        float textSize = sharedPref.getFloat(TEXT_SIZE_KEY, DEFAULT_TEXT_SIZE);
        textView.setTextSize(textSize);
        float textRotation = sharedPref.getFloat(TEXT_ROTATION_KEY, DEFAULT_TEXT_ROTATION);
        textView.setRotation(textRotation);
        textSizeProgress = sharedPref.getInt(TEXT_SIZE_PROGRESS_KEY, DEFAULT_TEXT_SIZE_PROGRESS);
        textRotationProgress = sharedPref.getInt(TEXT_ROTATION_PROGRESS_KEY,
                DEFAULT_TEXT_ROTATION_PROGRESS);
        int cardColor = sharedPref.getInt(CARD_COLOR_KEY,
                ContextCompat.getColor(this, android.R.color.white));
        cardView.setBackgroundColor(cardColor);
        int textColor = sharedPref.getInt(TEXT_COLOR_KEY,
                ContextCompat.getColor(this, android.R.color.black));
        textView.setTextColor(textColor);

        Log.d(LOG_TAG, "textView.getTextSize(): " + textView.getTextSize());
        Log.d(LOG_TAG, "textSizeProgress: " + textSizeProgress);
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "onPause()");
        super.onPause();

        disableAllOptions();
        int cardColor = Color.TRANSPARENT;
        Drawable background = cardView.getBackground();
        if (background instanceof ColorDrawable)
            cardColor = ((ColorDrawable) background).getColor();

        int textColor = textView.getTextColors().getDefaultColor();

        Log.d(LOG_TAG, "textView.getTextSize(): " + textView.getTextSize() / 2);
        Log.d(LOG_TAG, "textSizeProgress: " + textSizeProgress);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(TEXT_MSG_KEY, textView.getText().toString());
        editor.putFloat(TEXT_SIZE_KEY, textView.getTextSize() / 2);
        editor.putInt(TEXT_SIZE_PROGRESS_KEY, textSizeProgress);
        editor.putFloat(TEXT_ROTATION_KEY, textView.getRotation());
        editor.putInt(TEXT_ROTATION_PROGRESS_KEY, textRotationProgress);
        editor.putInt(CARD_COLOR_KEY, cardColor);
        editor.putInt(TEXT_COLOR_KEY, textColor);
        editor.commit();
    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        Log.d(LOG_TAG, "onSaveInstanceState()");
//        super.onSaveInstanceState(outState);
//        int color = Color.TRANSPARENT;
//        Drawable background = cardView.getBackground();
//        if (background instanceof ColorDrawable)
//            color = ((ColorDrawable) background).getColor();
//        outState.putInt(CARD_COLOR_KEY, color);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        Log.d(LOG_TAG, "onRestoreInstanceState()");
//        super.onRestoreInstanceState(savedInstanceState);
//        int color = savedInstanceState.getInt(CARD_COLOR_KEY,
//                ContextCompat.getColor(this, android.R.color.white));
//        cardView.setBackgroundColor(color);
//    }

    private void enableOption(int option) {
        // Disable all options that are enabled
        for (int i = 0; i < optionsEnabled.length; i++) {
            if (optionsEnabled[i]) {
                disableOption(i);
            }
        }
        optionsEnabled[option] = true;
        Log.d(LOG_TAG, "option: " + option);
        Log.d(LOG_TAG, "optionsEnabled[option]: " + optionsEnabled[option]);
        switch (option) {
            case TEXT_OPTIONS:
                Log.d(LOG_TAG, "enable TEXT_OPTIONS: " + TEXT_OPTIONS);
                enableTextOptions();
                break;
            case CARD_COLOR_OPTIONS:
                Log.d(LOG_TAG, "enable CARD_COLOR_OPTIONS: " + CARD_COLOR_OPTIONS);
                enableCardColorOptions();
                break;
            case TEXT_COLOR_OPTIONS:
                Log.d(LOG_TAG, "enable TEXT_COLOR_OPTIONS: " + TEXT_COLOR_OPTIONS);
                enableTextColorOptions();
                break;
            default:
                break;
        }
    }

    private void disableOption(int option) {
        Log.d(LOG_TAG, "disableOption(): " + option);
        optionsEnabled[option] = false;
        switch (option) {
            case TEXT_OPTIONS:
                Log.d(LOG_TAG, "disable TEXT_OPTIONS: " + TEXT_OPTIONS);
                mainViewGroup.removeView(findViewById(R.id.text_options));
                break;
            case CARD_COLOR_OPTIONS:
                Log.d(LOG_TAG, "disable CARD_COLOR_OPTIONS: " + CARD_COLOR_OPTIONS);
                mainViewGroup.removeView(findViewById(R.id.card_color_options));
                break;
            case TEXT_COLOR_OPTIONS:
                Log.d(LOG_TAG, "disable TEXT_COLOR_OPTIONS: " + TEXT_COLOR_OPTIONS);
                mainViewGroup.removeView(findViewById(R.id.text_color_options));
                break;
            default:
                break;
        }
    }

    /**
     * Disable all options that are enabled
     */
    private void disableAllOptions() {
        for (int i = 0; i < optionsEnabled.length; i++) {
            if (optionsEnabled[i]) {
                disableOption(i);
            }
        }
    }

    private void enableTextOptions() {
        Log.d(LOG_TAG, "enableTextOptions()");
        mainViewGroup = (ViewGroup) View.inflate(this, R.layout.text_options, mainViewGroup);

        textSizeSeekBar = (SeekBar) findViewById(R.id.text_size_seekbar);
        textSizeSeekBar.setProgress(textSizeProgress);
        textSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textSizeProgress = i;
                Log.d(LOG_TAG, "TextSize ***: " + (DEFAULT_TEXT_SIZE
                        + (i - DEFAULT_TEXT_SIZE_PROGRESS) / TEXT_SIZE_PROGRESS_STEP));
                textView.setTextSize(DEFAULT_TEXT_SIZE
                        + (i - DEFAULT_TEXT_SIZE_PROGRESS) / TEXT_SIZE_PROGRESS_STEP);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        textRotationSeekBar = (SeekBar) findViewById(R.id.text_rotation_seekbar);
        textRotationSeekBar.setProgress(textRotationProgress);
        textRotationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textRotationProgress = i;
                textView.setRotation(i * 360 / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void enableCardColorOptions() {
        Log.d(LOG_TAG, "enableCardColorOptions()");
        mainViewGroup = (ViewGroup) View.inflate(this, R.layout.card_color_options, mainViewGroup);

        final ArrayList<Integer> colorGrid = new ArrayList<>();
        colorGrid.add(R.color.c1);
        colorGrid.add(R.color.c2);
        colorGrid.add(R.color.c3);
        colorGrid.add(R.color.c4);
        colorGrid.add(R.color.c5);
        colorGrid.add(R.color.c6);
        colorGrid.add(R.color.c7);
        colorGrid.add(R.color.c8);
        colorGrid.add(R.color.c9);
        colorGrid.add(R.color.c10);
        colorGrid.add(R.color.c11);
        colorGrid.add(R.color.c12);
        colorGrid.add(R.color.c13);
        colorGrid.add(R.color.c14);
        colorGrid.add(R.color.c15);
        colorGrid.add(R.color.c16);

        final GridView gridView = (GridView) findViewById(R.id.card_color_options);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cardView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),
                        colorGrid.get(i)));
            }
        });

        gridView.setAdapter(new ArrayAdapter<Integer>(this, R.layout.card_color_grid_elem, colorGrid) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setText("");
                view.setBackgroundColor(ContextCompat.getColor(getContext(),
                        colorGrid.get(position)));
                return view;
            }
        });
    }

    private void enableTextColorOptions() {
        Log.d(LOG_TAG, "enableTextColorOptions()");
        mainViewGroup = (ViewGroup) View.inflate(this, R.layout.text_color_options, mainViewGroup);

        final ArrayList<Integer> colorGrid = new ArrayList<>();
        colorGrid.add(R.color.c1);
        colorGrid.add(android.R.color.black);
        colorGrid.add(R.color.c3);
        colorGrid.add(R.color.c4);
        colorGrid.add(android.R.color.white);
        colorGrid.add(R.color.c6);
        colorGrid.add(R.color.c7);
        colorGrid.add(R.color.c8);

        final GridView gridView = (GridView) findViewById(R.id.text_color_options);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                textView.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        colorGrid.get(i)));
            }
        });

        gridView.setAdapter(new ArrayAdapter<Integer>(this,
                R.layout.text_color_grid_elem,colorGrid) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setText("");
                view.setBackgroundColor(ContextCompat.getColor(getContext(),
                        colorGrid.get(position)));
                return view;
            }
        });
    }
}