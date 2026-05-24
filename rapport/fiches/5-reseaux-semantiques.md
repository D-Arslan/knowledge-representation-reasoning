# Fiche démo — Réseaux Sémantiques

> **Chapitre du cours :** 5 (Réseaux Sémantiques, Quillian 1968)
> **Durée de démo conseillée :** 4 minutes — **c'est la pièce maîtresse visuelle**

---

## Vue d'ensemble

| | |
|---|---|
| **Bibliothèque** | **Aucune** (Java pur) — Tweety n'offre pas la sémantique exigée par le cours USTHB (exceptions ES/EN, modalités structurelles) |
| **Moteur** | Implémentation maison : propagation de marqueurs (Fahlman 79) + **moteur d'héritage avec exceptions** (enum `Inheritance.INHERITED/BLOCKED/NOT_FOUND`) |
| **Couleur GUI** | peach (orange Catppuccin) |
| **Carte du dashboard** | `◎ Réseaux Sémantiques` |

---

## Le scénario en 2 phrases

C'est le module qui **démontre visuellement** la non-monotonie : on a des règles typiques (un Accusé est présumé innocent) qui peuvent être annulées par des **exceptions strictes** (un Récidiviste ne l'est plus). Lorsqu'on lance une requête, les nœuds passent en **vert (héritage réussi)** ou **rouge (bloqué par exception)** sur le canvas — c'est immédiat à comprendre, même pour quelqu'un qui ne connaît pas la théorie.

---

## Domaines

### Musique
- **19 nœuds** : 11 concepts (Personne, MondeMusical, Album, AlbumPlatine, Titre, Droits, Croyances, Négations, Organisation Musicale…) + 8 individus (Soolking, Universal, Machi Bessah, Fan, C1, N1…).
- **Exception clé** : `ArtisteIndep --[¬sous-contrat]--> Label` (exception stricte qui annule l'héritage du `sous-contrat` venant de `ArtisteSigne`).
- **Modalité** : « les fans ne croient PAS que les artistes ont des droits » → construction structurelle avec C1 (is-a Croyances) + N1 (is-a Négations).

### Justice (la démo phare)
- **19 nœuds** : 9 concepts (Personne, ActeurJudiciaire, PartieProces, Culpabilité, Innocence présumée, Preuve, Condamnation, Croyances, Négations) + 10 individus (Juge, Avocat, AvocatDéfense, Procureur, Témoin, Accusé, Récidiviste, C1, C2, N1).
- **Exception clé** : `Recidiviste --[¬presume]--> Innocence` — bloque la présomption d'innocence héritée d'Accusé.
- **Modalités opposées** : `C1 agent→Procureur, objet→Culpabilité` (le Procureur croit) vs `N1 argument→C2 agent→AvocatDéfense, objet→Culpabilité` (l'Avocat de la défense ne croit pas — N1 nie la modalité C2 entière).

---

## Commandes

### Mode console
```bash
cd projet_RCR
mvn exec:java -Dexec.mainClass="rcr.semantic.SemanticNetworkExample"
```

### Mode GUI
```bash
cd projet_RCR
mvn javafx:run
# → clic sur la carte "◎ Réseaux Sémantiques"
```

La fenêtre s'ouvre avec **2 onglets** : Industrie Musicale, Système Judiciaire.

---

## Démo step-by-step

### Étape 1 — Présenter le canvas (45 s)

Onglet par défaut : **Industrie Musicale**.

À pointer :
- **Layout hiérarchique auto-calculé** : les nœuds ne sont pas placés à la main. Un algorithme calcule les composantes connexes via `is-a`, fusionne les singletons, puis distribue les nœuds par niveau.
- **Arcs en courbes de Bézier** (pas de lignes droites) — moins de spaghetti visuel.
- **Légende** dans le panneau droit : vert = is-a strict, bleu pointillé = non-strict, rouge croisé = exception, jaune = relation ordinaire, violet = modal.
- **Hover** : passe la souris sur `Soolking` → ses arcs sortants (`is-a → ArtisteSigne`, `possede → AlbumPlatine`, `produit → MachiBessah`, `signe-chez → Universal`) s'illuminent.

**À dire :** *« Un réseau sémantique est un graphe de Quillian (1968). Les nœuds = concepts ou individus, les arcs = relations. La hiérarchie `is-a` permet l'héritage. Les **exceptions strictes** (style ES dans le cours) permettent d'annuler une propriété héritée. »*

### Étape 2 — Propagation de marqueurs M1/M2 (Fahlman 79) (45 s)

Dans le panneau droit, section **« Propagation de Marqueurs »** :
1. **M1 :** sélectionne `AlbumPlatine`
2. **M2 :** sélectionne `Artiste`
3. Clique **« ▶ Propager les marqueurs »**

→ Sur le canvas :
- `AlbumPlatine` et `Album` passent en **vert M1**
- `Artiste`, `Personne`, `MondeMusicale` passent en **bleu M2**

**À dire :** *« C'est l'algorithme à 3 étapes du cours : étape 1 = marquer source et requête, étape 2 = propager à rebours des arcs is-a, étape 3 = chercher l'intersection. Mais cet algorithme seul ne répond pas à la vraie question (« quel artiste possède un album platine ? ») — il faut aller plus loin. »*

### Étape 3 — La DÉMO PHARE : exception en direct (1 min 30 — *moment crucial*)

Bascule sur l'onglet **Système Judiciaire**.

Dans le panneau droit, section **« Requête avec exceptions »** :
1. **Catégorie source :** sélectionne `Accuse`
2. **Relation cible R :** sélectionne `presume`
3. **Catégorie cible :** sélectionne `Innocence`
4. Clique **« ▶ Évaluer la requête »**

→ **Sur le canvas** (à montrer en grand) :
- `Accusé` devient **VERT** avec un badge `INH` → la présomption d'innocence s'applique
- `Récidiviste` devient **ROUGE** avec un badge `¬` → **bloqué par l'exception stricte**

→ **Dans le panneau résultat** à droite :
```
✅ Héritages réussis :
  → Accusé  [presume] Innocence

❌ Bloqués par exception (¬presume) :
  → Récidiviste  ¬presume Innocence
```

**À dire (lentement, en pointant les couleurs) :** *« Voici exactement ce que le cours appelle l'exception stricte. Le Récidiviste hérite de `Accusé` via `is-a`, donc en théorie il devrait hériter de la `presume Innocence`. Mais l'arc `Recidiviste --[¬presume]--> Innocence` est un **lien barré** : il annule l'héritage. Le moteur que nous avons implémenté applique le **principe de proximité** : la spécialisation la plus proche (l'exception sur Récidiviste) prime sur la règle générale (la présomption d'Accusé). »*

### Étape 4 — Mentionner les modalités (15 s)

Pointer rapidement sur le canvas les nœuds **C1, C2, N1** et les arcs `agent`, `objet`, `argument` en violet.

**À dire :** *« Le cours interdit de représenter les modalités par un style d'arc. À la place, on les construit **structurellement** avec des nœuds-instances de classes modales `Croyances` et `Négations`. C1 « est une croyance », C1 « a pour agent » le Procureur, C1 « a pour objet » la Culpabilité. Pour nier la croyance de l'Avocat de la Défense, N1 prend C2 entier comme argument — c'est la construction partitionnée de l'Exemple 10 du cours. »*

---

## Phrases clés à dire

- *« C'est le seul module où on a écrit le moteur entièrement à la main. Tweety n'a pas la sémantique exigée par le cours (styles S/NS/ES/EN, modalités partitionnées). »*
- *« Le moteur `inherits(n, r, t)` effectue un BFS depuis n sur les arcs `is-a`. À chaque niveau, le **premier arc** rencontré décide — qu'il soit positif (INHERITED) ou négatif (BLOCKED). C'est le principe de proximité. »*
- *« La GUI ne se contente pas d'afficher : elle est **interactive**. L'utilisateur peut tester ses propres requêtes en sélectionnant source/relation/cible. »*

---

## Questions probables du jury

**Q : Pourquoi ne pas avoir utilisé Tweety pour les réseaux sémantiques ?**
R : *Tweety a un module `graphs` mais il offre seulement la structure de base (nœuds, arcs). Il n'a pas de notion d'exceptions strictes/non-strictes ni de construction partitionnée pour les modalités, qui sont les contributions du cours USTHB. Implémenter le moteur en Java pur ne représente que ~300 lignes et nous laisse maîtres de la sémantique.*

**Q : Que se passe-t-il si on a plusieurs exceptions au même niveau ?**
R : *Notre BFS visite les arcs is-a dans l'ordre d'insertion. Le premier arc décideur rencontré gagne. C'est une simplification — un vrai système devrait raisonner sur la spécificité (algorithme de Touretzky par exemple). Pour le TP, c'est suffisant.*

**Q : Quelle est la différence avec la logique des défauts ?**
R : *Les défauts sont des règles logiques avec des justifications de cohérence. Les réseaux sémantiques sont graphiques et utilisent des exceptions structurelles (liens barrés). Mais les deux capturent la non-monotonie. Notre projet montre les deux sur le même cas (Récidiviste) pour comparer.*

**Q : Pourquoi les modalités ne sont pas un style d'arc ?**
R : *Le cours est très explicite là-dessus (chapitre 5, exemples 9 et 10) : « Les modalités sont des nœuds, pas des styles d'arcs ». Cela permet de nier la modalité elle-même (via N1 argument→C2), ce qu'un style d'arc dédié ne permettrait pas.*

**Q : La GUI est très soignée. Combien de temps ?**
R : *Le rendu visuel (Bézier, hover, layout auto-calculé) représente environ 400 lignes de code Canvas en JavaFX. C'est un investissement raisonnable vu que ça matérialise visuellement la sémantique du cours.*

---

## Repli en cas de problème

- Si les nœuds se chevauchent dans le canvas → redimensionner la fenêtre (le layout est responsive)
- Si on ne voit pas les couleurs INH/BLK après la requête → vérifier qu'on est dans **« Requête avec exceptions »** (panneau du bas) et pas dans **« Propagation »** (panneau du haut)
- Si un nœud manque (ex: `Innocence` pas dans le ComboBox cible) → relancer la GUI ; le réseau est construit à l'ouverture de l'onglet
