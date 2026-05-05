package rcr.classical;

import org.tweetyproject.logics.pl.syntax.*;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.reasoner.SatReasoner;

/**
 * Logique Propositionnelle — domaine : système judiciaire.
 * Individus : Ali, Omar, Karim
 *
 * Clauses CNF :
 *   c1 : !ali  | innocent_ali             (accuse => presume innocent)
 *   c2 : !ali  | !temoignage_ali | acquitte_ali
 *   c3 : !omar | innocent_omar
 *   c4 : !recidiviste_omar | peine_lourde_omar
 *   c5 : !mineur_karim | tribunal_enfants_karim
 *   c6 : !crime_grave_karim | !tribunal_enfants_karim  (conflit avec c5)
 */
public class PropositionalLogicJustice {

    private static final SatReasoner REASONER;
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

    private static boolean isSat(PlBeliefSet kb) {
        return !REASONER.query(kb, FRESH);
    }

    private static boolean entails(PlBeliefSet kb, PlFormula phi) {
        return REASONER.query(kb, phi);
    }

    public static void run() {
        Proposition ali      = new Proposition("ali");
        Proposition omar     = new Proposition("omar");
        Proposition karim    = new Proposition("karim");
        Proposition tem_a    = new Proposition("temoignage_ali");
        Proposition rec_o    = new Proposition("recidiviste_omar");
        Proposition min_k    = new Proposition("mineur_karim");
        Proposition cg_k     = new Proposition("crime_grave_karim");
        Proposition inn_a    = new Proposition("innocent_ali");
        Proposition inn_o    = new Proposition("innocent_omar");
        Proposition acq_a    = new Proposition("acquitte_ali");
        Proposition peine_o  = new Proposition("peine_lourde_omar");
        Proposition trib_k   = new Proposition("tribunal_enfants_karim");

        System.out.println("=== Logique Propositionnelle : Systeme Judiciaire ===\n");
        System.out.println("Individus : Ali, Omar, Karim\n");
        System.out.println("Propositions atomiques :");
        System.out.println("  ali, omar, karim               — accuses (faits)");
        System.out.println("  temoignage_ali                 — ali a un temoignage (fait)");
        System.out.println("  recidiviste_omar               — omar est recidiviste (fait)");
        System.out.println("  mineur_karim, crime_grave_karim — karim (faits)");
        System.out.println("  innocent_ali, innocent_omar    — presomption d'innocence");
        System.out.println("  acquitte_ali                   — ali acquitte");
        System.out.println("  peine_lourde_omar              — peine alourdie");
        System.out.println("  tribunal_enfants_karim         — tribunal pour mineurs");
        System.out.println();
        System.out.println("Clauses CNF :");
        System.out.println("  Faits : ali, omar, karim, temoignage_ali, recidiviste_omar,");
        System.out.println("          mineur_karim, crime_grave_karim");
        System.out.println("  c1 : !ali  | innocent_ali");
        System.out.println("  c2 : !ali  | !temoignage_ali | acquitte_ali");
        System.out.println("  c3 : !omar | innocent_omar");
        System.out.println("  c4 : !recidiviste_omar | peine_lourde_omar");
        System.out.println("  c5 : !mineur_karim | tribunal_enfants_karim");
        System.out.println("  c6 : !crime_grave_karim | !tribunal_enfants_karim  (pour Q6)");
        System.out.println();

        // KB sans c6 (coherente pour Q1-Q5)
        PlBeliefSet kb = new PlBeliefSet();
        kb.add(ali); kb.add(omar); kb.add(karim);
        kb.add(tem_a); kb.add(rec_o); kb.add(min_k); kb.add(cg_k);
        kb.add(or(neg(ali), inn_a));              // c1
        kb.add(or(neg(ali), neg(tem_a), acq_a)); // c2
        kb.add(or(neg(omar), inn_o));             // c3
        kb.add(or(neg(rec_o), peine_o));          // c4
        kb.add(or(neg(min_k), trib_k));           // c5

        // --- Q1 ---
        System.out.println("--- Q1 : KB (sans c6) satisfiable ? ---");
        System.out.println("[PL] " + isSat(kb));
        System.out.println("[PL] => KB sans c6 est coherente.\n");

        // --- Q2 ---
        System.out.println("--- Q2 : KB |= acquitte_ali ? ---");
        System.out.println("[PL] " + entails(kb, acq_a));
        System.out.println("[PL] Chaine : ali(fait) ^ temoignage_ali(fait) + c2 => acquitte_ali.\n");

        // --- Q3 ---
        System.out.println("--- Q3 : KB |= innocent_ali ? ---");
        System.out.println("[PL] " + entails(kb, inn_a));
        System.out.println("[PL] Chaine : ali(fait) + c1 => innocent_ali.\n");

        // --- Q4 ---
        System.out.println("--- Q4 : KB |= peine_lourde_omar ? ---");
        System.out.println("[PL] " + entails(kb, peine_o));
        System.out.println("[PL] Chaine : recidiviste_omar(fait) + c4 => peine_lourde_omar.\n");

        // --- Q5 ---
        System.out.println("--- Q5 : KB |= innocent_omar ? ---");
        System.out.println("[PL] " + entails(kb, inn_o));
        System.out.println("[PL] Chaine : omar(fait) + c3 => innocent_omar.");
        System.out.println("[PL] Note : innocent_omar ET peine_lourde_omar sont tous deux derives");
        System.out.println("[PL]   (pas de contradiction en PL — ce sont deux propositions distinctes).\n");

        // --- Q6 : contradiction karim avec c6 ---
        System.out.println("--- Q6 : KB + c6 satisfiable ? (conflit mineur/crime grave) ---");
        PlBeliefSet kbQ6 = new PlBeliefSet();
        kbQ6.addAll(kb);
        kbQ6.add(or(neg(cg_k), neg(trib_k)));  // c6
        System.out.println("[PL] satisfiable : " + isSat(kbQ6));
        System.out.println("[PL] c5 + mineur_karim(fait)      => tribunal_enfants_karim");
        System.out.println("[PL] c6 + crime_grave_karim(fait) => !tribunal_enfants_karim");
        System.out.println("[PL] => Contradiction : c5 et c6 s'opposent pour karim.\n");

        // --- Q7 : ajout !innocent_omar ---
        System.out.println("--- Q7 : KB + {!innocent_omar} satisfiable ? ---");
        PlBeliefSet kbQ7 = new PlBeliefSet();
        kbQ7.addAll(kb);
        kbQ7.add(neg(inn_o));
        System.out.println("[PL] satisfiable : " + isSat(kbQ7));
        System.out.println("[PL] c3 + omar(fait) => innocent_omar,");
        System.out.println("[PL] mais !innocent_omar ajoute => innocent_omar ^ !innocent_omar (UNSAT).");
        System.out.println("[PL] => La logique classique est monotone : impossible de 'retirer' une");
        System.out.println("[PL]   conclusion. La logique des defauts gere ce cas avec des extensions.");
    }
}
