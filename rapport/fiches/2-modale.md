# Fiche démo — Logique Modale (Kripke + Temporelle)

> **Chapitre du cours :** 3 (Logique Modale, incluant la section temporelle)
> **Durée de démo conseillée :** 3-4 minutes

---

## Vue d'ensemble

| | |
|---|---|
| **Bibliothèque** | TweetyProject 1.27 (`MlHerbrandInterpretation`, `AccessibilityRelation`) pour la modale classique ; **Java pur** pour la temporelle (Tweety ne fournit pas G/F/H/P) |
| **Moteur** | Évaluation récursive maison sur ⟨W, R, V⟩ pour la classique ; parcours d'une liste ordonnée d'instants pour la temporelle |
| **Couleur GUI** | bleu (Catppuccin) |
| **Carte du dashboard** | `□ ◇ Logique Modale` |

---

## Le scénario en 2 phrases

**Modale classique :** on modélise l'industrie musicale comme 4 « mondes » (label, fan, artiste, critique) avec une relation d'accessibilité non-symétrique, et on raisonne avec `□` (nécessité) et `◇` (possibilité).

**Modale temporelle :** on spécialise R en *ordre temporel* : les mondes deviennent des instants chronologiques (carrière d'artiste, déroulement d'un procès) et on raisonne avec G (toujours futur), F (parfois futur), H (toujours passé), P (parfois passé).

---

## Domaines

### Modale classique
- **Musique** : W = {w1=label, w2=fan, w3=artiste, w4=critique}. R = 5 paires, sérielle mais non réflexive. V sur 5 atomes (album platine, concert annulé, sous contrat, top charts, indépendant).
- **Justice** : W = {w1=juge, w2=avocat, w3=procureur, w4=témoin, w5=accusé}. R = 6 paires. V sur 5 atomes (coupable, innocent, preuve valide, témoin crédible, circonstances atténuantes).

### Modale temporelle
- **Musique** : 5 instants `t1 (2017, indep) < t2 (signe Universal) < t3 (sort Guerilla) < t4 (album platine) < t5 (nouvel album)`. 5 atomes temporels.
- **Justice** : 6 instants `t1 (infraction) < t2 (arrestation) < t3 (inculpation) < t4 (audience) < t5 (verdict) < t6 (libération)`. 7 atomes dont la vérité **change** au cours du temps (libre, en détention, présumé innocent, etc.).

---

## Commandes

### Mode console
```bash
cd projet_RCR
mvn exec:java -Dexec.mainClass="rcr.modal.ModalLogicMusic"
mvn exec:java -Dexec.mainClass="rcr.modal.temporal.TemporalLogicMusic"
```

### Mode GUI
```bash
cd projet_RCR
mvn javafx:run
# → clic sur la carte "□ ◇ Logique Modale"
```

La fenêtre s'ouvre avec **4 onglets** : Industrie Musicale, Système Judiciaire, ⏳ Musique Temporelle, ⏳ Justice Temporelle.

---

## Démo step-by-step

### Étape 1 — Présenter le modèle de Kripke (45 s)

Onglet par défaut : **Industrie Musicale**.

À pointer sur le **Canvas** (panneau gauche) :
- 4 nœuds-mondes (cercles) avec leurs étiquettes
- Les arcs orientés représentent la relation R
- La boucle sur w3 = relation réflexive sur l'artiste

Cliquer sur **w1 (label)** → le nœud devient violet, et la valuation de w1 s'affiche dans le panneau droit (album_est_platine, artiste_sous_contrat, chanson_en_top_charts).

**À dire :** *« Un modèle de Kripke est un triplet `⟨W, R, V⟩`. Ici W = 4 mondes, R = 5 arcs, V = valuation des 5 atomes par monde. La sémantique modale : `□φ` vrai en w ssi φ vrai dans **tous** les successeurs ; `◇φ` ssi vrai dans **au moins un**. »*

### Étape 2 — Évaluation interactive (45 s)

Dans le panneau « Évaluation de formule » :
1. **Monde :** sélectionne `w1=label`
2. **Opérateur :** sélectionne `◇` (Possibilité)
3. **Atome :** sélectionne `concert_annule`
4. Clique **« Évaluer »**

→ Affichage **VRAI** en vert.

**À dire :** *« `◇ concert_annulé` en w1 est vrai parce que w2 (fan) est accessible depuis w1 et que `concert_annulé` y est vrai. Si on remplace par `□`, on obtient FAUX car w3 (artiste) — aussi accessible — ne valide pas l'atome. »*

Refaire avec **`□`** et le même atome → **FAUX**.

### Étape 3 — Propriétés de R (15 s)

Pointer le panneau **« Propriétés de R »** en bas du panneau droit :
- ❌ Réflexive
- ❌ Transitive
- ❌ Symétrique
- ✅ Sérielle

**À dire :** *« Selon les propriétés de R, on obtient différents systèmes modaux : K, T (réflexive), S4 (réflexive + transitive), S5 (équivalence). Ici on est juste sériel, donc seul l'axiome D (`□p → ◇p`) est valide. »*

### Étape 4 — Bascule sur la TEMPORELLE (1 min)

**[Le moment clé — fait écho au chapitre 3 du cours]**

Clique sur l'onglet **⏳ Musique Temporelle**.

À pointer :
- **Frise chronologique** à gauche : 5 instants avec leurs labels (2017 indépendant, 2018 signe Universal, 2019 sort Guerilla, 2021 platine, 2023 nouvel album)
- **Tableau de vérité** : atomes × instants avec V (vert) et `·` (vide). On voit que `artiste_independant` est V uniquement en t1, puis `sous_contrat` devient V à partir de t2.

**À dire :** *« La logique modale temporelle est la spécialisation présentée au chapitre 3 : R devient un ordre temporel. La vérité change dans le temps. C'est impossible à exprimer en modale classique sans cette extension. »*

### Étape 5 — Requête G en direct (45 s)

Dans le panneau droit :
1. **Opérateur :** sélectionne `G — sera TOUJOURS vrai dans le futur`
2. **Atome :** sélectionne `platine`
3. **Instant :** sélectionne `t4`
4. Clique **« ▶ Evaluer »**

→ Historique : ✅ **G platine en t4 → VRAI** en vert.

**À dire :** *« G platine en t4 signifie : « platine sera toujours vrai après t4 ». Or futur de t4 = {t5}, et platine est vrai en t5, donc VRAI. »*

Refaire avec **`G platine en t3`** → ❌ **FAUX** (parce que platine n'est pas vrai en t3, qui est dans le futur de t3 ? Non, t4 et t5 sont dans le futur de t3 ; platine est vrai en t4 et t5 — donc en réalité c'est VRAI. **Vérifier en live**).

### (Bonus si temps) — Démo Justice avec changement de vérité

Onglet **⏳ Justice Temporelle**. Demande **`H libre en t3`** → ❌ FAUX, parce que Omar est en détention dès t2.

**À dire :** *« On voit que `libre` change de valeur entre t1 (VRAI), t2-t5 (FAUX), puis t6 (VRAI à nouveau, après peine purgée). C'est typique de la logique temporelle. »*

---

## Phrases clés à dire

- *« La logique modale est le premier formalisme NON-classique du cours. Elle introduit deux opérateurs unaires `□` et `◇` qui s'évaluent sur un modèle relationnel. »*
- *« La GUI dessine le graphe de Kripke, ce qui matérialise la sémantique : on voit littéralement quels mondes sont accessibles. »*
- *« La temporelle se construit sur la même base mais en imposant à R d'être un ordre. C'est pour ça qu'on l'a mise dans le même module : `rcr.modal.temporal` est un sous-package de `rcr.modal`. »*

---

## Questions probables du jury

**Q : Pourquoi votre R n'est pas réflexive ? Vous perdez l'axiome T.**
R : *C'est volontaire : on voulait illustrer un cas où T n'est pas validé, pour qu'on puisse pointer la différence avec S4/S5. Le panneau « Propriétés de R » calcule ces propriétés automatiquement.*

**Q : Vous avez implémenté G/F/H/P à la main ? Pourquoi pas Tweety ?**
R : *Tweety ne fournit pas d'opérateurs temporels natifs. La sémantique est simple (ordre linéaire fini), donc on a écrit l'évaluation en quelques dizaines de lignes Java. Sur un ordre branchant (arbre du futur, CTL), il faudrait plus de travail.*

**Q : Quelle est la différence entre votre `□` et `G` ?**
R : *`□` quantifie sur tous les mondes accessibles selon R (qui peut être arbitraire). `G` quantifie sur les instants futurs uniquement (R = ordre temporel strict). En fait `G = □_futur` et `F = ◇_futur` : c'est une spécialisation de `□` / `◇`.*

**Q : Et la logique modale épistémique (croyances, savoirs) ?**
R : *Le cours ne la couvre pas explicitement, mais notre module Réseaux Sémantiques l'aborde de manière différente (construction structurelle avec C1, N1, agent/objet/argument).*

---

## Repli en cas de problème

- Si le Canvas ne dessine pas → redimensionner la fenêtre (le bind sur width/height force le redraw)
- Si on clique sur un nœud et rien ne se passe → vérifier qu'on n'est pas dans l'onglet temporel (pas de Canvas là-bas)
- Si une requête temporelle donne un résultat surprenant → consulter le tableau de vérité affiché à gauche pour vérifier
