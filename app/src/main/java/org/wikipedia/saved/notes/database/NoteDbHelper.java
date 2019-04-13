package org.wikipedia.saved.notes.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.wikipedia.WikipediaApp;
import org.wikipedia.database.contract.NoteContract;

import java.util.ArrayList;
import java.util.List;

public class NoteDbHelper {
    private static NoteDbHelper INSTANCE;

    private NoteDbHelper() {}

    public static NoteDbHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoteDbHelper();
        }
        return INSTANCE;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(NoteContract.TABLE, null, null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                Note note = Note.DATABASE_TABLE.fromCursor(cursor);
                notes.add(note);
            }
        }
        return notes;
    }

    public void createNote(@NonNull Note note) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.insertOrThrow(NoteContract.TABLE, null, Note.DATABASE_TABLE.toContentValues(note));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private SQLiteDatabase getReadableDatabase() {
        return WikipediaApp.getInstance().getDatabase().getReadableDatabase();
    }

    private SQLiteDatabase getWritableDatabase() {
        return WikipediaApp.getInstance().getDatabase().getWritableDatabase();
    }
}
