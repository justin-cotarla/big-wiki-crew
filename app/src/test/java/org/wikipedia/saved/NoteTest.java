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
    private final int extra = 1000;
    private String testContent = "Test content";

    private static final WikiSite WIKI = WikiSite.forLanguageCode("en");
    private PageTitle title = new PageTitle("Main page", WIKI, "//foo/thumb.jpg");

    @Test
    public void testGetPageTitleFromNote() {
        Note note = new Note(testContent, title, new Date());
        assertEquals(title, note.getPageTitle());
    }

    @Test
    public void testNoteTitleComparator() {
        PageTitle newerTitle = new PageTitle("Not Main page", WIKI, "//foo/thumb.jpg");

        Note olderNote = new Note(testContent, title, new Date());
        Note newerNote = new Note(testContent, newerTitle, new Date());
        int result = Note.getArticleTitleComparator().compare(olderNote, newerNote);
        assertTrue(result < 0);
    }

    @Test
    public void testNoteCreationComparator() {
        Note olderNote = new Note("A: a test title", title, new Date());
        Note newerNote = new Note("B: another test title", title, new Date(olderNote.createdAt().getTime() + extra));
        int result = Note.getCreatedArComparator().compare(olderNote, newerNote);
        assertTrue(result < 0);
    }
}
