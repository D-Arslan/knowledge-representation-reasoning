# Fiches de démo — TP RCR1

> **Pour la soutenance orale.** Chaque fiche contient : description du domaine,
> commandes à lancer, démo step-by-step, phrases prêtes à dire, questions
> probables du jury, plan B en cas de pépin.

---

## Les 5 fiches

| # | Fiche | Chapitre du cours | Durée démo | Quoi montrer en priorité |
|---|---|---|---|---|
| 1 | [Logique Classique](1-classique.md) | 1 et 2 | 2-3 min | Satisfiabilité + contradiction provoquée |
| 2 | [Logique Modale (+ Temporelle)](2-modale.md) | 3 | 3-4 min | Canvas Kripke interactif + onglet temporel |
| 3 | [Logique des Défauts](3-defauts.md) | 4 (non-monotones) | 3 min | **Démo non-monotonie Atlas** |
| 4 | [Logique de Description](4-description.md) | 6 | 3-4 min | TreeView + déduction HermiT en direct |
| 5 | [Réseaux Sémantiques](5-reseaux-semantiques.md) | 5 | 4 min | **Démo exception en couleur (Récidiviste)** |

---

## Ordre de présentation recommandé

L'enchaînement ci-dessous suit l'**ordre du cours** et construit progressivement le récit (du classique vers le non-monotone, de la console vers le visuel) :

```
1. Classique  (intro)
   → "Voilà la référence : monotone, complète, mais inflexible."

2. Modale  (introduit la non-classique)
   → "On ajoute □ et ◇ pour parler de mondes possibles, et G/F/H/P pour le temps."

3. Défauts  (non-monotonie logique)
   → "Première démonstration de la non-monotonie : ajout d'un fait
      invalide une conclusion (Atlas)."

4. Description  (standard W3C, OWA)
   → "Standard industriel des ontologies. HermiT déduit ce qu'on n'a pas dit."

5. Réseaux Sémantiques  (non-monotonie visuelle)  ← APOTHÉOSE
   → "Démo de l'exception en couleur sur le canvas. C'est le module
      le plus immédiat à comprendre."
```

**Pourquoi finir par les Sémantiques :** c'est la démo la plus visuellement impactante. On termine sur un effet "wow" avec les nœuds qui passent en vert / rouge en direct.

---

## Budget de temps (soutenance de 15-20 min)

| Phase | Durée |
|---|---|
| Intro (objectifs, stack, dashboard) | 1 min |
| Démo Classique | 2 min |
| Démo Modale + Temporelle | 3-4 min |
| Démo Défauts | 3 min |
| Démo Description | 3 min |
| Démo Réseaux Sémantiques | 4 min |
| Synthèse comparative | 1-2 min |
| **Total démo** | **17-19 min** |
| Q/R | jusqu'à la fin |

---

## Setup 5 minutes avant la soutenance

- [ ] Terminal ouvert dans `projet_RCR/`
- [ ] Lancer `mvn javafx:run` → dashboard visible
- [ ] **Pré-cliquer sur chaque carte** une fois pour précharger HermiT et le reasoner OWL (gain de 2 s par onglet)
- [ ] Avoir le code source ouvert dans l'IDE en arrière-plan (au cas où on demande de montrer une classe)
- [ ] Rapport PDF (`rapport_min_RCR.zip` compilé) prêt à projeter
- [ ] Eau, calme, respiration

---

## Plan B en cas de pépin technique

| Pépin | Solution |
|---|---|
| GUI ne se lance pas | `mvn exec:java` pour le mode console — montre les mêmes résultats |
| HermiT bloque | Avoir un screenshot ou un copier-coller de sortie console préparé |
| Maven échoue | Avoir un JAR pré-buildé (`mvn package` la veille) à portée |
| Projecteur HS | Rapport PDF + code source sur clé USB en backup |

---

## Phrases d'ouverture et de fermeture

### Ouverture (à dire avant la démo)
*« Notre projet implémente les 5 formalismes du module RCR1 sur 2 domaines réels :
l'industrie musicale algérienne et le système judiciaire. La consigne demandait
au moins 2 TPs ; nous en avons fait 5, en gardant les mêmes individus
(Soolking, Ali, Karim…) à travers tous les formalismes pour comparer leur
expressivité. Tout est accessible via un dashboard JavaFX unifié.
On commence par la logique classique, puis on monte en expressivité. »*

### Fermeture (à dire après la dernière démo)
*« Aucun formalisme n'est universellement supérieur. La logique classique est
monotone et inflexible. La modale exprime nécessité / possibilité / temps.
Les défauts et les réseaux sémantiques apportent la non-monotonie de manière
complémentaire — les défauts par des règles logiques, les sémantiques par des
exceptions structurelles. La logique de description offre le standard W3C
avec l'OWA. Le choix dépend du domaine et des garanties souhaitées. »*

---

## Fichiers liés à la présentation

- **Fiches de démo** : ce dossier `rapport/fiches/`
- **Script complet minuté** : [`../PRESENTATION.md`](../PRESENTATION.md)
- **Rapport à envoyer par email** : `rapport_min_RCR.zip` (~14 pages)
- **Rapport complet** (en backup) : `rapport_RCR.zip` (~50 pages avec TikZ)
- **Code source** : branche `dev` du repo GitHub
