package app.audio.Collections;

import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The type Album.
 */
@Getter @Setter
public final class Album extends AudioCollection {
    private String description;
    private List<Song> songs;
    private Integer releaseYear;
    private Integer followers;

    /**
     * Instantiates a new Album.
     *
     * @param name        the name
     * @param description the description
     * @param owner       the owner
     * @param songs       the songs
     * @param releaseYear the release year
     */
    public Album(final String name, final String description, final String owner,
                 final List<Song> songs, final Integer releaseYear) {
        super(name, owner);
        this.songs = songs;
        this.description = description;
        this.releaseYear = releaseYear;
    }

    @Override
    public int getNumberOfTracks() {
        return songs.size();
    }

    @Override
    public AudioFile getTrackByIndex(final int index) {
        return songs.get(index);
    }

    @Override
    public boolean matchesDescription(final String desc) {
        return description.equals(desc);
    }

    @Override
    public boolean containsTrack(final AudioFile track) {
        return songs.contains(track);
    }

    /**
     * Calculează numărul total de ascultări pentru toate melodiile dintr-un album.
     *
     * @return Numărul total de ascultări ale albumului, calculat ca suma ascultărilor individuale
     *         ale fiecărei melodii din album.
     */
    public int getListenCount() {
        // Inițializăm un contor pentru numărul total de ascultări al albumului curent
        int totalListens = 0;

        // Parcurgem lista de melodii din album
        for (Song song : songs) {

            // Adăugăm numărul de ascultări al fiecărei melodii la total
            totalListens += song.getListenCount();
        }
        return totalListens;
    }
}
