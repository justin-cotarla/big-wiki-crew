package org.wikipedia.saved.notes.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.wikipedia.WikipediaApp;
import org.wikipedia.database.contract.NoteContract;
import org.wikipedia.util.log.L;

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

    public Note getNoteById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(NoteContract.TABLE,
                null,
                NoteContract.Col.ID.getName() + " = ?",
                new String[]{ Long.toString(id) },
                null,
                null,
                null)) {
            if (cursor.moveToNext()) {
                return Note.DATABASE_TABLE.fromCursor(cursor);
            }
        }
        return null;
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

    public void saveNote(@NonNull Note note) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            long id = db.insertOrThrow(NoteContract.TABLE, null, Note.DATABASE_TABLE.toContentValues(note));
            note.id(id);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateNote(@NonNull Note note) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int result = db.update(
                    NoteContract.TABLE,
                    Note.DATABASE_TABLE.toContentValues(note),
                    NoteContract.Col.ID.getName() + " = ?", new String[]{ Long.toString(note.id()) });
            if (result != 1) {
                L.w("Failed to update db entry for note with ID " + note.id());
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteNote(@NonNull Note note) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int result = db.delete(NoteContract.TABLE,
                    NoteContract.Col.ID.getName() + " = ?",
                    new String[] { Long.toString(note.id()) });
            if (result != 1) {
                L.w("Failed to delete db entry for note with ID " + note.id());
            }
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
