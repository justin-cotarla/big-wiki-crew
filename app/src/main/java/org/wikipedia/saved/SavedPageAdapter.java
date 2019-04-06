package org.wikipedia.saved;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.wikipedia.readinglist.ReadingListsFragment;
import org.wikipedia.saved.notes.NotesFragment;

public class SavedPageAdapter extends FragmentPagerAdapter {

    private final int numTabs;

    public SavedPageAdapter(FragmentManager fm, int numTabs) {
        super(fm);
        this.numTabs = numTabs;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return ReadingListsFragment.newInstance();
            case 1:
                return NotesFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return this.numTabs;
    }
}
