package org.wikipedia.page.chat;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IconGenerator {
    private Map<String, Integer> iconMap;
    private Context context;

    final protected static String iconPrefix = "animal_";
    final protected static int ICON_LIMIT_LOW = 1;
    final protected static int ICON_LIMIT_HIGH = 45;

    public IconGenerator(Context context) {
        iconMap = new HashMap<>();
        this.context = context;
    }

    public int getIconFromName(String userName) {
        if (!iconMap.containsKey(userName)) {
            Random randomGenerator = new Random();
            int iconId = randomGenerator.nextInt(ICON_LIMIT_HIGH - ICON_LIMIT_LOW) + ICON_LIMIT_LOW;
            int resourceId = context.getResources().getIdentifier(iconPrefix + iconId, "drawable", context.getPackageName());

            iconMap.put(userName, resourceId);
        }

        return iconMap.get(userName);
    }
}
