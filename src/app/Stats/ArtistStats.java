package app.Stats;

import app.audio.Collections.Album;
import app.audio.Files.Song;
import app.user.UserAbstract;
import app.user.Artist;
import fileio.input.CommandInput;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Clasa ArtistStats este responsabilă pentru calcularea și adăugarea statisticilor specifice
 *       unui artist într-un ObjectNode.
 * Această clasă extinde StatsTemplate și implementează metodele abstracte pentru a oferi
 *       statistici pentru fiecare artist.
 */
public class ArtistStats extends StatsTemplate {

    /**
     * Adaugă statisticile specifice unui artist într-un ObjectNode.
     *
     * @param resultNode Nodul ObjectNode în care se adaugă statisticile.
     * @param currentUser Utilizatorul curent, presupus a fi un artist.
     * @param commandInput Datele de intrare ale comenzii.
     */
    @Override
    protected void addSpecificStats(final ObjectNode resultNode, final UserAbstract currentUser,
                                    final CommandInput commandInput) {
        // Verifică dacă utilizatorul curent este un artist
        if (currentUser.userType().equals("artist")) {

            // Cast la Artist pentru a accesa metodele specifice
            Artist artist = (Artist) currentUser;

            // Verifică dacă artistul are ascultători
            if (getListenersCount(artist) > 0) {
                // Adaugă statisticile specifice unui artist
                resultNode.set("topAlbums", createStatsNode(getTopAlbums(artist)));
                resultNode.set("topSongs", createStatsNode(getTopSongs(artist)));
                resultNode.set("topFans", OBJECT_MAPPER.valueToTree(getTopFans(artist)));
                resultNode.put("listeners", getListenersCount(artist));
            }
        }
    }

    /**
     * Verifică dacă există date statistice de afișat pentru un artist specific.
     *
     * @param currentUser Utilizatorul curent, presupus a fi un artist.
     * @return true dacă artistul are date statistice relevante, altfel false.
     */
    @Override
    protected boolean hasDataToDisplay(final UserAbstract currentUser) {
        // Verifică tipul utilizatorului curent
        if (currentUser.userType().equals("artist")) {
            // Cast la Artist pentru a accesa metodele specifice
            Artist artist = (Artist) currentUser;

            // Verifică dacă artistul are ascultători
            if (getListenersCount(artist) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculează și returnează o hartă a albumelor și numărului lor de ascultări
     *            pentru artistul curent.
     *
     * @param artist Artist-ul pentru care se calculeaza top-ul albumelor acestuia.
     * @return O hartă sortată {@link LinkedHashMap} care conține perechi cheie-valoare,
     *         unde cheia este numele albumului și valoarea este numărul total de ascultări.
     */
    public Map<String, Integer> getTopAlbums(final Artist artist) {
        // Inițializează o hartă pentru a stoca albumele și numărul lor de ascultări
        Map<String, Integer> topAlbums = new HashMap<>();

        // Parcurge fiecare album din lista de albume a artistului
        for (Album album : artist.getAlbums()) {
            // Obține numărul total de ascultări pentru fiecare album
            int listenCount = album.getListenCount();

            // Include doar albumele cu număr pozitiv de ascultări
            if (listenCount > 0) {
                topAlbums.put(album.getName(), listenCount);
            }
        }

        // Sortează harta în funcție de numărul de ascultări, descrescător
        // În caz de egalitate, sortează după numele albumului
        return topAlbums.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry::getKey))
                .limit(getLimit()) // Limita la primele 5 rezultate
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Calculează și returnează o hartă a melodiilor artistului și numărului lor de ascultări.
     * Această metodă parcurge toate albumele unui artist și sumează numărul de ascultări
     * pentru fiecare melodie, creând astfel o hartă cu melodiile și numărul total de ascultări.
     *
     * @param artist Artist-ul pentru care se calculeaza top-ul melodiilor acestuia.
     * @return O hartă sortată {@link LinkedHashMap} care conține perechi cheie-valoare,
     *         unde cheia este numele melodiei și valoarea este numărul total de ascultări.
     */
    public Map<String, Integer> getTopSongs(final Artist artist) {
        // Map pentru a stoca numărul de ascultări pentru fiecare melodie
        Map<String, Integer> topSongs = new HashMap<>();

        // Iterează prin toate albumele artistului și melodiile lor
        for (Album album : artist.getAlbums()) {
            for (Song song : album.getSongs()) {
                // Obține numărul de ascultări pentru fiecare melodie
                int listenCount = song.getListenCount();

                // Adaugă sau actualizează numărul de ascultări în map
                topSongs.merge(song.getName(), listenCount, Integer::sum);
            }
        }

        // Sortează harta în funcție de numărul de ascultări, descrescător
        // În caz de egalitate, sortează după numele melodiei
        return topSongs.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry::getKey))
                .limit(getLimit()) // Limita la primele 5 rezultate
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Calculează și returnează o listă a celor mai activi fani ai unui artist, bazată
     *            pe numărul de ascultări.
     *
     * @param artist Artist-ul pentru care se calculeaza top-ul fanilor acestuia.
     * @return O listă de {@link String} care conține topul utilizatorilor, sortați în funcție de
     *          numărul total de ascultări ale melodiilor artistului cur
     */
    public List<String> getTopFans(final Artist artist) {
        // Map pentru a stoca numărul de ascultări pentru fiecare fan
        Map<String, Integer> topFanListens = new HashMap<>();

        // Obține toate melodiile artistului curent
        List<Song> songs = artist.getAllSongs();

        // Parcurgem toate melodiile artistului
        for (Song song : songs) {
            // Obține mapa cu numărul de ascultări pentru fiecare utilizator al melodiei curente
            Map<String, Integer> listens = song.getUserListenCounts();

            // Adaugă sau actualizează numărul de ascultări pentru fiecare utilizator
            for (Map.Entry<String, Integer> entry : listens.entrySet()) {
                topFanListens.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        // Sortăm fanii în funcție de numărul total de ascultări și extragem numele lor într-o listă
        return topFanListens.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry::getKey))
                .limit(getLimit()) // Limita la primele 5 rezultate
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Calculează și returnează numărul total de ascultători unici ai unui artist.
     * Această metodă agregă toți ascultătorii unici de la toate melodiile artistului,
     * oferind astfel un număr total de ascultători unici pentru întreaga sa colecție de muzică.
     *
     * <p>Un ascultător unic este definit ca un utilizator care a ascultat cel puțin o dată oricare
     * dintre melodiile artistului. Aceasta include ascultări de la toate melodiile artistului,
     * indiferent dacă sunt ascultate o dată sau de mai multe ori.</p>
     *
     * @param artist Artist-ul pentru care se calculeaza nr. total de ascultători unici.
     * @return Numărul total de ascultători unici ai artistului curent.
     */
    public int getListenersCount(final Artist artist) {
        // Set pentru a stoca ascultătorii unici ai artistului
        Set<String> uniqueListeners = new HashSet<>();

        // Obține toate melodiile artistului
        List<Song> songs = artist.getAllSongs();

        // Parcurgem toate melodiile artistului
        for (Song song : songs) {
            // Obține setul de ascultători unici pentru fiecare melodie
            Set<String> listeners = song.getUniqueListeners();

            // Adaugă toți ascultătorii unici în setul global
            uniqueListeners.addAll(listeners);
        }

        // Returnează numărul total de ascultători unici
        return uniqueListeners.size();
    }
}
