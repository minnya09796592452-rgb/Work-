package com.castawaydepths.editor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

public final class MainActivity extends Activity {
    private static final String PREFS_NAME = "castaway_editor_settings";
    private static final String KEY_DIAGNOSTICS = "show_diagnostics";
    private static final String KEY_NATIVE_DETAILS = "show_native_details";

    static {
        System.loadLibrary("castaway_engine");
    }

    private static native String nativeBootstrapReport();

    private SharedPreferences preferences;
    private String nativeReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        try {
            nativeReport = nativeBootstrapReport();
        } catch (Throwable error) {
            nativeReport = "JNI ERROR\n" + error.getClass().getSimpleName()
                    + ": " + error.getMessage();
        }

        showHome();
    }

    private LinearLayout createContent() {
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(28, 36, 28, 36);
        content.setGravity(Gravity.TOP);
        return content;
    }

    private TextView createText(String value, float size) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setPadding(0, 8, 0, 8);
        return text;
    }

    private void showHome() {
        LinearLayout content = createContent();

        TextView title = createText("CASTAWAY DEPTHS", 24);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        content.addView(title);

        TextView subtitle = createText("EDITOR / CONTROL — BOOTSTRAP", 14);
        subtitle.setGravity(Gravity.CENTER_HORIZONTAL);
        content.addView(subtitle);

        Button settingsButton = new Button(this);
        settingsButton.setText("SETTINGS");
        settingsButton.setOnClickListener(view -> showSettings());
        content.addView(settingsButton);

        if (preferences.getBoolean(KEY_DIAGNOSTICS, true)) {
            content.addView(createText("DIAGNOSTICS: ON", 16));
            if (preferences.getBoolean(KEY_NATIVE_DETAILS, false)) {
                TextView report = createText(nativeReport, 14);
                report.setTextIsSelectable(true);
                content.addView(report);
            } else {
                content.addView(createText(
                        "Native bootstrap details are hidden. Enable them in Settings.", 14));
            }
        } else {
            content.addView(createText("DIAGNOSTICS: OFF", 16));
        }

        content.addView(createText(
                "Build stage only. Vulkan surface, swapchain, rendering, gameplay "
                        + "and save/load are not implemented by this bootstrap.",
                14));

        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
    }

    private void showSettings() {
        LinearLayout content = createContent();

        TextView title = createText("SETTINGS", 24);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        content.addView(title);

        Switch diagnosticsSwitch = new Switch(this);
        diagnosticsSwitch.setText("Diagnostic Panel");
        diagnosticsSwitch.setTextSize(16);
        diagnosticsSwitch.setPadding(0, 20, 0, 20);
        diagnosticsSwitch.setChecked(preferences.getBoolean(KEY_DIAGNOSTICS, true));
        content.addView(diagnosticsSwitch);

        Switch nativeDetailsSwitch = new Switch(this);
        nativeDetailsSwitch.setText("Native Bootstrap Details");
        nativeDetailsSwitch.setTextSize(16);
        nativeDetailsSwitch.setPadding(0, 20, 0, 20);
        nativeDetailsSwitch.setChecked(
                preferences.getBoolean(KEY_NATIVE_DETAILS, false));
        content.addView(nativeDetailsSwitch);

        TextView hint = createText(
                "Native details are shown on the home screen only when both "
                        + "switches are ON.", 14);
        content.addView(hint);

        diagnosticsSwitch.setOnCheckedChangeListener((button, checked) -> {
            preferences.edit().putBoolean(KEY_DIAGNOSTICS, checked).apply();
            nativeDetailsSwitch.setEnabled(checked);
            if (!checked) {
                nativeDetailsSwitch.setChecked(false);
            }
        });

        nativeDetailsSwitch.setEnabled(diagnosticsSwitch.isChecked());
        nativeDetailsSwitch.setOnCheckedChangeListener((button, checked) ->
                preferences.edit().putBoolean(KEY_NATIVE_DETAILS, checked).apply());

        Button backButton = new Button(this);
        backButton.setText("BACK");
        backButton.setOnClickListener(view -> showHome());
        content.addView(backButton);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
    }
}
