# Projet RCR — Logiques Formelles

**Module :** RCR1 — Représentation des Connaissances et Raisonnement  
**Niveau :** Master 1 SII — USTHB  
**Bibliothèque :** [TweetyProject 1.27](https://tweetyproject.org) · JavaFX 17.0.2 · Java 11

---

## Table des matières

1. [Présentation](#1-présentation)
2. [Architecture du projet](#2-architecture-du-projet)
3. [Logique Classique](#3-logique-classique)
4. [Logique Modale](#4-logique-modale)
5. [Logique des Défauts](#5-logique-des-défauts)
6. [Logique de Description](#6-logique-de-description)
7. [Réseaux Sémantiques](#7-réseaux-sémantiques)
8. [Interface graphique](#8-interface-graphique)
9. [Guide de lancement](#9-guide-de-lancement)
10. [Dépendances](#10-dépendances)

---

## 1. Présentation

Ce projet implémente plusieurs formalismes de représentation des connaissances :

| Formalisme | Domaine 1 | Domaine 2 | Statut |
|---|---|---|---|
| **Logique Classique** (propositionnelle + prédicats) | Industrie musicale | Système judiciaire | ✅ Implémenté |
| **Logique Modale** | Industrie musicale | Système judiciaire | ✅ Implémenté |
| **Logique des Défauts** | Industrie musicale | Système judiciaire | ✅ Implémenté |
| **Logique de Description** | Industrie musicale | Système judiciaire | ✅ Implémenté |
| **Réseaux Sémantiques** | Industrie musicale | Système judiciaire | ✅ Implémenté |

Chaque formalisme est accompagné d'une **interface graphique JavaFX** interactive.

---

## 2. Architecture du projet

```
projet_RCR/
├── pom.xml                          # Configuration Maven
└── src/main/
    ├── java/rcr/
    │   ├── Main.java                # Point d'entrée console
    │   ├── MainApp.java             # Point d'entrée JavaFX (GUI)
    │   ├── classical/
    │   │   ├── PropositionalLogicMusic.java     # CNF + SAT4J — musique
    │   │   ├── PropositionalLogicJustice.java   # CNF + SAT4J — justice
    │   │   ├── PredicateLogicMusic.java         # FOL chaînage avant — musique
    │   │   ├── PredicateLogicJustice.java       # FOL chaînage avant — justice
    │   │   └── ClassicalLogicExample.java       # Orchestrateur console
    │   ├── modal/
    │   │   ├── ModalLogicMusic.java    # Modèle de Kripke — musique
    │   │   ├── ModalLogicJustice.java  # Modèle de Kripke — justice
    │   │   └── ModalLogicExample.java  # Orchestrateur console
    │   ├── defaut/
    │   │   ├── DefaultLogicMusic.java    # Théorie des défauts — musique
    │   │   ├── DefaultLogicJustice.java  # Théorie des défauts — justice
    │   │   └── DefaultLogicExample.java  # Orchestrateur console
    │   ├── description/
    │   │   ├── DescriptionLogicMusic.java   # Ontologie OWL + HermiT — musique
    │   │   ├── DescriptionLogicJustice.java # Ontologie OWL + HermiT — justice
    │   │   └── DescriptionLogicExample.java # Orchestrateur console
    │   ├── semantic/
    │   │   └── SemanticNetworkExample.java  # Réseaux sémantiques (Fahlman) — 2 domaines
    │   └── ui/
    │       ├── DashboardController.java                    # Tableau de bord
    │       ├── classical/ClassicalLogicController.java     # Fenêtre logique classique
    │       ├── modal/ModalLogicController.java             # Fenêtre logique modale
    │       ├── defaut/DefaultLogicController.java          # Fenêtre logique des défauts
    │       ├── description/DescriptionLogicController.java # Fenêtre logique de description
    │       └── semantic/SemanticNetworkController.java     # Fenêtre réseaux sémantiques
    └── resources/rcr/
        ├── dashboard.fxml           # Interface du tableau de bord
        └── css/style.css            # Feuille de styles (thème sombre)
```

---

## 3. Logique Classique

### 3.1 Logique Propositionnelle (CNF + SAT4J)

Encodage de chaque domaine en clauses CNF, résolution par le solveur **SAT4J** via le `SatReasoner` de Tweety. Une entaille `KB ⊨ φ` est vérifiée par **réfutation** : on teste si `KB ∪ {¬φ}` est insatisfiable.

**Domaine musique** ([`PropositionalLogicMusic.java`](src/main/java/rcr/classical/PropositionalLogicMusic.java)) — 4 artistes (Soolking, Feu, Tif, Flenn), 7 faits + 5 clauses Horn, 5 questions :
- Q1 satisfiabilité · Q2-Q3 entailment direct · Q4 monotonie (ajout de fait) · Q5 contradiction (`KB + ¬royalties_flenn` UNSAT).

**Domaine justice** ([`PropositionalLogicJustice.java`](src/main/java/rcr/classical/PropositionalLogicJustice.java)) — 3 individus (Ali, Omar, Karim), 7 faits + 5 clauses, 7 questions dont une contradiction explicite (`c5` vs `c6` sur Karim) et une démonstration de **monotonie** (ajout de `¬innocent_omar` rend la KB UNSAT — impossible de retirer une conclusion en LP, ce que la logique des défauts gère par extensions).

### 3.2 Logique des Prédicats (FOL, chaînage avant)

Domaine fini → chaînage avant naïf jusqu'à point fixe. Axiomes universels `∀x (α(x) → β(x))` instanciés sur tous les individus, dérivation des conséquences puis interrogation par appartenance ensembliste.

**Musique** ([`PredicateLogicMusic.java`](src/main/java/rcr/classical/PredicateLogicMusic.java)) — 7 axiomes (chaînes `EstRappeurAlgerien → EstRappeur → EstArtiste → EstMusicien` et règles royalties / experience solo / album certifié), 6 questions incluant une preuve existentielle `∃x ExperienceSolo(x)`.

**Justice** ([`PredicateLogicJustice.java`](src/main/java/rcr/classical/PredicateLogicJustice.java)) — 8 axiomes (droits de l'accusé, peine récidiviste, tribunal mineur, suivi renforcé, nécessité d'avocat public quand mineur ∧ crime grave).

---

## 4. Logique Modale

### 4.1 Rappel théorique

La **logique modale** étend la logique propositionnelle avec deux opérateurs :

- **□φ** (nécessité) : φ est vraie dans **tous** les mondes accessibles
- **◊φ** (possibilité) : φ est vraie dans **au moins un** monde accessible

Un **modèle de Kripke** est un triplet `⟨W, R, V⟩` :
- `W` : ensemble de mondes possibles
- `R ⊆ W × W` : relation d'accessibilité entre mondes
- `V : Prop → 2^W` : fonction de valuation (quelles propositions sont vraies où)

**Sémantique :**
```
M, w ⊨ □φ  ⟺  ∀v, (w,v) ∈ R → M, v ⊨ φ
M, w ⊨ ◊φ  ⟺  ∃v, (w,v) ∈ R ∧ M, v ⊨ φ
```

**Propriétés de R** (définissent les systèmes modaux K, T, S4, S5…) :
| Propriété | Définition | Axiome modal |
|---|---|---|
| Réflexive | ∀w, (w,w) ∈ R | T : □φ → φ |
| Transitive | (w,v)∧(v,u) → (w,u) | 4 : □φ → □□φ |
| Symétrique | (w,v) → (v,w) | B : φ → □◊φ |
| Sérielle | ∀w, ∃v, (w,v) ∈ R | D : □φ → ◊φ |

### 4.2 Domaine 1 — Industrie musicale (`ModalLogicMusic.java`)

**Modèle de Kripke ⟨W, R, V⟩ :**

```
W = { w1=label, w2=fan, w3=artiste, w4=critique }

R = { (w1,w2), (w1,w3), (w3,w3), (w2,w4), (w4,w4) }

         w1 (label)
        /  \
     w2     w3 ↺
  (fan)↺  (artiste)
      \
      w4 (critique) ↺

Valuation V :
  album_est_platine     ∈ { w1, w3 }
  concert_annule        ∈ { w2, w4 }
  artiste_sous_contrat  ∈ { w1 }
  chanson_en_top_charts ∈ { w1, w2, w3, w4 }
  artiste_independant   ∈ { w3 }
```

**Formules évaluées :**

| # | Formule | Monde | Résultat | Explication |
|---|---|---|---|---|
| Q1 | □album_est_platine | w1 | **FAUX** | Successeurs(w1)={w2,w3} ; album faux en w2 |
| Q2 | ◊concert_annule | w1 | **VRAI** | concert vrai en w2 ∈ Successeurs(w1) |
| Q3 | □chanson_en_top_charts | w2 | **VRAI** | Successeurs(w2)={w4} ; chanson vrai en w4 |
| Q4 | ◊artiste_independant | w1 | **VRAI** | independant vrai en w3 ∈ Successeurs(w1) |
| Q5 | □(album→contrat) | w1 | **FAUX** | En w3 : album=V, contrat=F → implication F |

**Propriétés de R :** Non réflexive · Non transitive · Non symétrique · **Sérielle**

### 4.3 Domaine 2 — Système judiciaire (`ModalLogicJustice.java`)

**Modèle de Kripke ⟨W, R, V⟩ :**

```
W = { w1=juge, w2=avocat, w3=procureur, w4=témoin, w5=accusé }

R = { (w1,w1), (w1,w2), (w1,w3), (w2,w4), (w3,w5), (w5,w5) }

   w1 ↺ (juge)
   /  \
 w2    w3
 |      |
 w4    w5 ↺

Valuation V :
  accuse_est_coupable         ∈ { w3, w5 }
  accuse_est_innocent         ∈ { w2, w4 }
  preuve_valide               ∈ { w1, w3 }
  temoin_credible             ∈ { w1, w2, w4 }
  circonstances_attenuantes   ∈ { w2, w5 }
```

**Formules évaluées :**

| # | Formule | Monde | Résultat | Explication |
|---|---|---|---|---|
| Q1 | □preuve_valide | w1 | **FAUX** | preuve faux en w2 ∈ Successeurs(w1) |
| Q2 | ◊accuse_est_innocent | w1 | **VRAI** | innocent vrai en w2 ∈ Successeurs(w1) |
| Q3 | □accuse_est_coupable | w3 | **VRAI** | Successeurs(w3)={w5} ; coupable vrai en w5 |
| Q4 | ◊circonstances_attenuantes | w1 | **VRAI** | attenuantes vrai en w2 |
| Q5 | □temoin_credible | w2 | **VRAI** | Successeurs(w2)={w4} ; credible vrai en w4 |

**Propriétés de R :** Non réflexive (w2,w3,w4 n'ont pas de boucle) · Non transitive · Non symétrique · **Non sérielle** (w4 n'a aucun successeur)

### 4.4 Implémentation (API Tweety)

```java
// 1. Créer les propositions (prédicats 0-aire)
FolAtom album = new FolAtom(new Predicate("album_est_platine"));

// 2. Créer les mondes (ensemble d'atomes vrais)
MlHerbrandInterpretation w1 = new MlHerbrandInterpretation(
    Arrays.asList(album, contrat, chanson));

// 3. Construire la relation d'accessibilité
Set<Pair<Interpretation<...>, Interpretation<...>>> pairs = new HashSet<>();
pairs.add(new Pair<>(w1, w2));
AccessibilityRelation R = new AccessibilityRelation(pairs);

// 4. Évaluer une formule modale
boolean result = evaluate(w1, new Necessity(album), R);
// → true si album vrai dans TOUS les successeurs de w1
```

L'évaluation récursive gère : `FolAtom`, `Negation`, `Implication`, `Necessity (□)`, `Possibility (◊)`.

---

## 5. Logique des Défauts

### 5.1 Rappel théorique

La **logique des défauts** (Reiter, 1980) étend la logique du premier ordre avec des règles de raisonnement par défaut, permettant le **raisonnement non-monotone**.

**Théorie des défauts Δ = (W, D) :**
- `W` : ensemble de faits certains (base de connaissances)
- `D` : ensemble de défauts

**Format d'un défaut :**
```
α(x) : β(x) / γ(x)
```
- `α` : prérequis (doit être vrai)
- `β` : justification (doit être consistante)
- `γ` : conclusion (ce qu'on conclut par défaut)

**Extension :** ensemble maximal de croyances cohérentes obtenu en appliquant les défauts. S'il y a un conflit (deux défauts s'excluent mutuellement), il y a **plusieurs extensions**.

**Non-monotonie :** ajouter un fait à W peut invalider une conclusion précédemment déduite.

### 5.2 Domaine 1 — Industrie musicale (`DefaultLogicMusic.java`)

**Defaults D :**
```
d1 : EstArtiste(x)                    :: LibreDroits(x)       / LibreDroits(x)
d2 : SigneLabel(x)                    :: ¬LibreDroits(x)      / ¬LibreDroits(x)
d3 : EstArtiste(x) ∧ AlbumSorti(x)  :: Royalties(x)          / Royalties(x)
d4 : SigneLabel(x)                    :: PartageRoyalties(x)  / PartageRoyalties(x)
d5 : IndependantLongtemps(x)          :: ExperienceSolo(x)    / ExperienceSolo(x)
d6 : NouveauContrat(x)               :: ¬LibreDroits(x)      / ¬LibreDroits(x)
d7 : PlusDeDeuxAns(x) ∧ AlbumSorti(x):: AlbumCertifie(x)     / AlbumCertifie(x)
```

**Q1 — Soolking** *(EstArtiste + SigneLabel + AlbumSorti)* :
- d1 conclut `LibreDroits` / d2 conclut `¬LibreDroits` → **conflit** → 2 extensions
- d3 et d4 s'appliquent sans conflit → `Royalties` et `PartageRoyalties` dans **toutes** les extensions

**Q2 — DZ_Master** *(EstArtiste + IndependantLongtemps + NouveauContrat)* :
- d1 vs d6 → **conflit** sur LibreDroits → 2 extensions
- d5 → `ExperienceSolo` dans **toutes** les extensions (pas de conflit)

**Q3 — Atlas** *(EstArtiste + AlbumSorti + PlusDeDeuxAns)* :
- Aucun défaut conflictuel → **1 seule extension**
- Contient : `LibreDroits`, `Royalties`, `AlbumCertifie`

**Q4 — Non-monotonie (Atlas + SigneLabel)** :
- Avant ajout de `SigneLabel(atlas)` : `LibreDroits(atlas)` conclu dans toutes les extensions
- Après ajout de `SigneLabel(atlas)` : d1 vs d2 → conflit → `LibreDroits(atlas)` **n'est plus** dans toutes les extensions
- **Conclusion : le raisonnement par défaut est non-monotone**

### 5.3 Domaine 2 — Système judiciaire (`DefaultLogicJustice.java`)

**Defaults D :**
```
d1 : Accuse(x)      :: Innocent(x)           / Innocent(x)
d2 : Recidiviste(x) :: PeineLourde(x)         / PeineLourde(x)
d3 : Mineur(x)      :: TribunalEnfants(x)     / TribunalEnfants(x)
d4 : CrimeGrave(x)  :: ¬TribunalEnfants(x)   / ¬TribunalEnfants(x)
d5 : Temoignage(x)  :: Acquittement(x)        / Acquittement(x)
d6 : Recidiviste(x) :: ¬Innocent(x)           / ¬Innocent(x)
```

**Q1 — Ali** *(Accuse + Temoignage)* :
- d1 → `Innocent(ali)` / d5 → `Acquittement(ali)` — aucun conflit → **1 extension**

**Q2 — Omar** *(Accuse + Recidiviste)* :
- d1 vs d6 → **conflit** sur Innocent → 2 extensions
- d2 → `PeineLourde(omar)` dans **toutes** les extensions

**Q3 — Karim** *(Accuse + Mineur + CrimeGrave)* :
- d3 vs d4 → **conflit** sur TribunalEnfants → 2 extensions
- d1 → `Innocent(karim)` dans **toutes** les extensions

**Q4 — Non-monotonie (Ali + Recidiviste)** :
- Avant : `Innocent(ali)` conclu (1 extension)
- Après ajout de `Recidiviste(ali)` : d1 vs d6 → **2 extensions**, `Innocent` n'est plus dans toutes
- **Non-monotonie démontrée**

### 5.4 Implémentation (API Tweety RDL)

```java
// Théorie sous forme de chaîne — format parser Tweety
String theory =
    "Art = {soolking}\n"
    + "type(EstArtiste(Art))\n"           // déclaration de sort
    + "type(LibreDroits(Art))\n"          // déclaration de type
    + "EstArtiste(soolking)\n"            // fait certain W
    + "SigneLabel(soolking)\n"
    + "EstArtiste(X)::LibreDroits(X)/LibreDroits(X)\n"     // défaut d1
    + "SigneLabel(X)::!LibreDroits(X)/!LibreDroits(X)\n";  // défaut d2

// Calcul des extensions
RdlParser parser = new RdlParser();
DefaultTheory dt = parser.parseBeliefBase(theory);
Collection<Extension> extensions = new SimpleDefaultReasoner().getModels(dt.ground());
// → 2 extensions (conflit d1 vs d2)
```

**Points d'implémentation importants :**
- Les théories sont **séparées par individu** (ex: une théorie pour soolking, une pour atlas) — indispensable pour éviter l'explosion combinatoire du `DefaultProcessTree`
- La conjonction dans les prérequis s'écrit : `EstArtiste(X) && AlbumSorti(X)::Royalties(X)/Royalties(X)`
- La non-monotonie est démontrée en construisant deux théories : l'originale et l'originale + 1 fait supplémentaire

---

## 6. Logique de Description

### 6.1 Rappel théorique

La **logique de description** (DL) est un fragment décidable de la FOL conçu pour décrire des **ontologies** : un vocabulaire de **concepts** (classes), de **rôles** (propriétés) et d'**individus**, structuré en :

- **TBox** — axiomes terminologiques : `C ⊑ D` (subsomption), `C ≡ D` (équivalence)
- **ABox** — assertions : `C(a)` (appartenance), `R(a,b)` (rôle entre individus)

Constructeurs ALC utilisés ici : `⊓` (intersection), `⊔` (union), `¬` (négation), `∃R.C` (existentiel), `∀R.C` (universel).

**Hypothèses :** OWA (Open World Assumption — l'absence d'information n'est pas une négation) et absence d'UNA par défaut (deux individus peuvent désigner la même chose sauf déclaration `DifferentIndividuals`).

Le raisonneur **HermiT** est utilisé via l'OWL API 4.5 pour la classification, la satisfaisabilité, le test de subsomption et le calcul d'instances.

### 6.2 Domaine 1 — Industrie musicale ([`DescriptionLogicMusic.java`](src/main/java/rcr/description/DescriptionLogicMusic.java))

Ontologie `http://rcr1/musique` — concepts : `Artiste`, `Rappeur`, `RappeurAlgerien`, `Label`, `MajorLabel`, `IndependantLabel`, `Album`, `AlbumOr`, `AlbumPlatine`, `ArtisteSigné`, `ArtisteMajor`, `ArtisteIndependant`, `ArtisteLibreDroits`, `CollabInternationale`, etc.

**Individus :** soolking, feu, tif, flenn · universal (MajorLabel), believe (IndependantLabel) · albums guerilla, derniere_heure · certifications or et platine.

**10 requêtes** illustrent : assertion d'appartenance (R1-R4), classification d'albums (R5), récupération d'instances (R6-R7), subsomption (R8), disjonction (R9), et **démonstration de non-monotonie** (R10) — l'ajout d'un fait `signe_chez(flenn, universal)` invalide la dérivation `ArtisteLibreDroits(flenn)`, en parallèle avec le conflit d1/d2 observé en logique des défauts.

**Note OWA :** l'assertion explicite `tif:ArtisteLibreDroits` est nécessaire car l'absence de label ne se dérive pas automatiquement (équivalent CWA local).

### 6.3 Domaine 2 — Système judiciaire ([`DescriptionLogicJustice.java`](src/main/java/rcr/description/DescriptionLogicJustice.java))

Ontologie `http://rcr1/justice` — concepts : `Personne`, `ActeurJudiciaire`, `Magistrat`, `Juge`, `Procureur`, `Avocat`, `Accuse`, `Temoin`, `Infraction`, `Crime`, `Delit`, `Contravention`, `CrimeGrave`, `CourAssises`, `TribunalEnfants`, `AccuseMineur`, `AccuseRecidiviste`, `CasSimple`, `CasComplexe`, etc.

**11 requêtes** dont une section finale (R11) qui établit la **cohérence avec `DefaultLogicJustice`** : pour chaque accusé (ali, omar, karim), on confronte les conclusions DL aux extensions de Reiter et on commente la correspondance entre subsomption DL et conflits de défauts.

### 6.4 Implémentation (OWL API + HermiT)

```java
OWLOntologyManager mgr = OWLManager.createOWLOntologyManager();
OWLOntology onto = mgr.createOntology(IRI.create("http://rcr1/musique"));
OWLDataFactory f = mgr.getOWLDataFactory();

// TBox : Rappeur ≡ Artiste ⊓ ∃pratique.Rap ⊓ ∀produit.TitreRap
mgr.addAxiom(onto, f.getOWLEquivalentClassesAxiom(Rappeur,
    f.getOWLObjectIntersectionOf(Artiste,
        f.getOWLObjectSomeValuesFrom(pratique, Rap),
        f.getOWLObjectAllValuesFrom(produit, TitreRap))));

// ABox : assertion individu + propriété
mgr.addAxiom(onto, f.getOWLClassAssertionAxiom(RappeurAlgerien, soolking));
mgr.addAxiom(onto, f.getOWLObjectPropertyAssertionAxiom(signe_chez, soolking, universal));

// Raisonnement HermiT
OWLReasoner reasoner = new ReasonerFactory().createReasoner(onto);
boolean isSigned = reasoner.isEntailed(f.getOWLClassAssertionAxiom(ArtisteSigné, soolking));
Set<OWLNamedIndividual> libres = reasoner.getInstances(ArtisteLibreDroits, false).getFlattened();
```

---

## 7. Réseaux Sémantiques

### 7.1 Rappel théorique

Un **réseau sémantique** (Quillian 1968, Fahlman 1979) représente la connaissance par un **graphe étiqueté** :

- **Nœuds** : `Concept` (rectangle) ou `Individu` (ovale)
- **Arcs `is-a`** : liens d'héritage, déclinés en :
  - **Strict** (`S`) — règle absolue, aucune exception possible
  - **Non-strict** (`NS`) — règle typique (« en général », « typiquement »)
  - **Exception stricte** (`ES`) — annule une propriété héritée de manière définitive (lien barré dans le cours)
  - **Exception non-stricte** (`EN`) — annule typiquement une propriété héritée
- **Relations sémantiques ordinaires** : `produit`, `possède`, `agent`, `objet`, etc.
- **Modalités** (croire, savoir, vouloir…) : construites **structurellement** (cf. cours Chap.5 Exemples 9-10), avec des nœuds-instances `C1`, `N1` de classes modales `Croyances`, `Négations` reliés par les rôles `agent` / `objet` / `argument`. **Pas** de style d'arc dédié `M`.

**Moteur d'inférence** — *Propagation de Marqueurs (Fahlman 79)* en 3 étapes :
1. Marquer la source par `M1` et la requête par `M2`
2. Propager `M1` et `M2` à rebours des arcs `is-a`
3. Intersection `M1 ∩ M2` = ensemble des nœuds répondant à la question

### 7.2 Domaine 1 — Industrie musicale ([`SemanticNetworkExample.java`](src/main/java/rcr/semantic/SemanticNetworkExample.java))

19 nœuds (concepts : Personne, MondeMusical, Album, Droits, Croyances, Négations… ; individus : Soolking, Universal, Machi Bessah, Fan, C1, N1…), hiérarchie `Soolking → ArtisteSigne → Artiste → Personne/MondeMusicale`, exception stricte `ArtisteIndep --[¬sous-contrat]--> Label`, construction modale « les fans ne croient pas que les artistes ont des problèmes de droits » via `C1 (is-a Croyances) --[agent]--> Fan` + `C1 --[objet]--> N1 (is-a Negations)`.

**Questions :** Q1 « Quel artiste possède un album platine ? » (M1=AlbumPlatine, M2=Artiste → Soolking) · Q2 héritage `sous-contrat` avec démonstration du blocage par l'exception sur `ArtisteIndep`.

### 7.3 Domaine 2 — Système judiciaire

19 nœuds (Personne, ActeurJudiciaire, PartieProces, Culpabilité, Innocence présumée… ; Juge, Avocat, AvoDefense, Procureur, Témoin, Accusé, Récidiviste, C1, C2, N1), exception stricte `Recidiviste --[¬presume]--> Innocence` qui bloque la présomption d'innocence héritée d'`Accuse`.

**Modalités opposées (cf. cours Exemple 10) :** `C1 agent→Procureur, objet→Culpabilité` (le Procureur croit) **vs** `C2 agent→AvoDefense, objet→Culpabilité` nié par `N1 argument→C2` (l'Avocat Défense ne croit pas — N1 nie la **modalité** C2 elle-même, différent de nier l'objet).

### 7.4 Implémentation

Structure ad hoc Java (pas de dépendance externe) — classes internes `Node`, `Arc`, `SemanticNetwork` avec ajout typé (`addConcept` / `addIndividual` / `addIsA` / `addIsAns` / `addExcS` / `addExcNS` / `addRel` / `addNegRel`). BFS pour le calcul des ancêtres `is-a`, propagation de marqueurs trace pédagogique des `M1` et `M2` à chaque étape.

---

## 8. Interface graphique

L'interface JavaFX se compose de plusieurs écrans :

### Tableau de bord
5 cartes toutes actives en grille 2×3 (Sémantique centrée en bas sur 2 colonnes) : Logique Modale, Logique des Défauts, Logique Classique, Logique de Description, Réseaux Sémantiques.

### Fenêtre — Logique Modale
- **Onglets :** Industrie Musicale / Système Judiciaire
- **Graphe de Kripke interactif** : dessiné sur Canvas JavaFX — cliquer un nœud le sélectionne et affiche sa valuation
- **Évaluateur de formules** : choisir monde + opérateur (□/◊) + atome → résultat coloré en vert (VRAI) ou rouge (FAUX)
- **Propriétés de R** : réflexivité, transitivité, symétrie, sérialité calculées automatiquement
- **Sortie complète** : bouton "Tout évaluer" capture la sortie stdout de `run()` et l'affiche

### Fenêtre — Logique des Défauts
- **Onglets :** Système Judiciaire / Industrie Musicale
- Affichage de la théorie Δ = (W, D)
- **Bouton "Calculer toutes les extensions"** : lance le raisonneur RDL en thread background (pour ne pas bloquer l'UI) et affiche les extensions résultantes, conflits détectés et démonstration de non-monotonie

---

### Fenêtre — Logique Classique
- **Onglets** : Propositionnel · Prédicats — chacun avec deux sous-onglets (musique / justice)
- Affichage des clauses CNF / axiomes universels, puis lancement du raisonnement SAT4J ou du chaînage avant et affichage des résultats des questions

### Fenêtre — Logique de Description
- **Onglets** : Industrie musicale / Système judiciaire
- Affichage de la TBox + ABox sous forme synthétique
- Bouton **« Lancer HermiT »** qui exécute les 10-11 requêtes en thread background (le raisonneur peut prendre quelques secondes au premier appel) et affiche : appartenance d'individu, classification, subsomption, disjonction, démonstration de non-monotonie

### Fenêtre — Réseaux Sémantiques
- **Onglets** : Industrie musicale / Système judiciaire
- **Canvas interactif redimensionnable** : nœuds dessinés (rectangle = Concept, ovale = Individu) avec arcs colorés selon le type (vert = is-a strict, bleu pointillé = is-a non-strict, rouge croisé = exception stricte, violet = lien modal)
- **Zoom molette · Pan glisser-déposer · Reset**
- **Propagation de marqueurs interactive** : deux `ComboBox` (M1, M2) + bouton « Propager » qui colore les nœuds visités (`M1`=vert, `M2`=bleu, intersection=jaune) et affiche la sortie texte
- Bouton « Afficher sortie console » qui exécute `run()` en thread background

---

## 9. Guide de lancement

### Prérequis

| Outil | Version minimale | Vérification |
|---|---|---|
| Java JDK | 11 | `java -version` |
| Maven | 3.6+ | `mvn -version` |
| Connexion internet | — | Premier lancement uniquement (téléchargement des dépendances) |

### Lancement de l'interface graphique

```bash
cd projet_RCR
mvn javafx:run
```

> Le premier lancement télécharge les dépendances (~200 Mo). Les lancements suivants sont immédiats.

### Lancement en mode console (sans GUI)

```bash
mvn exec:java
```

Exécute les deux modules (modale + défauts) et affiche les résultats dans le terminal.

### Compilation seule

```bash
mvn compile
```

### Créer un JAR exécutable

```bash
mvn package
java -jar target/projet_RCR-1.0-SNAPSHOT-jar-with-dependencies.jar
```

> Note : le JAR console fonctionne tel quel. Pour le JAR GUI, JavaFX doit être sur le module-path.

---

## 10. Dépendances

```xml
<!-- Logiques formelles (modale, défauts, FOL…) -->
<dependency>
    <groupId>org.tweetyproject</groupId>
    <artifactId>tweety-full</artifactId>
    <version>1.27</version>
</dependency>

<!-- Interface graphique -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>17.0.2</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>17.0.2</version>
</dependency>

<!-- OWL API + HermiT (logique de description) -->
<dependency>
    <groupId>net.sourceforge.owlapi</groupId>
    <artifactId>owlapi-distribution</artifactId>
    <version>4.5.29</version>
</dependency>
<dependency>
    <groupId>net.sourceforge.owlapi</groupId>
    <artifactId>org.semanticweb.hermit</artifactId>
    <version>1.4.3.456</version>
</dependency>

<!-- SAT4J — déjà inclus dans tweety-full, utilisé pour la logique propositionnelle -->

```

---

## Auteurs

Projet réalisé dans le cadre du module RCR1 — Master 1 SII, USTHB.
