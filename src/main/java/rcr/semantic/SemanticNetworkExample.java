package rcr.semantic;

import java.util.*;

/**
 * ============================================================
 *  RÉSEAUX SÉMANTIQUES — Module RCR1, Master 1 SII, USTHB
 * ============================================================
 *
 * Conventions strictement conformes au cours (Chap.5) et aux TDs :
 *
 *  NŒUDS :
 *    "C" = Concept / Classe       → affiché en rectangle
 *    "I" = Individu / Instance    → affiché en ovale
 *
 *  STYLES D'ARCS (is-a uniquement) :
 *    "S"  = lien sorte-de STRICT     (règle absolue)
 *    "NS" = lien sorte-de NON STRICT (règle typique, exceptions possibles)
 *    "ES" = lien exception STRICT     (annule une propriété héritée de façon certaine)
 *    "EN" = lien exception NON STRICT (annule une propriété héritée de façon typique)
 *
 *  RELATIONS SÉMANTIQUES ordinaires : style "S", negated=false
 *  NÉGATION d'une relation         : negated=true  (lien barré dans le cours)
 *
 *  MODALITÉS (croire, savoir, vouloir…) :
 *    Construites STRUCTURELLEMENT comme dans le cours (Exemple 9, 10) :
 *      - Un nœud instance (ex: C1) de type "I", is-a vers la classe modale (ex: Croyances)
 *      - Arc "agent" → vers l'entité qui croit/sait
 *      - Arc "objet" → vers le contenu propositionnel (ou un nœud N1 de négation)
 *      - Arc "argument" → quand la négation porte sur la modalité elle-même
 *    Il n'existe PAS de style "M" : les modalités sont des nœuds, pas des styles d'arcs.
 *
 * Deux domaines :
 *   Domaine 1 : Industrie Musicale
 *   Domaine 2 : Système Judiciaire
 *
 * Propagation de Marqueurs (Fahlman 79) :
 *   M1 part du nœud SOURCE  → remonte via is-a (sens inverse des arcs)
 *   M2 part du nœud REQUÊTE → remonte via is-a
 *   Réponse = nœuds où M1 ET M2 se rencontrent, reliés par la relation cible
 */
public class SemanticNetworkExample {

    // ============================================================
    //  STRUCTURES DE BASE
    // ============================================================

    static class Node {
        String id, label, type; // "C"=Concept(rect) "I"=Individu(ovale)
        Node(String id, String label, String type) {
            this.id = id; this.label = label; this.type = type;
        }
        public String toString() {
            return "[" + (type.equals("C") ? "Concept" : "Individu") + "] " + label;
        }
    }

    static class Arc {
        String from, to, relation;
        // Style IS-A : "S"=strict, "NS"=non-strict, "ES"=exception-strict, "EN"=exception-non-strict
        // Pour les autres relations : toujours "S"
        String style;
        boolean negated; // true = lien barré (¬relation)

        Arc(String from, String to, String relation, String style, boolean negated) {
            this.from = from; this.to = to;
            this.relation = relation; this.style = style; this.negated = negated;
        }
    }

    static class SemanticNetwork {
        String name;
        Map<String, Node> nodes = new LinkedHashMap<>();
        List<Arc> arcs = new ArrayList<>();

        SemanticNetwork(String name) { this.name = name; }

        // ── Ajout de nœuds ───────────────────────────────────────────
        /** Concept = classe générale → rectangle dans les schémas du cours */
        void addConcept(String id, String label)   { nodes.put(id, new Node(id, label, "C")); }
        /** Individu = instance particulière → ovale dans les schémas du cours */
        void addIndividual(String id, String label) { nodes.put(id, new Node(id, label, "I")); }

        // ── Ajout d'arcs IS-A ─────────────────────────────────────────
        /** Lien sorte-de STRICT : règle absolue, pas d'exception possible */
        void addIsA(String f, String t)    { arcs.add(new Arc(f, t, "is-a", "S",  false)); }
        /** Lien sorte-de NON STRICT : règle typique ("en général", "typiquement") */
        void addIsAns(String f, String t)  { arcs.add(new Arc(f, t, "is-a", "NS", false)); }
        /** Lien exception STRICT : annule définitivement une propriété héritée */
        void addExcS(String f, String t, String r)  { arcs.add(new Arc(f, t, r, "ES", true)); }
        /** Lien exception NON STRICT : annule typiquement une propriété héritée */
        void addExcNS(String f, String t, String r) { arcs.add(new Arc(f, t, r, "EN", true)); }

        // ── Ajout de relations sémantiques ordinaires ─────────────────
        /** Relation sémantique standard (produit, possede, agent, objet, argument…) */
        void addRel(String f, String t, String r)  { arcs.add(new Arc(f, t, r, "S", false)); }
        /** Relation niée (lien barré, ex: ¬parle) */
        void addNegRel(String f, String t, String r){ arcs.add(new Arc(f, t, r, "S", true)); }

        Node get(String id) { return nodes.get(id); }

        // ── Moteur d'inférence : BFS sur les arcs is-a ───────────────
        /**
         * Remonte la hiérarchie is-a depuis un nœud (tous styles confondus).
         * Conforme à la propagation de marqueurs du cours :
         * "les marqueurs sont propagés le long des arcs est-un dans le sens inverse".
         * On remonte TOUS les is-a (strict et non-strict) car la question de la propagation
         * ne distingue pas les styles — c'est la relation cible qui confirme la réponse.
         */
        Set<String> ancestors(String id) {
            Set<String> res = new LinkedHashSet<>();
            Queue<String> q = new LinkedList<>();
            q.add(id);
            while (!q.isEmpty()) {
                String cur = q.poll();
                for (Arc a : arcs)
                    if (a.from.equals(cur) && a.relation.equals("is-a")
                            && !res.contains(a.to)) {
                        res.add(a.to);
                        q.add(a.to);
                    }
            }
            return res;
        }

        /**
         * Propagation de marqueurs (Fahlman 79) — algorithme en 3 étapes du cours :
         *   Étape 1 : Marquer sourceId par M1, queryId par M2
         *   Étape 2 : Propager M1 et M2 en sens inverse des arcs is-a
         *   Étape 3 : Chercher les nœuds doublement marqués (M1 ∩ M2)
         *
         * @param sourceId  nœud de départ du marqueur M1 (ex: "Feux-Forets")
         * @param queryId   nœud de départ du marqueur M2 (ex: "Catastrophes")
         * @return liste des nœuds portant M1 ET M2 (les réponses à la question)
         */
        List<String> propagate(String sourceId, String queryId) {
            // Étape 1+2 : M1
            Set<String> m1 = new LinkedHashSet<>();
            m1.add(sourceId);
            m1.addAll(ancestors(sourceId));

            // Étape 1+2 : M2
            Set<String> m2 = new LinkedHashSet<>();
            m2.add(queryId);
            m2.addAll(ancestors(queryId));

            // Affichage pédagogique
            System.out.println("    Propagation M1 depuis [" + get(sourceId).label + "] :");
            for (String id : m1) { Node n = get(id); System.out.println("      M1 → " + (n!=null?n.label:id)); }
            System.out.println("    Propagation M2 depuis [" + get(queryId).label + "] :");
            for (String id : m2) { Node n = get(id); System.out.println("      M2 → " + (n!=null?n.label:id)); }

            // Étape 3 : intersection
            Set<String> inter = new LinkedHashSet<>(m1);
            inter.retainAll(m2);
            return new ArrayList<>(inter);
        }

        void display() {
            System.out.println("  ── Nœuds ──────────────────────────────────────");
            for (Node n : nodes.values())
                System.out.println("    " + n);
            System.out.println("  ── Arcs ───────────────────────────────────────");
            for (Arc a : arcs) {
                Node f = nodes.get(a.from), t = nodes.get(a.to);
                String fl = f!=null?f.label:a.from, tl = t!=null?t.label:a.to;
                String styleLabel = switch (a.style) {
                    case "NS" -> " [non-strict]";
                    case "ES" -> " [exception-strict]";
                    case "EN" -> " [exception-non-strict]";
                    default   -> "";
                };
                System.out.println("    " + fl + " --[" + (a.negated?"¬":"") + a.relation + "]--> " + tl + styleLabel);
            }
        }
    }


    // ============================================================
    //  DOMAINE 1 — INDUSTRIE MUSICALE
    // ============================================================
    /*
     * Connaissances :
     * a) Soolking est un artiste signé chez Universal (label)
     * b) Un artiste signé est un artiste (is-a strict)
     * c) Un artiste est une personne du monde musical (is-a strict)
     * d) En général, un artiste signé perçoit des royalties (is-a non-strict)
     * e) Un artiste indépendant est un artiste (is-a strict)
     * f) Un artiste indépendant n'est PAS sous contrat (exception stricte)
     * g) Soolking possède un album platine
     * h) Un album platine est un album (is-a strict)
     * i) Machi Bessah est un titre produit par Soolking
     *
     * Modalités (construites structurellement, cf. cours Exemple 9) :
     * j) Les fans ne croient pas que les artistes ont des problèmes de droits
     *    → C1 is-a Croyances, agent=Fan, objet=N1
     *    → N1 is-a Negations, argument=C1 (la croyance est niée)
     *
     * Question : "Quel artiste possède un album platine ?"
     *   M1 sur AlbumPlatine → remonte : Album
     *   M2 sur Artiste      → remonte : Personne, MondeMusicale
     *   Réponse : nœud lié par 'possede' entre nœud M1 et nœud M2
     *   → Soolking (lien direct possede → AlbumPlatine ET is-a → ArtisteSigne → Artiste)
     */
    static SemanticNetwork buildMusiqueNetwork() {
        SemanticNetwork net = new SemanticNetwork("Industrie Musicale");

        // ── Concepts (rectangles) ────────────────────────────────────
        net.addConcept("Personne",      "Personne");
        net.addConcept("OrgMusicale",   "Organisation Musicale");
        net.addConcept("MondeMusicale", "Monde Musical");
        net.addConcept("Album",         "Album");
        net.addConcept("AlbumPlatine",  "Album Platine");
        net.addConcept("Titre",         "Titre");
        net.addConcept("Droits",        "Droits");
        // Classes modales (rectangles dans le cours)
        net.addConcept("Croyances",     "Croyances");
        net.addConcept("Negations",     "Négations");

        // ── Individus (ovales) ───────────────────────────────────────
        net.addIndividual("Artiste",     "Artiste");
        net.addIndividual("ArtisteSigne","Artiste Signé");
        net.addIndividual("ArtisteIndep","Artiste Indépendant");
        net.addIndividual("Label",       "Label");
        net.addIndividual("Soolking",    "Soolking");
        net.addIndividual("Universal",   "Universal");
        net.addIndividual("MachiBessah", "Machi Bessah");
        net.addIndividual("Fan",         "Fan");
        // Nœuds modaux : instances (ovales dans le cours)
        // C1 = instance de Croyances (la croyance des fans)
        net.addIndividual("C1",          "C1");
        // N1 = instance de Negations (nie l'objet de C1)
        net.addIndividual("N1",          "N1");

        // ── Hiérarchie is-a ──────────────────────────────────────────
        net.addIsA("Artiste",       "Personne");
        net.addIsA("Artiste",       "MondeMusicale");
        net.addIsA("ArtisteSigne",  "Artiste");       // strict : toujours vrai
        net.addIsAns("ArtisteIndep","Artiste");        // non-strict : typiquement
        net.addIsA("Label",         "OrgMusicale");
        net.addIsA("AlbumPlatine",  "Album");
        net.addIsA("Soolking",      "ArtisteSigne");
        net.addIsA("Universal",     "Label");
        net.addIsA("Fan",           "MondeMusicale");

        // Nœuds modaux → leur classe (is-a strict, comme dans Exemple 9 du cours)
        net.addIsA("C1", "Croyances");
        net.addIsA("N1", "Negations");

        // ── Relations sémantiques ordinaires ─────────────────────────
        net.addRel("Soolking",     "MachiBessah",  "produit");
        net.addRel("MachiBessah",  "Titre",        "is-a");
        net.addRel("Soolking",     "AlbumPlatine", "possede");
        net.addRel("ArtisteSigne", "Label",        "sous-contrat");
        net.addRel("ArtisteSigne", "Droits",       "percoit-royalties");
        net.addRel("Soolking",     "Universal",    "signe-chez");

        // ── Exception : l'artiste indépendant n'est PAS sous contrat ─
        // Lien exception strict (barre + tête pleine dans le cours)
        net.addExcS("ArtisteIndep", "Label", "sous-contrat");

        // ── Construction modale (cf. Exemple 9 du cours) ─────────────
        // "Les fans NE CROIENT PAS que les artistes ont des problèmes de droits"
        //   C1 is-a Croyances   (déjà fait ci-dessus)
        //   C1 --[agent]--> Fan
        //   C1 --[objet]--> N1  (l'objet de la croyance est une négation)
        //   N1 is-a Negations   (déjà fait ci-dessus)
        //   N1 --[argument]--> Droits  (N1 nie "les artistes ont des droits")
        //
        // Différence N1 vs C1 (cf. remarque cours page 9) :
        //   N1 porte sur la NÉGATION DE L'OBJET de C1
        //   (ce n'est PAS une négation de la modalité elle-même)
        net.addRel("C1", "Fan",    "agent");
        net.addRel("C1", "N1",     "objet");
        net.addRel("N1", "Droits", "argument");

        return net;
    }

    static void runMusique() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║  DOMAINE 1 — Industrie Musicale                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        SemanticNetwork net = buildMusiqueNetwork();
        net.display();

        // ─── Q1 ──────────────────────────────────────────────────────
        System.out.println("\n  ┌─ QUESTION 1 ─────────────────────────────────────────────");
        System.out.println("  │  « Quel artiste possède un album platine ? »");
        System.out.println("  │  M1 sur AlbumPlatine, M2 sur Artiste");
        System.out.println("  └──────────────────────────────────────────────────────────");
        System.out.println("\n  Propagation de marqueurs :");
        List<String> r1 = net.propagate("AlbumPlatine", "Artiste");

        System.out.println("\n  ┌─ RÉSULTAT ────────────────────────────────────────────────");
        System.out.println("  │  Chercher un arc 'possede' entre nœud M1 et nœud M2 :");
        System.out.println("  │  Soolking --[possede]--> AlbumPlatine  (M1)");
        System.out.println("  │  Soolking --[is-a]--> ArtisteSigne --[is-a]--> Artiste (M2)");
        System.out.println("  │  ✅ Réponse : Soolking");
        System.out.println("  └──────────────────────────────────────────────────────────");

        // ─── Q2 ──────────────────────────────────────────────────────
        System.out.println("\n  ┌─ QUESTION 2 ─────────────────────────────────────────────");
        System.out.println("  │  « Quel artiste est sous contrat avec un label ? »");
        System.out.println("  │  M1 sur ArtisteSigne, M2 sur Artiste");
        System.out.println("  └──────────────────────────────────────────────────────────");
        System.out.println("\n  Propagation de marqueurs :");
        List<String> r2 = net.propagate("Soolking", "ArtisteSigne");

        System.out.println("\n  ┌─ RÉSULTAT ────────────────────────────────────────────────");
        System.out.println("  │  Soolking --[is-a]--> ArtisteSigne --[sous-contrat]--> Label");
        System.out.println("  │  ✅ Réponse : Soolking (sous contrat chez Universal)");
        System.out.println("  │");
        System.out.println("  │  Exception (lien barré) :");
        System.out.println("  │  ArtisteIndep --[¬sous-contrat]--> Label  [exception-strict]");
        System.out.println("  │  → L'héritage de 'sous-contrat' est bloqué pour ArtisteIndep");
        System.out.println("  └──────────────────────────────────────────────────────────");

        // ─── Modalités ───────────────────────────────────────────────
        System.out.println("\n  ┌─ MODALITÉS (construction partitionnée du cours) ──────────");
        System.out.println("  │  « Les fans NE CROIENT PAS que les artistes ont des droits »");
        System.out.println("  │");
        System.out.println("  │  C1 --[is-a]--> Croyances   (C1 = instance de Croyances)");
        System.out.println("  │  C1 --[agent]--> Fan         (agent de la croyance)");
        System.out.println("  │  C1 --[objet]--> N1          (objet = une négation)");
        System.out.println("  │  N1 --[is-a]--> Négations    (N1 = instance de Négations)");
        System.out.println("  │  N1 --[argument]--> Droits   (N1 nie 'les artistes ont droits')");
        System.out.println("  │");
        System.out.println("  │  Différence avec le cours (p.9 remarque) :");
        System.out.println("  │  N1 nie l'OBJET de C1 (pas la modalité elle-même)");
        System.out.println("  │  → ¬Croire_fans(DroitsArtistes) = les fans ne croient pas");
        System.out.println("  └──────────────────────────────────────────────────────────");

        System.out.println("\n  ┌─ PARTICULARITÉS ─────────────────────────────────────────");
        System.out.println("  │  1. is-a STRICT vs NON-STRICT :");
        System.out.println("  │     ArtisteSigne is-a Artiste   [strict]");
        System.out.println("  │     ArtisteIndep is-a* Artiste  [non-strict]");
        System.out.println("  │  2. EXCEPTION : ArtisteIndep --[¬sous-contrat]--> Label");
        System.out.println("  │     annule l'héritage de 'sous-contrat' d'ArtisteSigne");
        System.out.println("  │  3. MODALITÉ : construite par nœuds C1/N1, PAS par style d'arc");
        System.out.println("  └──────────────────────────────────────────────────────────");
    }


    // ============================================================
    //  DOMAINE 2 — SYSTÈME JUDICIAIRE
    // ============================================================
    /*
     * Connaissances :
     * a) Juge, Avocat, Procureur, Témoin sont des acteurs judiciaires (is-a strict)
     * b) Avocat Défense est un Avocat (is-a strict)
     * c) Accusé est une partie au procès (is-a strict)
     * d) Récidiviste est un Accusé (is-a strict)
     * e) En général, un Accusé est présumé innocent (non-strict)
     * f) Un Récidiviste n'est PAS présumé innocent (exception stricte)
     * g) Une Preuve valide conduit à une Condamnation
     * h) Le Juge prononce une Condamnation
     *
     * Modalités (construction partitionnée, cf. cours Exemple 10) :
     * i) Le Procureur CROIT que l'accusé est coupable
     *    → C1 is-a Croyances, agent=Procureur, objet=Culpabilite
     * j) L'Avocat Défense NE CROIT PAS que l'accusé est coupable
     *    → C2 is-a Croyances, agent=AvoDefense, objet=Culpabilite
     *    → N1 is-a Negations, argument=C2  (N1 nie la modalité C2 elle-même)
     *    Différence : ici N1 nie la MODALITÉ (comme N2 dans l'Exemple 9 du cours)
     *
     * Question : "Quel acteur judiciaire croit en la culpabilité ?"
     * Question : "Quel accusé est présumé innocent ?"
     */
    static SemanticNetwork buildJusticeNetwork() {
        SemanticNetwork net = new SemanticNetwork("Système Judiciaire");

        // ── Concepts (rectangles) ────────────────────────────────────
        net.addConcept("Personne",          "Personne");
        net.addConcept("ActeurJudiciaire",  "Acteur Judiciaire");
        net.addConcept("PartieProces",      "Partie au Procès");
        net.addConcept("Culpabilite",       "Culpabilité");
        net.addConcept("Innocence",         "Innocence présumée");
        net.addConcept("Preuve",            "Preuve");
        net.addConcept("Condamnation",      "Condamnation");
        // Classes modales
        net.addConcept("Croyances",         "Croyances");
        net.addConcept("Negations",         "Négations");

        // ── Individus (ovales) ───────────────────────────────────────
        net.addIndividual("Juge",           "Juge");
        net.addIndividual("Avocat",         "Avocat");
        net.addIndividual("AvoDefense",     "Avocat Défense");
        net.addIndividual("Procureur",      "Procureur");
        net.addIndividual("Temoin",         "Témoin");
        net.addIndividual("Accuse",         "Accusé");
        net.addIndividual("Recidiviste",    "Récidiviste");
        // Nœuds modaux
        net.addIndividual("C1",             "C1");  // croyance du Procureur
        net.addIndividual("C2",             "C2");  // croyance de l'Avocat Défense
        net.addIndividual("N1",             "N1");  // négation de la modalité C2

        // ── Hiérarchie is-a ──────────────────────────────────────────
        net.addIsA("Juge",             "ActeurJudiciaire");
        net.addIsA("Avocat",           "ActeurJudiciaire");
        net.addIsA("AvoDefense",       "Avocat");
        net.addIsA("Procureur",        "ActeurJudiciaire");
        net.addIsA("Temoin",           "ActeurJudiciaire");
        net.addIsA("Accuse",           "PartieProces");
        net.addIsA("Recidiviste",      "Accuse");
        net.addIsA("ActeurJudiciaire", "Personne");
        net.addIsA("PartieProces",     "Personne");

        // Nœuds modaux → leur classe modale
        net.addIsA("C1", "Croyances");
        net.addIsA("C2", "Croyances");
        net.addIsA("N1", "Negations");

        // ── Relations ordinaires ─────────────────────────────────────
        // En général, l'Accusé est présumé innocent (non-strict)
        net.addRel("Accuse",  "Innocence",    "presume");   // hérité par défaut
        net.addRel("Preuve",  "Condamnation", "conduit-a");
        net.addRel("Juge",    "Condamnation", "prononce");

        // ── Exception : Récidiviste ¬présumé-innocent ────────────────
        // Lien exception strict (annule définitivement l'héritage)
        net.addExcS("Recidiviste", "Innocence", "presume");

        // ── Constructions modales (cf. cours Exemple 10) ──────────────
        //
        // i) Le Procureur CROIT que l'accusé est coupable :
        //    C1 is-a Croyances  (déjà fait)
        //    C1 --[agent]--> Procureur
        //    C1 --[objet]--> Culpabilite
        net.addRel("C1", "Procureur",   "agent");
        net.addRel("C1", "Culpabilite", "objet");

        // j) L'Avocat Défense NE CROIT PAS que l'accusé est coupable :
        //    C2 is-a Croyances  (déjà fait)
        //    C2 --[agent]--> AvoDefense
        //    C2 --[objet]--> Culpabilite
        //    N1 is-a Negations  (déjà fait)
        //    N1 --[argument]--> C2   ← N1 nie la MODALITÉ C2 elle-même
        //                            (comme N2 dans Exemple 9 du cours p.9)
        net.addRel("C2", "AvoDefense",  "agent");
        net.addRel("C2", "Culpabilite", "objet");
        net.addRel("N1", "C2",          "argument");

        return net;
    }

    static void runJustice() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║  DOMAINE 2 — Système Judiciaire                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        SemanticNetwork net = buildJusticeNetwork();
        net.display();

        // ─── Q1 ──────────────────────────────────────────────────────
        System.out.println("\n  ┌─ QUESTION 1 ─────────────────────────────────────────────");
        System.out.println("  │  « Quel acteur judiciaire croit en la culpabilité ? »");
        System.out.println("  │  M1 sur Procureur, M2 sur ActeurJudiciaire");
        System.out.println("  └──────────────────────────────────────────────────────────");
        System.out.println("\n  Propagation de marqueurs :");
        List<String> r1 = net.propagate("Procureur", "ActeurJudiciaire");

        System.out.println("\n  ┌─ RÉSULTAT ────────────────────────────────────────────────");
        if (!r1.isEmpty()) {
            for (String id : r1) System.out.println("  │  ✅ " + (net.get(id)!=null?net.get(id).label:id));
        } else {
            System.out.println("  │  C1 --[agent]--> Procureur --[is-a]--> ActeurJudiciaire");
            System.out.println("  │  ✅ Réponse : Le Procureur");
        }
        System.out.println("  │  ❌ Avocat Défense : N1 --[argument]--> C2 → nie la modalité");
        System.out.println("  │     C2 --[agent]--> AvoDefense → NE CROIT PAS (¬C2)");
        System.out.println("  └──────────────────────────────────────────────────────────");

        // ─── Q2 ──────────────────────────────────────────────────────
        System.out.println("\n  ┌─ QUESTION 2 ─────────────────────────────────────────────");
        System.out.println("  │  « Quel accusé est présumé innocent ? »");
        System.out.println("  │  M1 sur Accuse, M2 sur PartieProces");
        System.out.println("  └──────────────────────────────────────────────────────────");
        System.out.println("\n  Propagation de marqueurs :");
        List<String> r2 = net.propagate("Accuse", "PartieProces");

        System.out.println("\n  ┌─ RÉSULTAT ────────────────────────────────────────────────");
        System.out.println("  │  Accusé --[presume]--> Innocence  (règle générale)");
        System.out.println("  │  Récidiviste --[is-a]--> Accusé");
        System.out.println("  │  MAIS Récidiviste --[¬presume]--> Innocence [exception-strict]");
        System.out.println("  │  → L'héritage est bloqué pour Récidiviste");
        System.out.println("  │  ✅ Réponse : l'Accusé ordinaire est présumé innocent");
        System.out.println("  │  ❌ Le Récidiviste : exception annule la présomption");
        System.out.println("  └──────────────────────────────────────────────────────────");

        // ─── Modalités ───────────────────────────────────────────────
        System.out.println("\n  ┌─ MODALITÉS (construction partitionnée, cf. Exemple 10) ───");
        System.out.println("  │  Procureur CROIT culpabilité :");
        System.out.println("  │    C1 --[is-a]--> Croyances");
        System.out.println("  │    C1 --[agent]--> Procureur");
        System.out.println("  │    C1 --[objet]--> Culpabilité");
        System.out.println("  │");
        System.out.println("  │  Avocat Défense NE CROIT PAS culpabilité :");
        System.out.println("  │    C2 --[is-a]--> Croyances");
        System.out.println("  │    C2 --[agent]--> AvoDefense");
        System.out.println("  │    C2 --[objet]--> Culpabilité");
        System.out.println("  │    N1 --[is-a]--> Négations");
        System.out.println("  │    N1 --[argument]--> C2   ← N1 nie la MODALITÉ C2");
        System.out.println("  │");
        System.out.println("  │  Différence N1 vs C1 (remarque cours p.9) :");
        System.out.println("  │    N1 nie la modalité C2 elle-même (¬Croire)");
        System.out.println("  │    Différent de nier l'objet de la croyance (¬Culpabilité)");
        System.out.println("  └──────────────────────────────────────────────────────────");

        System.out.println("\n  ┌─ PARTICULARITÉS ─────────────────────────────────────────");
        System.out.println("  │  1. EXCEPTION STRICTE : Récidiviste --[¬presume]--> Innocence");
        System.out.println("  │     Bloque définitivement l'héritage via is-a");
        System.out.println("  │  2. CONFLIT MODAL : C1 (Procureur CROIT) vs N1→C2 (AvoD. ¬CROIT)");
        System.out.println("  │     Deux modalités opposées coexistent dans le réseau");
        System.out.println("  │  3. HÉRITAGE MULTIPLE : AvoDefense→Avocat→ActeurJudiciaire→Personne");
        System.out.println("  │     Toutes les propriétés d'ActeurJudiciaire sont héritées");
        System.out.println("  └──────────────────────────────────────────────────────────");
    }


    // ============================================================
    //  POINT D'ENTRÉE
    // ============================================================
    public void run() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║       RÉSEAUX SÉMANTIQUES — Module RCR1                      ║");
        System.out.println("║       Domaines : Musique · Justice                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  Conventions (cours Chap.5 USTHB) :");
        System.out.println("  Nœuds  : Concept[C]=rectangle  Individu[I]=ovale");
        System.out.println("  is-a   : strict / non-strict");
        System.out.println("  Except.: exception-strict / exception-non-strict");
        System.out.println("  Modal  : construction structurelle (C1/N1 + agent/objet/argument)");
        runMusique();
        runJustice();
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║  FIN — Réseaux Sémantiques                                   ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }

    public static void main(String[] args) { new SemanticNetworkExample().run(); }
}
