# Projet RCR — Logiques Formelles

**Module :** RCR1 — Représentation des Connaissances et Raisonnement  
**Niveau :** Master 1 SII — USTHB  
**Bibliothèque :** [TweetyProject 1.27](https://tweetyproject.org) · JavaFX 17.0.2 · Java 11

---

## Table des matières

1. [Présentation](#1-présentation)
2. [Architecture du projet](#2-architecture-du-projet)
3. [Logique Modale](#3-logique-modale)
4. [Logique des Défauts](#4-logique-des-défauts)
5. [Modules à venir](#5-modules-à-venir)
6. [Interface graphique](#6-interface-graphique)
7. [Guide de lancement](#7-guide-de-lancement)
8. [Dépendances](#8-dépendances)

---

## 1. Présentation

Ce projet implémente deux formalismes de représentation des connaissances :

| Formalisme | Domaine 1 | Domaine 2 | Statut |
|---|---|---|---|
| **Logique Modale** | Industrie musicale | Système judiciaire | ✅ Implémenté |
| **Logique des Défauts** | Industrie musicale | Système judiciaire | ✅ Implémenté |
| Réseaux Sémantiques | — | — | 🔜 À venir |
| Logique de Description | — | — | 🔜 À venir |

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
    │   ├── modal/
    │   │   ├── ModalLogicMusic.java    # Modèle de Kripke — musique
    │   │   ├── ModalLogicJustice.java  # Modèle de Kripke — justice
    │   │   └── ModalLogicExample.java  # Orchestrateur console
    │   ├── defaut/
    │   │   ├── DefaultLogicMusic.java    # Théorie des défauts — musique
    │   │   ├── DefaultLogicJustice.java  # Théorie des défauts — justice
    │   │   └── DefaultLogicExample.java  # Orchestrateur console
    │   ├── ui/
    │   │   ├── DashboardController.java         # Contrôleur du tableau de bord
    │   │   ├── modal/ModalLogicController.java  # Fenêtre logique modale
    │   │   └── defaut/DefaultLogicController.java # Fenêtre logique des défauts
    │   ├── semantic/SemanticNetworkExample.java  # Stub
    │   └── description/DescriptionLogicExample.java # Stub
    └── resources/rcr/
        ├── dashboard.fxml           # Interface du tableau de bord
        └── css/style.css            # Feuille de styles (thème sombre)
```

---

## 3. Logique Modale

### 3.1 Rappel théorique

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

### 3.2 Domaine 1 — Industrie musicale (`ModalLogicMusic.java`)

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

### 3.3 Domaine 2 — Système judiciaire (`ModalLogicJustice.java`)

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

### 3.4 Implémentation (API Tweety)

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

## 4. Logique des Défauts

### 4.1 Rappel théorique

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

### 4.2 Domaine 1 — Industrie musicale (`DefaultLogicMusic.java`)

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

### 4.3 Domaine 2 — Système judiciaire (`DefaultLogicJustice.java`)

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

### 4.4 Implémentation (API Tweety RDL)

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

## 5. Modules à venir

| Module | Description | API prévue |
|---|---|---|
| **Réseaux Sémantiques** | Hiérarchie de concepts avec héritage et exceptions | Tweety `graphs` |
| **Logique de Description** | Ontologies OWL, subsomption, classification | OWL API 4.5.29 + HermiT |

---

## 6. Interface graphique

L'interface JavaFX se compose de trois écrans :

### Tableau de bord
4 cartes 2×2 : 2 actives (Modale, Défauts) + 2 grisées (à venir).

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

## 7. Guide de lancement

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

## 8. Dépendances

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

<!-- OWL API + HermiT (pour la logique de description — à venir) -->
<dependency>
    <groupId>net.sourceforge.owlapi</groupId>
    <artifactId>owlapi-distribution</artifactId>
    <version>4.5.29</version>
</dependency>
```

---

## Auteurs

Projet réalisé dans le cadre du module RCR1 — Master 1 SII, USTHB.
