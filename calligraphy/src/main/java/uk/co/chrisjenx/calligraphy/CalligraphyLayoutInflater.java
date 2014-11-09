package uk.co.chrisjenx.calligraphy;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by chris on 19/12/2013
 * Project: Calligraphy
 */
class CalligraphyLayoutInflater extends LayoutInflater implements ActivityFactory2 {

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit."
    };

    private final int mAttributeId;
    private final CalligraphyFactory mCalligraphyFactory;

    protected CalligraphyLayoutInflater(Context context, int attributeId) {
        super(context);
        mAttributeId = attributeId;
        mCalligraphyFactory = new CalligraphyFactory(attributeId);
        setUpLayoutFactories();
    }

    protected CalligraphyLayoutInflater(LayoutInflater original, Context newContext, int attributeId) {
        super(original, newContext);
        mAttributeId = attributeId;
        mCalligraphyFactory = new CalligraphyFactory(attributeId);
        setUpLayoutFactories();
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new CalligraphyLayoutInflater(this, newContext, mAttributeId);
    }

    // ===
    // Wrapping goodies
    // ===

    /**
     * We don't want to unnecessary create/set our factories if there are none there. We try to be
     * as lazy as possible.
     */
    private void setUpLayoutFactories() {
        // If we are HC+ we get and set Factory2 otherwise we just wrap Factory1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (getFactory2() != null) {
                // Sets both Factory/Factory2
                setFactory2(getFactory2());
            }
        } else if (getFactory() != null) {
            setFactory(getFactory());
        }
    }

    @Override
    public void setFactory(Factory factory) {
        // Only set our factory and wrap calls to the Factory trying to be set!
        if (!(factory instanceof WrapperFactory)) {
            super.setFactory(new WrapperFactory(factory, mCalligraphyFactory));
        } else {
            super.setFactory(factory);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setFactory2(Factory2 factory2) {
        // Only set our factory and wrap calls to the Factory2 trying to be set!
        if (!(factory2 instanceof WrapperFactory2)) {
            super.setFactory2(new WrapperFactory2(factory2, mCalligraphyFactory));
        } else {
            super.setFactory2(factory2);
        }
    }

    // ===
    // LayoutInflater ViewCreators
    // ===

    /**
     * The LayoutInflater onCreateView is the fourth port of call for LayoutInflation.
     * Basically if this method doesn't inflate the View nothing probably will.
     */
    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        View view = null;
        for (String prefix : sClassPrefixList) {
            try {
                view = createView(name, prefix, attrs);
            } catch (ClassNotFoundException e) {
            }
        }
        // In this case we want to let the base class take a crack
        // at it.
        if (view == null) view = super.onCreateView(name, attrs);

        return mCalligraphyFactory.onActivityCreateView(view, attrs);
    }

    /**
     * The LayoutInflater onCreateView is the fourth port of call for LayoutInflation.
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected View onCreateView(View parent, String name, AttributeSet attrs) throws ClassNotFoundException {
        return mCalligraphyFactory.onActivityCreateView(
                super.onCreateView(parent, name, attrs),
                attrs);
    }

    /**
     * The Activity onCreateView (PrivateFactory) is the third port of call for LayoutInflation.
     * We opted to manual injection over aggressive reflection, this should be less fragile.
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public View onActivityCreateView(View view, AttributeSet attrs) {
        return mCalligraphyFactory.onActivityCreateView(view, attrs);
    }

    // ===
    // Wrapper Factories for Pre/Post HC
    // ===

    /**
     * Factory 1 is the first port of call for LayoutInflation
     */
    private static class WrapperFactory implements Factory {

        private final Factory mFactory;
        private final CalligraphyFactory mCalligraphyFactory;

        public WrapperFactory(Factory factory, CalligraphyFactory calligraphyFactory) {
            mFactory = factory;
            mCalligraphyFactory = calligraphyFactory;
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            return mCalligraphyFactory.onActivityCreateView(
                    mFactory.onCreateView(name, context, attrs),
                    attrs);
        }
    }

    /**
     * Factory 2 is the second port of call for LayoutInflation
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static class WrapperFactory2 implements Factory2 {
        private final Factory2 mFactory2;
        private final CalligraphyFactory mCalligraphyFactory;

        public WrapperFactory2(Factory2 factory2, CalligraphyFactory calligraphyFactory) {
            mFactory2 = factory2;
            mCalligraphyFactory = calligraphyFactory;
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            return mCalligraphyFactory.onActivityCreateView(
                    mFactory2.onCreateView(name, context, attrs),
                    attrs);
        }

        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            return mCalligraphyFactory.onActivityCreateView(
                    mFactory2.onCreateView(parent, name, context, attrs),
                    attrs);
        }
    }

}
