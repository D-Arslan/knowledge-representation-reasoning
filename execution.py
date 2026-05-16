/home/mrx/knowledge-representation-reasoning/

── README.md
├── apache-maven-3.9.9-bin.tar.gz
├── pom.xml
├── src
│   └── main
│       ├── java
│       │   └── rcr
│       │       ├── Main.java
│       │       ├── MainApp.java
│       │       ├── classical
│       │       │   ├── PredicateLogicJustice.java
│       │       │   ├── PredicateLogicMusic.java
│       │       │   ├── PropositionalLogicJustice.java
│       │       │   └── PropositionalLogicMusic.java
│       │       ├── defaut
│       │       │   ├── DefaultLogicExample.java
│       │       │   ├── DefaultLogicJustice.java
│       │       │   └── DefaultLogicMusic.java
│       │       ├── description
│       │       │   └── DescriptionLogicExample.java
│       │       ├── modal
│       │       │   ├── ModalLogicExample.java
│       │       │   ├── ModalLogicJustice.java
│       │       │   └── ModalLogicMusic.java
│       │       ├── semantic
│       │       │   └── SemanticNetworkExample.java
│       │       └── ui
│       │           ├── DashboardController.java
│       │           ├── classical
│       │           │   └── ClassicalLogicController.java
│       │           ├── defaut
│       │           │   └── DefaultLogicController.java
│       │           └── modal
│       │               └── ModalLogicController.java
│       └── resources
│           └── rcr
│               ├── css
│               │   └── style.css
│               └── dashboard.fxml
└── target
    ├── classes
    │   └── rcr
    │       ├── Main.class
    │       ├── MainApp$1.class
    │       ├── MainApp.class
    │       ├── classical
    │       │   ├── PredicateLogicJustice.class
    │       │   ├── PredicateLogicMusic.class
    │       │   ├── PropositionalLogicJustice.class
    │       │   └── PropositionalLogicMusic.class
    │       ├── css
    │       │   └── style.css
    │       ├── dashboard.fxml
    │       ├── defaut
    │       │   ├── DefaultLogicExample.class
    │       │   ├── DefaultLogicJustice.class
    │       │   └── DefaultLogicMusic.class
    │       ├── description
    │       │   └── DescriptionLogicExample.class
    │       ├── modal
    │       │   ├── ModalLogicExample.class
    │       │   ├── ModalLogicJustice.class
    │       │   └── ModalLogicMusic.class
    │       ├── semantic
    │       │   └── SemanticNetworkExample.class
    │       └── ui
    │           ├── DashboardController.class
    │           ├── classical
    │           │   ├── ClassicalLogicController$1.class
    │           │   └── ClassicalLogicController.class
    │           ├── defaut
    │           │   ├── DefaultLogicController$1.class
    │           │   └── DefaultLogicController.class
    │           └── modal
    │               ├── ModalLogicController$1.class
    │               ├── ModalLogicController$2.class
    │               └── ModalLogicController.class
    ├── generated-sources
    │   └── annotations
    └── maven-status
        └── maven-compiler-plugin
            └── compile
                └── default-compile
                    ├── createdFiles.lst
                    └── inputFiles.lst





cd ~/knowledge-representation-reasoning

# 1. Le nouveau contrôleur UI
mkdir -p src/main/java/rcr/ui/semantic
cp /home/mrx/Desktop/repco/semantic/SemanticNetworkController.java src/main/java/rcr/ui/semantic/

# 2. DashboardController mis à jour (ajoute openSemantic)
cp /home/mrx/Desktop/repco/semantic/DashboardController.java src/main/java/rcr/ui/

# 3. dashboard.fxml mis à jour (carte verte Réseaux Sémantiques)
cp /home/mrx/Desktop/repco/semantic/dashboard.fxml src/main/resources/rcr/

# 4. style.css mis à jour (styles card-semantic, btn-semantic)
cp /home/mrx/Desktop/repco/semantic/style.css src/main/resources/rcr/css/

# Lancer
mvn javafx:run 2>/dev/null

Sommaire
Stratégie d'interro
chaîtr 1 
chapitre 2 
etc 
etc 
etc
Fiche de révision express

Stratégie d'interro:(exemple d'un autre module)
La professeure a indiqué que l'interro sera identique à celles des années précédentes. L'analyse comparée des 4 sessions disponibles fait ressortir une structure d'interro quasi figée : 2 exercices, parfois 3, qui combinent les notions selon ce schéma constant :

Exercice 1 : un texte de connaissances (zoologie, IA, énergie, écologie) à représenter par les modes les plus appropriés. La consigne précise systématiquement la grammaire ALC à utiliser : C → A | ⊤ | ⊥ | ¬C | C⊓D | ∀R.C | ∃R.C | C⊔D | ≥nR | ≤nR. Selon les indices linguistiques (« en général », « typiquement », « savoir », « croire », « est un », hiérarchies), l'étudiant choisit entre logique des défauts, logique modale, réseau sémantique partitionné, logique de description.
Exercice 2 : un modèle de Kripke (souvent temporel avec G, F, H, P) avec une liste de formules à valider, justification de chaque réponse selon la règle M, x ⊨ □φ ssi φ vraie en tout monde accessible depuis x.
Exercice 3 (parfois) : un calcul d'extensions de logique des défauts, avec discussion des particularités (théorie sans extension, à plusieurs extensions, non monotonie).
Classement des notions par fréquence en interro sur les 4 sessions :

★★★ Modèle de Kripke et évaluation de formules modales : 4/4 sessions
★★★ Logique modale temporelle (G, F, H, P) : 3/4 sessions, modèles 2018, 2022, 2024
★★★ Logique de description ALC, modélisation : 3/4 sessions, 2017, 2022, 2024
★★★ Logique des défauts, calcul d'extensions : 2/4 sessions, 2017, 2018, ainsi que la base théorique de l'écriture des défauts dans toutes les autres
★★★ Réseaux sémantiques partitionnés avec modalités et défauts : 2/4 sessions, 2017, 2022, plus toutes les modélisations textuelles
★★ Propagation de marqueurs : 1/4 sessions explicite, 2017, plus 5 exercices de TD
★★ Modalités épistémiques et doxastiques : présentes dans toutes les modélisations textuelles
★ Méthode des tableaux sémantiques en LD : 0/4 sessions visibles, mais pédagogiquement insistée en cours, à connaître au niveau procédure
★ Preuves formelles en logique propositionnelle : 0/4 sessions, présent uniquement en TD1
Stratégie pratique : maîtriser parfaitement la lecture d'un modèle de Kripke (règle de □ et de ◊, opérateurs temporels), savoir choisir le bon mode de représentation à partir des marqueurs textuels, savoir calculer Γ(E) et identifier les extensions. Le reste sert à comprendre, mais rapportera moins de points.