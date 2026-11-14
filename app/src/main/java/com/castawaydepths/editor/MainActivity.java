package com.castawaydepths.editor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
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

    private SharedPreferences preferences;
    private String nativeReport;

    static {
        System.loadLibrary("castaway_engine");
    }

    private static native String nativeBootstrapReport();

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
                || data == null || data.getData() == null) return;

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
                    "Android did not grant persistent access. Please select again.");
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
        String uri = preferences.getString(KEY_LOGIC_INPUT_TREE_URI, null);
        return uri == null
                ? "LOGIC INPUT: Not selected"
                : "LOGIC INPUT: Download/NYAMIST/Castaway Depths (access saved)";
    }

    private void applyLandscapeLock(boolean enabled) {
        setRequestedOrientation(enabled
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String value, float size) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(Color.WHITE);
        view.setPadding(0, dp(5), 0, dp(5));
        return view;
    }

    private Button menuButton(String label, int color) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextSize(15);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackgroundTintList(ColorStateList.valueOf(color));
        return button;
    }

    private void showHome() {
        FrameLayout root = new FrameLayout(this);
        root.addView(new IslandSceneView(this),
                new FrameLayout.LayoutParams(-1, -1));

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setGravity(Gravity.CENTER_VERTICAL);
        panel.setPadding(dp(30), dp(18), dp(30), dp(18));

        TextView title = text("CASTAWAY DEPTHS", 30);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setShadowLayer(8, 2, 3, Color.BLACK);
        panel.addView(title);

        TextView subtitle = text("SURVIVE  •  EXPLORE  •  DISCOVER", 11);
        subtitle.setLetterSpacing(0.12f);
        panel.addView(subtitle);

        Space spacer = new Space(this);
        panel.addView(spacer, new LinearLayout.LayoutParams(1, dp(24), 1));

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button play = menuButton("PLAY", Color.rgb(22, 111, 91));
        play.setOnClickListener(view -> showPreview());
        actions.addView(play, new LinearLayout.LayoutParams(dp(130), dp(52)));

        Button settings = menuButton("SETTINGS", Color.rgb(35, 57, 68));
        settings.setOnClickListener(view -> showSettings());
        LinearLayout.LayoutParams settingsParams =
                new LinearLayout.LayoutParams(dp(145), dp(52));
        settingsParams.leftMargin = dp(12);
        actions.addView(settings, settingsParams);
        panel.addView(actions);

        TextView footer = text("A NEW JOURNEY BEGINS", 10);
        footer.setPadding(0, dp(12), 0, 0);
        panel.addView(footer);

        root.addView(panel, new FrameLayout.LayoutParams(-1, -1));
        setContentView(root);
    }

    private void showPreview() {
        FrameLayout root = new FrameLayout(this);
        root.addView(new IslandSceneView(this),
                new FrameLayout.LayoutParams(-1, -1));

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setGravity(Gravity.CENTER);
        panel.setPadding(dp(24), dp(20), dp(24), dp(20));

        TextView title = text("ISLAND PREVIEW", 28);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        panel.addView(title);

        TextView info = text(
                "YOUR JOURNEY STARTS HERE\n\n"
                + "World preview is ready for the next development stage.\n"
                + "Gameplay systems are not connected yet.", 14);
        info.setGravity(Gravity.CENTER);
        panel.addView(info);

        Button back = menuButton("BACK TO MAIN MENU",
                Color.rgb(22, 111, 91));
        back.setOnClickListener(view -> showHome());
        panel.addView(back);

        root.addView(panel, new FrameLayout.LayoutParams(-1, -1));
        setContentView(root);
    }

    private void showSettings() {
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(24), dp(24), dp(24), dp(24));
        content.setBackgroundColor(Color.rgb(17, 34, 43));

        TextView title = text("SETTINGS", 26);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        content.addView(title);

        Switch landscape = new Switch(this);
        landscape.setText("Landscape Lock");
        landscape.setTextColor(Color.WHITE);
        landscape.setChecked(
                preferences.getBoolean(KEY_LANDSCAPE_LOCK, false));
        landscape.setPadding(0, dp(16), 0, dp(16));
        landscape.setOnCheckedChangeListener((button, checked) -> {
            preferences.edit().putBoolean(KEY_LANDSCAPE_LOCK, checked).apply();
            applyLandscapeLock(checked);
        });
        content.addView(landscape);

        content.addView(text(
                "Required logic input folder:\n"
                + "/storage/emulated/0/Download/NYAMIST/Castaway Depths/",
                13));

        Button folder = menuButton("SELECT LOGIC INPUT FOLDER",
                Color.rgb(35, 90, 100));
        folder.setOnClickListener(view -> openLogicInputFolderPicker());
        content.addView(folder);
        content.addView(text(getLogicInputFolderStatus(), 12));

        Switch diagnostics = new Switch(this);
        diagnostics.setText("Diagnostic Panel");
        diagnostics.setTextColor(Color.WHITE);
        diagnostics.setChecked(preferences.getBoolean(KEY_DIAGNOSTICS, true));
        diagnostics.setPadding(0, dp(16), 0, dp(8));
        content.addView(diagnostics);

        Switch nativeDetails = new Switch(this);
        nativeDetails.setText("Native Bootstrap Details");
        nativeDetails.setTextColor(Color.WHITE);
        nativeDetails.setChecked(
                preferences.getBoolean(KEY_NATIVE_DETAILS, false));
        nativeDetails.setPadding(0, dp(8), 0, dp(16));
        content.addView(nativeDetails);

        diagnostics.setOnCheckedChangeListener((button, checked) -> {
            preferences.edit().putBoolean(KEY_DIAGNOSTICS, checked).apply();
            nativeDetails.setEnabled(checked);
            if (!checked) nativeDetails.setChecked(false);
        });
        nativeDetails.setEnabled(diagnostics.isChecked());
        nativeDetails.setOnCheckedChangeListener((button, checked) ->
                preferences.edit().putBoolean(KEY_NATIVE_DETAILS, checked).apply());

        content.addView(text(
                "Native details appear on the home screen only when both "
                + "diagnostic switches are ON.", 12));

        Button back = menuButton("BACK TO MAIN MENU",
                Color.rgb(22, 111, 91));
        back.setOnClickListener(view -> showHome());
        content.addView(back);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.rgb(17, 34, 43));
        scroll.addView(content);
        setContentView(scroll);
    }

    private static final class IslandSceneView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        IslandSceneView(Activity activity) {
            super(activity);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        private void fill(Canvas canvas, int color) {
            paint.setShader(null);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            canvas.drawPaint(paint);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int save = canvas.save();
            canvas.scale(getWidth() / 1000f, getHeight() / 562f);

            paint.setShader(new LinearGradient(0, 0, 0, 370,
                    Color.rgb(37, 104, 137),
                    Color.rgb(232, 174, 119), Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, 1000, 562, paint);
            paint.setShader(null);

            paint.setColor(Color.rgb(255, 218, 157));
            canvas.drawCircle(790, 125, 48, paint);

            paint.setShader(new LinearGradient(0, 235, 0, 562,
                    Color.rgb(22, 133, 157),
                    Color.rgb(5, 61, 91), Shader.TileMode.CLAMP));
            canvas.drawRect(0, 235, 1000, 562, paint);
            paint.setShader(null);

            paint.setColor(Color.argb(65, 220, 240, 220));
            paint.setStrokeWidth(3);
            for (int i = 0; i < 9; i++) {
                float y = 285 + i * 27;
                canvas.drawLine(470 + i * 13, y, 950, y - 5, paint);
            }

            Path island = new Path();
            island.moveTo(390, 320);
            island.cubicTo(470, 270, 520, 250, 590, 276);
            island.cubicTo(660, 240, 745, 270, 820, 323);
            island.lineTo(1000, 360);
            island.lineTo(1000, 430);
            island.lineTo(350, 430);
            island.close();
            paint.setColor(Color.rgb(39, 91, 67));
            canvas.drawPath(island, paint);

            Path beach = new Path();
            beach.moveTo(500, 350);
            beach.cubicTo(650, 315, 830, 335, 1000, 390);
            beach.lineTo(1000, 562);
            beach.lineTo(410, 562);
            beach.cubicTo(490, 480, 455, 410, 500, 350);
            beach.close();
            paint.setColor(Color.rgb(211, 178, 126));
            canvas.drawPath(beach, paint);

            drawPalm(canvas, 650, 335, 0.85f);
            drawPalm(canvas, 790, 330, 1.0f);
            drawPalm(canvas, 925, 370, 0.7f);

            paint.setShader(new LinearGradient(0, 0, 1000, 0,
                    Color.argb(110, 0, 0, 0),
                    Color.argb(0, 0, 0, 0), Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, 1000, 562, paint);
            paint.setShader(null);

            canvas.restoreToCount(save);
        }

        private void drawPalm(Canvas canvas, float x, float y, float scale) {
            canvas.save();
            canvas.translate(x, y);
            canvas.scale(scale, scale);

            paint.setShader(null);
            paint.setColor(Color.rgb(91, 66, 43));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(11);
            paint.setStrokeCap(Paint.Cap.ROUND);
            Path trunk = new Path();
            trunk.moveTo(0, 0);
            trunk.quadTo(-15, -45, 0, -105);
            canvas.drawPath(trunk, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(35, 102, 62));
            for (int i = 0; i < 7; i++) {
                double angle = Math.PI * i / 3.0 - Math.PI;
                Path leaf = new Path();
                leaf.moveTo(0, -105);
                float ex = (float) (Math.cos(angle) * 83);
                float ey = -105 + (float) (Math.sin(angle) * 43);
                leaf.quadTo(ex * 0.35f, -155, ex, ey);
                leaf.quadTo(ex * 0.35f, -125, 0, -105);
                canvas.drawPath(leaf, paint);
            }
            canvas.restore();
        }
    }
}
