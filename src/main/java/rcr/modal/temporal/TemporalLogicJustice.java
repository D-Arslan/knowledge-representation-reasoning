package rcr.modal.temporal;

import java.util.*;

/**
 * ============================================================
 *   LOGIQUE MODALE TEMPORELLE — Domaine : deroulement d'un proces
 *   (Cours RCR1 Master 1 SII USTHB, chapitre 3, section temporelle)
 * ============================================================
 *
 *  Modele : <T, <, V>
 *
 *  Domaine : 6 instants chronologiques representant les etapes
 *  d'une procedure judiciaire complete (depuis l'infraction jusqu'a
 *  la liberation), illustrant pour quelles propositions la verite
 *  CHANGE au fil du temps.
 *
 *  Cas etudie : Omar, recidiviste, est arrete puis condamne.
 */
public class TemporalLogicJustice {

    // Instants chronologiques : t1 < t2 < t3 < t4 < t5 < t6
    static final List<String> INSTANTS = Arrays.asList("t1","t2","t3","t4","t5","t6");

    static final Map<String, String> LABELS = new LinkedHashMap<>();
    static {
        LABELS.put("t1", "Infraction commise");
        LABELS.put("t2", "Arrestation et garde a vue");
        LABELS.put("t3", "Inculpation officielle");
        LABELS.put("t4", "Audience devant le tribunal");
        LABELS.put("t5", "Prononce du verdict (condamnation)");
        LABELS.put("t6", "Peine purgee, liberation");
    }

    // Valuation V
    static final Map<String, Set<String>> V = new LinkedHashMap<>();
    static {
        V.put("libre",            new LinkedHashSet<>(Arrays.asList("t1","t6")));
        V.put("en_detention",     new LinkedHashSet<>(Arrays.asList("t2","t3","t4","t5")));
        V.put("accuse",           new LinkedHashSet<>(Arrays.asList("t3","t4")));
        V.put("juge",             new LinkedHashSet<>(Arrays.asList("t4","t5")));
        V.put("condamne",         new LinkedHashSet<>(Arrays.asList("t5","t6")));
        V.put("presume_innocent", new LinkedHashSet<>(Arrays.asList("t1","t2","t3","t4")));
        V.put("droit_avocat",     new LinkedHashSet<>(Arrays.asList("t2","t3","t4","t5","t6")));
    }

    // ============================================================
    //  PRIMITIVES (identique a Music — la semantique ne change pas)
    // ============================================================

    private static int idx(String t) { return INSTANTS.indexOf(t); }

    private static List<String> futurs(String t) {
        int i = idx(t);
        return (i < 0) ? Collections.emptyList()
                       : INSTANTS.subList(i + 1, INSTANTS.size());
    }

    private static List<String> passes(String t) {
        int i = idx(t);
        return (i <= 0) ? Collections.emptyList() : INSTANTS.subList(0, i);
    }

    private static boolean vrai(String atom, String t) {
        return V.getOrDefault(atom, Collections.emptySet()).contains(t);
    }

    static boolean G(String atom, String t) {
        for (String s : futurs(t)) if (!vrai(atom, s)) return false;
        return true;
    }
    static boolean F(String atom, String t) {
        for (String s : futurs(t)) if (vrai(atom, s)) return true;
        return false;
    }
    static boolean H(String atom, String t) {
        for (String s : passes(t)) if (!vrai(atom, s)) return false;
        return true;
    }
    static boolean P(String atom, String t) {
        for (String s : passes(t)) if (vrai(atom, s)) return true;
        return false;
    }

    // ============================================================
    //  AFFICHAGE
    // ============================================================

    public static void run() {
        System.out.println("=== Logique Modale Temporelle : Deroulement d'un Proces ===\n");
        System.out.println("Cas : Omar (recidiviste) est arrete, juge puis condamne.\n");

        System.out.println("Instants chronologiques (t1 < t2 < t3 < t4 < t5 < t6) :");
        for (String t : INSTANTS) {
            System.out.println("  " + t + "  " + LABELS.get(t));
        }
        System.out.println();

        System.out.println("Valuation V :");
        for (Map.Entry<String, Set<String>> e : V.entrySet()) {
            System.out.println("  " + pad(e.getKey(), 20) + " = " + e.getValue());
        }
        System.out.println();

        System.out.println("Tableau verite atome x instant :");
        System.out.print("  " + pad("", 20));
        for (String t : INSTANTS) System.out.print(" | " + center(t, 3));
        System.out.println();
        System.out.println("  " + "-".repeat(20 + INSTANTS.size() * 6));
        for (String atom : V.keySet()) {
            System.out.print("  " + pad(atom, 20));
            for (String t : INSTANTS) {
                System.out.print(" | " + center(vrai(atom, t) ? "V" : ".", 3));
            }
            System.out.println();
        }
        System.out.println();

        // ===== Requetes temporelles =====
        System.out.println("--- Requetes temporelles ---\n");

        eval("Q1", "G libre", "t6",
            "Omar sera TOUJOURS libre apres t6",
            G("libre", "t6"),
            "futur de t6 = vide => quantification universelle vide est VRAIE");

        eval("Q2", "F condamne", "t1",
            "Omar sera condamne A UN MOMENT dans le futur",
            F("condamne", "t1"),
            "condamne vrai en t5 (verdict) et t6 (peine purgee) -> existentiel verifie");

        eval("Q3", "H libre", "t3",
            "Omar a TOUJOURS ete libre dans le passe de t3",
            H("libre", "t3"),
            "passe de t3 = {t1,t2} ; libre VRAI en t1 mais FAUX en t2 (detention) -> faux");

        eval("Q4", "P libre", "t5",
            "Omar a ete libre A UN MOMENT dans le passe de t5",
            P("libre", "t5"),
            "passe de t5 = {t1..t4} ; libre vrai en t1 -> existentiel verifie");

        eval("Q5", "H presume_innocent", "t4",
            "Omar a TOUJOURS ete presume innocent avant l'audience t4",
            H("presume_innocent", "t4"),
            "passe de t4 = {t1,t2,t3} ; presume_innocent vrai partout -> presomption d'innocence");

        eval("Q6", "G presume_innocent", "t5",
            "Omar sera TOUJOURS presume innocent apres le verdict t5",
            G("presume_innocent", "t5"),
            "futur de t5 = {t6} ; presume_innocent FAUX en t6 (condamne) -> faux");

        eval("Q7", "F libre", "t3",
            "Omar sera libre A UN MOMENT dans le futur",
            F("libre", "t3"),
            "libre vrai en t6 (apres avoir purge sa peine) -> meme un condamne sera libre un jour");

        eval("Q8", "G droit_avocat", "t2",
            "Omar aura TOUJOURS droit a un avocat apres son arrestation",
            G("droit_avocat", "t2"),
            "droit_avocat vrai pour tous {t3..t6} -> droit constitutionnel garanti");

        // ===== Lien avec le programme du cours =====
        System.out.println("\n--- Lien avec le cours (chapitre 3) ---");
        System.out.println("  Le passage d'un instant a un autre = relation d'accessibilite temporelle.");
        System.out.println("  La verite d'une proposition CHANGE dans le temps :");
        System.out.println("    'libre' : VRAI en t1, FAUX de t2 a t5, VRAI en t6");
        System.out.println("    'presume_innocent' : VRAI de t1 a t4 (avant verdict), FAUX apres");
        System.out.println("  Sans operateurs temporels, ces nuances seraient inexprimables.");

        System.out.println("\n  Tableau correspondance (cours) :");
        System.out.println("                | <>          | []          ");
        System.out.println("    Futur       | F (Futur)   | G (Going-to)");
        System.out.println("    Passe       | P (Past)    | H (Has-been)");
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
