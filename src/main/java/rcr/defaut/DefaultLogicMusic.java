package rcr.defaut;

import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.rdl.parser.RdlParser;
import org.tweetyproject.logics.rdl.reasoner.SimpleDefaultReasoner;
import org.tweetyproject.logics.rdl.semantics.Extension;
import org.tweetyproject.logics.rdl.syntax.DefaultTheory;

import java.util.*;

/**
 * Logique des Défauts — domaine : industrie musicale.
 * Artistes : Soolking, Feu, Tif, Flenn
 *
 * d1 : EstArtiste(x)                    :: LibreDroits(x)      / LibreDroits(x)
 * d2 : SigneLabel(x)                    :: !LibreDroits(x)     / !LibreDroits(x)
 * d3 : EstArtiste(x) && AlbumSorti(x)  :: Royalties(x)        / Royalties(x)
 * d4 : SigneLabel(x)                    :: PartageRoyalties(x) / PartageRoyalties(x)
 * d5 : IndependantLongtemps(x)          :: ExperienceSolo(x)   / ExperienceSolo(x)
 * d6 : NouveauContrat(x)               :: !LibreDroits(x)     / !LibreDroits(x)
 * d7 : PlusDeDeuxAns(x)&&AlbumSorti(x) :: AlbumCertifie(x)    / AlbumCertifie(x)
 */
public class DefaultLogicMusic {

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

    private static void printExtensions(String artiste,
                                        Collection<Extension> exts,
                                        String... faits) {
        List<List<String>> distincts = distinctsPour(exts, artiste);
        System.out.println("[DEFAUT] Artiste: " + artiste
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
    // Théories individuelles
    // ---------------------------------------------------------------

    // Soolking : EstArtiste + SigneLabel + AlbumSorti  →  d1 vs d2 (conflit LibreDroits), d3 d4 sans conflit
    private static final String T_SOOLKING =
        "Art = {soolking}\n"
        + "type(EstArtiste(Art))\n"
        + "type(LibreDroits(Art))\n"
        + "type(SigneLabel(Art))\n"
        + "type(AlbumSorti(Art))\n"
        + "type(Royalties(Art))\n"
        + "type(PartageRoyalties(Art))\n"
        + "EstArtiste(soolking)\n"
        + "SigneLabel(soolking)\n"
        + "AlbumSorti(soolking)\n"
        + "EstArtiste(X)::LibreDroits(X)/LibreDroits(X)\n"               // d1
        + "SigneLabel(X)::!LibreDroits(X)/!LibreDroits(X)\n"             // d2
        + "EstArtiste(X) && AlbumSorti(X)::Royalties(X)/Royalties(X)\n"  // d3
        + "SigneLabel(X)::PartageRoyalties(X)/PartageRoyalties(X)\n";    // d4

    // Feu : EstArtiste + IndependantLongtemps + NouveauContrat  →  d1 vs d6 (conflit), d5 sans conflit
    private static final String T_FEU =
        "Art = {feu}\n"
        + "type(EstArtiste(Art))\n"
        + "type(LibreDroits(Art))\n"
        + "type(IndependantLongtemps(Art))\n"
        + "type(NouveauContrat(Art))\n"
        + "type(ExperienceSolo(Art))\n"
        + "EstArtiste(feu)\n"
        + "IndependantLongtemps(feu)\n"
        + "NouveauContrat(feu)\n"
        + "EstArtiste(X)::LibreDroits(X)/LibreDroits(X)\n"                    // d1
        + "IndependantLongtemps(X)::ExperienceSolo(X)/ExperienceSolo(X)\n"    // d5
        + "NouveauContrat(X)::!LibreDroits(X)/!LibreDroits(X)\n";            // d6

    // Tif : EstArtiste + AlbumSorti + PlusDeDeuxAns  →  d1, d3, d7 sans conflit
    private static final String T_TIF =
        "Art = {tif}\n"
        + "type(EstArtiste(Art))\n"
        + "type(LibreDroits(Art))\n"
        + "type(AlbumSorti(Art))\n"
        + "type(PlusDeDeuxAns(Art))\n"
        + "type(Royalties(Art))\n"
        + "type(AlbumCertifie(Art))\n"
        + "EstArtiste(tif)\n"
        + "AlbumSorti(tif)\n"
        + "PlusDeDeuxAns(tif)\n"
        + "EstArtiste(X)::LibreDroits(X)/LibreDroits(X)\n"                        // d1
        + "EstArtiste(X) && AlbumSorti(X)::Royalties(X)/Royalties(X)\n"           // d3
        + "PlusDeDeuxAns(X) && AlbumSorti(X)::AlbumCertifie(X)/AlbumCertifie(X)\n"; // d7

    // Flenn (avant non-monotonie) : EstArtiste + AlbumSorti + PlusDeDeuxAns, sans SigneLabel
    private static final String T_FLENN =
        "Art = {flenn}\n"
        + "type(EstArtiste(Art))\n"
        + "type(LibreDroits(Art))\n"
        + "type(SigneLabel(Art))\n"
        + "type(AlbumSorti(Art))\n"
        + "type(PlusDeDeuxAns(Art))\n"
        + "type(Royalties(Art))\n"
        + "type(AlbumCertifie(Art))\n"
        + "EstArtiste(flenn)\n"
        + "AlbumSorti(flenn)\n"
        + "PlusDeDeuxAns(flenn)\n"
        + "EstArtiste(X)::LibreDroits(X)/LibreDroits(X)\n"                        // d1
        + "SigneLabel(X)::!LibreDroits(X)/!LibreDroits(X)\n"                      // d2
        + "EstArtiste(X) && AlbumSorti(X)::Royalties(X)/Royalties(X)\n"           // d3
        + "PlusDeDeuxAns(X) && AlbumSorti(X)::AlbumCertifie(X)/AlbumCertifie(X)\n"; // d7

    // Flenn + SigneLabel (non-monotonie : d1 vs d2 désormais en conflit)
    private static final String T_FLENN_NM =
        "Art = {flenn}\n"
        + "type(EstArtiste(Art))\n"
        + "type(LibreDroits(Art))\n"
        + "type(SigneLabel(Art))\n"
        + "type(AlbumSorti(Art))\n"
        + "type(PlusDeDeuxAns(Art))\n"
        + "type(Royalties(Art))\n"
        + "type(AlbumCertifie(Art))\n"
        + "EstArtiste(flenn)\n"
        + "AlbumSorti(flenn)\n"
        + "PlusDeDeuxAns(flenn)\n"
        + "SigneLabel(flenn)\n"                                                    // ← ajouté
        + "EstArtiste(X)::LibreDroits(X)/LibreDroits(X)\n"                        // d1
        + "SigneLabel(X)::!LibreDroits(X)/!LibreDroits(X)\n"                      // d2
        + "EstArtiste(X) && AlbumSorti(X)::Royalties(X)/Royalties(X)\n"           // d3
        + "PlusDeDeuxAns(X) && AlbumSorti(X)::AlbumCertifie(X)/AlbumCertifie(X)\n"; // d7

    // ---------------------------------------------------------------
    // Point d'entrée
    // ---------------------------------------------------------------
    public static void run() {
        System.out.println("=== Logique des Defauts : Industrie Musicale ===\n");
        System.out.println("Artistes : Soolking, Feu, Tif, Flenn\n");
        System.out.println("Defaults D :");
        System.out.println("  d1: EstArtiste(x) :: LibreDroits(x) / LibreDroits(x)");
        System.out.println("  d2: SigneLabel(x) :: !LibreDroits(x) / !LibreDroits(x)");
        System.out.println("  d3: EstArtiste(x)&&AlbumSorti(x) :: Royalties(x) / Royalties(x)");
        System.out.println("  d4: SigneLabel(x) :: PartageRoyalties(x) / PartageRoyalties(x)");
        System.out.println("  d5: IndependantLongtemps(x) :: ExperienceSolo(x) / ExperienceSolo(x)");
        System.out.println("  d6: NouveauContrat(x) :: !LibreDroits(x) / !LibreDroits(x)");
        System.out.println("  d7: PlusDeDeuxAns(x)&&AlbumSorti(x) :: AlbumCertifie(x) / AlbumCertifie(x)");
        System.out.println();

        try {
            // === Q1 : Soolking ===
            System.out.println("--- Q1 : soolking (SigneLabel → conflit d1 vs d2 sur LibreDroits) ---");
            Collection<Extension> extsSoolking = computeExtensions(T_SOOLKING);
            printExtensions("soolking", extsSoolking, "EstArtiste", "SigneLabel", "AlbumSorti");
            List<List<String>> distSoolking = distinctsPour(extsSoolking, "soolking");
            boolean royalties = dansToutes(distSoolking, "Royalties(soolking)");
            boolean partage   = dansToutes(distSoolking, "PartageRoyalties(soolking)");
            System.out.println("[DEFAUT] Conclusion: d1 vs d2 → conflit sur LibreDroits."
                    + " Royalties dans toutes les ext : " + royalties
                    + ". PartageRoyalties dans toutes les ext : " + partage + ".");
            System.out.println();

            // === Q2 : Feu ===
            System.out.println("--- Q2 : feu (NouveauContrat → conflit d1 vs d6, d5 sans conflit) ---");
            Collection<Extension> extsFeu = computeExtensions(T_FEU);
            printExtensions("feu", extsFeu, "EstArtiste", "IndependantLongtemps", "NouveauContrat");
            List<List<String>> distFeu = distinctsPour(extsFeu, "feu");
            boolean expSolo = dansToutes(distFeu, "ExperienceSolo(feu)");
            System.out.println("[DEFAUT] Conclusion: d1 (LibreDroits) vs d6 (!LibreDroits) → conflit."
                    + " ExperienceSolo present dans toutes les ext : " + expSolo + ".");
            System.out.println();

            // === Q3 : Tif ===
            System.out.println("--- Q3 : tif (pas de SigneLabel/NouveauContrat, aucun conflit) ---");
            Collection<Extension> extsTif = computeExtensions(T_TIF);
            printExtensions("tif", extsTif, "EstArtiste", "AlbumSorti", "PlusDeDeuxAns");
            System.out.println("[DEFAUT] Conclusion: d1, d3, d7 s'appliquent sans blocage."
                    + " LibreDroits, Royalties, AlbumCertifie dans toutes les extensions.");
            System.out.println();

            // === Q4 : Non-monotonie (Flenn) ===
            System.out.println("--- Q4 : Non-monotonie pour flenn (ajout SigneLabel(flenn)) ---");
            Collection<Extension> extsFlenn = computeExtensions(T_FLENN);
            List<List<String>> distFlenn = distinctsPour(extsFlenn, "flenn");
            boolean libreAvant = dansToutes(distFlenn, "LibreDroits(flenn)");
            System.out.println("[DEFAUT] Non-monotonie: AVANT ajout de SigneLabel(flenn)");
            System.out.println("[DEFAUT]   Extension : " + distFlenn.get(0));
            System.out.println("[DEFAUT]   LibreDroits(flenn) dans toutes les ext : " + libreAvant);

            Collection<Extension> extsFlennNM = computeExtensions(T_FLENN_NM);
            List<List<String>> distFlennNM = distinctsPour(extsFlennNM, "flenn");
            boolean libreApres = dansToutes(distFlennNM, "LibreDroits(flenn)");
            System.out.println("[DEFAUT] Non-monotonie: APRES ajout de SigneLabel(flenn) → "
                    + distFlennNM.size() + " extension(s) pour flenn :");
            for (int i = 0; i < distFlennNM.size(); i++)
                System.out.println("[DEFAUT]   Ext-" + (i + 1) + " : " + distFlennNM.get(i));
            System.out.println("[DEFAUT]   LibreDroits(flenn) dans toutes les ext : " + libreApres);
            System.out.println("[DEFAUT] → ajout de SigneLabel invalide LibreDroits : non-monotonie demontree.");

        } catch (Exception e) {
            System.err.println("[DEFAUT] Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
