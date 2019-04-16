package org.wikipedia.saved.notes;

import android.annotation.SuppressLint;

import org.wikipedia.saved.notes.database.Note;

import java.util.Collections;
import java.util.List;

public class NotesListSorter {

    public static final int SORT_BY_ARTICLE_NAME_ASC = 0;
    public static final int SORT_BY_ARTICLE_NAME_DESC = 1;
    public static final int SORT_BY_DATE_ADDED_NEWEST = 2;
    public static final int SORT_BY_DATE_ADDED_OLDEST = 3;

    protected NotesListSorter() {

    }

    @SuppressLint("NewApi")
    public static List<Note> sort(List<Note> notes, int sortMode) {
        switch (sortMode) {
            case SORT_BY_ARTICLE_NAME_ASC:
                Collections.sort(notes, Note.getArticleTitleComparator());
                break;
            case SORT_BY_ARTICLE_NAME_DESC:
                Collections.sort(notes, Note.getArticleTitleComparator().reversed());
                break;
            case SORT_BY_DATE_ADDED_NEWEST:
                Collections.sort(notes, Note.getCreatedAtComparator().reversed());
                break;
            case SORT_BY_DATE_ADDED_OLDEST:
                Collections.sort(notes, Note.getCreatedAtComparator());
                break;
            default:
                break;
        }

        return notes;
    }
}
