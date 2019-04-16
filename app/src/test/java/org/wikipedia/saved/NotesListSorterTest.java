package org.wikipedia.saved;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.page.PageTitle;
import org.wikipedia.saved.notes.database.Note;
import org.wikipedia.saved.notes.NotesListSorter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("magicnumber")
@RunWith(RobolectricTestRunner.class)
public class NotesListSorterTest {
    private List<Note> notes;
    private long now = new Date().getTime();

    private Note first = new Note("", new PageTitle("C", WikiSite.forLanguageCode("en"), ""), new Date(now));
    private Note second = new Note("", new PageTitle("A", WikiSite.forLanguageCode("en"), ""), new Date(now + 1000));
    private Note third = new Note("", new PageTitle("B", WikiSite.forLanguageCode("en"), ""), new Date(now + 2000));

    @Before
    public void setup() {
        notes = new ArrayList<>();
        notes.add(first);
        notes.add(second);
        notes.add(third);
    }

    @Test
    public void testSortByTitleAscending() {
        List<Note> sorted = NotesListSorter.sort(notes, 0);
        assertEquals(second, sorted.get(0));
        assertEquals(third, sorted.get(1));
        assertEquals(first, sorted.get(2));
    }

    @Test
    public void testSortByTitleDescending() {
        List<Note> sorted = NotesListSorter.sort(notes, 1);
        assertEquals(first, sorted.get(0));
        assertEquals(third, sorted.get(1));
        assertEquals(second, sorted.get(2));
    }

    @Test
    public void testSortByDateAddedAscending() {
        List<Note> sorted = NotesListSorter.sort(notes, 2);
        assertEquals(first, sorted.get(0));
        assertEquals(second, sorted.get(1));
        assertEquals(third, sorted.get(2));
    }

    @Test
    public void testSortByDateAddedDescending() {
        List<Note> sorted = NotesListSorter.sort(notes, 3);
        assertEquals(third, sorted.get(0));
        assertEquals(second, sorted.get(1));
        assertEquals(first, sorted.get(2));
    }
}