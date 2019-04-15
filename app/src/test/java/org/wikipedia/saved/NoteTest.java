package org.wikipedia.saved;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.page.PageTitle;
import org.wikipedia.saved.notes.database.Note;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class NoteTest {
    private static final WikiSite WIKI = WikiSite.forLanguageCode("en");
    private PageTitle title = new PageTitle("Main page", WIKI, "//foo/thumb.jpg");

    @Test
    public void testGetPageTitleFromNote() {
        Note note = new Note("Test content", title, new Date());
        assertEquals(title, note.getPageTitle());
    }

    @Test
    public void testNoteTitleComparator() {
        PageTitle newerTitle = new PageTitle("Not Main page", WIKI, "//foo/thumb.jpg");

        Note olderNote = new Note("Test content", title, new Date());
        Note newerNote = new Note("Test content", newerTitle, new Date());
        int result = Note.titleComparator.compare(olderNote, newerNote);
        assertTrue(result < 0);
    }

    @Test
    public void testNoteCreationComparator() {
        Note olderNote = new Note("A: a test title", title, new Date());
        Note newerNote = new Note("B: another test title", title, new Date(olderNote.creation().getTime() + 1000));
        int result = Note.creationComparator.compare(olderNote, newerNote);
        assertTrue(result < 0);
    }
}
