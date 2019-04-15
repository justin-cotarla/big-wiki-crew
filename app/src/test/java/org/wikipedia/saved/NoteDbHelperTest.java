package org.wikipedia.saved;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.wikipedia.WikipediaApp;
import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.saved.notes.database.Note;
import org.wikipedia.saved.notes.database.NoteDbHelper;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(application = WikipediaApp.class)
public class NoteDbHelperTest {
    private NoteDbHelper noteDbHelper;
    private Note note;

    @Before
    public void setup() {
        noteDbHelper = NoteDbHelper.getInstance();
        this.note = new Note("This is a saved note", new WikiSite("test.wikimedia.org"), "Article Title");
        noteDbHelper.saveNote(this.note);
    }

    @Test
    public void testGetSingleNoteById() {
        Note note = noteDbHelper.getNoteById(1);
        assertEquals("This is a saved note", note.content());
        assertEquals("Article Title", note.title());
        assertEquals("test.wikimedia.org", note.wiki().authority());
    }

    @Test
    public void testGetAllNotes() {
        List<Note> notes = noteDbHelper.getAllNotes();
        assertEquals(1, notes.size());
        assertEquals("Article Title", notes.get(0).title());
    }

    @Test
    public void testCreateNote() {
        Note note = new Note("Another saved note", new WikiSite("test.wikimedia.org"), "Another Title");
        noteDbHelper.saveNote(note);

        assertEquals(2, noteDbHelper.getAllNotes().size());
    }

    @Test
    public void testUpdateNote() {
        this.note.description("This is a test description");
        noteDbHelper.updateNote(this.note);

        Note updatedNote = noteDbHelper.getNoteById(this.note.id());
        assertEquals("This is a test description", updatedNote.description());
    }

    @Test
    public void testDeleteNote() {
        noteDbHelper.deleteNote(this.note);
        assertEquals(0, noteDbHelper.getAllNotes().size());
    }
}
