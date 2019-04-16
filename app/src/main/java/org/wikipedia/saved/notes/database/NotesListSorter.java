package org.wikipedia.saved.notes.database;

import android.annotation.SuppressLint;

import java.util.Collections;
import java.util.List;
import org.wikipedia.util.log.L;

public class NotesListSorter {

    public static final int SORT_BY_ARTICLE_NAME_ASC = 0;
    public static final int SORT_BY_ARTICLE_NAME_DESC = 1;
    public static final int SORT_BY_DATE_ADDED_ASC = 2;
    public static final int SORT_BY_DATE_ADDED_DESC = 3;

    @SuppressLint("NewApi")
    public static List<Note> sort(List<Note> notes, int sortMode) {
        switch (sortMode) {
            case SORT_BY_ARTICLE_NAME_ASC:
                L.i("Sort mode: article asc");
                Collections.sort(notes, Note.getArticleTitleComparator());
                break;
            case SORT_BY_ARTICLE_NAME_DESC:
                L.i("Sort mode: article desc");
                Collections.sort(notes, Note.getArticleTitleComparator().reversed());
                break;
            case SORT_BY_DATE_ADDED_ASC:
                L.i("Sort mode: date asc");
                Collections.sort(notes, Note.getCreatedAtComparator());
                break;
            case SORT_BY_DATE_ADDED_DESC:
                L.i("Sort mode: date desc");
                Collections.sort(notes, Note.getCreatedAtComparator().reversed());
                break;
            default:
                break;
        }

        return notes;
    }
}
