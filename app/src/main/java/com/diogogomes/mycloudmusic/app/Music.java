package com.diogogomes.mycloudmusic.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dgomes on 02/05/14.
 */
public class Music {
    /**
     * An array of sample (dummy) items.
     */
    public static List<MusicEntry> ITEMS = new ArrayList<MusicEntry>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, MusicEntry> ITEM_MAP = new HashMap<String, MusicEntry>();

    public static void addItem(MusicEntry item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class MusicEntry {
        public String id;
        public String content;

        public MusicEntry(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
