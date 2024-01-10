package app.statistics;

import app.Admin;
import app.user.UserAbstract;
import fileio.input.CommandInput;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

/**
 * Clasa abstractă StatsTemplate definește tamplate-ul pentru calcularea statisticilor pentru
 *        diferiți utilizatori.
 * Această clasă este destinată a fi extinsă de subclase care implementează logica specifică
 *        pentru diferite tipuri de utilizatori.
 */
public abstract class StatsTemplate {
    private final int limit = 5; // Limita la primele rezultate
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Obține instanța Admin pentru a avea acces la toate informațiile
    final Admin adminInstance = Admin.getInstance();

    /**
     * Calculează statisticile pentru un utilizator dat și le împachetează într-un ObjectNode.
     * Această metodă este un template method care definește scheletul algoritmului de calcul
     *          al statisticilor.
     *
     * @param currentUser Utilizatorul pentru care se calculează statisticile.
     * @param commandInput Informațiile de intrare legate de comandă.
     * @return Un ObjectNode care conține statisticile calculate sau un mesaj dacă nu există date.
     */
    public final ObjectNode calculateStats(final UserAbstract currentUser,
                                           final CommandInput commandInput) {
        // Creează un nou ObjectNode pentru a păstra rezultatele
        ObjectNode resultNode = OBJECT_MAPPER.createObjectNode();

        // Verifică dacă există date de afișat pentru utilizatorul curent
        if (hasDataToDisplay(currentUser)) {
            // Dacă există date, adaugă statisticile specifice în resultNode
            addSpecificStats(resultNode, currentUser, commandInput);
        } else {
            // Dacă nu există date, adaugă un mesaj corespunzător în resultNode
            resultNode.put("message", "No data to show for " + currentUser.userType() + " "
                        + commandInput.getUsername() + ".");
        }

        return resultNode;
    }

    /**
     * Creează și returnează un ObjectNode ce conține statisticile furnizate sub formă de mapă.
     *
     * @param stats Mapa ce conține perechi cheie-valoare, unde cheia este un String (de exemplu,
     *         numele unei melodii) și valoarea este un Integer (de exemplu, numărul de ascultări).
     * @return Un ObjectNode care conține toate statisticile date, fiecare pereche cheie-valoare
     *         fiind transformată într-un câmp al nodului JSON.
     */
    static ObjectNode createStatsNode(final Map<String, Integer> stats) {
        // Creează un nou ObjectNode folosind ObjectMapper
        ObjectNode statsNode = OBJECT_MAPPER.createObjectNode();

        // Adaugă fiecare pereche cheie-valoare din mapa stats în ObjectNode
        stats.forEach(statsNode::put);
        return statsNode;
    }

    /**
     * Adaugă statistici specifice în ObjectNode, în funcție de tipul utilizatorului.
     * Această metodă trebuie implementată de subclase pentru a defini logica specifică
     *          fiecărui tip de utilizator.
     *
     * @param resultNode Nodul în care se adaugă statisticile.
     * @param currentUser Utilizatorul curent pentru care se calculează statisticile.
     * @param commandInput Informații de intrare legate de comandă.
     */
    protected abstract void addSpecificStats(ObjectNode resultNode, UserAbstract currentUser,
                                             CommandInput commandInput);

    /**
     * Verifică dacă există date de afișat pentru utilizatorul dat.
     * Această metodă trebuie implementată de subclase pentru a determina dacă un anumit tip
     *         de utilizator are date de afișat.
     *
     * @param currentUser Utilizatorul curent pentru care se verifică disponibilitatea datelor.
     * @return true dacă există date de afișat, altfel false.
     */
    protected abstract boolean hasDataToDisplay(UserAbstract currentUser);

    /**
     * Sortează și limitează o hartă furnizată pe baza valorilor sale, în ordine descrescătoare.
     * În cazul valorilor egale, se face o sortare suplimentară în ordine alfabetică a cheilor.
     * Rezultatul este o hartă nouă, limitată la un număr specific de intrări, păstrând ordinea
     *            stabilită de sortare.
     *
     * @param map Harta originală de tipul Map<String, Integer> care trebuie sortată și limitată.
     *
     * @return O hartă <String, Integer> care conține elementele sortate și limitate
     *         din harta originală. Această hartă păstrează ordinea elementelor după sortare.
     *         Dimensiunea hărții este limitată la un număr linit = 5 de elemente.
     */
    protected Map<String, Integer> sortAndLimit(final Map<String, Integer> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry::getKey))
                .limit(limit) // Limita la primele 5 rezultate
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }
}
