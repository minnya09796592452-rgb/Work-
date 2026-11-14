package com.castawaydepths.editor;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
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
    private static final String KEY_LANDSCAPE_LOCK = "landscape_lock";
    private static final String KEY_LOGIC_INPUT_TREE_URI = "logic_input_tree_uri";
    private static final int REQUEST_LOGIC_INPUT_FOLDER = 4101;
    private static final String EXPECTED_LOGIC_INPUT_PATH =
            "primary:Download/NYAMIST/Castaway Depths";

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
        applyLandscapeLock(preferences.getBoolean(KEY_LANDSCAPE_LOCK, false));

        try {
            nativeReport = nativeBootstrapReport();
        } catch (Throwable error) {
            nativeReport = "JNI ERROR\n" + error.getClass().getSimpleName()
                    + ": " + error.getMessage();
        }

        showHome();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_LOGIC_INPUT_FOLDER || resultCode != RESULT_OK
                || data == null || data.getData() == null) {
            return;
        }

        Uri treeUri = data.getData();
        String documentId;
        try {
            documentId = Uri.decode(DocumentsContract.getTreeDocumentId(treeUri));
        } catch (IllegalArgumentException error) {
            showFolderSelectionError("Selected location is not a supported folder.");
            return;
        }

        if (!EXPECTED_LOGIC_INPUT_PATH.equals(documentId)) {
            showFolderSelectionError(
                    "Wrong folder. Select Download/NYAMIST/Castaway Depths exactly.");
            return;
        }

        int grantFlags = data.getFlags()
                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        try {
            getContentResolver().takePersistableUriPermission(treeUri, grantFlags);
            preferences.edit().putString(KEY_LOGIC_INPUT_TREE_URI,
                    treeUri.toString()).apply();
            showSettings();
        } catch (SecurityException error) {
            showFolderSelectionError(
                    "Android did not grant persistent access. Please select the folder again.");
        }
    }

    private void showFolderSelectionError(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Logic input folder")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> showSettings())
                .show();
    }

    private void openLogicInputFolderPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_LOGIC_INPUT_FOLDER);
    }

    private String getLogicInputFolderStatus() {
        String treeUri = preferences.getString(KEY_LOGIC_INPUT_TREE_URI, null);
        if (treeUri == null) {
            return "LOGIC INPUT: Not selected";
        }
        return "LOGIC INPUT: Download/NYAMIST/Castaway Depths (access saved)";
    }

    private void applyLandscapeLock(boolean enabled) {
        setRequestedOrientation(enabled
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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
        content.addView(createText(getLogicInputFolderStatus(), 14));

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

        Switch landscapeSwitch = new Switch(this);
        landscapeSwitch.setText("Landscape Lock");
        landscapeSwitch.setTextSize(16);
        landscapeSwitch.setPadding(0, 20, 0, 20);
        landscapeSwitch.setChecked(
                preferences.getBoolean(KEY_LANDSCAPE_LOCK, false));
        landscapeSwitch.setOnCheckedChangeListener((button, checked) -> {
            preferences.edit().putBoolean(KEY_LANDSCAPE_LOCK, checked).apply();
            applyLandscapeLock(checked);
        });
        content.addView(landscapeSwitch);

        content.addView(createText(
                "Required logic input folder:\\n/storage/emulated/0/Download/NYAMIST/Castaway Depths/",
                14));
        Button selectFolderButton = new Button(this);
        selectFolderButton.setText("SELECT LOGIC INPUT FOLDER");
        selectFolderButton.setOnClickListener(view -> openLogicInputFolderPicker());
        content.addView(selectFolderButton);
        content.addView(createText(getLogicInputFolderStatus(), 14));

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
