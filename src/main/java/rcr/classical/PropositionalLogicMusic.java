package rcr.classical;

import org.tweetyproject.logics.pl.syntax.*;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.reasoner.SatReasoner;

/**
 * Logique Propositionnelle — domaine : industrie musicale.
 * Artistes : Soolking, Feu, Tif, Flenn
 *
 * Clauses CNF :
 *   c1 : !soolking | !signe_soolking | partage_soolking
 *   c2 : !feu      | !signe_feu      | partage_feu
 *   c3 : !tif      | royalties_tif
 *   c4 : !flenn    | royalties_flenn
 *   c5 : !indep_feu | expsolo_feu
 */
public class PropositionalLogicMusic {

    private static final SatReasoner REASONER;
    // Atome frais jamais present dans la KB : query => true ssi KB est UNSAT
    private static final Proposition FRESH = new Proposition("__sat_witness__");

    static {
        SatSolver.setDefaultSolver(new Sat4jSolver());
        REASONER = new SatReasoner();
    }

    private static Negation neg(PlFormula f) { return new Negation(f); }

    private static Disjunction or(PlFormula... fs) {
        Disjunction d = new Disjunction();
        for (PlFormula f : fs) d.add(f);
        return d;
    }

    /** KB est satisfiable ssi elle n'entraine pas un atome frais (absent de toute clause). */
    private static boolean isSat(PlBeliefSet kb) {
        return !REASONER.query(kb, FRESH);
    }

    /** KB |= phi  via le raisonneur SAT (equivalent a la refutation). */
    private static boolean entails(PlBeliefSet kb, PlFormula phi) {
        return REASONER.query(kb, phi);
    }

    public static void run() {
        Proposition soolking   = new Proposition("soolking");
        Proposition feu        = new Proposition("feu");
        Proposition tif        = new Proposition("tif");
        Proposition flenn      = new Proposition("flenn");
        Proposition sl_s       = new Proposition("signe_soolking");
        Proposition sl_f       = new Proposition("signe_feu");
        Proposition indep_f    = new Proposition("indep_feu");
        Proposition partage_s  = new Proposition("partage_soolking");
        Proposition partage_f  = new Proposition("partage_feu");
        Proposition royal_t    = new Proposition("royalties_tif");
        Proposition royal_fl   = new Proposition("royalties_flenn");
        Proposition exp_f      = new Proposition("expsolo_feu");
        Proposition sl_fl      = new Proposition("signe_flenn");
        Proposition partage_fl = new Proposition("partage_flenn");

        System.out.println("=== Logique Propositionnelle : Industrie Musicale ===\n");
        System.out.println("Artistes : Soolking, Feu, Tif, Flenn\n");
        System.out.println("Propositions atomiques :");
        System.out.println("  soolking, feu, tif, flenn        — est artiste (faits)");
        System.out.println("  signe_soolking, signe_feu        — signe a un label (faits)");
        System.out.println("  indep_feu                        — independant longtemps (fait)");
        System.out.println("  partage_soolking, partage_feu    — partage royalties avec label");
        System.out.println("  royalties_tif, royalties_flenn   — royalties directes");
        System.out.println("  expsolo_feu                      — experience solo");
        System.out.println();
        System.out.println("Clauses CNF :");
        System.out.println("  Faits : soolking, feu, tif, flenn, signe_soolking, signe_feu, indep_feu");
        System.out.println("  c1 : !soolking | !signe_soolking | partage_soolking");
        System.out.println("  c2 : !feu | !signe_feu | partage_feu");
        System.out.println("  c3 : !tif | royalties_tif");
        System.out.println("  c4 : !flenn | royalties_flenn");
        System.out.println("  c5 : !indep_feu | expsolo_feu");
        System.out.println();

        PlBeliefSet kb = new PlBeliefSet();
        kb.add(soolking); kb.add(feu); kb.add(tif); kb.add(flenn);
        kb.add(sl_s); kb.add(sl_f); kb.add(indep_f);
        kb.add(or(neg(soolking), neg(sl_s), partage_s));   // c1
        kb.add(or(neg(feu), neg(sl_f), partage_f));        // c2
        kb.add(or(neg(tif), royal_t));                     // c3
        kb.add(or(neg(flenn), royal_fl));                  // c4
        kb.add(or(neg(indep_f), exp_f));                   // c5

        // --- Q1 : satisfiabilite ---
        System.out.println("--- Q1 : KB satisfiable ? ---");
        System.out.println("[PL] " + isSat(kb));
        System.out.println("[PL] => Les faits et clauses sont coherents.\n");

        // --- Q2 : KB |= partage_soolking ---
        System.out.println("--- Q2 : KB |= partage_soolking ? ---");
        System.out.println("[PL] " + entails(kb, partage_s));
        System.out.println("[PL] Preuve par refutation : KB + {!partage_soolking} est UNSAT.");
        System.out.println("[PL] Chaine : soolking(fait) ^ signe_soolking(fait) + c1 => partage_soolking.\n");

        // --- Q3 : KB |= expsolo_feu ---
        System.out.println("--- Q3 : KB |= expsolo_feu ? ---");
        System.out.println("[PL] " + entails(kb, exp_f));
        System.out.println("[PL] Preuve par refutation : KB + {!expsolo_feu} est UNSAT.");
        System.out.println("[PL] Chaine : indep_feu(fait) + c5 => expsolo_feu.\n");

        // --- Q4 : KB + {signe_flenn} |= partage_flenn ---
        System.out.println("--- Q4 : KB + {signe_flenn} |= partage_flenn ? ---");
        PlBeliefSet kbQ4 = new PlBeliefSet();
        kbQ4.addAll(kb);
        kbQ4.add(sl_fl);
        kbQ4.add(or(neg(flenn), neg(sl_fl), partage_fl)); // c6
        System.out.println("[PL] " + entails(kbQ4, partage_fl));
        System.out.println("[PL] Monotonie : l'ajout de signe_flenn + regle c6 entraine partage_flenn.");
        System.out.println("[PL] Chaine : flenn(fait) ^ signe_flenn(ajout) + c6 => partage_flenn.\n");

        // --- Q5 : KB + {!royalties_flenn} satisfiable ? ---
        System.out.println("--- Q5 : KB + {!royalties_flenn} satisfiable ? (contradiction) ---");
        PlBeliefSet kbQ5 = new PlBeliefSet();
        kbQ5.addAll(kb);
        kbQ5.add(neg(royal_fl));
        System.out.println("[PL] satisfiable : " + isSat(kbQ5));
        System.out.println("[PL] c4 + flenn(fait) => royalties_flenn,");
        System.out.println("[PL] mais l'ajout de !royalties_flenn cree royalties_flenn ^ !royalties_flenn.");
        System.out.println("[PL] => Contradiction detectee par le solveur SAT4J (UNSAT).");
    }
}
