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
 * Logique Modale — domaine : industrie musicale.
 *
 * Modèle de Kripke <W, R, V> :
 *   W = {w1=label, w2=fan, w3=artiste, w4=critique}
 *   R = {(w1,w2),(w1,w3),(w3,w3),(w2,w4),(w4,w4)}
 *   V : album∈{w1,w3}, concert∈{w2,w4}, contrat∈{w1},
 *       chanson∈{w1-w4}, independant∈{w3}
 */
public class ModalLogicMusic {

    // ---------------------------------------------------------------
    // Évaluation récursive d'une formule modale/propositionnelle
    // au monde `world` dans le modèle de Kripke défini par R.
    //
    // Cas de base (FolAtom)   : vrai ssi l'atome est dans le monde.
    // Négation, Implication   : connecteurs classiques.
    // Necessity  (□)          : vrai ssi φ est vrai dans TOUS les successeurs.
    // Possibility (◊)         : vrai ssi φ est vrai dans AU MOINS UN successeur.
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
            boolean antecedent = evaluate(world, (FolFormula) parts.getFirst(), R);
            boolean consequent = evaluate(world, (FolFormula) parts.getSecond(), R);
            return !antecedent || consequent;

        } else if (formula instanceof Necessity) {
            FolFormula inner = (FolFormula) ((MlFormula) formula).getFormula();
            Set<Interpretation<FolBeliefSet, FolFormula>> succs = R.getSuccessors(world);
            if (succs.isEmpty()) return true; // vrai vacuoirement
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

    // ---------------------------------------------------------------
    // Vérification des propriétés de la relation d'accessibilité
    // ---------------------------------------------------------------
    private static void verifierProprietes(
            List<MlHerbrandInterpretation> worlds,
            AccessibilityRelation R) {

        System.out.println("--- Propriétés de la relation R ---");

        // Réflexivité : ∀w, (w,w) ∈ R
        boolean reflexive = true;
        for (MlHerbrandInterpretation w : worlds) {
            if (!R.getSuccessors(w).contains(w)) {
                reflexive = false;
                break;
            }
        }
        System.out.println("Réflexive  : " + (reflexive ? "OUI" : "NON")
                + "  — w1 n'a pas de boucle sur lui-même");

        // Transitivité : ∀w,v,u, (w,v)∧(v,u) → (w,u)
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

        // Symétrie : ∀w,v, (w,v) → (v,w)
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

        // Sérialité : ∀w, ∃v, (w,v) ∈ R
        boolean serial = true;
        for (MlHerbrandInterpretation w : worlds) {
            if (R.getSuccessors(w).isEmpty()) {
                serial = false;
                break;
            }
        }
        System.out.println("Sérielle   : " + (serial ? "OUI" : "NON")
                + "  — chaque monde a au moins un successeur");
    }

    // ---------------------------------------------------------------
    // Point d'entrée
    // ---------------------------------------------------------------
    public static void run() {
        System.out.println("=== Logique Modale : Industrie Musicale ===");
        System.out.println("Modèle de Kripke <W, R, V>\n");

        // ------ Propositions (prédicats 0-aire = variables propositionnelles) ------
        FolAtom album      = new FolAtom(new Predicate("album_est_platine"));
        FolAtom concert    = new FolAtom(new Predicate("concert_annule"));
        FolAtom contrat    = new FolAtom(new Predicate("artiste_sous_contrat"));
        FolAtom chanson    = new FolAtom(new Predicate("chanson_en_top_charts"));
        FolAtom independant = new FolAtom(new Predicate("artiste_independant"));

        // ------ Mondes (MlHerbrandInterpretation = ensemble d'atomes vrais) ------
        //  w1 = label   : album, contrat, chanson
        MlHerbrandInterpretation w1 = new MlHerbrandInterpretation(
                Arrays.asList(album, contrat, chanson));
        //  w2 = fan     : concert, chanson
        MlHerbrandInterpretation w2 = new MlHerbrandInterpretation(
                Arrays.asList(concert, chanson));
        //  w3 = artiste : album, chanson, independant
        MlHerbrandInterpretation w3 = new MlHerbrandInterpretation(
                Arrays.asList(album, chanson, independant));
        //  w4 = critique: concert, chanson
        MlHerbrandInterpretation w4 = new MlHerbrandInterpretation(
                Arrays.asList(concert, chanson));

        // ------ Relation d'accessibilité R ------
        // R = {(w1,w2),(w1,w3),(w3,w3),(w2,w4),(w4,w4)}
        Set<Pair<Interpretation<FolBeliefSet, FolFormula>,
                 Interpretation<FolBeliefSet, FolFormula>>> pairs = new HashSet<>();
        pairs.add(new Pair<>(w1, w2));
        pairs.add(new Pair<>(w1, w3));
        pairs.add(new Pair<>(w3, w3));
        pairs.add(new Pair<>(w2, w4));
        pairs.add(new Pair<>(w4, w4));
        AccessibilityRelation R = new AccessibilityRelation(pairs);

        System.out.println("Mondes   : w1=label, w2=fan, w3=artiste, w4=critique");
        System.out.println("Relation : (w1,w2),(w1,w3),(w3,w3),(w2,w4),(w4,w4)");
        System.out.println("Valuation: album∈{w1,w3} | concert∈{w2,w4} | contrat∈{w1}"
                + " | chanson∈{w1-w4} | indép∈{w3}\n");

        // ------ Évaluation des formules ------

        // 1. □album_est_platine en w1
        // "Le label sait-il nécessairement que l'album est platine ?"
        // Successeurs(w1) = {w2, w3}. album faux en w2 → □ FAUX
        printResult(
                "□album_est_platine", "w1",
                evaluate(w1, new Necessity(album), R),
                "Successeurs de w1 = {w2,w3}. album_est_platine est faux en w2 "
                + "→ la nécessité n'est pas satisfaite.");

        // 2. ◊concert_annule en w1
        // "Est-il possible selon le label que le concert soit annulé ?"
        // Successeurs(w1) = {w2, w3}. concert vrai en w2 → ◊ VRAI
        printResult(
                "◊concert_annule", "w1",
                evaluate(w1, new Possibility(concert), R),
                "Successeurs de w1 = {w2,w3}. concert_annule est vrai en w2 "
                + "→ la possibilité est satisfaite.");

        // 3. □chanson_en_top_charts en w2
        // "Le fan croit-il nécessairement que la chanson est en top charts ?"
        // Successeurs(w2) = {w4}. chanson vrai en w4 → □ VRAI
        printResult(
                "□chanson_en_top_charts", "w2",
                evaluate(w2, new Necessity(chanson), R),
                "Successeurs de w2 = {w4}. chanson_en_top_charts est vrai en w4 "
                + "→ la nécessité est satisfaite.");

        // 4. ◊artiste_independant en w1
        // "Le label considère-t-il possible que l'artiste soit indépendant ?"
        // Successeurs(w1) = {w2, w3}. independant vrai en w3 → ◊ VRAI
        printResult(
                "◊artiste_independant", "w1",
                evaluate(w1, new Possibility(independant), R),
                "Successeurs de w1 = {w2,w3}. artiste_independant est vrai en w3 "
                + "→ la possibilité est satisfaite.");

        // 5. □(album_est_platine → artiste_sous_contrat) en w1
        // "Est-il nécessaire que si l'album est platine alors l'artiste est sous contrat ?"
        // Successeurs(w1) = {w2, w3}.
        //   En w3 : album VRAI, contrat FAUX → implication FAUSSE → □ FAUX
        Implication albumImplContrat = new Implication(album, contrat);
        printResult(
                "□(album_est_platine → artiste_sous_contrat)", "w1",
                evaluate(w1, new Necessity(albumImplContrat), R),
                "En w3 (successeur de w1) : album_est_platine=VRAI, artiste_sous_contrat=FAUX "
                + "→ l'implication est fausse → □ non satisfaite.");

        // ------ Propriétés de R ------
        System.out.println();
        verifierProprietes(Arrays.asList(w1, w2, w3, w4), R);
    }
}
