package org.wikipedia.saved;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.page.PageTitle;
import org.wikipedia.saved.notes.database.Note;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class NoteTest {
    private static final WikiSite WIKI = WikiSite.forLanguageCode("en");

    @Test
    public void testGetPageTitleFromNote() {
        PageTitle title = new PageTitle("Main page", WIKI, "//foo/thumb.jpg");
        Note note = new Note("Test content", title);

        assertEquals(title, note.getPageTitle());
    }
}
