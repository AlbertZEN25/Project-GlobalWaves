package app.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.audio.Collections.Album;
import app.audio.Collections.AlbumOutput;
import app.audio.Files.Song;
import app.pages.ArtistPage;
import app.pages.pageContent.Event;
import app.pages.pageContent.Merchandise;
import lombok.Getter;

/**
 * The type Artist.
 */
public final class Artist extends ContentCreator {

    @Getter
    private final ArrayList<Album> albums;
    @Getter
    private final ArrayList<Merchandise> merch;
    @Getter
    private final ArrayList<Event> events;
    @Getter
    private double songRevenue; // Venituri din cântece
    @Getter
    private double merchRevenue; // Venituri din merch
    @Getter
    // O harta cu fiecare melodie a artistului si cat venit a generat fiecare
    private Map<String, Double> artistSongsRevenue = new HashMap<>();

    /**
     * Instantiates a new Artist.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public Artist(final String username, final int age, final String city) {
        super(username, age, city);
        albums = new ArrayList<>();
        merch = new ArrayList<>();
        events = new ArrayList<>();
        songRevenue = 0.0;
        merchRevenue = 0.0;

        super.setPage(new ArtistPage(this));
    }

    /**
     * Gets event.
     *
     * @param eventName the event name
     * @return the event
     */
    public Event getEvent(final String eventName) {
        for (Event event : events) {
            if (event.getName().equals(eventName)) {
                return event;
            }
        }

        return null;
    }

    /**
     * Gets album.
     *
     * @param albumName the album name
     * @return the album
     */
    public Album getAlbum(final String albumName) {
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }

        return null;
    }

    /**
     * Gets all songs.
     *
     * @return the all songs
     */
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        albums.forEach(album -> songs.addAll(album.getSongs()));

        return songs;
    }

    /**
     * Show albums array list.
     *
     * @return the array list
     */
    public ArrayList<AlbumOutput> showAlbums() {
        ArrayList<AlbumOutput> albumOutput = new ArrayList<>();
        for (Album album : albums) {
            albumOutput.add(new AlbumOutput(album));
        }

        return albumOutput;
    }

    /**
     * Get user type
     *
     * @return user type string
     */
    public String userType() {
        return "artist";
    }

    /**
     * Adaugă venituri din melodii la totalul artistului.
     * Această metodă crește veniturile totale ale artistului din cântece prin adăugarea
     *            sumei specificate.
     *
     * @param revenue Veniturile obținute de la melodia curentă.
     */
    public void addSongRevenue(final double revenue) {
        this.songRevenue += revenue;
    }

    /**
     * Adaugă venituri din vânzarea de merchandise la totalul artistului.
     * Această metodă crește veniturile totale ale artistului din merchandise prin adăugarea
     *            sumei specificate.
     *
     * @param revenue Veniturile obținute din vânzarea unui produs de merch.
     */
    public void addMerchRevenue(final double revenue) {
        this.merchRevenue += revenue;
    }

    /**
     * Determină melodia cea mai profitabilă a unui artist.
     * <p>
     * Această metodă analizează un map care conține veniturile asociate fiecărei piese a artistului
     *         și returnează titlul piesei cu cel mai mare venit. Dacă nu există nicio piesă sau
     *         dacă veniturile sunt goale, returnează "N/A".
     * </p>
     *
     * @return Titlul piesei cu cel mai mare venit, sau "N/A" dacă nu există date.
     */
    public String determineMostProfitableSong() {
        // Verifică dacă harta cu veniturile pieselor este goală
        if (artistSongsRevenue.isEmpty()) {
            return "N/A";
        }

        // Procesează harta pentru a găsi piesa cu cel mai mare venit
        return artistSongsRevenue.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("N/A");
    }
}
