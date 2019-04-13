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

    @Before
    public void setup() {
        noteDbHelper = NoteDbHelper.getInstance();
    }

    @Test
    public void testGetAllNotes() {
        WikiSite site = new WikiSite("test.wikimedia.org");
        Note note = new Note("This is a saved note", site, "Article Title");

        List<Note> notes = noteDbHelper.getAllNotes();
        assertEquals(0, notes.size());

        noteDbHelper.createNote(note);
        notes = noteDbHelper.getAllNotes();
        assertEquals(1, notes.size());
        assertEquals(notes.get(0).title(), "Article Title");
    }
}
