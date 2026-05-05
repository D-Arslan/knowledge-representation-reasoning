package rcr.classical;

import java.util.*;

/**
 * Logique des Predicats (1er ordre) — domaine : système judiciaire.
 * Individus : ali, omar, karim
 *
 * Axiomes :
 *   ax1 : Ax (EstAccuse(x) => DroitAuProces(x))
 *   ax2 : Ax (EstAccuse(x) => DroitDefense(x))
 *   ax3 : Ax (ATemoignage(x) => CandidatAcquittement(x))
 *   ax4 : Ax (EstRecidiviste(x) => PeineLourde(x))
 *   ax5 : Ax (EstMineur(x) => TribunalEnfants(x))
 *   ax6 : Ax (CrimeGrave(x) => SupJuridiction(x))
 *   ax7 : Ax (PeineLourde(x) => SuiviRenforce(x))
 *   ax8 : Ax (TribunalEnfants(x) ^ CrimeGrave(x) => NecessiteAvocatPublic(x))
 *
 * Inference : chainage avant (forward chaining) sur le domaine fini.
 */
public class PredicateLogicJustice {

    private static final String[] INDIVIDUS = {"ali", "omar", "karim"};

    private static Set<String> deriver(Set<String> faits) {
        Set<String> derives = new HashSet<>(faits);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String x : INDIVIDUS) {
                // ax1 : EstAccuse(x) => DroitAuProces(x)
                if (derives.contains("EstAccuse(" + x + ")")
                        && derives.add("DroitAuProces(" + x + ")")) changed = true;
                // ax2 : EstAccuse(x) => DroitDefense(x)
                if (derives.contains("EstAccuse(" + x + ")")
                        && derives.add("DroitDefense(" + x + ")")) changed = true;
                // ax3 : ATemoignage(x) => CandidatAcquittement(x)
                if (derives.contains("ATemoignage(" + x + ")")
                        && derives.add("CandidatAcquittement(" + x + ")")) changed = true;
                // ax4 : EstRecidiviste(x) => PeineLourde(x)
                if (derives.contains("EstRecidiviste(" + x + ")")
                        && derives.add("PeineLourde(" + x + ")")) changed = true;
                // ax5 : EstMineur(x) => TribunalEnfants(x)
                if (derives.contains("EstMineur(" + x + ")")
                        && derives.add("TribunalEnfants(" + x + ")")) changed = true;
                // ax6 : CrimeGrave(x) => SupJuridiction(x)
                if (derives.contains("CrimeGrave(" + x + ")")
                        && derives.add("SupJuridiction(" + x + ")")) changed = true;
                // ax7 : PeineLourde(x) => SuiviRenforce(x)
                if (derives.contains("PeineLourde(" + x + ")")
                        && derives.add("SuiviRenforce(" + x + ")")) changed = true;
                // ax8 : TribunalEnfants(x) ^ CrimeGrave(x) => NecessiteAvocatPublic(x)
                if (derives.contains("TribunalEnfants(" + x + ")")
                        && derives.contains("CrimeGrave(" + x + ")")
                        && derives.add("NecessiteAvocatPublic(" + x + ")")) changed = true;
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
        System.out.println("=== Logique des Predicats : Systeme Judiciaire ===\n");
        System.out.println("Individus : ali, omar, karim\n");
        System.out.println("Predicats :");
        System.out.println("  EstAccuse/1, ATemoignage/1, EstRecidiviste/1");
        System.out.println("  EstMineur/1, CrimeGrave/1");
        System.out.println("  DroitAuProces/1, DroitDefense/1");
        System.out.println("  CandidatAcquittement/1, PeineLourde/1, SuiviRenforce/1");
        System.out.println("  TribunalEnfants/1, SupJuridiction/1, NecessiteAvocatPublic/1");
        System.out.println();
        System.out.println("Axiomes universels :");
        System.out.println("  ax1 : Ax (EstAccuse(x) => DroitAuProces(x))");
        System.out.println("  ax2 : Ax (EstAccuse(x) => DroitDefense(x))");
        System.out.println("  ax3 : Ax (ATemoignage(x) => CandidatAcquittement(x))");
        System.out.println("  ax4 : Ax (EstRecidiviste(x) => PeineLourde(x))");
        System.out.println("  ax5 : Ax (EstMineur(x) => TribunalEnfants(x))");
        System.out.println("  ax6 : Ax (CrimeGrave(x) => SupJuridiction(x))");
        System.out.println("  ax7 : Ax (PeineLourde(x) => SuiviRenforce(x))");
        System.out.println("  ax8 : Ax (TribunalEnfants(x) ^ CrimeGrave(x) => NecessiteAvocatPublic(x))");
        System.out.println();
        System.out.println("Faits (base de connaissances) :");
        System.out.println("  EstAccuse      : ali, omar, karim");
        System.out.println("  ATemoignage    : ali");
        System.out.println("  EstRecidiviste : omar");
        System.out.println("  EstMineur      : karim");
        System.out.println("  CrimeGrave     : karim");
        System.out.println();

        Set<String> faits = new HashSet<>(Arrays.asList(
            "EstAccuse(ali)",  "EstAccuse(omar)",  "EstAccuse(karim)",
            "ATemoignage(ali)",
            "EstRecidiviste(omar)",
            "EstMineur(karim)", "CrimeGrave(karim)"
        ));

        Set<String> derives = deriver(faits);

        // --- Q1 : DroitAuProces(ali) ---
        System.out.println("--- Q1 : KB |= DroitAuProces(ali) ? ---");
        System.out.println("[FOL] EstAccuse(ali)(fait) -ax1-> DroitAuProces(ali)");
        System.out.println("[FOL] " + query(derives, "DroitAuProces(ali)") + "\n");

        // --- Q2 : CandidatAcquittement(ali) ---
        System.out.println("--- Q2 : KB |= CandidatAcquittement(ali) ? ---");
        System.out.println("[FOL] ATemoignage(ali)(fait) -ax3-> CandidatAcquittement(ali)");
        System.out.println("[FOL] " + query(derives, "CandidatAcquittement(ali)") + "\n");

        // --- Q3 : PeineLourde(omar) ---
        System.out.println("--- Q3 : KB |= PeineLourde(omar) ? ---");
        System.out.println("[FOL] EstRecidiviste(omar)(fait) -ax4-> PeineLourde(omar)");
        System.out.println("[FOL] " + query(derives, "PeineLourde(omar)") + "\n");

        // --- Q4 : SuiviRenforce(omar) (chaine multi-etapes) ---
        System.out.println("--- Q4 : KB |= SuiviRenforce(omar) ? (chaine 2 etapes) ---");
        System.out.println("[FOL] EstRecidiviste(omar) -ax4-> PeineLourde(omar) -ax7-> SuiviRenforce(omar)");
        System.out.println("[FOL] " + query(derives, "SuiviRenforce(omar)") + "\n");

        // --- Q5 : TribunalEnfants(karim) ---
        System.out.println("--- Q5 : KB |= TribunalEnfants(karim) ? ---");
        System.out.println("[FOL] EstMineur(karim)(fait) -ax5-> TribunalEnfants(karim)");
        System.out.println("[FOL] " + query(derives, "TribunalEnfants(karim)") + "\n");

        // --- Q6 : NecessiteAvocatPublic(karim) (chaine conjonctive) ---
        System.out.println("--- Q6 : KB |= NecessiteAvocatPublic(karim) ? (chaine conjonctive) ---");
        System.out.println("[FOL] EstMineur(karim) -ax5-> TribunalEnfants(karim)");
        System.out.println("[FOL]   ^ CrimeGrave(karim)(fait) -ax8-> NecessiteAvocatPublic(karim)");
        System.out.println("[FOL] " + query(derives, "NecessiteAvocatPublic(karim)") + "\n");

        // --- Q7 : Ex CandidatAcquittement(x) ---
        System.out.println("--- Q7 : Ex CandidatAcquittement(x) ? ---");
        Optional<String> temoin = existentiel(derives, "CandidatAcquittement");
        System.out.println("[FOL] " + temoin.isPresent() + " — temoin : " + temoin.orElse("aucun") + "\n");

        // --- Q8 : Tous les accuses ont DroitDefense ---
        System.out.println("--- Q8 : Ax (EstAccuse(x) => DroitDefense(x)) verifiable pour tous ? ---");
        boolean q8 = Arrays.stream(INDIVIDUS).allMatch(
            x -> query(derives, "DroitDefense(" + x + ")"));
        System.out.println("[FOL] Tous les accuses ont DroitDefense : " + q8);
        System.out.println("[FOL] => ax2 s'applique universellement : ali, omar, karim.");
        System.out.println("[FOL]");
        System.out.println("[FOL] Remarque sur la tension karim :");
        System.out.println("[FOL]   TribunalEnfants(karim) = " + query(derives, "TribunalEnfants(karim)"));
        System.out.println("[FOL]   SupJuridiction(karim)  = " + query(derives, "SupJuridiction(karim)"));
        System.out.println("[FOL]   En logique classique, les deux conclusions coexistent sans conflit.");
        System.out.println("[FOL]   La logique des defauts modelise ce cas par des extensions distinctes.");
    }
}
