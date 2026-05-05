package rcr.classical;

import java.util.*;

/**
 * Logique des Predicats (1er ordre) — domaine : industrie musicale.
 * Individus : soolking, feu, tif, flenn
 *
 * Axiomes :
 *   ax1 : Ax (EstRappeurAlgerien(x) => EstRappeur(x))
 *   ax2 : Ax (EstRappeur(x) => EstArtiste(x))
 *   ax3 : Ax (EstArtiste(x) => EstMusicien(x))
 *   ax4 : Ax (EstArtiste(x) ^ AlbumSorti(x) => RecitRoyalties(x))
 *   ax5 : Ax (SigneLabel(x) => PartageRoyalties(x))
 *   ax6 : Ax (IndependantLongtemps(x) => ExperienceSolo(x))
 *   ax7 : Ax (PlusDeDeuxAns(x) ^ AlbumSorti(x) => AlbumCertifie(x))
 *
 * Inference : chainage avant (forward chaining) sur le domaine fini.
 */
public class PredicateLogicMusic {

    private static final String[] INDIVIDUS = {"soolking", "feu", "tif", "flenn"};

    private static Set<String> deriver(Set<String> faits) {
        Set<String> derives = new HashSet<>(faits);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String x : INDIVIDUS) {
                // ax1 : EstRappeurAlgerien(x) => EstRappeur(x)
                if (derives.contains("EstRappeurAlgerien(" + x + ")")
                        && derives.add("EstRappeur(" + x + ")")) changed = true;
                // ax2 : EstRappeur(x) => EstArtiste(x)
                if (derives.contains("EstRappeur(" + x + ")")
                        && derives.add("EstArtiste(" + x + ")")) changed = true;
                // ax3 : EstArtiste(x) => EstMusicien(x)
                if (derives.contains("EstArtiste(" + x + ")")
                        && derives.add("EstMusicien(" + x + ")")) changed = true;
                // ax4 : EstArtiste(x) ^ AlbumSorti(x) => RecitRoyalties(x)
                if (derives.contains("EstArtiste(" + x + ")")
                        && derives.contains("AlbumSorti(" + x + ")")
                        && derives.add("RecitRoyalties(" + x + ")")) changed = true;
                // ax5 : SigneLabel(x) => PartageRoyalties(x)
                if (derives.contains("SigneLabel(" + x + ")")
                        && derives.add("PartageRoyalties(" + x + ")")) changed = true;
                // ax6 : IndependantLongtemps(x) => ExperienceSolo(x)
                if (derives.contains("IndependantLongtemps(" + x + ")")
                        && derives.add("ExperienceSolo(" + x + ")")) changed = true;
                // ax7 : PlusDeDeuxAns(x) ^ AlbumSorti(x) => AlbumCertifie(x)
                if (derives.contains("PlusDeDeuxAns(" + x + ")")
                        && derives.contains("AlbumSorti(" + x + ")")
                        && derives.add("AlbumCertifie(" + x + ")")) changed = true;
            }
        }
        return derives;
    }

    private static boolean query(Set<String> derives, String formule) {
        return derives.contains(formule);
    }

    private static Optional<String> existentiel(Set<String> derives, String predicat) {
        return derives.stream().filter(f -> f.startsWith(predicat + "(")).findFirst();
    }

    public static void run() {
        System.out.println("=== Logique des Predicats : Industrie Musicale ===\n");
        System.out.println("Individus : soolking, feu, tif, flenn\n");
        System.out.println("Predicats :");
        System.out.println("  EstRappeurAlgerien/1, EstRappeur/1, EstArtiste/1, EstMusicien/1");
        System.out.println("  SigneLabel/1, AlbumSorti/1, RecitRoyalties/1, PartageRoyalties/1");
        System.out.println("  IndependantLongtemps/1, ExperienceSolo/1");
        System.out.println("  PlusDeDeuxAns/1, AlbumCertifie/1");
        System.out.println();
        System.out.println("Axiomes universels :");
        System.out.println("  ax1 : Ax (EstRappeurAlgerien(x) => EstRappeur(x))");
        System.out.println("  ax2 : Ax (EstRappeur(x) => EstArtiste(x))");
        System.out.println("  ax3 : Ax (EstArtiste(x) => EstMusicien(x))");
        System.out.println("  ax4 : Ax (EstArtiste(x) ^ AlbumSorti(x) => RecitRoyalties(x))");
        System.out.println("  ax5 : Ax (SigneLabel(x) => PartageRoyalties(x))");
        System.out.println("  ax6 : Ax (IndependantLongtemps(x) => ExperienceSolo(x))");
        System.out.println("  ax7 : Ax (PlusDeDeuxAns(x) ^ AlbumSorti(x) => AlbumCertifie(x))");
        System.out.println();
        System.out.println("Faits (base de connaissances) :");
        System.out.println("  EstRappeurAlgerien : soolking, feu, tif, flenn");
        System.out.println("  SigneLabel         : soolking, feu");
        System.out.println("  AlbumSorti         : soolking, feu, tif, flenn");
        System.out.println("  IndependantLongtemps : feu");
        System.out.println("  PlusDeDeuxAns      : tif, flenn");
        System.out.println();

        Set<String> faits = new HashSet<>(Arrays.asList(
            "EstRappeurAlgerien(soolking)", "EstRappeurAlgerien(feu)",
            "EstRappeurAlgerien(tif)",      "EstRappeurAlgerien(flenn)",
            "SigneLabel(soolking)",          "SigneLabel(feu)",
            "AlbumSorti(soolking)",          "AlbumSorti(feu)",
            "AlbumSorti(tif)",               "AlbumSorti(flenn)",
            "IndependantLongtemps(feu)",
            "PlusDeDeuxAns(tif)",            "PlusDeDeuxAns(flenn)"
        ));

        Set<String> derives = deriver(faits);

        // --- Q1 : chaine EstRappeurAlgerien => RecitRoyalties pour tous ---
        System.out.println("--- Q1 : Ax (EstRappeurAlgerien(x) => ... => RecitRoyalties(x)) ? ---");
        System.out.println("[FOL] Chaine de derivation pour chaque artiste :");
        for (String x : INDIVIDUS) {
            System.out.println("[FOL]   EstRappeurAlgerien(" + x + ")"
                + " -ax1-> EstRappeur(" + x + ")"
                + " -ax2-> EstArtiste(" + x + ")"
                + " -ax4+AlbumSorti-> RecitRoyalties(" + x + ")");
        }
        boolean q1 = Arrays.stream(INDIVIDUS).allMatch(
            x -> query(derives, "RecitRoyalties(" + x + ")"));
        System.out.println("[FOL] Tous les artistes ont RecitRoyalties : " + q1 + "\n");

        // --- Q2 : PartageRoyalties(soolking) ---
        System.out.println("--- Q2 : KB |= PartageRoyalties(soolking) ? ---");
        System.out.println("[FOL] SigneLabel(soolking)(fait) -ax5-> PartageRoyalties(soolking)");
        System.out.println("[FOL] " + query(derives, "PartageRoyalties(soolking)") + "\n");

        // --- Q3 : ExperienceSolo(feu) ---
        System.out.println("--- Q3 : KB |= ExperienceSolo(feu) ? ---");
        System.out.println("[FOL] IndependantLongtemps(feu)(fait) -ax6-> ExperienceSolo(feu)");
        System.out.println("[FOL] " + query(derives, "ExperienceSolo(feu)") + "\n");

        // --- Q4 : AlbumCertifie(tif) ---
        System.out.println("--- Q4 : KB |= AlbumCertifie(tif) ? ---");
        System.out.println("[FOL] PlusDeDeuxAns(tif)(fait) ^ AlbumSorti(tif)(fait) -ax7-> AlbumCertifie(tif)");
        System.out.println("[FOL] " + query(derives, "AlbumCertifie(tif)") + "\n");

        // --- Q5 : Ex ExperienceSolo(x) ---
        System.out.println("--- Q5 : Ex ExperienceSolo(x) ? ---");
        Optional<String> temoin = existentiel(derives, "ExperienceSolo");
        System.out.println("[FOL] " + temoin.isPresent() + " — temoin : " + temoin.orElse("aucun") + "\n");

        // --- Q6 : chaine ax1+ax2+ax3 => EstMusicien ---
        System.out.println("--- Q6 : KB |= EstMusicien(soolking) ? (chaine ax1+ax2+ax3) ---");
        System.out.println("[FOL] EstRappeurAlgerien(soolking) -ax1-> EstRappeur(soolking)");
        System.out.println("[FOL]   -ax2-> EstArtiste(soolking) -ax3-> EstMusicien(soolking)");
        System.out.println("[FOL] " + query(derives, "EstMusicien(soolking)"));
        System.out.println("[FOL] Chaine de longueur 3 — derivation multi-etapes en logique des predicats.");
    }
}
