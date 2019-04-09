package org.wikipedia.saved.notes.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import org.wikipedia.database.DatabaseTable;
import org.wikipedia.database.contract.NoteContract;
import org.wikipedia.dataclient.WikiSite;

public class NoteTable extends DatabaseTable<Note> {
    private static final int DB_VER_INTRODUCED = 18;

    public NoteTable() {
        super(NoteContract.TABLE, NoteContract.URI);
    }

    @Override
    public Note fromCursor(@NonNull Cursor cursor) {
        String lang = NoteContract.Col.LANG.val(cursor);
        String site = NoteContract.Col.SITE.val(cursor);
        Note note = new Note(
                NoteContract.Col.CONTENT.val(cursor),
                lang == null ? new WikiSite(site) : new WikiSite(site, lang),
                NoteContract.Col.TITLE.val(cursor));
        note.id(NoteContract.Col.ID.val(cursor));
        note.description(NoteContract.Col.DESCRIPTION.val(cursor));
        note.thumbUrl(NoteContract.Col.THUMBNAIL_URL.val(cursor));
        return note;
    }

    @Override
    protected ContentValues toContentValues(@NonNull Note row) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NoteContract.Col.CONTENT.getName(), row.content());
        contentValues.put(NoteContract.Col.SITE.getName(), row.wiki().authority());
        contentValues.put(NoteContract.Col.TITLE.getName(), row.title());
        contentValues.put(NoteContract.Col.THUMBNAIL_URL.getName(), row.thumbUrl());
        contentValues.put(NoteContract.Col.DESCRIPTION.getName(), row.description());
        contentValues.put(NoteContract.Col.LANG.getName(), row.wiki().languageCode());
        return contentValues;
    }

    @Override
    protected String[] getUnfilteredPrimaryKeySelectionArgs(@NonNull Note row) {
        return new String[] {row.title()};
    }

    @Override
    protected int getDBVersionIntroducedAt() {
        return DB_VER_INTRODUCED;
    }
}
