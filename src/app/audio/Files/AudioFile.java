package app.audio.Files;

import app.audio.LibraryEntry;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public abstract class AudioFile extends LibraryEntry {
    private final Integer duration;

    @Getter
    private Integer listenCount; // Nr. de ascultari al fișier-ului curent (in total)
    // Hartă cu numărul de ascultări ale fișier-ului pentru fiecare user
    private final Map<String, Integer> userListenCounts = new HashMap<>();
    // Set cu utilizatorii unici care au ascultat fișierul audio
    private final Set<String> uniqueListeners = new HashSet<>();

    public AudioFile(final String name, final Integer duration) {
        super(name);
        this.duration = duration;
        this.listenCount = 0;
    }

    /**
     * Metodă pentru a incrementa numărul de ascultări.
     */
    public void incrementListenCount() {
        this.listenCount++;
    }

    /**
     * Metodă pentru a incrementa numărul de ascultări de către un anumit utilizator
     * și a adăuga utilizatorul în setul de ascultători unici.
     *
     * @param username Numele de utilizator.
     */
    public void incrementUserListenCount(final String username) {
        userListenCounts.merge(username, 1, Integer::sum);
        uniqueListeners.add(username);
    }

    /**
     * Obține o hartă cu numărul de ascultări ale melodiei/episodului pentru fiecare utilizator.
     *
     * @return Harta cu ascultări.
     */
    public Map<String, Integer> getUserListenCounts() {
        return new HashMap<>(userListenCounts);
    }

    /**
     * Obține un set cu utilizatorii unici care au ascultat melodia/eăisodul.
     *
     * @return Setul de utilizatori unici.
     */
    public Set<String> getUniqueListeners() {
        return new HashSet<>(uniqueListeners);
    }
}