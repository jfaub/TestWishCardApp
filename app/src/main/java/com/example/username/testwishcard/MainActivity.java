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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.picasso.transformations.ColorFilterTransformation;
import jp.wasabeef.picasso.transformations.GrayscaleTransformation;
import jp.wasabeef.picasso.transformations.gpu.SepiaFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.ToonFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.VignetteFilterTransformation;

/**
 * This app let us customize a wish card.
 */
public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private static final int NUMBER_OF_OPTIONS = 5;

    // Keys needed to save the state of our wish card
    private static final String TEXT_MSG_KEY = "TEXT_MSG";
    private static final String TEXT_SIZE_KEY = "TEXT_SIZE";
    private static final String TEXT_SIZE_PROGRESS_KEY = "TEXT_SIZE_PROGRESS";
    private static final String TEXT_ROTATION_KEY = "TEXT_ROTATION";
    private static final String TEXT_ROTATION_PROGRESS_KEY = "TEXT_ROTATION_PROGRESS";
    private static final String CARD_COLOR_KEY = "CARD_COLOR";
    private static final String TEXT_COLOR_KEY = "TEXT_COLOR";
    private static final String PHOTO_FILTER_KEY = "FILTER";

    // Option flags to know which option is active
    // Begin by 0 index because "boolean[] optionsEnabled" relies on it (or use a Map?)
    private static final int TEXT_OPTIONS = 0;
    private static final int CARD_COLOR_OPTIONS = 1;
    private static final int TEXT_COLOR_OPTIONS = 2;
    private static final int PHOTO_FILTER_OPTIONS = 3;

    // Filter names
    private static final String PHOTO_FILTER_2 = "F2";
    private static final String PHOTO_FILTER_3 = "F3";
    private static final String PHOTO_FILTER_4 = "F4";
    private static final String PHOTO_FILTER_5 = "F5";


    // Default values when no modification has been made on the wish card
    private static final int DEFAULT_TEXT_SIZE = 16;
    private static final int DEFAULT_TEXT_SIZE_PROGRESS = 25;
    private static final float TEXT_SIZE_PROGRESS_STEP = 5.0f;
    private static final int DEFAULT_TEXT_ROTATION = 0;
    private static final int DEFAULT_TEXT_ROTATION_PROGRESS = 0;
    private static final String DEFAULT_PHOTO_FILTER = "F1";

    SharedPreferences sharedPref;

    private boolean[] optionsEnabled;

    private ViewGroup mainViewGroup;

    private LinearLayout cardView;
    private ImageView photoView;
    private EditText textView;

    private int textSizeProgress;
    private int textRotationProgress;
    private String photoFilter;

    private HashMap<String, Transformation> filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        // For tracking which option is active
        optionsEnabled = new boolean[NUMBER_OF_OPTIONS];
        for (int i = 0; i < optionsEnabled.length; i++) {
            optionsEnabled[i] = false;
        }

        // Define some types of filters
        filters = new HashMap<>();
        filters.put(DEFAULT_PHOTO_FILTER, new ColorFilterTransformation(0));
        filters.put(PHOTO_FILTER_2, new GrayscaleTransformation());
        filters.put(PHOTO_FILTER_3, new ToonFilterTransformation(this));
        filters.put(PHOTO_FILTER_4, new SepiaFilterTransformation(this));
        filters.put(PHOTO_FILTER_5, new VignetteFilterTransformation(this));

        textSizeProgress = DEFAULT_TEXT_SIZE_PROGRESS;
        textRotationProgress = DEFAULT_TEXT_ROTATION_PROGRESS;
        photoFilter = DEFAULT_PHOTO_FILTER;

        mainViewGroup = (ViewGroup) findViewById(R.id.activity_main);
        cardView = (LinearLayout) findViewById(R.id.card);
        photoView = (ImageView) findViewById(R.id.photo);
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

        ImageButton filterOptionsBtn = (ImageButton) findViewById(R.id.option_photo_filter_btn);
        filterOptionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!optionsEnabled[PHOTO_FILTER_OPTIONS]) {
                    enableOption(PHOTO_FILTER_OPTIONS);
                } else {
                    disableOption(PHOTO_FILTER_OPTIONS);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Restore the state of the wish card
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

        photoFilter = sharedPref.getString(PHOTO_FILTER_KEY, DEFAULT_PHOTO_FILTER);
        photoView.setScaleType(ImageView.ScaleType.CENTER);
        Picasso.with(getApplicationContext()).load(R.drawable.cascade)
                .transform(filters.get((photoFilter)))
                .placeholder(R.drawable.progress_animation)
                .into(photoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }

                    @Override
                    public void onError() {
                        Log.d(LOG_TAG, "Error while loading image with picasso");
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableAllOptions();
        int cardColor = Color.TRANSPARENT;
        Drawable background = cardView.getBackground();
        if (background instanceof ColorDrawable)
            cardColor = ((ColorDrawable) background).getColor();

        int textColor = textView.getTextColors().getDefaultColor();

        Log.d(LOG_TAG, "textView.getTextSize(): " + textView.getTextSize());
        Log.d(LOG_TAG, "textSizeProgress: " + textSizeProgress);

        // Save the state of the wish card
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(TEXT_MSG_KEY, textView.getText().toString());
        editor.putFloat(TEXT_SIZE_KEY, pixelsToSp(this, textView.getTextSize()));
        editor.putInt(TEXT_SIZE_PROGRESS_KEY, textSizeProgress);
        editor.putFloat(TEXT_ROTATION_KEY, textView.getRotation());
        editor.putInt(TEXT_ROTATION_PROGRESS_KEY, textRotationProgress);
        editor.putInt(CARD_COLOR_KEY, cardColor);
        editor.putInt(TEXT_COLOR_KEY, textColor);
        editor.putString(PHOTO_FILTER_KEY, photoFilter);
        editor.apply();
    }

    /**
     * Convert size in pixels to a size in sp
     *
     * @param context: current context
     * @param px: size in pixels
     * @return
     */
    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }

    /**
     * Activate an option an show the corresponding sub-options
     *
     * @param option: flag for the option to activate
     */
    private void enableOption(int option) {
        disableAllOptions();
        optionsEnabled[option] = true;
        switch (option) {
            case TEXT_OPTIONS:
                enableTextOptions();
                break;
            case CARD_COLOR_OPTIONS:
                enableCardColorOptions();
                break;
            case TEXT_COLOR_OPTIONS:
                enableTextColorOptions();
                break;
            case PHOTO_FILTER_OPTIONS:
                enablePhotoFilterOptions();
                break;
            default:
                break;
        }
    }

    /**
     * Deactivate an option an hide the corresponding sub-options
     *
     * @param option: flag for the option to activate
     */
    private void disableOption(int option) {
        optionsEnabled[option] = false;
        switch (option) {
            case TEXT_OPTIONS:
                findViewById(R.id.text_options).setVisibility(View.GONE);
                break;
            case CARD_COLOR_OPTIONS:
                findViewById(R.id.card_color_options).setVisibility(View.GONE);
                break;
            case TEXT_COLOR_OPTIONS:
                findViewById(R.id.text_color_options).setVisibility(View.GONE);
                break;
            case PHOTO_FILTER_OPTIONS:
                findViewById(R.id.photo_filter_options).setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * Deactivate all options that are enabled
     */
    private void disableAllOptions() {
        for (int i = 0; i < optionsEnabled.length; i++) {
            if (optionsEnabled[i]) {
                disableOption(i);
            }
        }
    }

    /**
     * Activate the option for text size and text rotation
     */
    private void enableTextOptions() {
        findViewById(R.id.text_options).setVisibility(View.VISIBLE);

        SeekBar textSizeSeekBar = (SeekBar) findViewById(R.id.text_size_seekbar);
        textSizeSeekBar.setProgress(textSizeProgress);
        textSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textSizeProgress = i;
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

        SeekBar textRotationSeekBar = (SeekBar) findViewById(R.id.text_rotation_seekbar);
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

    /**
     * Activate the option for card color
     */
    private void enableCardColorOptions() {
        findViewById(R.id.card_color_options).setVisibility(View.VISIBLE);

        final Integer[] colors = new Integer[]{
                R.color.c1, R.color.c2, R.color.c3, R.color.c4,
                R.color.c5, R.color.c6, R.color.c7, R.color.c8,
                R.color.c9, R.color.c10, R.color.c11, R.color.c12,
                R.color.c13, R.color.c14, R.color.c15, R.color.c16
        };

        final GridView gridView = (GridView) findViewById(R.id.card_color_options);
        gridView.setNumColumns((colors.length - colors.length / 2));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cardView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),
                        colors[i]));
            }
        });

        gridView.setAdapter(new ArrayAdapter<Integer>(this,
                R.layout.card_color_grid_elem, colors) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setText("");
                view.setBackgroundColor(ContextCompat.getColor(getContext(),
                        colors[position]));
                return view;
            }
        });
    }

    /**
     * Activate the option for text color
     */
    private void enableTextColorOptions() {
        findViewById(R.id.text_color_options).setVisibility(View.VISIBLE);

        final Integer[] colors = new Integer[]{
                R.color.c1, android.R.color.black, R.color.c3, R.color.c4,
                android.R.color.white, R.color.c6, R.color.c7, R.color.c8
        };

        final GridView gridView = (GridView) findViewById(R.id.text_color_options);
        gridView.setNumColumns(colors.length);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                textView.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        colors[i]));
            }
        });

        gridView.setAdapter(new ArrayAdapter<Integer>(this,
                R.layout.text_color_grid_elem, colors) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setText("");
                view.setBackgroundColor(ContextCompat.getColor(getContext(),
                        colors[position]));
                return view;
            }
        });
    }

    /**
     * Activate the option for applying a filter on the photo
     */
    private void enablePhotoFilterOptions() {
        findViewById(R.id.photo_filter_options).setVisibility(View.VISIBLE);

        final List<String> filterGrid = new ArrayList<>(filters.keySet());
        Collections.sort(filterGrid);

        final GridView gridView = (GridView) findViewById(R.id.photo_filter_options);
        gridView.setNumColumns(filterGrid.size());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                photoView.setScaleType(ImageView.ScaleType.CENTER);
                String photoFilterName = ((TextView) view).getText().toString();
                photoFilter = photoFilterName;
                Picasso.with(getApplicationContext()).load(R.drawable.cascade)
                        .transform(filters.get((photoFilterName)))
                        .placeholder(R.drawable.progress_animation)
                        .into(photoView, new Callback() {
                            @Override
                            public void onSuccess() {
                                photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }

                            @Override
                            public void onError() {
                                Log.d(LOG_TAG, "Error while loading image with picasso");
                            }
                        });
            }
        });

        gridView.setAdapter(new ArrayAdapter<>(this, R.layout.photo_filter_grid_elem, filterGrid));
    }
}