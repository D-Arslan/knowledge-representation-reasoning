# Fiche démo — Logique Classique

> **Chapitres du cours :** 1 (introduction, logique propositionnelle) et 2 (logique des prédicats)
> **Durée de démo conseillée :** 2-3 minutes

---

## Vue d'ensemble

| | |
|---|---|
| **Bibliothèque** | TweetyProject 1.27 (module `pl` pour propositionnel, `fol` pour prédicats) + SAT4J |
| **Moteur** | `SatReasoner` (SAT4J) pour la propositionnelle ; chaînage avant maison pour les prédicats |
| **Couleur GUI** | rouge (rose Catppuccin) |
| **Carte du dashboard** | `∀∃ Logique Classique` |

---

## Le scénario en 1 phrase

On modélise la même réalité (*« Soolking, signé chez un label, partage les royalties »*) deux fois : en propositionnel (atomes plats, résolus par SAT4J) puis en prédicats (axiomes universels, résolus par chaînage avant), pour mettre en évidence l'apport des quantificateurs.

---

## Domaines

### Propositionnel
- **Musique** : 4 atomes-artistes (Soolking, Feu, Tif, Flenn) + atomes auxiliaires (`signe_X`, `partage_X`, `royalties_X`, `expsolo_X`). 5 clauses CNF qui codent les règles de l'industrie.
- **Justice** : 3 atomes-accusés (Ali, Omar, Karim) + témoignages, récidive, peines. 5 clauses pour Q1–Q5, une 6ème (`c6`) optionnelle qui crée volontairement une contradiction sur Karim (Q6).

### Prédicats
- **Musique** : domaine fini `{soolking, feu, tif, flenn}`, 7 axiomes universels, chaîne `EstRappeurAlgerien → EstRappeur → EstArtiste → EstMusicien`.
- **Justice** : domaine `{ali, omar, karim}`, 8 axiomes (droits, peines, juridiction selon mineur/crime grave).

---

## Commandes

### Mode console (un onglet à la fois)
```bash
cd projet_RCR
mvn exec:java -Dexec.mainClass="rcr.classical.PropositionalLogicMusic"
mvn exec:java -Dexec.mainClass="rcr.classical.PredicateLogicMusic"
# ou Justice à la place de Music
```

### Mode GUI (recommandé pour la démo)
```bash
cd projet_RCR
mvn javafx:run
# → clic sur la carte "∀∃ Logique Classique"
```

La fenêtre s'ouvre avec **4 onglets** : `∧∨ Prop · Musique`, `∧∨ Prop · Justice`, `∀∃ Pred · Musique`, `∀∃ Pred · Justice`.

---

## Démo step-by-step

### Étape 1 — Présenter la KB propositionnelle (30 s)

Reste sur l'onglet par défaut **`∧∨ Prop · Musique`**.

À pointer dans le panneau gauche :
- Section **« Faits »** : les atomes positifs (soolking, feu, tif, flenn, signe_soolking, signe_feu, indep_feu)
- Section **« Clauses CNF »** : 5 clauses numérotées c1–c5

**À dire :** *« La KB est en forme normale conjonctive. SAT4J résout la satisfiabilité en temps NP, et on teste l'entailment KB ⊨ φ par réfutation : KB ⊨ φ ssi KB ∪ {¬φ} est UNSAT. »*

### Étape 2 — Test de satisfiabilité + entailment (45 s)

Panneau droit :
1. Clique **« ▶ KB est-elle satisfiable ? »** → ✅ **VRAI** apparaît dans l'historique
2. Dans le ComboBox « Test d'entailment », choisis `partage_soolking`
3. Clique **« ▶ Tester KB ⊨ φ »** → ✅ **VRAI**

**À dire :** *« Soolking signe chez un label, donc par c1 il partage les royalties. SAT4J le confirme par réfutation. »*

### Étape 3 — Provoquer une contradiction (45 s)

Dans la section **« Ajouter temporairement une formule »** :
1. Choisis `royalties_flenn`
2. **Coche** la case « Ajouter la négation »
3. Clique **« ▶ KB + {φ} satisfiable ? »** → ❌ **FAUX (contradiction)**

**À dire :** *« On ajoute `¬royalties_flenn` à la KB. Mais flenn est un fait, et la clause c4 (`¬flenn ∨ royalties_flenn`) impose `royalties_flenn`. Contradiction. C'est la logique classique : monotone et inflexible. Pour vivre avec ce conflit, il faudrait passer aux défauts. »*

### Étape 4 — Basculer sur les prédicats (45 s)

Clique sur l'onglet **`∀∃ Pred · Musique`**.

À pointer :
- Panneau gauche : faits dérivés **groupés par prédicat** (`EstRappeurAlgerien : ·soolking ·feu ·tif ·flenn` puis `EstRappeur : ⇒ soolking ⇒ feu ⇒ tif ⇒ flenn`). Le `·` = fait initial, le `⇒` = fait dérivé.

Test interactif (panneau droit) :
1. Dans « Test universel », choisis `EstMusicien`
2. Clique **« ▶ Tester ∀x P(x) »** → ✅ **VRAI**

**À dire :** *« Le chaînage avant a propagé toute la chaîne `EstRappeurAlgerien → EstRappeur → EstArtiste → EstMusicien` aux 4 individus. On vérifie que la propriété est universelle. »*

### (Bonus si temps) — Test existentiel

1. Dans « Test existentiel », choisis `ExperienceSolo`
2. → ✅ **VRAI**, témoin : `feu`

---

## Phrases clés à dire

- *« La logique classique sert de référence : elle est complète, décidable (pour la propositionnelle) et monotone. Toutes les autres logiques se positionnent par rapport à elle. »*
- *« SAT4J est intégré nativement à Tweety. On n'a pas eu à choisir entre les deux. »*
- *« La GUI calcule la dérivation au démarrage de l'onglet (`deriverTout()`), ce qui rend les requêtes ensuite instantanées. »*

---

## Questions probables du jury

**Q : Pourquoi le chaînage avant et pas backward chaining (résolution SLD) ?**
R : *Le domaine est fini et petit (4 individus, 7 axiomes), donc le forward chaining converge en quelques itérations. Sur un domaine infini ou avec des règles complexes, on passerait à de la résolution SLD comme dans Prolog.*

**Q : Quelle est la complexité de SAT ?**
R : *NP-complet en général, mais SAT4J implémente CDCL (Conflict-Driven Clause Learning) qui résout des instances de millions de clauses en pratique. Sur notre KB de 12 clauses, c'est instantané.*

**Q : Pourquoi avoir mis la propositionnelle et les prédicats dans le même module ?**
R : *Le cours les présente comme les « logiques classiques » du chapitre 1-2, en opposition aux « non-classiques » (modale, défauts, etc.). C'est cohérent pédagogiquement, et ça permet de comparer la même réalité avec et sans quantificateurs.*

---

## Repli en cas de problème

- Si le ComboBox n'a pas l'atome attendu → vérifier qu'on est sur le bon onglet (Music vs Justice).
- Si « Tout inférer » (bouton console) est trop lent → ne pas le lancer pendant la démo, c'est juste un fallback.
