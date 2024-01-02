package app.user;

import lombok.Getter;
import lombok.Setter;

/**
 * Clasa care reprezintă veniturile unui artist, inclusiv veniturile din melodii, merchandising,
 *          și alte informații relevante precum cea mai profitabilă melodie.
 * Această clasă este utilizată pentru a stoca și gestiona veniturile generate de un artist
 *          în cadrul platformei.
 */
@Getter @Setter
public class ArtistRevenue {
    private double songRevenue; // Veniturile artistului din melodii
    private double merchRevenue; // Veniturile artistului din vânzările de merch
    private int ranking; // Clasamentul artistului pe platformă bazat pe venituri
    private String mostProfitableSong; // Melodia cea mai profitabilă a artistului

    public ArtistRevenue(final double songRevenue, final double merchRevenue, final int ranking,
                         final String mostProfitableSong) {
        this.songRevenue = songRevenue;
        this.merchRevenue = merchRevenue;
        this.ranking = ranking;
        this.mostProfitableSong = mostProfitableSong;
    }
}
