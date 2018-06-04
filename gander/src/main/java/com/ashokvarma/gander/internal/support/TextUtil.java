package com.ashokvarma.gander.internal.support;


import android.widget.TextView;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 04/06/18
 */
public class TextUtil {
    /**
     * Pref Matters
     * <p>
     * PrecomputedText is not yet in support library, But still this is left
     * because the callable which is formatting Json, Xml will now be done in background thread
     * <p>
     * Pref Matters
     */
    public static void asyncSetText(TextView textView, Executor bgExecutor, final Callable<String> callable) {
        // construct precompute related parameters using the TextView that we will set the text on.
//        final PrecomputedText.Params params = textView.getTextMetricsParams();
        final Reference<TextView> textViewRef = new WeakReference<>(textView);
        bgExecutor.execute(new Runnable() {
            @Override
            public void run() {
                TextView textView = textViewRef.get();
                if (textView == null) return;
                try {
                    final String longString = callable.call();
//                final PrecomputedText precomputedText = PrecomputedText.create(longString, params);
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView textViewInternal = textViewRef.get();
                            if (textViewInternal == null) return;
//                        textView.setText(precomputedText);
                            textViewInternal.setText(longString);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
