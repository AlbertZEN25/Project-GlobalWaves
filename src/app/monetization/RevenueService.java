package app.monetization;

import app.Admin;
import app.audio.Files.Song;
import app.user.Artist;
import app.user.User;
import lombok.Setter;

import java.util.List;

public final class RevenueService {

    private final double totalValue = 1000000.0;
    @Setter
    private double adPrice;

    public RevenueService(final double adPrice) {
        this.adPrice = adPrice;
    }

    /**
     * Distribuie veniturile generate de ascultări, fie pentru utilizatorii
     *            Premium, fie pentru cei Free.
     *
     * @param user Utilizatorul curent.
     * @param isPremium Flag care indică dacă trebuie să procesăm ascultările Premium sau Free.
     */
    private void distributeRevenue(final User user, final boolean isPremium) {
        List<Song> songsListened = isPremium
                ? user.getSongsListenedPremium() : user.getSongsListenedFree();
        int totalListenedSongs = songsListened.size();

        if (totalListenedSongs == 0) {
            return;
        }

        double valuePerSong = isPremium
                ? totalValue / totalListenedSongs : adPrice / totalListenedSongs;

        for (Song song : songsListened) {
            String artistName = song.getArtist();
            Artist artist = Admin.getInstance().getArtist(artistName);
            artist.addSongRevenue(valuePerSong);
            artist.getArtistSongsRevenue().merge(song.getName(), valuePerSong, Double::sum);
        }

        if (!isPremium) {
            songsListened.clear();  // Curăță lista doar pentru utilizatorii Free
        }
    }

    /**
     * Distribuie veniturile generate de ascultările efectuate de utilizatorii Premium.
     *
     * @param user Utilizatorul curent care e pe modul Premium
     */
    public void revenueFromPremiumListens(final User user) {
        distributeRevenue(user, true);
    }

    /**
     * Distribuie veniturile generate de ascultările efectuate de utilizatorii normali
     *
     * @param user Utilizatorul curent care nu este Premium
     */
    public void revenueFromFreeListens(final User user) {
        distributeRevenue(user, false);
    }
}
