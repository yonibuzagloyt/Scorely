package com.example.scorely.data.repository;

import com.example.scorely.data.local.ItemEntity;
import com.example.scorely.data.local.ItemWithTags;
import com.example.scorely.data.local.TagEntity;
import com.example.scorely.model.Album;
import com.example.scorely.model.Game;
import com.example.scorely.model.MediaItem;
import com.example.scorely.model.MediaType;

/** ממיר בין עצמי המודל (MediaItem) לישויות Room ובחזרה. */
public class ItemMapper {

    public static ItemEntity toEntity(MediaItem m) {
        ItemEntity e = new ItemEntity();
        e.id = m.getId();
        e.type = m.getType().name();
        e.title = m.getTitle();
        e.releaseDate = m.getReleaseDate();
        e.coverUrl = m.getCoverUrl();
        e.updatedAt = m.getUpdatedAt();
        if (m instanceof Game) {
            Game g = (Game) m;
            e.creator = g.getDeveloper();
            e.platformsCsv = String.join(",", g.getPlatforms());
            e.criticScore = g.getCriticScore();
        } else if (m instanceof Album) {
            Album a = (Album) m;
            e.creator = a.getArtist();
            e.trackCount = a.getTrackCount();
        }
        return e;
    }

    public static MediaItem toDomain(ItemWithTags row) {
        ItemEntity e = row.item;
        MediaItem result;
        if (MediaType.valueOf(e.type) == MediaType.GAME) {
            Game g = new Game(e.id, e.title, e.releaseDate, e.coverUrl, e.creator, e.criticScore);
            if (e.platformsCsv != null && !e.platformsCsv.isEmpty())
                for (String p : e.platformsCsv.split(",")) g.addPlatform(p);
            result = g;
        } else {
            result = new Album(e.id, e.title, e.releaseDate, e.coverUrl, e.creator, e.trackCount);
        }
        if (row.tags != null) for (TagEntity t : row.tags) result.addTag(t.name);
        return result;
    }
}
