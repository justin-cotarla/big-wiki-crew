package org.wikipedia.onboarding.newfeatures;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import org.wikipedia.R;
import org.wikipedia.activity.SingleFragmentActivity;


public class NewFeaturesActivity
        extends SingleFragmentActivity<NewFeaturesFragment>
        implements NewFeaturesFragment.Callback {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.base100);
    }

    @NonNull public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, NewFeaturesActivity.class);
    }

    @Override public void onComplete() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (getFragment().onBackPressed()) {
            return;
        }
        setResult(RESULT_OK);
        finish();
    }

    @Override protected NewFeaturesFragment createFragment() {
        return NewFeaturesFragment.newInstance();
    }
}
