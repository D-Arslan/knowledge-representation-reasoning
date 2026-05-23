package rcr.ui.description;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import rcr.description.DescriptionLogicJustice;
import rcr.description.DescriptionLogicMusic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller JavaFX — Logique de Description (refonte interactive).
 *
 * Refonte par rapport à la version précédente :
 *   - Charge l'ontologie via DescriptionLogicMusic/Justice.buildOntology() au
 *     démarrage de chaque onglet (thread background pour ne pas freezer la GUI).
 *   - TreeView de la hiérarchie de classes calculée dynamiquement par HermiT
 *     (getSubClasses) au lieu d'un TextArea hardcodé.
 *   - ListView des individus avec leur type principal.
 *   - 3 modes de requête interactive : Instance (a:C), Subsomption (C ⊑ D),
 *     Disjonction (C ⊓ D = ⊥) — chaque requête lancée s'ajoute à l'historique
 *     avec son résultat coloré.
 *   - Bouton « Lancer toutes les requêtes prédéfinies » qui capture la sortie
 *     console de DescriptionLogicMusic/Justice.run() pour la démo.
 */
public class DescriptionLogicController {

    public BorderPane buildUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#1e1e2e;");

        Label header = new Label("Logique de Description — OWL API 4.5 · HermiT");
        header.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#cdd6f4;"
                + "-fx-padding:14 24;-fx-background-color:#181825;");
        root.setTop(header);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color:#1e1e2e;");

        Tab tabMusic   = new Tab("  Industrie Musicale  ");
        tabMusic.setContent(buildPane(true));

        Tab tabJustice = new Tab("  Système Judiciaire  ");
        tabJustice.setContent(buildPane(false));

        tabs.getTabs().addAll(tabMusic, tabJustice);
        root.setCenter(tabs);
        return root;
    }

    // =========================================================
    //  PANE PRINCIPAL D'UN DOMAINE
    // =========================================================
    private BorderPane buildPane(boolean isMusic) {
        BorderPane pane = new BorderPane();
        pane.setStyle("-fx-background-color:#1e1e2e;");

        // ── Bannière de chargement (visible avant que HermiT soit prêt) ──
        Label loading = new Label("⏳  Chargement de l'ontologie OWL et initialisation HermiT…");
        loading.setStyle("-fx-text-fill:#fab387;-fx-font-size:14px;-fx-padding:40;"
                + "-fx-font-style:italic;");
        VBox loadingBox = new VBox(loading);
        loadingBox.setAlignment(javafx.geometry.Pos.CENTER);
        loadingBox.setStyle("-fx-background-color:#1e1e2e;");
        pane.setCenter(loadingBox);

        // Chargement asynchrone — HermiT peut prendre 1-3s au premier appel
        Task<DLContext> task = new Task<>() {
            @Override protected DLContext call() {
                OWLOntology onto = isMusic
                        ? DescriptionLogicMusic.buildOntology()
                        : DescriptionLogicJustice.buildOntology();
                OWLReasoner reasoner = new ReasonerFactory().createReasoner(onto);
                reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY,
                        InferenceType.CLASS_ASSERTIONS);
                return new DLContext(onto, reasoner, isMusic);
            }
        };
        task.setOnSucceeded(ev -> pane.setCenter(buildInteractivePane(task.getValue())));
        task.setOnFailed(ev -> {
            loading.setText("❌  Erreur d'initialisation : " + task.getException().getMessage());
            loading.setStyle("-fx-text-fill:#f38ba8;-fx-font-size:13px;-fx-padding:40;");
            task.getException().printStackTrace();
        });
        new Thread(task, "dl-init-" + (isMusic ? "music" : "justice")).start();

        return pane;
    }

    // =========================================================
    //  PANE INTERACTIF (une fois HermiT prêt)
    // =========================================================
    private SplitPane buildInteractivePane(DLContext ctx) {
        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.HORIZONTAL);
        split.setDividerPositions(0.48);
        split.setStyle("-fx-background-color:#1e1e2e;");

        split.getItems().addAll(
                buildExplorerPane(ctx),
                buildQueryPane(ctx)
        );
        return split;
    }

    // =========================================================
    //  PANE GAUCHE — Exploration ontologie
    // =========================================================
    private VBox buildExplorerPane(DLContext ctx) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(14));
        vbox.setStyle("-fx-background-color:#1e1e2e;");

        // Stats
        int nbClasses = ctx.onto.getClassesInSignature().size();
        int nbIndividuals = ctx.onto.getIndividualsInSignature().size();
        int nbAxioms = ctx.onto.getAxiomCount();
        Label stats = new Label(String.format(
                "📊  %d classes  ·  %d individus  ·  %d axiomes  ·  raisonneur %s",
                nbClasses, nbIndividuals, nbAxioms,
                ctx.reasoner.isConsistent() ? "consistant ✓" : "INCONSISTANT ✗"));
        stats.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:11.5px;-fx-padding:4 0;");

        // ── TreeView TBox ──
        Label lblTbox = section("Hiérarchie des classes (TBox)");
        TreeView<String> treeView = buildClassHierarchy(ctx);
        treeView.setStyle("-fx-background-color:#181825;-fx-control-inner-background:#181825;"
                + "-fx-text-fill:#cdd6f4;");
        treeView.setShowRoot(false);
        VBox.setVgrow(treeView, Priority.ALWAYS);

        // ── ListView ABox ──
        Label lblAbox = section("Individus (ABox)");
        ListView<String> indivList = new ListView<>();
        indivList.setStyle("-fx-background-color:#181825;-fx-control-inner-background:#181825;"
                + "-fx-text-fill:#cdd6f4;");
        List<OWLNamedIndividual> sortedIndivs = ctx.onto.getIndividualsInSignature().stream()
                .sorted(Comparator.comparing(i -> i.getIRI().getFragment()))
                .collect(Collectors.toList());
        for (OWLNamedIndividual ind : sortedIndivs) {
            String types = ctx.reasoner.getTypes(ind, true).getFlattened().stream()
                    .filter(c -> !c.isOWLThing())
                    .map(c -> c.getIRI().getFragment())
                    .sorted()
                    .collect(Collectors.joining(", "));
            String display = ind.getIRI().getFragment()
                    + (types.isEmpty() ? "" : "  :  " + types);
            indivList.getItems().add(display);
        }
        indivList.setPrefHeight(160);

        vbox.getChildren().addAll(stats, lblTbox, treeView, lblAbox, indivList);
        return vbox;
    }

    private TreeView<String> buildClassHierarchy(DLContext ctx) {
        TreeItem<String> rootItem = new TreeItem<>("owl:Thing");
        buildClassChildren(rootItem, ctx.reasoner.getTopClassNode().getRepresentativeElement(),
                ctx.reasoner, new HashSet<>());
        rootItem.setExpanded(true);
        // Ouvrir 2 niveaux
        for (TreeItem<String> child : rootItem.getChildren()) child.setExpanded(true);
        return new TreeView<>(rootItem);
    }

    private void buildClassChildren(TreeItem<String> parent, OWLClass cls,
                                     OWLReasoner reasoner, Set<OWLClass> visited) {
        Set<OWLClass> subs = reasoner.getSubClasses(cls, true).getFlattened();
        // Tri alphabétique pour stabilité
        List<OWLClass> sorted = subs.stream()
                .filter(c -> !c.isOWLNothing())
                .filter(visited::add)
                .sorted(Comparator.comparing(c -> c.getIRI().getFragment()))
                .collect(Collectors.toList());
        for (OWLClass sub : sorted) {
            // Compter les instances directes pour l'affichage
            int nbInstances = reasoner.getInstances(sub, true).getFlattened().size();
            String label = sub.getIRI().getFragment()
                    + (nbInstances > 0 ? "  (" + nbInstances + " ind.)" : "");
            TreeItem<String> item = new TreeItem<>(label);
            parent.getChildren().add(item);
            buildClassChildren(item, sub, reasoner, visited);
        }
    }

    // =========================================================
    //  PANE DROIT — Requêtes interactives
    // =========================================================
    private VBox buildQueryPane(DLContext ctx) {
        VBox vbox = new VBox(12);
        vbox.setPadding(new Insets(14));
        vbox.setStyle("-fx-background-color:#1e1e2e;");

        // Listes triées
        List<String> classNames = ctx.onto.getClassesInSignature().stream()
                .map(c -> c.getIRI().getFragment())
                .sorted()
                .collect(Collectors.toList());
        List<String> indNames = ctx.onto.getIndividualsInSignature().stream()
                .map(i -> i.getIRI().getFragment())
                .sorted()
                .collect(Collectors.toList());

        // ── Historique des requêtes ──
        Label lblHist = section("Historique des requêtes");
        TextFlowLog historyLog = new TextFlowLog();
        historyLog.append("→ Lance une requête ci-dessous pour voir le résultat.", "#6c7086");
        ScrollPane histScroll = new ScrollPane(historyLog.flow);
        histScroll.setFitToWidth(true);
        histScroll.setStyle("-fx-background-color:#181825;-fx-background:#181825;");
        // L'historique prend tout l'espace restant après les autres sections fixes
        histScroll.setPrefHeight(180);
        VBox.setVgrow(histScroll, Priority.ALWAYS);

        // ── Onglets des 3 modes ──
        TabPane queryTabs = new TabPane();
        queryTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        queryTabs.setStyle("-fx-background-color:#1e1e2e;");
        // Garantir que tout le contenu d'un onglet (ComboBox + bouton) est visible
        queryTabs.setMinHeight(290);
        queryTabs.setPrefHeight(290);

        Tab tInstance = new Tab("  Instance  ",
                buildInstanceTab(ctx, indNames, classNames, historyLog));
        Tab tSubsumption = new Tab("  Subsomption  ",
                buildSubsumptionTab(ctx, classNames, historyLog));
        Tab tDisjoint = new Tab("  Disjonction  ",
                buildDisjointTab(ctx, classNames, historyLog));
        queryTabs.getTabs().addAll(tInstance, tSubsumption, tDisjoint);

        // ── Bouton "Toutes les requêtes prédéfinies" (capture console) ──
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#45475a;");

        Label lblPredef = section("Requêtes prédéfinies (R1–R10)");
        Label lblPredefDesc = new Label(
                "Lance l'intégralité des requêtes du domaine (incluant la démo de\n"
              + "non-monotonie en R10) et affiche la sortie console complète.");
        lblPredefDesc.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:11px;-fx-wrap-text:true;");

        TextArea predefOut = new TextArea();
        predefOut.setEditable(false);
        predefOut.setPrefRowCount(8);
        predefOut.setStyle("-fx-control-inner-background:#181825;-fx-background-color:#181825;"
                + "-fx-text-fill:#cdd6f4;-fx-font-family:Monospaced;-fx-font-size:10.5px;");
        Button btnPredef = new Button("▶  Lancer les requêtes R1–R10");
        btnPredef.setMaxWidth(Double.MAX_VALUE);
        btnPredef.setStyle("-fx-background-color:#a6e3a1;-fx-text-fill:#1e1e2e;"
                + "-fx-font-weight:bold;-fx-font-size:12px;-fx-padding:8 0;");
        btnPredef.setOnAction(e -> {
            btnPredef.setDisable(true);
            predefOut.setText("Exécution en cours…");
            Task<String> t = new Task<>() {
                @Override protected String call() { return captureRun(ctx.isMusic); }
            };
            t.setOnSucceeded(ev -> { predefOut.setText(t.getValue()); btnPredef.setDisable(false); });
            t.setOnFailed(ev   -> { predefOut.setText("Erreur : " + t.getException()); btnPredef.setDisable(false); });
            new Thread(t, "dl-predef").start();
        });

        Button btnClearHist = new Button("✕  Effacer l'historique");
        btnClearHist.setMaxWidth(Double.MAX_VALUE);
        btnClearHist.setStyle("-fx-background-color:#45475a;-fx-text-fill:#cdd6f4;"
                + "-fx-font-size:11px;-fx-padding:6 0;");
        btnClearHist.setOnAction(e -> {
            historyLog.clear();
            historyLog.append("→ Historique effacé.", "#6c7086");
        });

        vbox.getChildren().addAll(
                section("Requête interactive (HermiT en direct)"),
                queryTabs,
                lblHist, histScroll, btnClearHist,
                sep, lblPredef, lblPredefDesc, btnPredef, predefOut
        );
        return vbox;
    }

    // ── Onglet : Instance (a : C ?) ──
    private VBox buildInstanceTab(DLContext ctx, List<String> indNames,
                                   List<String> classNames, TextFlowLog log) {
        VBox v = new VBox(8);
        v.setPadding(new Insets(12, 8, 12, 8));
        Label q = new Label("« L'individu a est-il une instance de la classe C ? »");
        q.setStyle("-fx-text-fill:#f9e2af;-fx-font-size:11.5px;-fx-font-style:italic;-fx-wrap-text:true;");

        ComboBox<String> aBox = new ComboBox<>();
        aBox.getItems().addAll(indNames);
        if (!indNames.isEmpty()) aBox.getSelectionModel().select(0);
        aBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> cBox = new ComboBox<>();
        cBox.getItems().addAll(classNames);
        if (!classNames.isEmpty()) cBox.getSelectionModel().select(0);
        cBox.setMaxWidth(Double.MAX_VALUE);

        Button run = new Button("▶  Tester  a : C");
        run.setMaxWidth(Double.MAX_VALUE);
        run.setStyle("-fx-background-color:#89b4fa;-fx-text-fill:#1e1e2e;"
                + "-fx-font-weight:bold;-fx-padding:8 0;");
        run.setOnAction(e -> {
            String a = aBox.getValue();
            String c = cBox.getValue();
            if (a == null || c == null) return;
            OWLDataFactory f = ctx.onto.getOWLOntologyManager().getOWLDataFactory();
            OWLNamedIndividual ind = f.getOWLNamedIndividual(ctx.iri(a));
            OWLClass cls = f.getOWLClass(ctx.iri(c));
            boolean result = ctx.reasoner.isEntailed(f.getOWLClassAssertionAxiom(cls, ind));
            String prefix = result ? "✅" : "❌";
            String color = result ? "#a6e3a1" : "#f38ba8";
            log.append(prefix + " " + a + " : " + c + "  →  " + (result ? "VRAI" : "FAUX"), color);
        });

        v.getChildren().addAll(q, label("Individu a :"), aBox,
                label("Classe C :"), cBox, run);
        return v;
    }

    // ── Onglet : Subsomption (C ⊑ D ?) ──
    private VBox buildSubsumptionTab(DLContext ctx, List<String> classNames, TextFlowLog log) {
        VBox v = new VBox(8);
        v.setPadding(new Insets(12, 8, 12, 8));
        Label q = new Label("« La classe C est-elle un sous-concept de D ? »");
        q.setStyle("-fx-text-fill:#f9e2af;-fx-font-size:11.5px;-fx-font-style:italic;-fx-wrap-text:true;");

        ComboBox<String> cBox = new ComboBox<>();
        cBox.getItems().addAll(classNames);
        if (!classNames.isEmpty()) cBox.getSelectionModel().select(0);
        cBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> dBox = new ComboBox<>();
        dBox.getItems().addAll(classNames);
        if (classNames.size() > 1) dBox.getSelectionModel().select(1);
        else if (!classNames.isEmpty()) dBox.getSelectionModel().select(0);
        dBox.setMaxWidth(Double.MAX_VALUE);

        Button run = new Button("▶  Tester  C ⊑ D");
        run.setMaxWidth(Double.MAX_VALUE);
        run.setStyle("-fx-background-color:#cba6f7;-fx-text-fill:#1e1e2e;"
                + "-fx-font-weight:bold;-fx-padding:8 0;");
        run.setOnAction(e -> {
            String c = cBox.getValue();
            String d = dBox.getValue();
            if (c == null || d == null) return;
            OWLDataFactory f = ctx.onto.getOWLOntologyManager().getOWLDataFactory();
            OWLClass cls1 = f.getOWLClass(ctx.iri(c));
            OWLClass cls2 = f.getOWLClass(ctx.iri(d));
            boolean result = ctx.reasoner.isEntailed(f.getOWLSubClassOfAxiom(cls1, cls2));
            String prefix = result ? "✅" : "❌";
            String color = result ? "#a6e3a1" : "#f38ba8";
            log.append(prefix + " " + c + " ⊑ " + d + "  →  " + (result ? "VRAI" : "FAUX"), color);
        });

        v.getChildren().addAll(q, label("Classe C :"), cBox,
                label("Classe D :"), dBox, run);
        return v;
    }

    // ── Onglet : Disjonction (C ⊓ D = ⊥ ?) ──
    private VBox buildDisjointTab(DLContext ctx, List<String> classNames, TextFlowLog log) {
        VBox v = new VBox(8);
        v.setPadding(new Insets(12, 8, 12, 8));
        Label q = new Label("« Les classes C et D sont-elles disjointes (aucune instance commune) ? »");
        q.setStyle("-fx-text-fill:#f9e2af;-fx-font-size:11.5px;-fx-font-style:italic;-fx-wrap-text:true;");

        ComboBox<String> cBox = new ComboBox<>();
        cBox.getItems().addAll(classNames);
        if (!classNames.isEmpty()) cBox.getSelectionModel().select(0);
        cBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> dBox = new ComboBox<>();
        dBox.getItems().addAll(classNames);
        if (classNames.size() > 1) dBox.getSelectionModel().select(1);
        else if (!classNames.isEmpty()) dBox.getSelectionModel().select(0);
        dBox.setMaxWidth(Double.MAX_VALUE);

        Button run = new Button("▶  Tester  C ⊓ D = ⊥");
        run.setMaxWidth(Double.MAX_VALUE);
        run.setStyle("-fx-background-color:#f38ba8;-fx-text-fill:#1e1e2e;"
                + "-fx-font-weight:bold;-fx-padding:8 0;");
        run.setOnAction(e -> {
            String c = cBox.getValue();
            String d = dBox.getValue();
            if (c == null || d == null) return;
            OWLDataFactory f = ctx.onto.getOWLOntologyManager().getOWLDataFactory();
            OWLClass cls1 = f.getOWLClass(ctx.iri(c));
            OWLClass cls2 = f.getOWLClass(ctx.iri(d));
            boolean result = ctx.reasoner.isEntailed(f.getOWLDisjointClassesAxiom(cls1, cls2));
            String prefix = result ? "✅" : "❌";
            String color = result ? "#a6e3a1" : "#f38ba8";
            log.append(prefix + " " + c + " ⊓ " + d + " = ⊥  →  " + (result ? "VRAI (disjoints)" : "FAUX (intersection possible)"), color);
        });

        v.getChildren().addAll(q, label("Classe C :"), cBox,
                label("Classe D :"), dBox, run);
        return v;
    }

    // =========================================================
    //  CONTEXTE PARTAGÉ + UTILITAIRES
    // =========================================================
    /** Encapsule ontologie + reasoner + helpers IRI pour un domaine. */
    private static class DLContext {
        final OWLOntology onto;
        final OWLReasoner reasoner;
        final boolean isMusic;
        final String namespace;
        DLContext(OWLOntology onto, OWLReasoner reasoner, boolean isMusic) {
            this.onto = onto;
            this.reasoner = reasoner;
            this.isMusic = isMusic;
            this.namespace = isMusic ? "http://rcr1/musique#" : "http://rcr1/justice#";
        }
        IRI iri(String localName) {
            return IRI.create(namespace + localName);
        }
    }

    /** Petit log coloré pour l'historique des requêtes (TextFlow ligne par ligne). */
    private static class TextFlowLog {
        final javafx.scene.text.TextFlow flow = new javafx.scene.text.TextFlow();
        TextFlowLog() {
            flow.setStyle("-fx-background-color:#181825;-fx-padding:8;");
        }
        void append(String text, String hexColor) {
            Platform.runLater(() -> {
                javafx.scene.text.Text t = new javafx.scene.text.Text(text + "\n");
                t.setStyle("-fx-fill:" + hexColor + ";-fx-font-family:Monospaced;-fx-font-size:11.5px;");
                flow.getChildren().add(t);
            });
        }
        void clear() {
            Platform.runLater(() -> flow.getChildren().clear());
        }
    }

    private String captureRun(boolean isMusic) {
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, java.nio.charset.StandardCharsets.UTF_8)) {
            System.setOut(ps);
            if (isMusic) DescriptionLogicMusic.run();
            else         DescriptionLogicJustice.run();
        } finally {
            System.setOut(old);
        }
        return baos.toString(java.nio.charset.StandardCharsets.UTF_8);
    }

    private Label section(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight:bold;-fx-text-fill:#a6e3a1;-fx-font-size:13px;");
        return l;
    }

    private Label label(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:11.5px;");
        return l;
    }
}
