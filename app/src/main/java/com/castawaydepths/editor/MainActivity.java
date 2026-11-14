package com.castawaydepths.editor;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class MainActivity extends Activity {
    static {
        System.loadLibrary("castaway_engine");
    }

    private static native String nativeBootstrapReport();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String report;
        try {
            report = nativeBootstrapReport();
        } catch (Throwable error) {
            report = "JNI ERROR\n" + error.getClass().getSimpleName() + ": " + error.getMessage();
        }

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(28, 36, 28, 36);
        content.setGravity(Gravity.TOP);

        TextView title = new TextView(this);
        title.setText("CASTAWAY DEPTHS");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        content.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("EDITOR / CONTROL — BOOTSTRAP");
        subtitle.setTextSize(14);
        subtitle.setPadding(0, 12, 0, 24);
        subtitle.setGravity(Gravity.CENTER_HORIZONTAL);
        content.addView(subtitle);

        TextView status = new TextView(this);
        status.setText(report + "\n\nBuild stage only. Vulkan surface, swapchain, rendering, gameplay and save/load are not implemented by this bootstrap.");
        status.setTextSize(16);
        status.setTextIsSelectable(true);
        content.addView(status);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);
    }
}
