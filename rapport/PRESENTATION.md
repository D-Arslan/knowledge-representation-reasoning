# Présentation orale — TP RCR1

**Master 1 SII · USTHB · 2025-2026**
**Durée estimée :** 15-20 minutes (démo) + Q/R
**Consigne enseignante :** *« Il faut présenter l'exécution des TPs et non pas les rapports. »*

→ **80 % démo en direct sur la GUI, 20 % slides de support pour les transitions.**

---

## Setup avant la soutenance

1. Lancer un terminal dans `projet_RCR/`
2. Vérifier que tout compile : `mvn clean compile`
3. **Pré-lancer la GUI** pour ne pas attendre devant le jury : `mvn javafx:run`
4. Pré-cliquer sur chaque carte une fois pour précharger HermiT et le reasoner OWL (gain de 2-3 s par onglet)
5. Avoir le code source ouvert en parallèle dans l'IDE (au cas où l'on te demande de montrer une classe)
6. Avoir le rapport PDF prêt à projeter sur demande

---

## Slides de support (~ 5-6 slides texte)

> Idée : présentation simple type *reveal.js* ou même PowerPoint minimaliste. Ces slides
> servent uniquement de **transitions** entre les démos GUI. La star de la présentation
> reste l'application JavaFX.

### Slide 1 — Titre

```
RCR1 — Logiques Formelles
Représentation et raisonnement sur connaissances réelles

Master 1 SII · USTHB · 2025-2026
[Nom du binôme]
```

### Slide 2 — Objectifs du projet

```
• Implémenter 5 formalismes de représentation des connaissances
• Les appliquer à 2 domaines réels (musique algérienne · système judiciaire)
• Comparer leur expressivité et leurs limites
• Démontrer la non-monotonie en pratique
• Fournir une interface graphique unifiée et interactive
```

### Slide 3 — Stack technique

```
Java 11 · Maven 3.6+ · JavaFX 17
TweetyProject 1.27   → modale, défauts, propositionnelle (SAT4J), FOL
OWL API 4.5 + HermiT → logique de description
Code Java pur        → réseaux sémantiques (structure ad hoc)
```

### Slide 4 — Architecture en deux schémas

À gauche : l'arborescence des packages (5 modules métier + UI).
À droite : le dashboard JavaFX en grille 2×3 (image du Tikz du rapport ou capture d'écran).

### Slide 5 — Pour chaque formalisme, on montre :

```
1. Le problème modélisé (1 phrase)
2. La représentation (TBox / Kripke / défauts / réseau / CNF)
3. Le raisonnement en direct dans la GUI
4. Une question piège ou un cas limite (exception, non-monotonie, OWA…)
```

### Slide 6 — Conclusion

```
• 5 formalismes complémentaires, aucun n'est universellement supérieur
• Le choix dépend du domaine et des garanties souhaitées
• Démonstration explicite de la non-monotonie (défauts + réseaux sémantiques)
• Cohérence inter-formalismes sur les mêmes individus
```

---

# Script de démo en direct

> **À chaque étape, dire à voix haute ce que tu fais pour que le jury suive.**
> Les phrases « entre guillemets » sont à dire ; les listes à puces sont les
> actions à effectuer dans la GUI.

---

## 0. Ouverture (1 min)

« Notre projet implémente cinq formalismes de représentation des connaissances,
chacun appliqué à deux domaines : l'industrie musicale algérienne et le système
judiciaire. L'interface unifie l'accès aux raisonneurs en temps réel. »

- Montrer le dashboard avec ses 5 cartes
- Décrire l'organisation : *« chaque carte ouvre une fenêtre dédiée. »*

---

## 1. Logique Modale (3 min) — démo Kripke

**Pourquoi commencer par là :** c'est la GUI la plus visuelle (graphe de mondes
dessiné), elle pose le ton.

### Actions
1. Cliquer sur **Logique Modale**, onglet **Industrie Musicale**
2. Pointer le graphe de Kripke :
   - 4 mondes : `w1 label, w2 fan, w3 artiste, w4 critique`
   - Arcs : `(w1,w2), (w1,w3), (w3,w3), (w2,w4), (w4,w4)`
3. Évaluation interactive :
   - Choisir `w1`, opérateur `□`, atome `album_est_platine`
   - Lancer → **FAUX** (parce que w2 n'a pas album_est_platine)
   - Changer l'opérateur en `◊` → **VRAI**
4. Montrer le panneau **Propriétés de R** : non réflexive · non transitive · non symétrique · **sérielle**

### Phrase clé à dire
« Un modèle de Kripke est un triplet `⟨W, R, V⟩`. La nécessité $\Box \varphi$ est
vraie en $w$ ssi $\varphi$ est vraie dans **tous** les successeurs ; la possibilité
$\Diamond \varphi$ ssi $\varphi$ est vraie dans **au moins un**. Le clic sur un monde
affiche sa valuation, et le bouton 'Évaluer' applique récursivement la sémantique. »

---

## 2. Logique des Défauts (3 min) — démo non-monotonie

### Actions
1. Onglet **Logique des Défauts** → **Industrie Musicale**
2. Pointer la théorie : 7 défauts $d_1$–$d_7$, format `prerequis :: justification / conclusion`
3. Cliquer sur **Calculer toutes les extensions** (attendre 2 s)
4. **Souligner Q1 — Soolking** : `EstArtiste + SigneLabel + AlbumSorti`
   - $d_1$ conclut `LibreDroits`
   - $d_2$ conclut `¬LibreDroits`
   - → **2 extensions différentes**
5. **Souligner Q4 — Atlas (démo non-monotonie)** :
   - AVANT ajout de `SigneLabel(atlas)` : 1 extension avec `LibreDroits(atlas)`
   - APRÈS ajout : 2 extensions, `LibreDroits` n'est plus dans toutes
   - *« Ajouter un fait a invalidé une conclusion : c'est ça la non-monotonie. »*

### Phrase clé à dire
« Reiter modélise les règles par défaut comme `α : β / γ` : si la prérequis $\alpha$
est dérivable et la justification $\beta$ cohérente avec ce qu'on sait, on conclut
$\gamma$. Quand deux défauts s'excluent, on obtient plusieurs extensions stables.
Ajouter une information peut invalider une conclusion qu'on avait — c'est
la propriété fondamentale de **non-monotonie**. »

---

## 3. Réseaux Sémantiques (4 min) — pièce maîtresse

> **Le module phare en matière de visuel + démo de l'exception.**

### Actions

#### Démo musique (1 min)
1. Onglet **Réseaux Sémantiques** → **Industrie Musicale**
2. Survol d'un nœud (`Soolking`) → ses arcs sortants s'illuminent
3. Pointer les conventions : rectangle = Concept, ovale = Individu
4. Pointer une exception stricte : `ArtisteIndép --[¬sous-contrat]--> Label` (en rouge)

#### Démo Fahlman M1/M2 (1 min)
1. Panneau « Propagation de Marqueurs »
2. M1 = `AlbumPlatine`, M2 = `Artiste` → cliquer **Propager**
3. Les nœuds visités passent en vert (M1) / bleu (M2), l'intersection en jaune

#### Démo de l'exception en direct (2 min, le plus impactant)
1. Onglet **Système Judiciaire**
2. Panneau **« Requête avec exceptions »**
3. Source = `Accusé`, Relation = `presume`, Cible = `Innocence`
4. Cliquer **Évaluer la requête**
5. Pointer immédiatement les couleurs sur le canvas :
   - `Accusé` devient **vert** avec badge `INH` → présomption d'innocence héritée
   - `Récidiviste` devient **rouge** avec badge `¬` → **exception stricte bloque l'héritage**
6. Lire le panneau résultat textuel à droite :
   ```
   ✅ Héritages réussis :
     → Accusé  [presume] Innocence

   ❌ Bloqués par exception (¬presume) :
     → Récidiviste  ¬presume Innocence
   ```

### Phrase clé à dire
« Le moteur que nous avons implémenté ne se contente pas de propager des marqueurs
à la Fahlman : il évalue effectivement la relation cible en respectant les
exceptions. L'algorithme `inherits(n, r, t)` remonte la hiérarchie `is-a` en BFS,
et le premier arc rencontré décide — qu'il soit positif ou négatif. C'est le
**principe de proximité** : la spécialisation la plus proche prime sur la règle
générale. »

---

## 4. Logique de Description (3 min) — démo HermiT

> **Note d'honnêteté à mentionner :** OWA + assertions explicites pour libreDroits.

### Actions
1. Onglet **Logique de Description** → **Industrie Musicale**
2. Attendre la fin du chargement (« ⏳ Chargement… » → l'UI apparaît)
3. **Pointer la TreeView à gauche** :
   - *« Cette hiérarchie est calculée dynamiquement par HermiT, pas codée en dur. »*
   - Déplier `Artiste` → on voit `ArtisteMajor, ArtisteSigné, ArtisteIndependant, ArtisteLibreDroits…`
4. **Pointer la ListView des individus** :
   - `soolking : ArtisteMajor, ArtistePartageRoyalties, RappeurAlgerien`
   - *« HermiT a déduit ces classes — on n'a asserté que `RappeurAlgerien(soolking)`. »*
5. **Requête interactive — Instance**
   - `feu` ∈ `ArtisteLibreDroits` → **FAUX** (signe chez believe)
   - `tif` ∈ `ArtisteLibreDroits` → **VRAI** (asserté en ABox)
6. **Requête interactive — Subsomption**
   - `Rappeur` ⊑ `Artiste` → **VRAI**
   - `Rappeur` ⊑ `Personne` → **VRAI** (transitivité automatique)
7. **Requête interactive — Disjonction**
   - `ArtisteMajor` ⊓ `ArtisteIndependant` = ⊥ → **VRAI**
   - `Juge` ⊓ `Procureur` = ⊥ → **FAUX** (faiblesse intentionnelle du modèle)

### Phrase d'honnêteté à dire
« Pour `ArtisteLibreDroits`, sous l'hypothèse du monde ouvert d'OWL, l'absence
d'un fait n'est pas une négation. Nous avons donc inséré une assertion explicite
en ABox pour `tif` et `flenn`, ce qui revient à une fermeture du monde locale.
La démo R10 dans le rapport reproduit ce comportement de non-monotonie en
ajoutant `signe_chez(flenn, universal)` puis en relançant le raisonneur. »

---

## 5. Logique Classique (2 min) — démo SAT4J + chaînage avant

### Actions
1. Onglet **Logique Classique** → **Prop. Musique**
2. Pointer KB (faits + clauses CNF)
3. Bouton **« KB satisfiable ? »** → ✅ VRAI
4. Test entailment : sélectionner `partage_soolking`, **« Tester KB ⊨ φ »** → ✅ VRAI
5. **Provoquer une contradiction** :
   - Section « Ajouter temporairement », sélectionner `royalties_flenn`, cocher « négation »
   - Cliquer **« KB + {¬φ} satisfiable ? »** → ❌ FAUX (contradiction)
   - *« La logique classique est monotone et inconsistante. Pour vivre avec
     des contradictions, il faudrait les défauts ou les sémantiques paracohérentes. »*
6. Basculer sur **Pred. Musique** : montrer la liste des faits dérivés groupés par prédicat
7. Test universel `∀x EstMusicien(x)` → ✅ VRAI (chaîne `EstRappeurAlgerien → EstRappeur → EstArtiste → EstMusicien` appliquée aux 4 artistes)

---

## 6. Synthèse comparative (1-2 min)

Revenir au dashboard et résumer à l'oral le tableau comparatif :

| Formalisme | Monotonie | Hypothèse du monde | Gestion exceptions | Standard |
|---|---|---|---|---|
| Modale | Monotone | — | — | Kripke |
| Défauts | **Non-monotone** | CWA implicite | Multi-extensions | Reiter 80 |
| Classique | Monotone | CWA implicite | — | SAT / FOL |
| Description | Monotone | **OWA** | — | OWL 2 / W3C |
| Réseaux sémantiques | **Non-monotone** | — | **Exceptions ES/EN** | Quillian, Fahlman |

**Phrase finale** : *« Aucun formalisme n'est universellement supérieur. Le choix
dépend du domaine, des hypothèses sur le monde et des garanties de raisonnement
souhaitées. Notre projet illustre cette complémentarité sur un cas commun
— l'individu Récidiviste — qui est représenté différemment dans chaque formalisme
mais conduit à des conclusions cohérentes. »*

---

# Questions probables du jury et réponses préparées

### Q : « Pourquoi avoir choisi Tweety plutôt que d'écrire vos propres raisonneurs ? »

R : *« Tweety est la toolbox standard de la communauté pour les logiques non-classiques ;
elle nous a permis de comparer plusieurs formalismes avec une API homogène.
Pour les réseaux sémantiques, en revanche, nous avons écrit notre propre moteur
parce que Tweety n'offre pas de support natif pour les exceptions d'héritage —
ce qui nous a permis d'implémenter exactement le formalisme du cours
(styles S/NS/ES/EN, modalités structurelles). »*

### Q : « Quelle est la complexité de votre moteur d'héritage ? »

R : *« Chaque appel à `inherits(n, r, t)` est en O(|arcs| × niveau) à cause du
BFS qui à chaque nœud examine les arcs sortants. Pour `queryRelation(src, r, tgt)`
on itère sur tous les descendants de `src` croisés avec ceux de `tgt`, soit
O(|descendants(src)| × |descendants(tgt)| × |arcs|). Pour les domaines du TP
c'est instantané. »*

### Q : « Comment garantir que vos défauts donnent les bonnes extensions ? »

R : *« On délègue au `SimpleDefaultReasoner` de Tweety, qui implémente la
construction de Reiter. Pour éviter l'explosion combinatoire du `DefaultProcessTree`,
nous avons séparé les théories par individu — c'est une optimisation
documentée dans le rapport (chap. 3). »*

### Q : « La logique de description est monotone, mais votre R10 prétend démontrer
de la non-monotonie. Comment ? »

R : *« Honnêtement, ce n'est pas de la non-monotonie au sens strict — DL est bien
monotone. R10 construit en réalité **deux ontologies différentes** et compare leurs
conclusions. C'est plus une démonstration de l'effet de l'OWA et des assertions
explicites qu'une vraie non-monotonie. La vraie non-monotonie est dans la
logique des défauts et dans les réseaux sémantiques avec exceptions. »*

### Q : « Pourquoi avoir choisi le domaine musical algérien ? »

R : *« Pour rester dans un domaine réel et culturellement pertinent — les artistes
choisis (Soolking, Feu, Tif, Flenn) sont des rappeurs connus, ce qui rend les
exemples concrets. Le système judiciaire est l'autre domaine du fait qu'il
permet de modéliser naturellement des exceptions (récidiviste vs présumé innocent,
mineur vs majeur). »*

### Q : « Comment vous êtes-vous répartis le travail ? »

R : *« Le travail a été découpé par formalisme sur des branches Git dédiées
(`logique-modale`, `logique-defauts`, etc.), puis intégré sur `main` par
fusion. Le co-binôme a pris en charge les réseaux sémantiques sur la branche
`logique-semantique`. L'interface unifiée a été développée en intégration
continue à mesure que chaque formalisme arrivait. »*

---

# Checklist 5 minutes avant de passer

- [ ] Terminal ouvert dans `projet_RCR/`
- [ ] `mvn javafx:run` lancé, dashboard visible
- [ ] Chaque carte ouverte au moins une fois pour précharger HermiT / Tweety
- [ ] Rapport PDF disponible sur l'écran (en cas de question pointue)
- [ ] Code source ouvert dans l'IDE en arrière-plan
- [ ] Eau à portée de main
- [ ] Diapos slides sur écran principal si besoin

---

# Plan B en cas de problème technique

- **Si la GUI plante** : basculer sur le mode console (`mvn exec:java`) qui affiche les mêmes résultats
- **Si HermiT est trop lent** : montrer le code et le rapport, expliquer que le raisonneur a été pré-lancé
- **Si Maven échoue** : avoir un JAR pré-buildé (`mvn package` la veille) à portée
- **Si projecteur HS** : rapport PDF et code source sur clé USB en backup
