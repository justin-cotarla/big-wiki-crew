package org.wikipedia.history;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.wikipedia.WikipediaApp;
import org.wikipedia.settings.Prefs;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@RunWith(RobolectricTestRunner.class)
public class NoHistoryUpdateHistoryTaskTest {
    WikipediaApp mockWikipediaApp;
    UpdateHistoryTask historyTask;
    HistoryEntry mockHistoryEntry;

    @Before
    public void setUp() throws Exception {
        mockWikipediaApp = mock(WikipediaApp.class);
        mockHistoryEntry = mock(HistoryEntry.class);
        historyTask = new UpdateHistoryTask(mockHistoryEntry);
    }

    @Test
    public void testNoHistoryUpdateHistoryTaskRun() {
        Prefs.setShowEditNoHistory(true);

        try {
            historyTask.run();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(mockWikipediaApp, never()).getDatabaseClient(HistoryEntry.class);

    }
}