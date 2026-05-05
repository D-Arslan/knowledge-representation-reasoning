package rcr.defaut;

import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.rdl.parser.RdlParser;
import org.tweetyproject.logics.rdl.reasoner.SimpleDefaultReasoner;
import org.tweetyproject.logics.rdl.semantics.Extension;
import org.tweetyproject.logics.rdl.syntax.DefaultTheory;

import java.util.*;

/**
 * Logique des Défauts — domaine : système judiciaire.
 *
 * Δ = (W, D) avec théories séparées par individu pour rester tractable.
 * Format Tweety RDL : prerequis(X) :: justification(X) / conclusion(X)
 *
 * d1 : Accuse(x)      :: Innocent(x)          / Innocent(x)
 * d2 : Recidiviste(x) :: PeineLourde(x)        / PeineLourde(x)
 * d3 : Mineur(x)      :: TribunalEnfants(x)    / TribunalEnfants(x)
 * d4 : CrimeGrave(x)  :: !TribunalEnfants(x)  / !TribunalEnfants(x)
 * d5 : Temoignage(x)  :: Acquittement(x)       / Acquittement(x)
 * d6 : Recidiviste(x) :: !Innocent(x)          / !Innocent(x)
 */
public class DefaultLogicJustice {

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private static Collection<Extension> computeExtensions(String theory) throws Exception {
        RdlParser parser = new RdlParser();
        SimpleDefaultReasoner reasoner = new SimpleDefaultReasoner();
        DefaultTheory dt = parser.parseBeliefBase(theory);
        return reasoner.getModels(dt.ground());
    }

    private static List<String> atomsPour(Extension ext, String individu) {
        List<String> result = new ArrayList<>();
        for (FolFormula f : ext) {
            String s = f.toString();
            if (s.contains("(" + individu + ")")) result.add(s);
        }
        Collections.sort(result);
        return result;
    }

    private static List<List<String>> distinctsPour(
            Collection<Extension> exts, String individu) {
        LinkedHashSet<List<String>> seen = new LinkedHashSet<>();
        for (Extension ext : exts) seen.add(atomsPour(ext, individu));
        return new ArrayList<>(seen);
    }

    private static boolean dansToutes(List<List<String>> distincts, String formule) {
        return distincts.stream().allMatch(ext ->
                ext.stream().anyMatch(s -> s.equals(formule)));
    }

    private static void printExtensions(String individu,
                                        Collection<Extension> exts,
                                        String... faits) {
        List<List<String>> distincts = distinctsPour(exts, individu);
        System.out.println("[DEFAUT] Individu: " + individu
                + " | Faits: " + Arrays.toString(faits));
        if (distincts.size() == 1) {
            System.out.println("[DEFAUT] Extension unique : " + distincts.get(0));
        } else {
            System.out.println("[DEFAUT] CONFLIT → " + distincts.size()
                    + " extensions distinctes :");
            for (int i = 0; i < distincts.size(); i++)
                System.out.println("[DEFAUT]   Ext-" + (i + 1) + " : " + distincts.get(i));
        }
    }

    // ---------------------------------------------------------------
    // Théories individuelles (petites = tractables)
    // ---------------------------------------------------------------

    // Ali : Accuse + Temoignage  →  d1, d5 (aucun conflit)
    private static final String T_ALI =
        "Person = {ali}\n"
        + "type(Accuse(Person))\n"
        + "type(Innocent(Person))\n"
        + "type(Temoignage(Person))\n"
        + "type(Acquittement(Person))\n"
        + "Accuse(ali)\n"
        + "Temoignage(ali)\n"
        + "Accuse(X)::Innocent(X)/Innocent(X)\n"          // d1
        + "Temoignage(X)::Acquittement(X)/Acquittement(X)\n"; // d5

    // Omar : Accuse + Recidiviste  →  d1 vs d6 (conflit Innocent), d2 sans conflit
    private static final String T_OMAR =
        "Person = {omar}\n"
        + "type(Accuse(Person))\n"
        + "type(Innocent(Person))\n"
        + "type(Recidiviste(Person))\n"
        + "type(PeineLourde(Person))\n"
        + "Accuse(omar)\n"
        + "Recidiviste(omar)\n"
        + "Accuse(X)::Innocent(X)/Innocent(X)\n"           // d1
        + "Recidiviste(X)::PeineLourde(X)/PeineLourde(X)\n" // d2
        + "Recidiviste(X)::!Innocent(X)/!Innocent(X)\n";   // d6

    // Karim : Accuse + Mineur + CrimeGrave  →  d3 vs d4 (conflit TribunalEnfants), d1 sans conflit
    private static final String T_KARIM =
        "Person = {karim}\n"
        + "type(Accuse(Person))\n"
        + "type(Innocent(Person))\n"
        + "type(Mineur(Person))\n"
        + "type(TribunalEnfants(Person))\n"
        + "type(CrimeGrave(Person))\n"
        + "Accuse(karim)\n"
        + "Mineur(karim)\n"
        + "CrimeGrave(karim)\n"
        + "Accuse(X)::Innocent(X)/Innocent(X)\n"                        // d1
        + "Mineur(X)::TribunalEnfants(X)/TribunalEnfants(X)\n"           // d3
        + "CrimeGrave(X)::!TribunalEnfants(X)/!TribunalEnfants(X)\n";   // d4

    // Ali + Recidiviste (non-monotonie)
    private static final String T_ALI_NM =
        "Person = {ali}\n"
        + "type(Accuse(Person))\n"
        + "type(Innocent(Person))\n"
        + "type(Recidiviste(Person))\n"
        + "type(Temoignage(Person))\n"
        + "type(Acquittement(Person))\n"
        + "Accuse(ali)\n"
        + "Temoignage(ali)\n"
        + "Recidiviste(ali)\n"                                      // ← ajouté
        + "Accuse(X)::Innocent(X)/Innocent(X)\n"                    // d1
        + "Temoignage(X)::Acquittement(X)/Acquittement(X)\n"         // d5
        + "Recidiviste(X)::!Innocent(X)/!Innocent(X)\n";            // d6

    // ---------------------------------------------------------------
    // Point d'entrée
    // ---------------------------------------------------------------
    public static void run() {
        System.out.println("=== Logique des Defauts : Systeme Judiciaire ===\n");
        System.out.println("Defaults D :");
        System.out.println("  d1: Accuse(x) :: Innocent(x) / Innocent(x)");
        System.out.println("  d2: Recidiviste(x) :: PeineLourde(x) / PeineLourde(x)");
        System.out.println("  d3: Mineur(x) :: TribunalEnfants(x) / TribunalEnfants(x)");
        System.out.println("  d4: CrimeGrave(x) :: !TribunalEnfants(x) / !TribunalEnfants(x)");
        System.out.println("  d5: Temoignage(x) :: Acquittement(x) / Acquittement(x)");
        System.out.println("  d6: Recidiviste(x) :: !Innocent(x) / !Innocent(x)");
        System.out.println();

        try {
            // === Q1 : Ali ===
            System.out.println("--- Q1 : ali (d1 + d5, aucun conflit) ---");
            Collection<Extension> extsAli = computeExtensions(T_ALI);
            printExtensions("ali", extsAli, "Accuse(ali)", "Temoignage(ali)");
            List<List<String>> distAli = distinctsPour(extsAli, "ali");
            System.out.println("[DEFAUT] Conclusion: d1 applique → Innocent(ali)."
                    + " d5 applique → Acquittement(ali). Aucun conflit, une seule extension.");
            System.out.println();

            // === Q2 : Omar ===
            System.out.println("--- Q2 : omar (d1 vs d6 conflit, d2 sans conflit) ---");
            Collection<Extension> extsOmar = computeExtensions(T_OMAR);
            printExtensions("omar", extsOmar, "Accuse(omar)", "Recidiviste(omar)");
            List<List<String>> distOmar = distinctsPour(extsOmar, "omar");
            boolean peineDansToutes = dansToutes(distOmar, "PeineLourde(omar)");
            System.out.println("[DEFAUT] Conclusion: d1 (Innocent) et d6 (!Innocent) se bloquent"
                    + " mutuellement → " + distOmar.size() + " extensions."
                    + " PeineLourde(omar) present dans toutes : " + peineDansToutes + ".");
            System.out.println();

            // === Q3 : Karim ===
            System.out.println("--- Q3 : karim (d3 vs d4 conflit, d1 sans conflit) ---");
            Collection<Extension> extsKarim = computeExtensions(T_KARIM);
            printExtensions("karim", extsKarim, "Accuse(karim)", "Mineur(karim)", "CrimeGrave(karim)");
            List<List<String>> distKarim = distinctsPour(extsKarim, "karim");
            boolean innocentKarimPartout = dansToutes(distKarim, "Innocent(karim)");
            System.out.println("[DEFAUT] Conclusion: d3 (TribunalEnfants) et d4 (!TribunalEnfants)"
                    + " se bloquent → " + distKarim.size() + " extensions."
                    + " Innocent(karim) present dans toutes : " + innocentKarimPartout + ".");
            System.out.println();

            // === Q4 : Non-monotonie ===
            System.out.println("--- Q4 : Non-monotonie pour ali (ajout Recidiviste(ali)) ---");

            boolean innocentAvant = dansToutes(distAli, "Innocent(ali)");
            System.out.println("[DEFAUT] Non-monotonie: AVANT ajout de Recidiviste(ali)");
            System.out.println("[DEFAUT]   Extension : " + distAli.get(0));
            System.out.println("[DEFAUT]   Innocent(ali) conclu dans toutes les extensions : "
                    + innocentAvant);

            Collection<Extension> extsAliNM = computeExtensions(T_ALI_NM);
            List<List<String>> distAliNM = distinctsPour(extsAliNM, "ali");
            boolean innocentApres = dansToutes(distAliNM, "Innocent(ali)");

            System.out.println("[DEFAUT] Non-monotonie: APRES ajout de Recidiviste(ali) → "
                    + distAliNM.size() + " extension(s) pour ali :");
            for (int i = 0; i < distAliNM.size(); i++)
                System.out.println("[DEFAUT]   Ext-" + (i + 1) + " : " + distAliNM.get(i));
            System.out.println("[DEFAUT]   Innocent(ali) conclu dans toutes les extensions : "
                    + innocentApres);
            System.out.println("[DEFAUT] → ajout de Recidiviste(ali) invalide Innocent(ali)"
                    + " : non-monotonie demontree.");

        } catch (Exception e) {
            System.err.println("[DEFAUT] Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
