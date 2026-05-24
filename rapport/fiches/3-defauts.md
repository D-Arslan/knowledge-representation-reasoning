# Fiche démo — Logique des Défauts

> **Chapitre du cours :** 4 (Logiques Non Monotones)
> **Durée de démo conseillée :** 3 minutes

---

## Vue d'ensemble

| | |
|---|---|
| **Bibliothèque** | TweetyProject 1.27 (module `rdl` : Reiter's Default Logic) |
| **Moteur** | `SimpleDefaultReasoner` (construction des extensions stables de Reiter via `DefaultProcessTree`) |
| **Couleur GUI** | violet (lavande Catppuccin) |
| **Carte du dashboard** | `⊣ Logique des Défauts` |

---

## Le scénario en 1 phrase

C'est **le module phare pour la non-monotonie** : on modélise des règles « typiques » (`d_1` : un artiste est libre de droits) qui peuvent être contredites par des exceptions (`d_2` : un artiste signé n'est PAS libre de droits), et on montre que l'ajout d'un fait invalide une conclusion précédente.

---

## Domaines

### Musique
Théorie $\Delta = (W, D)$ avec **7 défauts** $d_1$–$d_7$. Trois cas étudiés :

| Cas | Faits | Conflit | Résultat |
|---|---|---|---|
| **Soolking** | `EstArtiste + SigneLabel + AlbumSorti` | $d_1$ (LibreDroits) vs $d_2$ (¬LibreDroits) | **2 extensions** |
| **DZ_Master** | `EstArtiste + IndepLongtemps + NouveauContrat` | $d_1$ vs $d_6$ | **2 extensions** |
| **Atlas** | `EstArtiste + AlbumSorti + PlusDe2Ans` | aucun | **1 extension** (cas-démo non-monotonie) |

### Justice
Théorie avec **6 défauts** $d_1$–$d_6$. Trois cas :

| Accusé | Faits | Conflit | Résultat |
|---|---|---|---|
| **Ali** | `Accuse + Temoignage` | aucun ($d_1$, $d_5$ s'appliquent) | 1 extension : Innocent + Acquittement |
| **Omar** | `Accuse + Recidiviste` | $d_1$ (Innocent) vs $d_6$ (¬Innocent) | **2 extensions** + PeineLourde dans toutes |
| **Karim** | `Accuse + Mineur + CrimeGrave` | $d_3$ (TribunalEnfants) vs $d_4$ (¬TribunalEnfants) | **2 extensions** + Innocent dans toutes |

---

## Commandes

### Mode console
```bash
cd projet_RCR
mvn exec:java -Dexec.mainClass="rcr.defaut.DefaultLogicMusic"
mvn exec:java -Dexec.mainClass="rcr.defaut.DefaultLogicJustice"
```

### Mode GUI
```bash
cd projet_RCR
mvn javafx:run
# → clic sur la carte "⊣ Logique des Défauts"
```

La fenêtre s'ouvre avec **2 onglets** : Système Judiciaire (par défaut), Industrie Musicale.

---

## Démo step-by-step

### Étape 1 — Présenter la théorie (45 s)

Onglet **Système Judiciaire** (par défaut).

À pointer dans le panneau :
- Section **« Théorie Δ = (W, D) »**
- Les 6 défauts au format Reiter : `prerequis :: justification / conclusion`

**À dire :** *« Un défaut s'écrit α : β / γ : si la prérequis α est dérivée et que la justification β reste cohérente, on ajoute la conclusion γ. Quand deux défauts s'excluent (ex. d1 et d6 sur Omar), on obtient plusieurs extensions stables — c'est-à-dire plusieurs « mondes » de croyances possibles. »*

### Étape 2 — Lancer le calcul des extensions (45 s)

Clique sur **« ▶ Calculer toutes les extensions »**.

(Attendre 1-2 s — calcul de `getModels(dt.ground())` en thread background pour ne pas freezer la GUI.)

La sortie apparaît dans le TextArea. À pointer :
- **Omar** : 2 extensions, l'une avec `Innocent(omar)`, l'autre avec `¬Innocent(omar)`. Mais `PeineLourde(omar)` dans **toutes** les extensions (pas de conflit sur $d_2$).
- **Karim** : 2 extensions, l'une avec `TribunalEnfants(karim)`, l'autre avec `¬TribunalEnfants(karim)`. `Innocent(karim)` dans **toutes**.

**À dire :** *« Le raisonneur Reiter ne fait pas un choix — il calcule **tous** les ensembles stables possibles. À nous de décider lequel adopter (sceptique vs crédule). »*

### Étape 3 — La démo NON-MONOTONIE (1 min — *moment crucial*)

Bascule sur l'onglet **Industrie Musicale**.

À pointer dans la sortie générée (après avoir cliqué « Calculer les extensions ») :

**AVANT ajout du fait :**
- Atlas : 1 extension contenant `LibreDroits(atlas)`

**APRÈS ajout de `SigneLabel(atlas)` :**
- Atlas : 2 extensions, `LibreDroits(atlas)` n'est plus dans **toutes**

**À dire (lentement, clairement) :** *« Voici la non-monotonie en action. Avec la théorie initiale, on déduisait `LibreDroits(atlas)`. Mais quand on ajoute le fait `SigneLabel(atlas)`, le défaut $d_2$ entre en jeu et contredit $d_1$. La conclusion `LibreDroits` qu'on avait disparaît de certaines extensions. C'est exactement l'exemple Titi-autruche du cours, transposé à l'industrie musicale. »*

---

## Phrases clés à dire

- *« La logique classique est monotone : ajouter une information ne peut pas retirer une conclusion. La logique des défauts s'attaque exactement à cette limite. »*
- *« On a utilisé `SimpleDefaultReasoner` de Tweety qui implémente l'algorithme original de Reiter (1980). »*
- *« Une optimisation importante : on construit une théorie **par individu** plutôt qu'une théorie globale. Sinon le `DefaultProcessTree` explose combinatoirement. »*

---

## Questions probables du jury

**Q : Comment décidez-vous quelle extension adopter quand il y en a plusieurs ?**
R : *On ne décide pas — on les présente toutes. Deux approches existent : le raisonnement **sceptique** (on n'adopte que ce qui est dans **toutes** les extensions, ex. `PeineLourde(omar)`), et le raisonnement **crédule** (on adopte ce qui est dans **au moins une**, ex. `Innocent(omar)`). Notre GUI montre les deux.*

**Q : Pourquoi séparer les théories par individu ?**
R : *Le `DefaultProcessTree` de Tweety explore tous les ordres d'application des défauts. Avec 3 individus × 6 défauts dans une seule théorie groundée, ça devient exponentiel. En traitant chaque individu séparément, on garde 6 défauts par calcul et c'est tractable. C'est documenté dans le README et le rapport (chap. 5.4).*

**Q : Comment représentez-vous les exceptions ?**
R : *Par un défaut séparé qui conclut la négation, comme dans le cours. Par exemple, $d_6$ : `Recidiviste(x) :: ¬Innocent(x) / ¬Innocent(x)` est l'exception au défaut $d_1$. Quand Omar est récidiviste, les deux s'appliquent et donnent 2 extensions.*

**Q : Quel est le lien avec la propagation de marqueurs des réseaux sémantiques ?**
R : *Les deux formalismes capturent la non-monotonie. Mais les défauts sont des règles **logiques** (avec justification de cohérence), alors que les réseaux sémantiques modélisent l'héritage **structurel** avec exceptions explicites. Notre projet montre les deux pour le même cas (Récidiviste).*

---

## Repli en cas de problème

- Si le calcul des extensions est trop lent → c'est sûrement que la théorie n'est pas séparée par individu, vérifier le code
- Si l'extension n'apparaît pas → le `DefaultProcessTree` peut renvoyer un ensemble vide si la théorie est inconsistante ; vérifier qu'aucun défaut ne contredit W directement
