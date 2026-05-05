package rcr.modal;

import org.tweetyproject.commons.Interpretation;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.fol.syntax.*;
import org.tweetyproject.logics.ml.semantics.AccessibilityRelation;
import org.tweetyproject.logics.ml.semantics.MlHerbrandInterpretation;
import org.tweetyproject.logics.ml.syntax.MlFormula;
import org.tweetyproject.logics.ml.syntax.Necessity;
import org.tweetyproject.logics.ml.syntax.Possibility;

import java.util.*;

/**
 * Logique Modale — domaine : système judiciaire.
 *
 * Modèle de Kripke <W, R, V> :
 *   W = {w1=juge, w2=avocat_défense, w3=procureur, w4=témoin, w5=accusé}
 *   R = {(w1,w1),(w1,w2),(w1,w3),(w2,w4),(w3,w5),(w5,w5)}
 *   V : coupable∈{w3,w5}, innocent∈{w2,w4}, preuve∈{w1,w3},
 *       credible∈{w1,w2,w4}, attenuantes∈{w2,w5}
 */
public class ModalLogicJustice {

    // ---------------------------------------------------------------
    // Même évaluateur récursif Kripke que ModalLogicMusic.
    // ---------------------------------------------------------------
    private static boolean evaluate(
            MlHerbrandInterpretation world,
            FolFormula formula,
            AccessibilityRelation R) {

        if (formula instanceof FolAtom) {
            return world.satisfies(formula);

        } else if (formula instanceof Negation) {
            FolFormula inner = ((Negation) formula).getFormula();
            return !evaluate(world, inner, R);

        } else if (formula instanceof Implication) {
            Pair<RelationalFormula, RelationalFormula> parts =
                    ((Implication) formula).getFormulas();
            boolean ant  = evaluate(world, (FolFormula) parts.getFirst(), R);
            boolean cons = evaluate(world, (FolFormula) parts.getSecond(), R);
            return !ant || cons;

        } else if (formula instanceof Necessity) {
            FolFormula inner = (FolFormula) ((MlFormula) formula).getFormula();
            Set<Interpretation<FolBeliefSet, FolFormula>> succs = R.getSuccessors(world);
            if (succs.isEmpty()) return true;
            for (Interpretation<FolBeliefSet, FolFormula> succ : succs) {
                if (!evaluate((MlHerbrandInterpretation) succ, inner, R)) return false;
            }
            return true;

        } else if (formula instanceof Possibility) {
            FolFormula inner = (FolFormula) ((MlFormula) formula).getFormula();
            Set<Interpretation<FolBeliefSet, FolFormula>> succs = R.getSuccessors(world);
            for (Interpretation<FolBeliefSet, FolFormula> succ : succs) {
                if (evaluate((MlHerbrandInterpretation) succ, inner, R)) return true;
            }
            return false;
        }
        throw new IllegalArgumentException("Type de formule non supporté : "
                + formula.getClass().getSimpleName());
    }

    private static void printResult(String formulaStr, String worldName,
                                    boolean result, String explanation) {
        System.out.println("[MODAL] Formule: " + formulaStr
                + " | Monde: " + worldName
                + " | Résultat: " + (result ? "VRAI" : "FAUX"));
        System.out.println("[MODAL] Explication: " + explanation);
        System.out.println();
    }

    private static void verifierProprietes(
            List<MlHerbrandInterpretation> worlds,
            AccessibilityRelation R) {

        System.out.println("--- Propriétés de la relation R ---");

        // Réflexivité
        boolean reflexive = true;
        for (MlHerbrandInterpretation w : worlds) {
            if (!R.getSuccessors(w).contains(w)) {
                reflexive = false;
                break;
            }
        }
        System.out.println("Réflexive  : " + (reflexive ? "OUI" : "NON")
                + "  — w2, w3, w4 n'ont pas de boucle réflexive");

        // Transitivité
        boolean transitive = true;
        outerT:
        for (MlHerbrandInterpretation w : worlds) {
            Set<Interpretation<FolBeliefSet, FolFormula>> wSucc = R.getSuccessors(w);
            for (Interpretation<FolBeliefSet, FolFormula> v : wSucc) {
                Set<Interpretation<FolBeliefSet, FolFormula>> vSucc =
                        R.getSuccessors((MlHerbrandInterpretation) v);
                for (Interpretation<FolBeliefSet, FolFormula> u : vSucc) {
                    if (!wSucc.contains(u)) {
                        transitive = false;
                        break outerT;
                    }
                }
            }
        }
        System.out.println("Transitive : " + (transitive ? "OUI" : "NON")
                + "  — (w1,w2)+(w2,w4) sans (w1,w4)");

        // Symétrie
        boolean symmetric = true;
        outerS:
        for (MlHerbrandInterpretation w : worlds) {
            for (Interpretation<FolBeliefSet, FolFormula> v : R.getSuccessors(w)) {
                if (!R.getSuccessors((MlHerbrandInterpretation) v).contains(w)) {
                    symmetric = false;
                    break outerS;
                }
            }
        }
        System.out.println("Symétrique : " + (symmetric ? "OUI" : "NON")
                + "  — (w1,w2) sans (w2,w1)");

        // Sérialité
        boolean serial = true;
        MlHerbrandInterpretation isolé = null;
        for (MlHerbrandInterpretation w : worlds) {
            if (R.getSuccessors(w).isEmpty()) {
                serial = false;
                isolé = w;
                break;
            }
        }
        System.out.println("Sérielle   : " + (serial ? "OUI" : "NON")
                + (isolé != null ? "  — w4 (témoin) n'a aucun successeur" : ""));
    }

    // ---------------------------------------------------------------
    // Point d'entrée
    // ---------------------------------------------------------------
    public static void run() {
        System.out.println("=== Logique Modale : Système Judiciaire ===");
        System.out.println("Modèle de Kripke <W, R, V>\n");

        // ------ Propositions ------
        FolAtom coupable    = new FolAtom(new Predicate("accuse_est_coupable"));
        FolAtom innocent    = new FolAtom(new Predicate("accuse_est_innocent"));
        FolAtom preuve      = new FolAtom(new Predicate("preuve_valide"));
        FolAtom credible    = new FolAtom(new Predicate("temoin_credible"));
        FolAtom attenuantes = new FolAtom(new Predicate("circonstances_attenuantes"));

        // ------ Mondes ------
        //  w1 = juge           : preuve, credible
        MlHerbrandInterpretation w1 = new MlHerbrandInterpretation(
                Arrays.asList(preuve, credible));
        //  w2 = avocat défense : innocent, credible, attenuantes
        MlHerbrandInterpretation w2 = new MlHerbrandInterpretation(
                Arrays.asList(innocent, credible, attenuantes));
        //  w3 = procureur      : coupable, preuve
        MlHerbrandInterpretation w3 = new MlHerbrandInterpretation(
                Arrays.asList(coupable, preuve));
        //  w4 = témoin         : innocent, credible
        MlHerbrandInterpretation w4 = new MlHerbrandInterpretation(
                Arrays.asList(innocent, credible));
        //  w5 = accusé         : coupable, attenuantes
        MlHerbrandInterpretation w5 = new MlHerbrandInterpretation(
                Arrays.asList(coupable, attenuantes));

        // ------ Relation d'accessibilité R ------
        // R = {(w1,w1),(w1,w2),(w1,w3),(w2,w4),(w3,w5),(w5,w5)}
        Set<Pair<Interpretation<FolBeliefSet, FolFormula>,
                 Interpretation<FolBeliefSet, FolFormula>>> pairs = new HashSet<>();
        pairs.add(new Pair<>(w1, w1));
        pairs.add(new Pair<>(w1, w2));
        pairs.add(new Pair<>(w1, w3));
        pairs.add(new Pair<>(w2, w4));
        pairs.add(new Pair<>(w3, w5));
        pairs.add(new Pair<>(w5, w5));
        AccessibilityRelation R = new AccessibilityRelation(pairs);

        System.out.println("Mondes   : w1=juge, w2=avocat, w3=procureur, w4=témoin, w5=accusé");
        System.out.println("Relation : (w1,w1),(w1,w2),(w1,w3),(w2,w4),(w3,w5),(w5,w5)");
        System.out.println("Valuation: coupable∈{w3,w5} | innocent∈{w2,w4} | preuve∈{w1,w3}"
                + " | credible∈{w1,w2,w4} | attenuantes∈{w2,w5}\n");

        // ------ Évaluation des formules ------

        // 1. □preuve_valide en w1
        // "Le juge considère-t-il la preuve nécessairement valide ?"
        // Successeurs(w1) = {w1,w2,w3}. preuve faux en w2 → □ FAUX
        printResult(
                "□preuve_valide", "w1",
                evaluate(w1, new Necessity(preuve), R),
                "Successeurs de w1 = {w1,w2,w3}. preuve_valide est faux en w2 (avocat) "
                + "→ la nécessité n'est pas satisfaite.");

        // 2. ◊accuse_est_innocent en w1
        // "Le juge considère-t-il possible l'innocence de l'accusé ?"
        // Successeurs(w1) = {w1,w2,w3}. innocent vrai en w2 → ◊ VRAI
        printResult(
                "◊accuse_est_innocent", "w1",
                evaluate(w1, new Possibility(innocent), R),
                "Successeurs de w1 = {w1,w2,w3}. accuse_est_innocent est vrai en w2 (avocat) "
                + "→ la possibilité est satisfaite.");

        // 3. □accuse_est_coupable en w3
        // "Le procureur est-il certain de la culpabilité ?"
        // Successeurs(w3) = {w5}. coupable vrai en w5 → □ VRAI
        printResult(
                "□accuse_est_coupable", "w3",
                evaluate(w3, new Necessity(coupable), R),
                "Successeurs de w3 = {w5}. accuse_est_coupable est vrai en w5 (accusé) "
                + "→ la nécessité est satisfaite.");

        // 4. ◊circonstances_attenuantes en w1
        // "Le juge considère-t-il possible des circonstances atténuantes ?"
        // Successeurs(w1) = {w1,w2,w3}. attenuantes vrai en w2 → ◊ VRAI
        printResult(
                "◊circonstances_attenuantes", "w1",
                evaluate(w1, new Possibility(attenuantes), R),
                "Successeurs de w1 = {w1,w2,w3}. circonstances_attenuantes est vrai en w2 (avocat) "
                + "→ la possibilité est satisfaite.");

        // 5. □temoin_credible en w2
        // "L'avocat croit-il nécessairement que le témoin est crédible ?"
        // Successeurs(w2) = {w4}. credible vrai en w4 → □ VRAI
        printResult(
                "□temoin_credible", "w2",
                evaluate(w2, new Necessity(credible), R),
                "Successeurs de w2 = {w4}. temoin_credible est vrai en w4 (témoin) "
                + "→ la nécessité est satisfaite.");

        // ------ Propriétés de R ------
        System.out.println();
        verifierProprietes(Arrays.asList(w1, w2, w3, w4, w5), R);
    }
}
