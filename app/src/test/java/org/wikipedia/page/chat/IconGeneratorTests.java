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
    private String packageName = "Justin Cortana";
    private String userPrefix = "anon";
    private String badUserName = "no-numbers-here";
    private int userNum1 = 0;
    private int userNumOverflow = IconGenerator.ICON_LIMIT_HIGH;
    private int userNumHigh = IconGenerator.ICON_LIMIT_HIGH - IconGenerator.ICON_LIMIT_LOW;

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
            when(resourcesMock.getIdentifier(IconGenerator.ICON_PREFIX + i, drawablePath, packageName)).thenReturn(i);
        }
    }

    @Test
    public void testCorrectIconGenerated() {
        int userIcon = iconGenerator.getIconFromName(getUserName(userNum1));
        assert(userIcon == IconGenerator.ICON_LIMIT_LOW);
    }

    @Test
    public void testOverflowIconGenerated() {
        int userIcon = iconGenerator.getIconFromName(getUserName(userNumOverflow));
        assert(userIcon == IconGenerator.ICON_LIMIT_LOW);
    }

    @Test
    public void testUperBoundIconGenerated() {
        int userIcon = iconGenerator.getIconFromName(getUserName(userNumHigh));
        assert(userIcon == IconGenerator.ICON_LIMIT_HIGH);
    }

    @Test
    public void testUserIconSeveralMessages() {
        int userIconMessage1 = iconGenerator.getIconFromName(getUserName(userNum1));
        int userIconMessage2 = iconGenerator.getIconFromName(getUserName(userNum1));

        assert(userIconMessage1 == userIconMessage2);
    }

    @Test
    public void testIconGeneratorBadUserName() {
        int userIcon = iconGenerator.getIconFromName(badUserName);
        assert(userIcon == IconGenerator.ICON_LIMIT_LOW);
    }

    private String getUserName(int userNum) {
        return userPrefix + userNum;
    }
}
