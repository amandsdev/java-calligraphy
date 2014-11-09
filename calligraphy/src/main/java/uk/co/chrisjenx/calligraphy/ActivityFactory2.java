package uk.co.chrisjenx.calligraphy;

import android.util.AttributeSet;
import android.view.View;

/**
 * Created by chris on 09/11/14.
 * For Calligraphy.
 */
public interface ActivityFactory2 {

    /**
     * Used to Wrap the Activity onCreateView method.
     *
     * You implement this method like so in you base activity.
     * <pre>
     * {@code
     * public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
     *   return CalligraphyContextWrapper.get(getBaseContext()).onActivityCreateView(super.onCreateView(parent, name, context, attrs), attrs);
     * }
     * }
     * </pre>
     *
     * @param view  result of {@code super.onCreateView(parent, name, context, attrs)}, this might be null, which is fine.
     * @param attrs see {@link android.view.LayoutInflater.Factory2#onCreateView(android.view.View, String, android.content.Context, android.util.AttributeSet)}
     * @return the result from the activities {@code onCreateView()}
     * @see android.view.LayoutInflater.Factory2
     */
    View onActivityCreateView(View view, AttributeSet attrs);
}
