package app.monetization;

import app.Admin;
import app.audio.Files.Song;
import app.user.Artist;
import app.user.User;
import lombok.Setter;

import java.util.List;

public final class RevenueService {
    private static RevenueService instance = null;
    private final double totalValue = 1000000.0;
    @Setter
    private double adPrice;

    private RevenueService() {
    }

    /**
     * Returnează instanța singleton a clasei RevenueService.
     *
     * @return Instanța singleton a clasei RevenueService.
     */
    public static RevenueService getInstance() {
        if (instance == null) {
            instance = new RevenueService();
        }
        return instance;
    }

    /**
     * Distribuie veniturile generate de ascultările efectuate de utilizatorii Premium.
     * Această metodă calculează veniturile pe care fiecare artist le va primi pe baza
     *           numărului de melodii ascultate de utilizatorii Premium.
     *
     * @param user Utilizatorul curent care e pe modul Premium.
     */
    public void revenueFromPremiumListens(final User user) {

        List<Song> premiumSongs = user.getSongsListenedPremium();

        // Calculează numărul total de melodii ascultate in modul Premium
        int totalListenedSongs = premiumSongs.size();

        // Verifică dacă există melodii ascultate
        if (totalListenedSongs == 0) {
            return;
        }

        // Calculează valoarea pe melodie, bazată pe totalul de ascultări
        double valuePerSong = totalValue / totalListenedSongs;

        // Parcurge lista de melodii ascultate
        for (Song song : premiumSongs) {
            // Obține numele artistului pentru melodia curentă
            String artistName = song.getArtist();

            // Obține artistul și adaugă venitul la totalul său
            Artist artist = Admin.getInstance().getArtist(artistName);
            artist.addSongRevenue(valuePerSong);

            // Adaugă venitul la melodia curentă
            artist.getArtistSongsRevenue().merge(song.getName(), valuePerSong, Double::sum);
        }
    }

    /**
     * Distribuie veniturile generate de ascultările efectuate de utilizatorii normali.
     * Această metodă calculează veniturile pe care fiecare artist le va primi pe baza
     *           numărului de melodii ascultate de userii normali intre reclame.
     *
     * @param user Utilizatorul curent care vede reclama.
     */
    public void revenueFromFreeListens(final User user) {

        List<Song> freeSongs = user.getSongsListenedFree();

        // Calculează numărul total de melodii ascultate intre reclame
        int totalListenedSongs = freeSongs.size();

        // Verifică dacă există melodii ascultate
        if (totalListenedSongs == 0) {
            return;
        }

        // Calculează valoarea pe melodie, bazată pe totalul de ascultări
        double valuePerSong = adPrice / totalListenedSongs;

        // Parcurge lista de melodii ascultate
        for (Song song : freeSongs) {
            // Obține numele artistului pentru melodia curentă
            String artistName = song.getArtist();

            // Obține artistul și adaugă venitul la totalul său
            Artist artist = Admin.getInstance().getArtist(artistName);
            artist.addSongRevenue(valuePerSong);

            // Adaugă venitul la melodia curentă
            artist.getArtistSongsRevenue().merge(song.getName(), valuePerSong, Double::sum);
        }
        // Curăță lista de melodii ascultate între reclame
        freeSongs.clear();
    }
}
