# Fiche démo — Logique de Description

> **Chapitre du cours :** 6 (Logiques de Description, ALC)
> **Durée de démo conseillée :** 3-4 minutes

---

## Vue d'ensemble

| | |
|---|---|
| **Bibliothèque** | OWL API 4.5.29 + HermiT 1.4.3.456 (raisonneur OWL DL par tableaux) |
| **Moteur** | HermiT — précompile la hiérarchie de classes et les assertions à l'ouverture de l'onglet |
| **Couleur GUI** | vert (Catppuccin) |
| **Carte du dashboard** | `⊑ ∃ Logique de Description` |

---

## Le scénario en 1 phrase

On modélise une ontologie OWL DL riche (30+ concepts, 10 individus) sur l'industrie musicale algérienne, et on laisse HermiT **déduire automatiquement** des classes qu'on n'a pas assertées (par ex. `soolking` devient `ArtisteMajor` parce qu'il signe chez Universal qui est un MajorLabel).

---

## Domaines

### Musique
- **TBox** : 35+ concepts (Artiste, Rappeur, RappeurAlgerien, Label, MajorLabel, Album, AlbumOr, ArtisteSigné, ArtisteIndépendant, ArtisteLibreDroits, ArtisteMajor, ArtistePartageRoyalties…) et 14 rôles (signe_chez, produit, contient, recoit, chante_en…).
- **ABox** : 10 individus (Soolking, Feu, Tif, Flenn, Universal, Believe, Guerilla, Derniere_heure, certifications Or et Platine) avec assertions de classe et de rôle.
- **10 requêtes R1–R10** dont une **démo de non-monotonie** (R10 sur Flenn — voir Question piège ci-dessous).

### Justice
- **TBox** : 28 concepts (Personne, ActeurJudiciaire, Magistrat, Juge, Procureur, Avocat, Accuse, AccuseMineur, AccuseRecidiviste, CasSimple, CasComplexe, Tribunal, CourAssises, TribunalEnfants…).
- **ABox** : Ali (présumé innocent + témoignage), Omar (récidiviste), Karim (mineur + crime grave) + juge_salem, avocat_riad, procureur_malik.
- **11 requêtes R1–R11**.

---

## Commandes

### Mode console
```bash
cd projet_RCR
mvn exec:java -Dexec.mainClass="rcr.description.DescriptionLogicMusic"
mvn exec:java -Dexec.mainClass="rcr.description.DescriptionLogicJustice"
```

### Mode GUI
```bash
cd projet_RCR
mvn javafx:run
# → clic sur la carte "⊑ ∃ Logique de Description"
```

Au démarrage de chaque onglet, **HermiT met ~2 secondes** à initialiser. La GUI affiche « ⏳ Chargement… » pendant ce temps.

> **Conseil pré-démo :** ouvre l'onglet Musique ET l'onglet Justice **5 minutes avant** la soutenance pour précharger HermiT. Ensuite tout sera instantané.

---

## Démo step-by-step

### Étape 1 — Présenter la TBox et l'ABox (45 s)

Onglet **Industrie Musicale**.

À pointer dans le panneau gauche :
- **Stats** en haut : `📊 35 classes · 10 individus · 480+ axiomes · raisonneur consistant ✓`
- **TreeView de la hiérarchie de classes** : *« Cette hiérarchie n'est pas codée en dur. C'est HermiT qui la calcule via `getSubClasses(c, true)`. Si on modifie la TBox dans le code, l'arbre s'actualise. »*
- Déplier `Personne → Artiste` : on voit `ArtisteMajor`, `ArtisteSigné`, `ArtisteIndependant`, `ArtisteLibreDroits`, `ArtisteAvecRoyalties`, `ArtistePartageRoyalties`, `ArtisteExperimente` avec leur nombre d'instances entre parenthèses.
- **ListView des individus** en bas : `soolking : ArtisteMajor, ArtistePartageRoyalties, RappeurAlgerien` — *« On n'a asserté que `RappeurAlgerien(soolking)`. Le reste est inféré par HermiT depuis les relations `signe_chez` et `produit`. »*

### Étape 2 — Requête d'instance (45 s)

Panneau droit, onglet **Instance** (par défaut).

1. **Individu a :** sélectionne `feu`
2. **Classe C :** sélectionne `ArtisteLibreDroits`
3. Clique **« ▶ Tester a : C »**

→ Historique : ❌ **feu : ArtisteLibreDroits → FAUX** (rouge)

**À dire :** *« feu signe chez believe, qui est un IndependantLabel, qui est un Label. Donc feu est dans `∃signe_chez.Label`, ce qui contredit la définition de `ArtisteLibreDroits ≡ Artiste ⊓ ¬∃signe_chez.Label`. »*

Refaire avec **tif** au lieu de feu → ✅ **VRAI** (asserté en ABox sous OWA — voir étape 5).

### Étape 3 — Requête de subsomption (30 s)

Onglet **Subsomption** dans le panneau droit.

1. **Classe C :** `Rappeur`
2. **Classe D :** `Personne`
3. Clique **« ▶ Tester C ⊑ D »**

→ ✅ **VRAI**

**À dire :** *« HermiT déduit la chaîne `Rappeur ⊑ Artiste ⊑ Personne` par transitivité, même si on n'a pas écrit cette inclusion directement. »*

### Étape 4 — Requête de disjonction (30 s)

Onglet **Disjonction**.

1. **Classe C :** `ArtisteMajor`
2. **Classe D :** `ArtisteIndependant`
3. Clique **« ▶ Tester C ⊓ D = ⊥ »**

→ ✅ **VRAI** (disjoints)

**À dire :** *« Les deux concepts sont disjoints par construction : `ArtisteMajor ≡ Artiste ⊓ ∃signe_chez.MajorLabel` et `ArtisteIndependant ≡ Artiste ⊓ ¬∃signe_chez.MajorLabel`. Leur intersection est vide. »*

### Étape 5 — Honnêteté sur l'OWA (30 s — *important*)

Onglet **Instance** à nouveau.

1. **Individu a :** sélectionne `tif`
2. **Classe C :** sélectionne `ArtisteLibreDroits`
3. → ✅ **VRAI**

**À dire :** *« Attention : ce VRAI est **asserté** en ABox, pas dérivé. Sous l'OWA (Open World Assumption), l'absence de `signe_chez(tif, X)` dans l'ABox ne veut pas dire qu'il n'a pas de label — il pourrait y en avoir un qu'on ignore. Pour forcer la conclusion, on a inséré explicitement `ArtisteLibreDroits(tif)` en ABox. C'est une fermeture du monde **locale**, équivalente à une CWA partielle. »*

### (Bonus) — Lancer toutes les requêtes R1–R10

Bouton vert en bas du panneau droit : **« ▶ Lancer les requêtes R1–R10 »**.

→ Sortie console capturée dans le TextArea : les 10 questions s'enchaînent avec leur résultat et justification. C'est le mode « démo complète ».

---

## Phrases clés à dire

- *« La logique de description est le standard W3C pour le Web sémantique. C'est elle qui permet d'écrire des ontologies OWL réutilisables. »*
- *« HermiT implémente un algorithme de tableaux pour ALC + extensions (rôles transitifs, nominaux). Il est complet et terminant. »*
- *« Notre `DescriptionLogicMusic.buildOntology()` est exposée publiquement pour que la GUI partage l'ontologie avec le mode console — sans dupliquer le code. »*

---

## Questions probables du jury

**Q : Pourquoi le `TitreRap` apparaît pour les albums `guerilla` et `derniere_heure` ?**
R : *Très bonne observation, c'est un effet de bord de l'axiome `Rappeur ≡ Artiste ⊓ ∃pratique.Rap ⊓ ∀produit.TitreRap`. Comme Soolking est un Rappeur et qu'il `produit guerilla`, le `∀produit.TitreRap` force HermiT à inférer que `guerilla` est un `TitreRap` — même si on voulait juste un Album. C'est exactement le genre de subtilité qu'un raisonneur DL révèle.*

**Q : Votre R10 démontre la non-monotonie. Mais la logique de description n'est-elle pas monotone ?**
R : *Excellente question, et oui DL **est** monotone — c'est honnêtement une « fausse » démo de non-monotonie. Ce qu'on fait dans R10, c'est créer **deux ontologies différentes** (avant/après ajout de `signe_chez(flenn, universal)`) et comparer leurs conclusions. La vraie non-monotonie est dans la logique des défauts et les réseaux sémantiques.*

**Q : Pourquoi HermiT et pas Pellet ou Fact++ ?**
R : *HermiT est packagé en JAR Java pur (compatible avec OWL API 4), il a un excellent support du fragment SROIQ (OWL 2 DL complet), et il est encore maintenu. Pellet est plus ancien et moins actif. Fact++ nécessite des bindings natifs.*

**Q : Quelle est la complexité de la subsomption en OWL DL ?**
R : *Pour ALC, c'est ExpTime-complet. Pour SROIQ (OWL 2 DL), c'est N2ExpTime-complet en théorie, mais en pratique HermiT est rapide sur la plupart des ontologies grâce à des optimisations (caching, blocking, etc.). Sur notre ontologie de 35 concepts, c'est instantané.*

---

## Repli en cas de problème

- Si HermiT met plus de 5 s à démarrer → vérifier qu'on n'est pas en cold start (Maven qui télécharge HermiT). Le faire avant la démo.
- Si « Recompile » d'Overleaf échoue sur le PDF → ce n'est pas lié à la GUI, mais au LaTeX.
- Si la TreeView est vide → l'ontologie est inconsistante. Vérifier `reasoner.isConsistent()` dans les stats en haut.
