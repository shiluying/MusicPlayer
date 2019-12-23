package com.shiluying.musicplayer.Music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MusicContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<MusicItem> ITEMS = new ArrayList<MusicItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, MusicItem> ITEM_MAP = new HashMap<String, MusicItem>();

    private static final int COUNT = 25;
    /**
     * A dummy item representing a piece of content.
     */
    public static class MusicItem {
        public final Integer id;
        public final String content;

        public MusicItem(Integer id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return "Id"+id+"Content"+content;
        }
    }
}
