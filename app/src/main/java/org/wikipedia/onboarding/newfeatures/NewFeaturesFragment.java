package org.wikipedia.onboarding.newfeatures;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.wikipedia.R;
import org.wikipedia.model.EnumCode;
import org.wikipedia.model.EnumCodeMap;
import org.wikipedia.onboarding.OnboardingFragment;
import org.wikipedia.onboarding.OnboardingPageView;


public class NewFeaturesFragment extends OnboardingFragment {
    private PageViewCallback pageViewCallback = new PageViewCallback();

    @NonNull public static NewFeaturesFragment newInstance() {
        return new NewFeaturesFragment();
    }

    @Override protected PagerAdapter getAdapter() {
        return new OnboardingPagerAdapter();
    }

    @Override protected int getDoneButtonText() {
        return R.string.onboarding_get_started;
    }

    private class PageViewCallback implements OnboardingPageView.Callback {
        OnboardingPageView onboardingPageView;

        @Override public void onSwitchChange(@NonNull OnboardingPageView view, boolean checked) {

        }

        @Override public void onLinkClick(@NonNull OnboardingPageView view, @NonNull String url) {

        }

        @Override
        public void onListActionButtonClicked(@NonNull OnboardingPageView view) {

        }

        @Nullable OnboardingPageView getOnboardingPageView() {
            return onboardingPageView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pageViewCallback != null && pageViewCallback.getOnboardingPageView() != null) {
            pageViewCallback.getOnboardingPageView().refresh();
        }
    }

    private class OnboardingPagerAdapter extends PagerAdapter {
        @NonNull @Override public Object instantiateItem(@NonNull ViewGroup container, int position) {
            OnboardingPage page = OnboardingPage.of(position);
            OnboardingPageView view = inflate(page, container);
            view.setTag(position);
            view.setCallback(pageViewCallback);
            return view;
        }

        @NonNull
        public OnboardingPageView inflate(@NonNull OnboardingPage page, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            OnboardingPageView view = (OnboardingPageView) inflater.inflate(page.getLayout(), parent, false);
            parent.addView(view);
            return view;
        }

        @Override public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            OnboardingPageView view = ((OnboardingPageView) object);
            container.removeView(view);
            view.setCallback(null);
            view.setTag(-1);
        }

        @Override public int getCount() {
            return OnboardingPage.size();
        }

        @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    enum OnboardingPage implements EnumCode {
        PAGE_IMAGE_SEARCH(R.layout.inflate_initial_onboarding_feature_zero),
        PAGE_TTS(R.layout.inflate_initial_onboarding_feature_one),
        PAGE_SELECTIVE_TRANSLATION(R.layout.inflate_initial_onboarding_feature_two),
        PAGE_NO_HISTORY(R.layout.inflate_initial_onboarding_feature_three),
        PAGE_CATEGORIES(R.layout.inflate_initial_onboarding_feature_four),
        PAGE_DISCOVER(R.layout.inflate_initial_onboarding_feature_five),
        PAGE_CHAT(R.layout.inflate_initial_onboarding_feature_six);

        private static EnumCodeMap<OnboardingPage> MAP
                = new EnumCodeMap<>(OnboardingPage.class);

        @LayoutRes private final int layout;

        int getLayout() {
            return layout;
        }

        @NonNull public static OnboardingPage of(int code) {
            return MAP.get(code);
        }

        public static int size() {
            return MAP.size();
        }

        @Override public int code() {
            return ordinal();
        }

        OnboardingPage(@LayoutRes int layout) {
            this.layout = layout;
        }
    }
}
