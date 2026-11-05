package com.example.scorely.data.remote;

import com.example.scorely.model.Album;
import com.example.scorely.model.Game;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/** ממיר DTO מה-API לעצמי המודל (Game / Album). */
public class DtoMapper {

    private static long parseDate(String s) {
        if (s == null) return 0;
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(s).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    public static Game toGame(RawgDtos.RawgGame d) {
        String dev = (d.developers != null && !d.developers.isEmpty()) ? d.developers.get(0).name : "";
        Game g = new Game("game_" + d.id, d.name, parseDate(d.released), d.backgroundImage, dev, d.metacritic);
        if (d.genres != null) for (RawgDtos.Named n : d.genres) g.addTag(n.name);
        if (d.platforms != null) for (RawgDtos.PlatformWrapper p : d.platforms) g.addPlatform(p.platform.name);
        return g;
    }

    public static Album toAlbum(DeezerDtos.DeezerAlbum d) {
        String artist = d.artist != null ? d.artist.name : "";
        Album a = new Album("album_" + d.id, d.title, parseDate(d.releaseDate), d.coverMedium, artist, d.nbTracks);
        if (d.genres != null && d.genres.data != null)
            for (DeezerDtos.Genre g : d.genres.data) a.addTag(g.name);
        return a;
    }
}
