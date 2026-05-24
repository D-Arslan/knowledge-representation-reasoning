package rcr.modal.temporal;

import java.util.*;

/**
 * ============================================================
 *   LOGIQUE MODALE TEMPORELLE — Domaine : carriere d'artiste
 *   (Cours RCR1 Master 1 SII USTHB, chapitre 3, section temporelle)
 * ============================================================
 *
 *  Modele : <T, <, V>
 *    T = ensemble d'instants chronologiques
 *    < = ordre temporel (passe -> futur)
 *    V = valuation : pour chaque atome, l'ensemble des instants ou il est vrai
 *
 *  Operateurs temporels :
 *    G p   : p sera TOUJOURS vrai dans le futur   (universel futur)
 *    F p   : p sera VRAI A UN MOMENT dans le futur (existentiel futur)
 *    H p   : p a TOUJOURS ete vrai dans le passe  (universel passe)
 *    P p   : p a ete VRAI A UN MOMENT dans le passe (existentiel passe)
 *
 *  Semantique formelle :
 *    M, t |= G p   <=>   forall s : t < s => p est vrai en s
 *    M, t |= F p   <=>   exists s : t < s ET p est vrai en s
 *    M, t |= H p   <=>   forall s : s < t => p est vrai en s
 *    M, t |= P p   <=>   exists s : s < t ET p est vrai en s
 *
 *  Domaine : la carriere de Soolking en 5 instants chronologiques
 *  (representant des annees-cles de son parcours d'artiste algerien).
 */
public class TemporalLogicMusic {

    // Instants ordonnes chronologiquement : t1 < t2 < t3 < t4 < t5
    static final List<String> INSTANTS = Arrays.asList("t1", "t2", "t3", "t4", "t5");

    static final Map<String, String> LABELS = new LinkedHashMap<>();
    static {
        LABELS.put("t1", "2017 - Artiste independant");
        LABELS.put("t2", "2018 - Signe chez Universal");
        LABELS.put("t3", "2019 - Sort l'album Guerilla");
        LABELS.put("t4", "2021 - Album certifie Platine");
        LABELS.put("t5", "2023 - Sort un nouvel album");
    }

    // Valuation V : atome -> instants ou l'atome est vrai
    static final Map<String, Set<String>> V = new LinkedHashMap<>();
    static {
        V.put("artiste_independant", new LinkedHashSet<>(Arrays.asList("t1")));
        V.put("sous_contrat",        new LinkedHashSet<>(Arrays.asList("t2","t3","t4","t5")));
        V.put("album_sorti",         new LinkedHashSet<>(Arrays.asList("t3","t4","t5")));
        V.put("platine",             new LinkedHashSet<>(Arrays.asList("t4","t5")));
        V.put("en_tournee",          new LinkedHashSet<>(Arrays.asList("t3","t4")));
    }

    // ============================================================
    //  PRIMITIVES TEMPORELLES
    // ============================================================

    private static int idx(String t) { return INSTANTS.indexOf(t); }

    /** Instants strictement APRES t (= relation <). */
    private static List<String> futurs(String t) {
        int i = idx(t);
        return (i < 0) ? Collections.emptyList()
                       : INSTANTS.subList(i + 1, INSTANTS.size());
    }

    /** Instants strictement AVANT t. */
    private static List<String> passes(String t) {
        int i = idx(t);
        return (i <= 0) ? Collections.emptyList() : INSTANTS.subList(0, i);
    }

    /** L'atome est-il vrai en t selon V ? */
    private static boolean vrai(String atom, String t) {
        return V.getOrDefault(atom, Collections.emptySet()).contains(t);
    }

    // ============================================================
    //  OPERATEURS TEMPORELS (semantique du cours)
    // ============================================================

    /** G p en t : p sera TOUJOURS vrai dans le futur strict de t. */
    static boolean G(String atom, String t) {
        for (String s : futurs(t)) if (!vrai(atom, s)) return false;
        return true;
    }

    /** F p en t : p sera vrai A UN MOMENT dans le futur strict de t. */
    static boolean F(String atom, String t) {
        for (String s : futurs(t)) if (vrai(atom, s)) return true;
        return false;
    }

    /** H p en t : p a TOUJOURS ete vrai dans le passe strict de t. */
    static boolean H(String atom, String t) {
        for (String s : passes(t)) if (!vrai(atom, s)) return false;
        return true;
    }

    /** P p en t : p a ete vrai A UN MOMENT dans le passe strict de t. */
    static boolean P(String atom, String t) {
        for (String s : passes(t)) if (vrai(atom, s)) return true;
        return false;
    }

    // ============================================================
    //  AFFICHAGE
    // ============================================================

    public static void run() {
        System.out.println("=== Logique Modale Temporelle : Carriere de Soolking ===\n");

        // Affichage de la frise chronologique
        System.out.println("Instants chronologiques (t1 < t2 < t3 < t4 < t5) :");
        for (String t : INSTANTS) {
            System.out.println("  " + t + "  " + LABELS.get(t));
        }
        System.out.println();

        // Affichage de la valuation
        System.out.println("Valuation V (atome -> instants ou il est vrai) :");
        for (Map.Entry<String, Set<String>> e : V.entrySet()) {
            System.out.println("  " + pad(e.getKey(), 24) + " = " + e.getValue());
        }
        System.out.println();

        // Tableau atome x instant
        System.out.println("Tableau verite atome x instant :");
        System.out.print("  " + pad("", 24));
        for (String t : INSTANTS) System.out.print(" | " + center(t, 3));
        System.out.println();
        System.out.println("  " + "-".repeat(24 + INSTANTS.size() * 6));
        for (String atom : V.keySet()) {
            System.out.print("  " + pad(atom, 24));
            for (String t : INSTANTS) {
                System.out.print(" | " + center(vrai(atom, t) ? "V" : ".", 3));
            }
            System.out.println();
        }
        System.out.println();

        // ===== Requetes temporelles =====
        System.out.println("--- Requetes temporelles ---\n");

        eval("Q1", "G platine", "t4",
            "platine sera TOUJOURS vrai apres t4",
            G("platine", "t4"),
            "platine vrai en {t4, t5} ; futur de t4 = {t5} ; vrai partout dans le futur");

        eval("Q2", "F sous_contrat", "t1",
            "sous_contrat sera vrai A UN MOMENT apres t1",
            F("sous_contrat", "t1"),
            "sous_contrat vrai en t2 (signe Universal) qui est dans le futur de t1");

        eval("Q3", "H artiste_independant", "t2",
            "artiste_independant a TOUJOURS ete vrai dans le passe de t2",
            H("artiste_independant", "t2"),
            "passe de t2 = {t1} ; artiste_independant vrai en t1");

        eval("Q4", "P album_sorti", "t5",
            "album_sorti a ete vrai A UN MOMENT dans le passe de t5",
            P("album_sorti", "t5"),
            "album_sorti vrai en t3 (sortie de Guerilla) qui est dans le passe de t5");

        eval("Q5", "G en_tournee", "t3",
            "en_tournee sera TOUJOURS vrai apres t3",
            G("en_tournee", "t3"),
            "en_tournee vrai en t4 mais FAUX en t5 (fin de tournee) -> faux");

        eval("Q6", "H sous_contrat", "t5",
            "Soolking a TOUJOURS ete sous contrat dans le passe de t5",
            H("sous_contrat", "t5"),
            "passe de t5 = {t1,t2,t3,t4} ; sous_contrat FAUX en t1 (indep.) -> faux");

        // ===== Demo des correspondances avec K, T, S4 =====
        System.out.println("\n--- Correspondance avec la logique modale classique ---");
        System.out.println("  G = []_futur   (Box dans le futur)");
        System.out.println("  F = <>_futur   (Diamond dans le futur)");
        System.out.println("  H = []_passe   (Box dans le passe)");
        System.out.println("  P = <>_passe   (Diamond dans le passe)");
        System.out.println();
        System.out.println("Dualites attendues (verifiees par construction) :");
        System.out.println("  F p == NON G NON p");
        System.out.println("  P p == NON H NON p");
        System.out.println();
        System.out.println("Propriete G->F (axiome (A10) modal D / serialite) :");
        System.out.println("  G platine en t3 = " + G("platine","t3")
                + " -> F platine en t3 = " + F("platine","t3"));
        System.out.println("  L'ordre temporel sur {t1..t5} est SERIEL pour tous sauf t5");
        System.out.println("  (t5 n'a pas de futur => G p toujours vrai, F p toujours faux)");
    }

    // ============================================================
    //  HELPERS
    // ============================================================

    private static void eval(String num, String formule, String t,
                              String lecture, boolean res, String justification) {
        System.out.println("  [" + num + "] " + formule + "  en " + t);
        System.out.println("        Lecture : " + lecture);
        System.out.println("        Resultat : " + (res ? "VRAI" : "FAUX"));
        System.out.println("        Pourquoi : " + justification);
        System.out.println();
    }

    private static String pad(String s, int w) {
        if (s.length() >= w) return s;
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < w) sb.append(' ');
        return sb.toString();
    }

    private static String center(String s, int w) {
        if (s.length() >= w) return s;
        int total = w - s.length();
        int left = total / 2;
        int right = total - left;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < left; i++) sb.append(' ');
        sb.append(s);
        for (int i = 0; i < right; i++) sb.append(' ');
        return sb.toString();
    }

    public static void main(String[] args) { run(); }
}
