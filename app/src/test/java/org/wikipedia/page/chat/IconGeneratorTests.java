package org.wikipedia.page.chat;

import android.content.Context;
import android.content.res.Resources;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IconGeneratorTests {
    private IconGenerator iconGenerator;
    private String drawablePath = "drawable";
    private String packageName = "barista-berge";
    private String user1 = "Justin Cortana";

    private Context contextMock;
    private Resources resourcesMock;

    @Before
    public void setUp() {
        contextMock = mock(Context.class);
        resourcesMock = mock(Resources.class);

        iconGenerator = new IconGenerator(contextMock);

        when(contextMock.getResources()).thenReturn(resourcesMock);
        when(contextMock.getPackageName()).thenReturn(packageName);

        for (int i = IconGenerator.ICON_LIMIT_LOW; i <= IconGenerator.ICON_LIMIT_HIGH; i++) {
            when(resourcesMock.getIdentifier(IconGenerator.iconPrefix + i, drawablePath, packageName)).thenReturn(i);
        }
    }

    @Test
    public void testMainUserIcon() {
        String mainUser = ChatFragment.MAIN_USER;

        int userIcon = iconGenerator.getIconFromName(mainUser);
        assert(userIcon >= IconGenerator.ICON_LIMIT_LOW);
        assert(userIcon <= IconGenerator.ICON_LIMIT_HIGH);
    }

    @Test
    public void testUserIconSeveralMessages() {
        int userIconMessage1 = iconGenerator.getIconFromName(user1);
        int userIconMessage2 = iconGenerator.getIconFromName(user1);

        assert(userIconMessage1 == userIconMessage2);
        assert(userIconMessage2 >= IconGenerator.ICON_LIMIT_LOW);
        assert(userIconMessage2 <= IconGenerator.ICON_LIMIT_HIGH);
    }
}
