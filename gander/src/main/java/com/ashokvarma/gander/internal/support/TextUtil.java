package com.ashokvarma.gander.internal.support;


import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
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
     */
    public static void asyncSetText(Executor bgExecutor, final AsyncTextProvider asyncTextProvider) {
        // construct precompute related parameters using the TextView that we will set the text on.
//        final PrecomputedText.Params params = textView.getTextMetricsParams();
        final Reference<AsyncTextProvider> asyncTextProviderReference = new WeakReference<>(asyncTextProvider);
        bgExecutor.execute(new Runnable() {
            @Override
            public void run() {
                AsyncTextProvider asyncTextProvider = asyncTextProviderReference.get();
                if (asyncTextProvider == null) return;
                try {
                    CharSequence longString = asyncTextProvider.getText();
                    asyncTextProvider = null;//clear ref
//                final PrecomputedText precomputedText = PrecomputedText.create(longString, params);

                    asyncTextProvider = asyncTextProviderReference.get();
                    if (asyncTextProvider == null) return;
//                        asyncTextProvider.setText(precomputedText);
                    asyncTextProvider.setText(longString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface AsyncTextProvider {
        CharSequence getText();

        void setText(final CharSequence charSequence);
    }

    public static boolean isNullOrWhiteSpace(CharSequence text) {
        return text == null || text.length() == 0 || text.toString().trim().length() == 0;
    }
}
