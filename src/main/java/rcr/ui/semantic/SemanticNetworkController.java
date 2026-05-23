package rcr.ui.semantic;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import rcr.semantic.SemanticNetworkExample;
import rcr.semantic.SemanticNetworkExample.SemanticNetwork;
import rcr.semantic.SemanticNetworkExample.SemanticNetwork.Inheritance;
import rcr.semantic.SemanticNetworkExample.SemanticNetwork.InheritanceTrace;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * Controller JavaFX — Réseaux Sémantiques (refonte visuelle Phase 2).
 *
 * Améliorations par rapport à la version précédente :
 *   1. Layout hiérarchique auto-calculé (niveaux par BFS sur is-a, composantes
 *      séparées : hiérarchie principale au centre, modalités à gauche/droite).
 *   2. Arcs courbes (Bézier quadratique) avec point de contrôle perpendiculaire
 *      — élimine le visuel "spaghetti" des lignes droites qui se croisent.
 *   3. Labels d'arcs avec fond semi-transparent pour rester lisibles même quand
 *      ils chevauchent d'autres arcs.
 *   4. Hover : passer la souris sur un nœud illumine ses arcs sortants.
 *   5. Coloration INHERITED (vert) / BLOCKED (rouge) lors de la requête avec
 *      exceptions — illustre visuellement le blocage par lien barré.
 *   6. Ombres douces sous les nœuds (effet "carte") pour donner de la profondeur.
 */
public class SemanticNetworkController {

    // ── Canvas redimensionnable ────────────────────────────────
    static class ResizableCanvas extends Canvas {
        ResizableCanvas() { super(); }
        @Override public boolean isResizable()        { return true; }
        @Override public double prefWidth(double h)   { return getWidth(); }
        @Override public double prefHeight(double w)  { return getHeight(); }
    }

    // =========================================================
    //  DOMAINE 1 — INDUSTRIE MUSICALE
    //  { id, label, type }  — positions calculées automatiquement
    // =========================================================
    private static final Object[][] MUS_NODES = {
        {"Personne",      "Personne",              "C"},
        {"OrgMusicale",   "Organisation\nMusicale", "C"},
        {"MondeMusicale", "Monde Musical",          "C"},
        {"Album",         "Album",                  "C"},
        {"AlbumPlatine",  "Album Platine",          "C"},
        {"Titre",         "Titre",                  "C"},
        {"Droits",        "Droits",                 "C"},
        {"Croyances",     "Croyances",              "C"},
        {"Negations",     "Négations",              "C"},
        {"Artiste",       "Artiste",                "I"},
        {"ArtisteSigne",  "Artiste Signé",          "I"},
        {"ArtisteIndep",  "Artiste Indép.",         "I"},
        {"Label",         "Label",                  "I"},
        {"Soolking",      "Soolking",               "I"},
        {"Universal",     "Universal",              "I"},
        {"MachiBessah",   "Machi Bessah",           "I"},
        {"Fan",           "Fan",                    "I"},
        {"C1",            "C1",                     "I"},
        {"N1",            "N1",                     "I"},
    };

    private static final String[][] MUS_ARCS = {
        {"Artiste",      "Personne",      "is-a",           "S"},
        {"Artiste",      "MondeMusicale", "is-a",           "S"},
        {"ArtisteSigne", "Artiste",       "is-a",           "S"},
        {"ArtisteIndep", "Artiste",       "is-a",           "NS"},
        {"Label",        "OrgMusicale",   "is-a",           "S"},
        {"AlbumPlatine", "Album",         "is-a",           "S"},
        {"Soolking",     "ArtisteSigne",  "is-a",           "S"},
        {"Universal",    "Label",         "is-a",           "S"},
        {"Fan",          "MondeMusicale", "is-a",           "S"},
        {"C1",           "Croyances",     "is-a",           "S"},
        {"N1",           "Negations",     "is-a",           "S"},
        {"Soolking",     "MachiBessah",   "produit",        "R"},
        {"MachiBessah",  "Titre",         "is-a",           "S"},
        {"Soolking",     "AlbumPlatine",  "possede",        "R"},
        {"ArtisteSigne", "Label",         "sous-contrat",   "R"},
        {"ArtisteSigne", "Droits",        "percoit-royal.", "R"},
        {"Soolking",     "Universal",     "signe-chez",     "R"},
        {"ArtisteIndep", "Label",         "¬sous-contrat",  "ES"},
        {"C1",           "Fan",           "agent",          "M"},
        {"C1",           "N1",            "objet",          "M"},
        {"N1",           "Droits",        "argument",       "M"},
    };

    // =========================================================
    //  DOMAINE 2 — SYSTÈME JUDICIAIRE
    // =========================================================
    private static final Object[][] JUS_NODES = {
        {"Personne",         "Personne",           "C"},
        {"ActeurJudiciaire", "Acteur Judiciaire",  "C"},
        {"PartieProces",     "Partie au Procès",   "C"},
        {"Culpabilite",      "Culpabilité",        "C"},
        {"Innocence",        "Innocence\nprésumée","C"},
        {"Preuve",           "Preuve",             "C"},
        {"Condamnation",     "Condamnation",       "C"},
        {"Croyances",        "Croyances",          "C"},
        {"Negations",        "Négations",          "C"},
        {"Juge",             "Juge",               "I"},
        {"Avocat",           "Avocat",             "I"},
        {"AvoDefense",       "Avocat\nDéfense",    "I"},
        {"Procureur",        "Procureur",          "I"},
        {"Temoin",           "Témoin",             "I"},
        {"Accuse",           "Accusé",             "I"},
        {"Recidiviste",      "Récidiviste",        "I"},
        {"C1",               "C1",                "I"},
        {"C2",               "C2",                "I"},
        {"N1",               "N1",                "I"},
    };

    private static final String[][] JUS_ARCS = {
        {"Juge",             "ActeurJudiciaire",  "is-a",      "S"},
        {"Avocat",           "ActeurJudiciaire",  "is-a",      "S"},
        {"AvoDefense",       "Avocat",            "is-a",      "S"},
        {"Procureur",        "ActeurJudiciaire",  "is-a",      "S"},
        {"Temoin",           "ActeurJudiciaire",  "is-a",      "S"},
        {"Accuse",           "PartieProces",      "is-a",      "S"},
        {"Recidiviste",      "Accuse",            "is-a",      "S"},
        {"ActeurJudiciaire", "Personne",          "is-a",      "S"},
        {"PartieProces",     "Personne",          "is-a",      "S"},
        {"C1",               "Croyances",         "is-a",      "S"},
        {"C2",               "Croyances",         "is-a",      "S"},
        {"N1",               "Negations",         "is-a",      "S"},
        {"Accuse",           "Innocence",         "presume",   "R"},
        {"Preuve",           "Condamnation",      "conduit-a", "R"},
        {"Juge",             "Condamnation",      "prononce",  "R"},
        {"Recidiviste",      "Innocence",         "¬presume",  "ES"},
        {"C1",               "Procureur",         "agent",     "M"},
        {"C1",               "Culpabilite",       "objet",     "M"},
        {"C2",               "AvoDefense",        "agent",     "M"},
        {"C2",               "Culpabilite",       "objet",     "M"},
        {"N1",               "C2",                "argument",  "M"},
    };

    // =========================================================
    //  BUILD UI
    // =========================================================
    public BorderPane buildUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#1e1e2e;");

        Label header = new Label("Réseaux Sémantiques — Propagation de Marqueurs");
        header.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#cdd6f4;"
                + "-fx-padding:14 24;-fx-background-color:#181825;");
        root.setTop(header);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color:#1e1e2e;");

        tabs.getTabs().add(new Tab("  Industrie Musicale  ",
                buildNetworkPane(MUS_NODES, MUS_ARCS,
                        "Quel artiste possède un album platine ?",
                        "AlbumPlatine", "Artiste",
                        "Soolking  (possede→AlbumPlatine | is-a→ArtisteSigne→Artiste)", true)));

        tabs.getTabs().add(new Tab("  Système Judiciaire  ",
                buildNetworkPane(JUS_NODES, JUS_ARCS,
                        "Quel acteur judiciaire croit en la culpabilité ?",
                        "Procureur", "ActeurJudiciaire",
                        "Le Procureur  (C1 agent→Procureur, C1 objet→Culpabilité)", false)));

        tabs.getTabs().forEach(t -> t.setClosable(false));
        root.setCenter(tabs);
        return root;
    }

    // =========================================================
    //  PANE RÉSEAU
    // =========================================================
    private SplitPane buildNetworkPane(Object[][] nodes, String[][] arcs,
                                       String question,
                                       String m1Default, String m2Default,
                                       String answer, boolean isMus) {
        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.HORIZONTAL);
        split.setDividerPositions(0.62);
        split.setStyle("-fx-background-color:#1e1e2e;");

        // ── Layout auto-calculé (une fois, immuable) ──────────
        Map<String, double[]> layout = computeLayout(nodes, arcs);

        // ── État partagé ───────────────────────────────────────
        int[]       selected    = {-1};
        int[]       hovered     = {-1};
        Set<String> m1Set       = new HashSet<>();
        Set<String> m2Set       = new HashSet<>();
        Set<String> inheritedSet = new HashSet<>();
        Set<String> blockedSet   = new HashSet<>();
        double[]    zoom        = {1.0};
        double[]    pan         = {0.0, 0.0};
        double[]    dragStart   = {0.0, 0.0};
        boolean[]   isDragging  = {false};

        // ── Canvas responsive ──────────────────────────────────
        ResizableCanvas canvas = new ResizableCanvas();
        canvas.setWidth(700);
        canvas.setHeight(560);

        Pane canvasPane = new Pane(canvas);
        canvasPane.setStyle("-fx-background-color:#11111b;");
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        Runnable redrawAll = () -> redraw(canvas, nodes, arcs, layout,
                selected[0], hovered[0], m1Set, m2Set,
                inheritedSet, blockedSet, zoom[0], pan);

        canvas.widthProperty().addListener(o -> redrawAll.run());
        canvas.heightProperty().addListener(o -> redrawAll.run());

        // Zoom molette
        canvas.addEventHandler(ScrollEvent.SCROLL, ev -> {
            double f = ev.getDeltaY() > 0 ? 1.1 : 0.91;
            zoom[0] = Math.max(0.3, Math.min(zoom[0] * f, 4.0));
            redrawAll.run();
            ev.consume();
        });

        // Pan (glisser)
        canvas.setOnMousePressed(ev -> {
            dragStart[0] = ev.getX() - pan[0];
            dragStart[1] = ev.getY() - pan[1];
            isDragging[0] = false;
        });
        canvas.setOnMouseDragged(ev -> {
            pan[0] = ev.getX() - dragStart[0];
            pan[1] = ev.getY() - dragStart[1];
            isDragging[0] = true;
            redrawAll.run();
        });

        // Clic nœud (ne se déclenche pas si on a glissé)
        canvas.setOnMouseReleased(ev -> {
            if (isDragging[0]) return;
            int hit = hitTestNode(ev.getX(), ev.getY(), nodes, layout,
                    canvas.getWidth(), canvas.getHeight(), zoom[0], pan);
            selected[0] = (selected[0] == hit) ? -1 : hit;
            redrawAll.run();
        });

        // Hover — illumine les arcs sortants
        canvas.setOnMouseMoved(ev -> {
            int hit = hitTestNode(ev.getX(), ev.getY(), nodes, layout,
                    canvas.getWidth(), canvas.getHeight(), zoom[0], pan);
            if (hit != hovered[0]) {
                hovered[0] = hit;
                redrawAll.run();
            }
        });
        canvas.setOnMouseExited(ev -> {
            if (hovered[0] != -1) {
                hovered[0] = -1;
                redrawAll.run();
            }
        });

        Label hint = new Label("  🖱  Molette = zoom  •  Glisser = déplacer  •  Survoler = illuminer arcs  •  Clic = sélectionner");
        hint.setStyle("-fx-text-fill:#6c7086;-fx-font-size:11px;-fx-padding:6 12;");
        VBox leftBox = new VBox(hint, canvasPane);
        VBox.setVgrow(canvasPane, Priority.ALWAYS);
        leftBox.setStyle("-fx-background-color:#11111b;");

        // Panel droit
        VBox infoPanel = buildInfoPanel(canvas, nodes, arcs, layout, selected, hovered,
                m1Set, m2Set, inheritedSet, blockedSet, zoom, pan,
                question, m1Default, m2Default, answer, isMus, redrawAll);
        ScrollPane rightScroll = new ScrollPane(infoPanel);
        rightScroll.setFitToWidth(true);
        rightScroll.setStyle("-fx-background-color:#1e1e2e;-fx-background:#1e1e2e;");

        split.getItems().addAll(leftBox, rightScroll);
        return split;
    }

    // =========================================================
    //  PANEL DROIT
    // =========================================================
    private VBox buildInfoPanel(Canvas canvas, Object[][] nodes, String[][] arcs,
                                Map<String, double[]> layout,
                                int[] selected, int[] hovered,
                                Set<String> m1Set, Set<String> m2Set,
                                Set<String> inheritedSet, Set<String> blockedSet,
                                double[] zoom, double[] pan,
                                String question, String m1Default, String m2Default,
                                String answer, boolean isMus, Runnable redrawAll) {
        SemanticNetwork net = isMus
                ? SemanticNetworkExample.buildMusiqueNetwork()
                : SemanticNetworkExample.buildJusticeNetwork();

        VBox vbox = new VBox(12);
        vbox.setPadding(new Insets(16));
        vbox.setStyle("-fx-background-color:#1e1e2e;");

        // ── Légende ──
        Label secLeg = section("Légende des liens");
        GridPane legend = new GridPane();
        legend.setHgap(10); legend.setVgap(4);
        String[][] leg = {
            {"─────", "#a6e3a1", "is-a strict (S)"},
            {"─ ─ ─", "#89b4fa", "is-a non-strict (NS)"},
            {"╳────", "#f38ba8", "exception stricte (¬relation)"},
            {"─·─·─", "#f9e2af", "relation sémantique ordinaire"},
            {"┄┄┄┄┄", "#cba6f7", "lien modal (agent · objet · argument)"},
        };
        for (int i = 0; i < leg.length; i++) {
            Label s = new Label(leg[i][0]);
            s.setStyle("-fx-text-fill:"+leg[i][1]+";-fx-font-family:Monospaced;-fx-font-size:11px;");
            Label d = new Label(leg[i][2]);
            d.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:11px;");
            legend.add(s, 0, i); legend.add(d, 1, i);
        }
        Label secTypes = section("Types de nœuds");
        Label lblTypes = new Label("Rectangle = Concept [C]   •   Ovale = Individu [I]");
        lblTypes.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:11px;");

        Button btnReset = btn("⟳  Réinitialiser zoom / position", "#313244");
        btnReset.setStyle(btnReset.getStyle() + "-fx-text-fill:#cdd6f4;");
        btnReset.setOnAction(e -> {
            zoom[0] = 1.0; pan[0] = 0; pan[1] = 0;
            redrawAll.run();
        });

        // ── Question prédéfinie ──
        Separator sep1 = sep();
        Label secQ = section("Question de propagation");
        Label lblQ = new Label(question);
        lblQ.setStyle("-fx-text-fill:#f9e2af;-fx-font-size:12px;-fx-wrap-text:true;-fx-font-style:italic;");

        // ── Propagation classique (M1 ∩ M2) ──
        Separator sep2 = sep();
        Label secProp = section("Propagation de Marqueurs");
        Label lblM1 = field("Nœud source M1 :");
        ComboBox<String> m1Box = buildCombo(nodes, m1Default);
        Label lblM2 = field("Nœud requête M2 :");
        ComboBox<String> m2Box = buildCombo(nodes, m2Default);

        Label resultProp = new Label("—");
        resultProp.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:12px;-fx-wrap-text:true;");

        Button btnProp = btn("▶  Propager les marqueurs", "#a6e3a1");
        btnProp.setStyle(btnProp.getStyle() + "-fx-text-fill:#1e1e2e;");
        btnProp.setOnAction(e -> {
            String id1 = (String) nodes[m1Box.getSelectionModel().getSelectedIndex()][0];
            String id2 = (String) nodes[m2Box.getSelectionModel().getSelectedIndex()][0];
            m1Set.clear(); m1Set.addAll(ancestorsIsa(id1, arcs)); m1Set.add(id1);
            m2Set.clear(); m2Set.addAll(ancestorsIsa(id2, arcs)); m2Set.add(id2);
            inheritedSet.clear(); blockedSet.clear();
            Set<String> inter = new LinkedHashSet<>(m1Set);
            inter.retainAll(m2Set);
            redrawAll.run();
            StringBuilder sb = new StringBuilder();
            sb.append("M1 : "); appendLabels(sb, m1Set, nodes);
            sb.append("\n\nM2 : "); appendLabels(sb, m2Set, nodes);
            sb.append("\n\n");
            if (inter.isEmpty()) sb.append("❌ Aucune intersection.");
            else {
                sb.append("✅ M1 ∩ M2 :\n");
                for (String id : inter) for (Object[] n : nodes)
                    if (n[0].equals(id)) { sb.append("  → ").append(n[1]).append("\n"); break; }
            }
            resultProp.setText(sb.toString());
            resultProp.setStyle("-fx-text-fill:#a6e3a1;-fx-font-size:11.5px;"
                    + "-fx-font-family:Monospaced;-fx-wrap-text:true;");
        });

        Button btnClear = btn("✕  Effacer marqueurs", "#45475a");
        btnClear.setStyle(btnClear.getStyle() + "-fx-text-fill:#cdd6f4;");
        btnClear.setOnAction(e -> {
            m1Set.clear(); m2Set.clear();
            inheritedSet.clear(); blockedSet.clear();
            resultProp.setText("—");
            resultProp.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:12px;-fx-wrap-text:true;");
            redrawAll.run();
        });

        // ── Requête avec exceptions (moteur d'héritage) ──
        Separator sepQ = sep();
        Label secQuery = section("Requête avec exceptions");
        Label lblQueryDesc = new Label(
                "Évalue : « les individus de Source ont-ils la relation R\n"
              + "vers une Cible ? » en respectant les exceptions ¬R.");
        lblQueryDesc.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:11px;-fx-wrap-text:true;");

        Label lblQSrc = field("Catégorie source :");
        ComboBox<String> qSrc = buildCombo(nodes, m2Default);
        Label lblQRel = field("Relation cible R :");
        ComboBox<String> qRel = new ComboBox<>();
        for (String r : net.distinctRelations()) qRel.getItems().add(r);
        if (!qRel.getItems().isEmpty()) qRel.getSelectionModel().select(0);
        qRel.setMaxWidth(Double.MAX_VALUE);
        Label lblQTgt = field("Catégorie cible :");
        ComboBox<String> qTgt = buildCombo(nodes, m1Default);

        Label resultQ = new Label("—");
        resultQ.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:12px;-fx-wrap-text:true;");

        Button btnQuery = btn("▶  Évaluer la requête", "#fab387");
        btnQuery.setStyle(btnQuery.getStyle() + "-fx-text-fill:#1e1e2e;");
        btnQuery.setOnAction(e -> {
            String srcId = (String) nodes[qSrc.getSelectionModel().getSelectedIndex()][0];
            String tgtId = (String) nodes[qTgt.getSelectionModel().getSelectedIndex()][0];
            String rel   = qRel.getSelectionModel().getSelectedItem();
            if (rel == null) { resultQ.setText("⚠  Aucune relation disponible."); return; }
            List<Object[]> all = net.queryRelationWithBlocked(srcId, rel, tgtId);
            m1Set.clear(); m2Set.clear();
            inheritedSet.clear(); blockedSet.clear();
            StringBuilder sb = new StringBuilder();
            sb.append("Requête : « individus de ").append(labelOf(net, srcId))
                    .append(" qui ont ").append(rel)
                    .append(" → ").append(labelOf(net, tgtId)).append(" ? »\n\n");
            boolean anyInh = false, anyBlk = false;
            for (Object[] row : all) {
                InheritanceTrace tr = (InheritanceTrace) row[2];
                if (tr.result == Inheritance.INHERITED) {
                    if (!anyInh) { sb.append("✅ Héritages réussis :\n"); anyInh = true; }
                    String s = labelOf(net, (String) row[0]);
                    String t = labelOf(net, (String) row[1]);
                    String d = labelOf(net, tr.decisionNode);
                    sb.append("  → ").append(s).append("  [").append(rel)
                            .append("] ").append(t);
                    if (!row[0].equals(tr.decisionNode)) sb.append("   (via ").append(d).append(")");
                    sb.append("\n");
                    inheritedSet.add((String) row[0]);
                }
            }
            for (Object[] row : all) {
                InheritanceTrace tr = (InheritanceTrace) row[2];
                if (tr.result == Inheritance.BLOCKED) {
                    if (!anyBlk) {
                        if (anyInh) sb.append("\n");
                        sb.append("❌ Bloqués par exception (¬").append(rel).append(") :\n");
                        anyBlk = true;
                    }
                    String s = labelOf(net, (String) row[0]);
                    String t = labelOf(net, (String) row[1]);
                    sb.append("  → ").append(s).append("  ¬").append(rel)
                            .append(" ").append(t).append("\n");
                    blockedSet.add((String) row[0]);
                }
            }
            if (!anyInh && !anyBlk) sb.append("(aucun héritage ni exception trouvés)");
            resultQ.setText(sb.toString());
            resultQ.setStyle("-fx-text-fill:#cdd6f4;-fx-font-size:11.5px;"
                    + "-fx-font-family:Monospaced;-fx-wrap-text:true;");
            redrawAll.run();
        });

        // ── Réponse attendue ──
        Separator sep3 = sep();
        Label secAns = section("Réponse attendue");
        Label lblAns = new Label("✅  " + answer);
        lblAns.setStyle("-fx-text-fill:#a6e3a1;-fx-font-size:12px;-fx-wrap-text:true;-fx-font-weight:bold;");

        // ── Console ──
        Separator sep4 = sep();
        Label secOut = section("Sortie console complète");
        TextArea outArea = new TextArea();
        outArea.setEditable(false);
        outArea.setPrefRowCount(10);
        outArea.setStyle("-fx-control-inner-background:#181825;-fx-background-color:#181825;"
                + "-fx-text-fill:#cdd6f4;-fx-font-family:Monospaced;-fx-font-size:10.5px;");
        VBox.setVgrow(outArea, Priority.ALWAYS);
        Button btnRun = btn("▶  Afficher sortie console", "#45475a");
        btnRun.setStyle(btnRun.getStyle() + "-fx-text-fill:#cdd6f4;");
        btnRun.setOnAction(e -> {
            btnRun.setDisable(true);
            outArea.setText("Calcul en cours…");
            Task<String> task = new Task<>() {
                @Override protected String call() { return captureRun(); }
            };
            task.setOnSucceeded(ev -> { outArea.setText(task.getValue()); btnRun.setDisable(false); });
            task.setOnFailed(ev   -> { outArea.setText("Erreur : " + task.getException()); btnRun.setDisable(false); });
            new Thread(task, "sem-run").start();
        });

        vbox.getChildren().addAll(
                secLeg, legend, secTypes, lblTypes, btnReset,
                sep1, secQ, lblQ,
                sep2, secProp, lblM1, m1Box, lblM2, m2Box,
                btnProp, btnClear, resultProp,
                sepQ, secQuery, lblQueryDesc,
                lblQSrc, qSrc, lblQRel, qRel, lblQTgt, qTgt,
                btnQuery, resultQ,
                sep3, secAns, lblAns,
                sep4, secOut, btnRun, outArea
        );
        return vbox;
    }

    // =========================================================
    //  LAYOUT AUTO — niveaux is-a + composantes connexes
    // =========================================================
    /**
     * Calcule une position [rx, ry] dans [0..1] pour chaque nœud.
     *
     * Algorithme :
     *   1. Composantes connexes via is-a, puis fusion des singletons (1 nœud
     *      isolé) avec leur voisin le plus connecté (par n'importe quel arc).
     *      Cela évite les multiples petites colonnes pour Culpabilité, Innocence,
     *      Preuve, etc. qui ne sont pas dans la hiérarchie is-a principale.
     *   2. Pour chaque composante, calculer le "niveau" de chaque nœud via is-a :
     *      level(n) = max(level(parent_is-a)) + 1, racines = niveau 0.
     *   3. Placer les composantes côte à côte. Zigzag vertical sur niveaux denses.
     *   4. Largeur des nœuds (~ longueur du label) prise en compte pour l'espacement.
     */
    private Map<String, double[]> computeLayout(Object[][] nodes, String[][] arcs) {
        // Adjacence is-a (undirected) — pour composantes "structurelles"
        Map<String, Set<String>> isaAdj = new LinkedHashMap<>();
        // Adjacence tous arcs confondus — pour rattacher les singletons
        Map<String, Set<String>> fullAdj = new LinkedHashMap<>();
        for (Object[] n : nodes) {
            isaAdj.put((String) n[0], new LinkedHashSet<>());
            fullAdj.put((String) n[0], new LinkedHashSet<>());
        }
        for (String[] a : arcs) {
            fullAdj.get(a[0]).add(a[1]);
            fullAdj.get(a[1]).add(a[0]);
            if (a[2].equals("is-a")) {
                isaAdj.get(a[0]).add(a[1]);
                isaAdj.get(a[1]).add(a[0]);
            }
        }

        // Composantes connexes sous is-a uniquement
        Map<String, Integer> compOf = new HashMap<>();
        List<List<String>> components = new ArrayList<>();
        for (Object[] n : nodes) {
            String id = (String) n[0];
            if (compOf.containsKey(id)) continue;
            int cid = components.size();
            List<String> comp = new ArrayList<>();
            Deque<String> stack = new ArrayDeque<>();
            stack.push(id);
            while (!stack.isEmpty()) {
                String cur = stack.pop();
                if (compOf.containsKey(cur)) continue;
                compOf.put(cur, cid);
                comp.add(cur);
                for (String nb : isaAdj.get(cur)) if (!compOf.containsKey(nb)) stack.push(nb);
            }
            components.add(comp);
        }

        // Fusion des singletons (composantes de 1 nœud) avec leur voisin le plus
        // connecté (toutes relations confondues). On évite ainsi 4-5 colonnes
        // pour Culpabilité, Innocence, Preuve, Condamnation, etc.
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int ci = 0; ci < components.size(); ci++) {
                List<String> comp = components.get(ci);
                if (comp.size() != 1) continue;
                String singleton = comp.get(0);
                Set<String> neighbors = fullAdj.get(singleton);
                // Choisir le voisin appartenant à la composante la plus grande
                int bestComp = -1, bestSize = 0;
                for (String nb : neighbors) {
                    Integer nbComp = compOf.get(nb);
                    if (nbComp == null || nbComp == ci) continue;
                    int size = components.get(nbComp).size();
                    if (size > bestSize) { bestSize = size; bestComp = nbComp; }
                }
                if (bestComp >= 0) {
                    components.get(bestComp).add(singleton);
                    compOf.put(singleton, bestComp);
                    comp.clear();
                    changed = true;
                }
            }
        }
        components.removeIf(List::isEmpty);

        // Trier : composante la plus grande à gauche (centre principal)
        components.sort((a, b) -> Integer.compare(b.size(), a.size()));

        // Niveaux via is-a (un is-a vers ailleurs ⇒ niveau = max(parents) + 1)
        Map<String, Integer> level = new HashMap<>();
        Map<String, List<String>> isaTargets = new HashMap<>();
        for (Object[] n : nodes) isaTargets.put((String) n[0], new ArrayList<>());
        for (String[] a : arcs) {
            if (a[2].equals("is-a")) isaTargets.get(a[0]).add(a[1]);
        }
        for (Object[] n : nodes) computeLevel((String) n[0], isaTargets, level);

        // Pour chaque composante, regrouper par niveau et trouver largeur max
        int[] compWidths = new int[components.size()];
        int totalWidth = 0;
        List<Map<Integer, List<String>>> compByLevel = new ArrayList<>();
        for (int ci = 0; ci < components.size(); ci++) {
            Map<Integer, List<String>> byLevel = new TreeMap<>();
            for (String id : components.get(ci)) {
                byLevel.computeIfAbsent(level.get(id), k -> new ArrayList<>()).add(id);
            }
            int max = byLevel.values().stream().mapToInt(List::size).max().orElse(1);
            compWidths[ci] = max;
            totalWidth += max;
            compByLevel.add(byLevel);
        }

        int maxLevel = level.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        Map<String, double[]> result = new LinkedHashMap<>();
        // Marges généreuses pour éviter qu'un nœud large (Organisation Musicale...)
        // ne dépasse du bord du canvas — la moitié de la largeur d'un nœud doit tenir.
        double leftMargin = 0.08, rightMargin = 0.08;
        double availableX = 1.0 - leftMargin - rightMargin;
        double yTop = 0.10, yBot = 0.90;
        double xCursor = leftMargin;

        for (int ci = 0; ci < components.size(); ci++) {
            Map<Integer, List<String>> byLevel = compByLevel.get(ci);
            // Largeur proportionnelle à la largeur (en nb de nœuds) de la composante
            double compFrac = (compWidths[ci] + 0.5) / (totalWidth + components.size() * 0.5);
            double compW = availableX * compFrac;

            for (Map.Entry<Integer, List<String>> e : byLevel.entrySet()) {
                int lv = e.getKey();
                List<String> lvNodes = e.getValue();
                lvNodes.sort(String::compareTo);
                int count = lvNodes.size();

                double yBase = (maxLevel == 0)
                        ? (yTop + yBot) / 2
                        : yTop + (yBot - yTop) * lv / (double) maxLevel;
                // Zigzag vertical si trop de nœuds sur ce niveau
                double zigzagAmplitude = (count > 4) ? 0.07 : 0.0;

                for (int i = 0; i < count; i++) {
                    double t = (count == 1) ? 0.5 : (i + 0.5) / count;
                    double rx = xCursor + compW * t;
                    double ry = yBase + (i % 2 == 0 ? -zigzagAmplitude : zigzagAmplitude);
                    result.put(lvNodes.get(i), new double[]{rx, ry});
                }
            }
            xCursor += compW + 0.015; // petit gap entre composantes
        }

        return result;
    }

    private int computeLevel(String id, Map<String, List<String>> isaTargets,
                             Map<String, Integer> level) {
        if (level.containsKey(id)) return level.get(id);
        level.put(id, 0); // cycle protection
        int max = -1;
        for (String t : isaTargets.get(id)) {
            int lvl = computeLevel(t, isaTargets, level);
            if (lvl > max) max = lvl;
        }
        int result = (max < 0) ? 0 : max + 1;
        level.put(id, result);
        return result;
    }

    // =========================================================
    //  DESSIN — Bézier + labels avec fond + hover + ombres
    // =========================================================
    private void redraw(Canvas canvas, Object[][] nodes, String[][] arcs,
                        Map<String, double[]> layout,
                        int selected, int hovered,
                        Set<String> m1, Set<String> m2,
                        Set<String> inherited, Set<String> blocked,
                        double zoom, double[] pan) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double W = canvas.getWidth(), H = canvas.getHeight();
        if (W <= 0 || H <= 0) return;

        // Fond avec léger dégradé
        gc.setFill(Color.web("#11111b"));
        gc.fillRect(0, 0, W, H);

        // Identifier les arcs sortants du nœud survolé pour les illuminer
        String hoveredId = (hovered >= 0 && hovered < nodes.length)
                ? (String) nodes[hovered][0] : null;

        // Précompter combien d'arcs entre chaque paire (from, to) pour offset Bézier
        Map<String, Integer> pairCount = new HashMap<>();
        Map<String, Integer> pairIdx = new HashMap<>();
        for (String[] a : arcs) {
            String k = pairKey(a[0], a[1]);
            pairCount.merge(k, 1, Integer::sum);
        }

        // ── Dessin des arcs (Bézier quadratique) ──
        for (String[] arc : arcs) {
            double[] fp = toPx(arc[0], layout, W, H, zoom, pan);
            double[] tp = toPx(arc[1], layout, W, H, zoom, pan);
            if (fp == null || tp == null) continue;

            boolean isHoveredArc = hoveredId != null && arc[0].equals(hoveredId);

            Color col; double[] dash = null;
            switch (arc[3]) {
                case "NS": col = Color.web("#89b4fa"); dash = new double[]{7*zoom, 4*zoom}; break;
                case "ES": col = Color.web("#f38ba8"); dash = new double[]{5*zoom, 3*zoom}; break;
                case "EN": col = Color.web("#fab387"); dash = new double[]{5*zoom, 3*zoom}; break;
                case "M":  col = Color.web("#cba6f7"); dash = new double[]{3*zoom, 4*zoom}; break;
                case "R":  col = Color.web("#f9e2af"); break;
                default:   col = Color.web("#a6e3a1"); break;
            }
            if (isHoveredArc) col = col.brighter();

            // Point de contrôle Bézier : perpendiculaire au milieu, alterné
            double midX = (fp[0] + tp[0]) / 2.0, midY = (fp[1] + tp[1]) / 2.0;
            double dx = tp[0] - fp[0], dy = tp[1] - fp[1];
            double dist = Math.hypot(dx, dy);
            if (dist < 1) continue;
            // perpendiculaire normalisée
            double px = -dy / dist, py = dx / dist;
            String k = pairKey(arc[0], arc[1]);
            int idx = pairIdx.merge(k, 1, Integer::sum) - 1;
            int total = pairCount.get(k);
            double offsetMag = Math.min(dist * 0.18, 35 * zoom);
            double offsetSign = (total == 1) ? 0
                    : ((idx % 2 == 0 ? 1 : -1) * (1 + idx / 2) * offsetMag * 0.6);
            // Légère courbure même pour arc unique (effet plus propre)
            if (total == 1) offsetSign = offsetMag * 0.35;
            double cx = midX + px * offsetSign;
            double cy = midY + py * offsetSign;

            // Tronquer les extrémités pour ne pas pénétrer dans les nœuds
            double[] fpClip = clipFromNode(fp, cx, cy, 30 * zoom);
            double[] tpClip = clipToNode(tp, cx, cy, 33 * zoom);

            // Tracé
            gc.setStroke(col);
            gc.setLineWidth((isHoveredArc ? 2.6 : 1.7) * zoom);
            gc.setLineDashes(dash != null ? dash : null);
            gc.beginPath();
            gc.moveTo(fpClip[0], fpClip[1]);
            gc.quadraticCurveTo(cx, cy, tpClip[0], tpClip[1]);
            gc.stroke();
            gc.setLineDashes(null);

            // Lien barré (exception) : croix au milieu de la courbe
            if (arc[2].startsWith("¬")) {
                // point au milieu de la courbe (t=0.5)
                double mqx = 0.25*fpClip[0] + 0.5*cx + 0.25*tpClip[0];
                double mqy = 0.25*fpClip[1] + 0.5*cy + 0.25*tpClip[1];
                double cs = 6 * zoom;
                gc.setLineWidth(2.2 * zoom);
                gc.strokeLine(mqx-cs, mqy-cs, mqx+cs, mqy+cs);
                gc.strokeLine(mqx+cs, mqy-cs, mqx-cs, mqy+cs);
            }

            // Flèche à l'extrémité, tangente à la courbe en tpClip
            double tangX = tpClip[0] - cx, tangY = tpClip[1] - cy;
            double tangLen = Math.hypot(tangX, tangY);
            double angle = (tangLen > 0.01) ? Math.atan2(tangY, tangX)
                                            : Math.atan2(dy, dx);
            arrow(gc, tpClip[0], tpClip[1], angle, col, zoom);

            // Label avec fond semi-transparent
            String labelText = arc[2];
            double labelX = 0.25*fpClip[0] + 0.5*cx + 0.25*tpClip[0];
            double labelY = 0.25*fpClip[1] + 0.5*cy + 0.25*tpClip[1];
            drawArcLabel(gc, labelText, labelX, labelY, col, zoom);
        }

        // ── Dessin des nœuds (avec ombre + état) ──
        for (int i = 0; i < nodes.length; i++) {
            String id    = (String) nodes[i][0];
            String label = (String) nodes[i][1];
            String type  = (String) nodes[i][2];
            double[] px  = toPx(id, layout, W, H, zoom, pan);
            if (px == null) continue;
            double cx = px[0], cy = px[1];

            boolean isSel = i == selected;
            boolean isHov = i == hovered;
            boolean inM1  = m1.contains(id);
            boolean inM2  = m2.contains(id);
            boolean both  = inM1 && inM2;
            boolean isInh = inherited.contains(id);
            boolean isBlk = blocked.contains(id);

            Color fill, stroke, textCol;
            // Priorité : INHERITED > BLOCKED > both > M1 > M2 > selected > hover > default
            if      (isInh)  { fill=Color.web("#a6e3a1"); stroke=Color.web("#40a02b"); textCol=Color.web("#1e1e2e"); }
            else if (isBlk)  { fill=Color.web("#f38ba8"); stroke=Color.web("#d20f39"); textCol=Color.web("#1e1e2e"); }
            else if (both)   { fill=Color.web("#f9e2af"); stroke=Color.web("#fab387"); textCol=Color.web("#1e1e2e"); }
            else if (inM1)   { fill=Color.web("#a6e3a1"); stroke=Color.web("#40a02b"); textCol=Color.web("#1e1e2e"); }
            else if (inM2)   { fill=Color.web("#89b4fa"); stroke=Color.web("#1e66f5"); textCol=Color.web("#1e1e2e"); }
            else if (isSel)  { fill=Color.web("#cba6f7"); stroke=Color.web("#f5c2e7"); textCol=Color.web("#1e1e2e"); }
            else if (isHov)  { fill=Color.web("#45475a"); stroke=Color.web("#bac2de"); textCol=Color.web("#f5e0dc"); }
            else             { fill=Color.web("#313244"); stroke=Color.web("#585b70"); textCol=Color.web("#cdd6f4"); }

            String[] lines = label.split("\n");
            int maxLen = Arrays.stream(lines).mapToInt(String::length).max().orElse(6);
            double bw = Math.max(78, maxLen * 7.4) * zoom;
            double bh = (22 + lines.length * 16.0) * zoom;
            double R  = Math.max(bw, bh) / 2.0;

            // Ombre douce (offset diagonal)
            gc.setFill(Color.web("#000000", 0.35));
            if (type.equals("C")) {
                gc.fillRoundRect(cx-bw/2 + 3*zoom, cy-bh/2 + 3*zoom, bw, bh,
                                 12*zoom, 12*zoom);
            } else {
                double or = Math.max(bw, bh) / 2.0;
                gc.fillOval(cx-or + 3*zoom, cy-or + 3*zoom, 2*or, 2*or);
            }

            // Forme
            gc.setFill(fill); gc.setStroke(stroke);
            gc.setLineWidth((isSel || isHov || isInh || isBlk ? 2.6 : 1.8) * zoom);

            if (type.equals("C")) {
                gc.fillRoundRect(cx-bw/2, cy-bh/2, bw, bh, 12*zoom, 12*zoom);
                gc.strokeRoundRect(cx-bw/2, cy-bh/2, bw, bh, 12*zoom, 12*zoom);
            } else {
                double or = Math.max(bw, bh) / 2.0;
                gc.fillOval(cx-or, cy-or, 2*or, 2*or);
                gc.strokeOval(cx-or, cy-or, 2*or, 2*or);
                R = or;
            }

            // Texte
            gc.setFill(textCol);
            gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11*zoom));
            for (int li=0; li<lines.length; li++) {
                double ty = cy - (lines.length-1)*8*zoom + li*16*zoom;
                double textW = textWidth(lines[li], gc.getFont());
                gc.fillText(lines[li], cx - textW/2.0, ty + 3*zoom);
            }

            // Badge marqueur (M1/M2/INH/BLK)
            gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10*zoom));
            String badge = null; Color badgeCol = null;
            if      (isInh) { badge = "INH"; badgeCol = Color.web("#40a02b"); }
            else if (isBlk) { badge = "¬"; badgeCol = Color.web("#d20f39"); }
            else if (both)  { badge = "M1∩M2"; badgeCol = Color.web("#fab387"); }
            else if (inM1)  { badge = "M1"; badgeCol = Color.web("#a6e3a1"); }
            else if (inM2)  { badge = "M2"; badgeCol = Color.web("#89b4fa"); }
            if (badge != null) {
                double bgX = cx - textWidth(badge, gc.getFont())/2.0 - 6*zoom;
                double bgY = cy + R + 6*zoom;
                double bgW = textWidth(badge, gc.getFont()) + 12*zoom;
                double bgH = 16 * zoom;
                gc.setFill(Color.web("#1e1e2e", 0.85));
                gc.fillRoundRect(bgX, bgY, bgW, bgH, 6*zoom, 6*zoom);
                gc.setStroke(badgeCol);
                gc.setLineWidth(1.2 * zoom);
                gc.strokeRoundRect(bgX, bgY, bgW, bgH, 6*zoom, 6*zoom);
                gc.setFill(badgeCol);
                gc.fillText(badge, cx - textWidth(badge, gc.getFont())/2.0, bgY + 12*zoom);
            }
        }
    }

    private void drawArcLabel(GraphicsContext gc, String text, double x, double y,
                              Color col, double zoom) {
        gc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 10 * zoom));
        double w = textWidth(text, gc.getFont());
        double h = 14 * zoom;
        double pad = 4 * zoom;
        // Fond opaque pour la lisibilité
        gc.setFill(Color.web("#11111b", 0.92));
        gc.fillRoundRect(x - w/2 - pad, y - h/2, w + 2*pad, h, 5*zoom, 5*zoom);
        // Bordure subtile
        gc.setStroke(col.deriveColor(0, 1, 1, 0.5));
        gc.setLineWidth(0.8 * zoom);
        gc.strokeRoundRect(x - w/2 - pad, y - h/2, w + 2*pad, h, 5*zoom, 5*zoom);
        // Texte
        gc.setFill(col);
        gc.fillText(text, x - w/2, y + 4*zoom);
    }

    // =========================================================
    //  UTILITAIRES
    // =========================================================

    /** Coordonnées relatives [0..1] (depuis layout calculé) → pixels avec zoom et pan */
    private double[] toPx(String nodeId, Map<String, double[]> layout,
                          double W, double H, double zoom, double[] pan) {
        double[] rel = layout.get(nodeId);
        if (rel == null) return null;
        return new double[]{
            rel[0] * W * zoom + pan[0],
            rel[1] * H * zoom + pan[1]
        };
    }

    private int hitTestNode(double mx, double my, Object[][] nodes,
                            Map<String, double[]> layout,
                            double W, double H, double zoom, double[] pan) {
        for (int i = 0; i < nodes.length; i++) {
            double[] px = toPx((String) nodes[i][0], layout, W, H, zoom, pan);
            if (px == null) continue;
            double R = 40 * zoom;
            double dx = mx - px[0], dy = my - px[1];
            if (dx*dx + dy*dy < R*R) return i;
        }
        return -1;
    }

    /** Tronquer le départ d'un arc pour qu'il sorte du bord du nœud */
    private double[] clipFromNode(double[] p, double cx, double cy, double radius) {
        double dx = cx - p[0], dy = cy - p[1];
        double d = Math.hypot(dx, dy);
        if (d < 0.01) return p;
        return new double[]{p[0] + radius * dx/d, p[1] + radius * dy/d};
    }

    /** Tronquer l'arrivée d'un arc pour qu'il touche le bord du nœud */
    private double[] clipToNode(double[] p, double cx, double cy, double radius) {
        double dx = cx - p[0], dy = cy - p[1];
        double d = Math.hypot(dx, dy);
        if (d < 0.01) return p;
        return new double[]{p[0] + radius * dx/d, p[1] + radius * dy/d};
    }

    private String pairKey(String a, String b) {
        return a.compareTo(b) < 0 ? a + "→" + b : b + "→" + a;
    }

    /** Approximation rapide de la largeur d'un texte (sans Text node coûteux). */
    private double textWidth(String s, Font font) {
        Text t = new Text(s);
        t.setFont(font);
        return t.getLayoutBounds().getWidth();
    }

    private Set<String> ancestorsIsa(String startId, String[][] arcs) {
        Set<String> vis = new LinkedHashSet<>();
        Queue<String> q = new LinkedList<>();
        q.add(startId);
        while (!q.isEmpty()) {
            String cur = q.poll();
            for (String[] a : arcs)
                if (a[0].equals(cur) && a[2].equals("is-a") && !vis.contains(a[1])) {
                    vis.add(a[1]); q.add(a[1]);
                }
        }
        return vis;
    }

    private void appendLabels(StringBuilder sb, Set<String> ids, Object[][] nodes) {
        for (String id : ids)
            for (Object[] n : nodes)
                if (n[0].equals(id)) { sb.append(n[1]).append("  "); break; }
    }

    private void arrow(GraphicsContext gc, double tx, double ty,
                       double angle, Color col, double zoom) {
        double len = 11 * zoom, sp = 0.42;
        gc.setFill(col);
        gc.fillPolygon(
            new double[]{tx, tx-len*Math.cos(angle-sp), tx-len*Math.cos(angle+sp)},
            new double[]{ty, ty-len*Math.sin(angle-sp), ty-len*Math.sin(angle+sp)}, 3);
    }

    private ComboBox<String> buildCombo(Object[][] nodes, String defaultId) {
        ComboBox<String> box = new ComboBox<>();
        int def = 0;
        for (int i=0; i<nodes.length; i++) {
            box.getItems().add((String) nodes[i][1]);
            if (nodes[i][0].equals(defaultId)) def = i;
        }
        box.getSelectionModel().select(def);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private String captureRun() {
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, java.nio.charset.StandardCharsets.UTF_8)) {
            System.setOut(ps);
            new SemanticNetworkExample().run();
        } finally { System.setOut(old); }
        return baos.toString(java.nio.charset.StandardCharsets.UTF_8);
    }

    private static String labelOf(SemanticNetwork net, String id) {
        SemanticNetworkExample.Node n = net.get(id);
        return n != null ? n.label : id;
    }

    private Label section(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-weight:bold;-fx-text-fill:#a6e3a1;-fx-font-size:13px;");
        return l;
    }
    private Label field(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:12px;");
        return l;
    }
    private Separator sep() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color:#45475a;");
        return s;
    }
    private Button btn(String text, String bg) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color:"+bg+";-fx-font-weight:bold;"
                + "-fx-font-size:12px;-fx-padding:8 0;");
        return b;
    }
}
