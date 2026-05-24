package rcr.ui.modal;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import rcr.modal.ModalLogicJustice;
import rcr.modal.ModalLogicMusic;
import rcr.modal.temporal.TemporalLogicJustice;
import rcr.modal.temporal.TemporalLogicMusic;

import javafx.application.Platform;
import javafx.scene.text.TextFlow;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.function.BiFunction;

public class ModalLogicController {

    // ---- Music Kripke model ----
    private static final String[] MUS_NAMES  = {"w1=label", "w2=fan", "w3=artiste", "w4=critique"};
    private static final double[][] MUS_POS  = {{110,180},{270,90},{270,270},{430,180}};
    private static final String[] MUS_ATOMS  = {
        "album_est_platine","concert_annule","artiste_sous_contrat",
        "chanson_en_top_charts","artiste_independant"
    };
    private static final int[][] MUS_WATOMS  = {{0,2,3},{1,3},{0,3,4},{1,3}};
    private static final int[][] MUS_EDGES   = {{0,1},{0,2},{2,2},{1,3},{3,3}};

    // ---- Justice Kripke model ----
    private static final String[] JUS_NAMES  = {"w1=juge","w2=avocat","w3=proc.","w4=témoin","w5=accusé"};
    private static final double[][] JUS_POS  = {{90,180},{240,90},{240,270},{400,90},{400,270}};
    private static final String[] JUS_ATOMS  = {
        "accuse_est_coupable","accuse_est_innocent","preuve_valide",
        "temoin_credible","circonstances_attenuantes"
    };
    private static final int[][] JUS_WATOMS  = {{2,3},{1,3,4},{0,2},{1,3},{0,4}};
    private static final int[][] JUS_EDGES   = {{0,0},{0,1},{0,2},{1,3},{2,4},{4,4}};

    public BorderPane buildUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#1e1e2e;");

        Label header = new Label("Logique Modale — Modèles de Kripke");
        header.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#cdd6f4;"
                + "-fx-padding:14 24;-fx-background-color:#181825;");
        root.setTop(header);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color:#1e1e2e;");

        Tab tabMusic = new Tab("  Industrie Musicale  ");
        tabMusic.setContent(buildModelPane(
                MUS_NAMES, MUS_POS, MUS_ATOMS, MUS_WATOMS, MUS_EDGES, true));

        Tab tabJustice = new Tab("  Système Judiciaire  ");
        tabJustice.setContent(buildModelPane(
                JUS_NAMES, JUS_POS, JUS_ATOMS, JUS_WATOMS, JUS_EDGES, false));

        Tab tabTempMusic = new Tab("  ⏳ Musique Temporelle  ");
        tabTempMusic.setContent(buildTemporalPane(true));

        Tab tabTempJustice = new Tab("  ⏳ Justice Temporelle  ");
        tabTempJustice.setContent(buildTemporalPane(false));

        tabs.getTabs().addAll(tabMusic, tabJustice, tabTempMusic, tabTempJustice);
        root.setCenter(tabs);
        return root;
    }

    // ==================================================================
    //  ONGLET TEMPOREL (G / F / H / P) — cours chap. 3
    // ==================================================================
    private SplitPane buildTemporalPane(boolean isMusic) {
        // Donnees du domaine
        List<String> instants = isMusic ? TemporalLogicMusic.INSTANTS : TemporalLogicJustice.INSTANTS;
        Map<String, String> labels = isMusic ? TemporalLogicMusic.LABELS : TemporalLogicJustice.LABELS;
        Map<String, Set<String>> V = isMusic ? TemporalLogicMusic.V : TemporalLogicJustice.V;

        // Operateurs : 4 lambdas qui delegent aux methodes statiques publiques
        BiFunction<String,String,Boolean> opG = isMusic ? TemporalLogicMusic::G : TemporalLogicJustice::G;
        BiFunction<String,String,Boolean> opF = isMusic ? TemporalLogicMusic::F : TemporalLogicJustice::F;
        BiFunction<String,String,Boolean> opH = isMusic ? TemporalLogicMusic::H : TemporalLogicJustice::H;
        BiFunction<String,String,Boolean> opP = isMusic ? TemporalLogicMusic::P : TemporalLogicJustice::P;

        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.HORIZONTAL);
        split.setDividerPositions(0.58);
        split.setStyle("-fx-background-color:#1e1e2e;");

        // ----- GAUCHE : frise + tableau de verite -----
        VBox left = new VBox(12);
        left.setPadding(new Insets(14));
        left.setStyle("-fx-background-color:#1e1e2e;");

        Label titleTitle = section(isMusic ? "Carriere d'un artiste (Soolking)"
                                            : "Deroulement d'un proces (Omar)");
        Label subtitle = new Label("Modele <T, <, V> : T = instants chronologiques, < = ordre temporel");
        subtitle.setStyle("-fx-text-fill:#6c7086;-fx-font-size:11px;");

        // Frise chronologique en texte
        Label friseTitle = field("Frise chronologique :");
        VBox frise = new VBox(4);
        frise.setStyle("-fx-background-color:#181825;-fx-padding:10;");
        for (String t : instants) {
            Label l = new Label("  " + t + "  →  " + labels.get(t));
            l.setStyle("-fx-text-fill:#cdd6f4;-fx-font-family:Monospaced;-fx-font-size:11.5px;");
            frise.getChildren().add(l);
        }

        // Tableau de verite atome x instant (GridPane)
        Label tableTitle = field("Tableau de verite (V = vrai, . = faux) :");
        GridPane truth = buildTruthTable(instants, V);
        ScrollPane truthScroll = new ScrollPane(truth);
        truthScroll.setFitToWidth(true);
        truthScroll.setStyle("-fx-background-color:#181825;-fx-background:#181825;");
        truthScroll.setPrefHeight(220);

        left.getChildren().addAll(titleTitle, subtitle,
                friseTitle, frise,
                tableTitle, truthScroll);

        // ----- DROITE : requete interactive + historique -----
        VBox right = new VBox(12);
        right.setPadding(new Insets(14));
        right.setStyle("-fx-background-color:#1e1e2e;");

        Label rTitle = section("Requete temporelle");
        Label rDesc = new Label("Selectionne un operateur, un atome et un instant\n"
                              + "pour evaluer la formule sur le modele.");
        rDesc.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:11px;-fx-wrap-text:true;");

        // Operateur
        Label lblOp = field("Operateur temporel :");
        ComboBox<String> opBox = new ComboBox<>();
        opBox.getItems().addAll(
                "G — sera TOUJOURS vrai dans le futur",
                "F — sera vrai A UN MOMENT dans le futur",
                "H — a TOUJOURS ete vrai dans le passe",
                "P — a ete vrai A UN MOMENT dans le passe");
        opBox.getSelectionModel().select(0);
        opBox.setMaxWidth(Double.MAX_VALUE);

        // Atome
        Label lblAtom = field("Atome :");
        ComboBox<String> atomBox = new ComboBox<>();
        for (String a : V.keySet()) atomBox.getItems().add(a);
        atomBox.getSelectionModel().select(0);
        atomBox.setMaxWidth(Double.MAX_VALUE);

        // Instant
        Label lblInstant = field("Instant :");
        ComboBox<String> instantBox = new ComboBox<>();
        for (String t : instants) instantBox.getItems().add(t + "  (" + labels.get(t) + ")");
        instantBox.getSelectionModel().select(0);
        instantBox.setMaxWidth(Double.MAX_VALUE);

        // Historique
        TextFlow log = new TextFlow();
        log.setStyle("-fx-background-color:#181825;-fx-padding:8;");
        appendLogLine(log, "→ Lance une requete pour voir le resultat.", "#6c7086");
        ScrollPane logScroll = new ScrollPane(log);
        logScroll.setFitToWidth(true);
        logScroll.setStyle("-fx-background-color:#181825;-fx-background:#181825;");
        VBox.setVgrow(logScroll, Priority.ALWAYS);

        // Bouton evaluer
        Button btnEval = new Button("▶  Evaluer");
        btnEval.setMaxWidth(Double.MAX_VALUE);
        btnEval.setStyle("-fx-background-color:#89b4fa;-fx-text-fill:#1e1e2e;"
                + "-fx-font-weight:bold;-fx-font-size:13px;-fx-padding:8 0;");
        btnEval.setOnAction(e -> {
            int idxOp = opBox.getSelectionModel().getSelectedIndex();
            String atom = atomBox.getValue();
            int idxT = instantBox.getSelectionModel().getSelectedIndex();
            String t = instants.get(idxT);
            char opSym = "GFHP".charAt(idxOp);
            BiFunction<String,String,Boolean> op =
                    (idxOp == 0) ? opG : (idxOp == 1) ? opF : (idxOp == 2) ? opH : opP;
            boolean res = op.apply(atom, t);
            String prefix = res ? "✅" : "❌";
            String color = res ? "#a6e3a1" : "#f38ba8";
            String line = prefix + "  " + opSym + " " + atom + "  en " + t
                        + "   →  " + (res ? "VRAI" : "FAUX");
            appendLogLine(log, line, color);
        });

        // Bouton clear
        Button btnClear = new Button("✕  Effacer historique");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setStyle("-fx-background-color:#45475a;-fx-text-fill:#cdd6f4;"
                + "-fx-font-size:11px;-fx-padding:6 0;");
        btnClear.setOnAction(e -> {
            Platform.runLater(log.getChildren()::clear);
            appendLogLine(log, "→ Historique efface.", "#6c7086");
        });

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#45475a;");
        Label histTitle = section("Historique des requetes");

        right.getChildren().addAll(rTitle, rDesc,
                lblOp, opBox, lblAtom, atomBox, lblInstant, instantBox,
                btnEval, btnClear,
                sep, histTitle, logScroll);

        split.getItems().addAll(left, right);
        return split;
    }

    // ----- Tableau de verite (GridPane) -----
    private GridPane buildTruthTable(List<String> instants, Map<String, Set<String>> V) {
        GridPane grid = new GridPane();
        grid.setHgap(6); grid.setVgap(3);
        grid.setStyle("-fx-background-color:#181825;-fx-padding:10;");

        // Header : ""  | t1 | t2 | ... | tN
        Label headerEmpty = new Label("");
        grid.add(headerEmpty, 0, 0);
        for (int i = 0; i < instants.size(); i++) {
            Label h = new Label(instants.get(i));
            h.setStyle("-fx-text-fill:#fab387;-fx-font-weight:bold;-fx-font-family:Monospaced;"
                    + "-fx-font-size:11.5px;-fx-padding:0 6;");
            grid.add(h, i + 1, 0);
        }

        // Lignes
        int row = 1;
        for (Map.Entry<String, Set<String>> e : V.entrySet()) {
            Label name = new Label(e.getKey());
            name.setStyle("-fx-text-fill:#cdd6f4;-fx-font-family:Monospaced;-fx-font-size:11px;");
            grid.add(name, 0, row);
            for (int i = 0; i < instants.size(); i++) {
                boolean v = e.getValue().contains(instants.get(i));
                Label cell = new Label(v ? "V" : "·");
                cell.setStyle("-fx-font-family:Monospaced;-fx-font-size:11.5px;-fx-font-weight:bold;"
                        + "-fx-text-fill:" + (v ? "#a6e3a1" : "#45475a") + ";"
                        + "-fx-padding:0 8;");
                grid.add(cell, i + 1, row);
            }
            row++;
        }
        return grid;
    }

    private void appendLogLine(TextFlow flow, String text, String hexColor) {
        Platform.runLater(() -> {
            javafx.scene.text.Text t = new javafx.scene.text.Text(text + "\n");
            t.setStyle("-fx-fill:" + hexColor + ";-fx-font-family:Monospaced;-fx-font-size:11.5px;");
            flow.getChildren().add(t);
        });
    }

    // ------------------------------------------------------------------
    private SplitPane buildModelPane(String[] names, double[][] pos, String[] atoms,
                                     int[][] wAtoms, int[][] edges, boolean isMusic) {
        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.HORIZONTAL);
        split.setDividerPositions(0.47);
        split.setStyle("-fx-background-color:#1e1e2e;");

        // ---- LEFT : Kripke canvas ----
        int[] sel = {-1};  // mutable selected-node index
        Canvas canvas = new Canvas(520, 400);
        drawGraph(canvas, names, pos, atoms, wAtoms, edges, sel[0]);

        canvas.setOnMouseClicked(ev -> {
            double mx = ev.getX(), my = ev.getY();
            int hit = -1;
            for (int i = 0; i < pos.length; i++) {
                double dx = mx - pos[i][0], dy = my - pos[i][1];
                if (Math.sqrt(dx * dx + dy * dy) < 34) { hit = i; break; }
            }
            sel[0] = (sel[0] == hit) ? -1 : hit;   // toggle
            drawGraph(canvas, names, pos, atoms, wAtoms, edges, sel[0]);
        });

        Label hint = new Label("  Cliquer un nœud pour le sélectionner");
        hint.setStyle("-fx-text-fill:#6c7086;-fx-font-size:11px;-fx-padding:6 12;");

        VBox leftBox = new VBox(hint, new StackPane(canvas));
        leftBox.setStyle("-fx-background-color:#181825;");
        StackPane.setMargin(canvas, new Insets(10));

        // ---- RIGHT : evaluator panel ----
        ScrollPane rightScroll = new ScrollPane(
                buildEvaluatorPanel(canvas, sel, names, pos, atoms, wAtoms, edges, isMusic));
        rightScroll.setFitToWidth(true);
        rightScroll.setStyle("-fx-background-color:#1e1e2e;-fx-background:#1e1e2e;");

        split.getItems().addAll(leftBox, rightScroll);
        return split;
    }

    // ------------------------------------------------------------------
    private VBox buildEvaluatorPanel(Canvas canvas, int[] sel,
                                     String[] names, double[][] pos,
                                     String[] atoms, int[][] wAtoms, int[][] edges,
                                     boolean isMusic) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(16));
        vbox.setStyle("-fx-background-color:#1e1e2e;");

        // -- Section: formula evaluation --
        Label secEval = section("Évaluation interactive");

        Label lblWorld = field("Monde courant :");
        ComboBox<String> worldBox = new ComboBox<>();
        worldBox.getItems().addAll(names);
        worldBox.getSelectionModel().selectFirst();
        worldBox.setMaxWidth(Double.MAX_VALUE);
        worldBox.setOnAction(e -> {
            sel[0] = worldBox.getSelectionModel().getSelectedIndex();
            drawGraph(canvas, names, pos, atoms, wAtoms, edges, sel[0]);
        });

        Label valuationLabel = new Label("—");
        valuationLabel.setStyle("-fx-text-fill:#a6e3a1;-fx-font-family:Monospaced;-fx-font-size:11px;-fx-wrap-text:true;");
        updateValuation(valuationLabel, 0, atoms, wAtoms);
        worldBox.setOnAction(e -> {
            int idx = worldBox.getSelectionModel().getSelectedIndex();
            sel[0] = idx;
            drawGraph(canvas, names, pos, atoms, wAtoms, edges, sel[0]);
            updateValuation(valuationLabel, idx, atoms, wAtoms);
        });

        Label lblOp = field("Opérateur :");
        ComboBox<String> opBox = new ComboBox<>();
        opBox.getItems().addAll("□  Nécessité (vrai dans TOUS les successeurs)",
                                "◊  Possibilité (vrai dans AU MOINS UN successeur)");
        opBox.getSelectionModel().selectFirst();
        opBox.setMaxWidth(Double.MAX_VALUE);

        Label lblAtom = field("Atome :");
        ComboBox<String> atomBox = new ComboBox<>();
        atomBox.getItems().addAll(atoms);
        atomBox.getSelectionModel().selectFirst();
        atomBox.setMaxWidth(Double.MAX_VALUE);

        Label resultLabel = new Label("—");
        resultLabel.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:#a6adc8;"
                + "-fx-wrap-text:true;-fx-padding:6 0;");

        Button btnEval = btn("Évaluer", "#89b4fa");
        btnEval.setOnAction(e -> {
            int wIdx  = worldBox.getSelectionModel().getSelectedIndex();
            boolean nec = opBox.getSelectionModel().getSelectedIndex() == 0;
            int aIdx  = atomBox.getSelectionModel().getSelectedIndex();
            boolean res = evalFormula(wIdx, nec, aIdx, wAtoms, edges);
            String op   = nec ? "□" : "◊";
            resultLabel.setText(op + "(" + atoms[aIdx] + ")\n  en " + names[wIdx]
                    + "  →  " + (res ? "VRAI ✓" : "FAUX ✗"));
            resultLabel.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-wrap-text:true;"
                    + "-fx-padding:6 0;-fx-text-fill:" + (res ? "#a6e3a1" : "#f38ba8") + ";");
        });

        // -- Section: properties --
        Separator sep1 = sep();
        Label secProps = section("Propriétés de R");
        GridPane propsGrid = buildPropsGrid(names.length, edges);

        // -- Section: full output --
        Separator sep2 = sep();
        Label secAll = section("Sortie complète");
        TextArea outArea = new TextArea();
        outArea.setEditable(false);
        outArea.setPrefRowCount(12);
        outArea.setStyle("-fx-background-color:#f5f5f5;-fx-text-fill:#1a1a2e;"
                + "-fx-font-family:Monospaced;-fx-font-size:10.5px;");

        Button btnAll = btn("Tout évaluer", "#45475a");
        btnAll.setStyle(btnAll.getStyle() + "-fx-text-fill:#cdd6f4;");
        btnAll.setOnAction(e -> {
            btnAll.setDisable(true);
            outArea.setText("Calcul en cours...");
            Task<String> task = new Task<>() {
                @Override protected String call() { return captureRun(isMusic); }
            };
            task.setOnSucceeded(ev -> { outArea.setText(task.getValue()); btnAll.setDisable(false); });
            task.setOnFailed(ev -> { outArea.setText("Erreur: " + task.getException()); btnAll.setDisable(false); });
            new Thread(task, "modal-run").start();
        });

        vbox.getChildren().addAll(
                secEval,
                lblWorld, worldBox,
                new Label("Valuation :") {{ setStyle("-fx-text-fill:#6c7086;-fx-font-size:11px;"); }},
                valuationLabel,
                lblOp, opBox,
                lblAtom, atomBox,
                btnEval, resultLabel,
                sep1, secProps, propsGrid,
                sep2, secAll, btnAll, outArea
        );
        VBox.setVgrow(outArea, Priority.ALWAYS);
        return vbox;
    }

    // ------------------------------------------------------------------
    private void drawGraph(Canvas canvas, String[] names, double[][] pos,
                           String[] atoms, int[][] wAtoms, int[][] edges, int selected) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double W = canvas.getWidth(), H = canvas.getHeight();
        gc.setFill(Color.web("#181825"));
        gc.fillRect(0, 0, W, H);

        final double R = 32;
        Color edgeColor = Color.web("#89b4fa");

        // Draw edges
        gc.setStroke(edgeColor);
        gc.setLineWidth(1.8);
        for (int[] e : edges) {
            int fi = e[0], ti = e[1];
            if (fi == ti) {
                // Self-loop: small oval above node
                double cx = pos[fi][0], cy = pos[fi][1];
                gc.strokeOval(cx - 14, cy - R - 28, 28, 28);
                // arrowhead pointing down into node
                double tipY = cy - R - 1;
                arrowHead(gc, cx, tipY, Math.PI / 2, edgeColor);
            } else {
                double fx = pos[fi][0], fy = pos[fi][1];
                double tx = pos[ti][0], ty = pos[ti][1];
                double angle = Math.atan2(ty - fy, tx - fx);
                double sx = fx + R * Math.cos(angle);
                double sy = fy + R * Math.sin(angle);
                double ex = tx - (R + 5) * Math.cos(angle);
                double ey = ty - (R + 5) * Math.sin(angle);
                gc.strokeLine(sx, sy, ex, ey);
                arrowHead(gc, ex, ey, angle, edgeColor);
            }
        }

        // Draw nodes
        for (int i = 0; i < names.length; i++) {
            double cx = pos[i][0], cy = pos[i][1];
            boolean isSel = (i == selected);
            String[] parts = names[i].split("=");
            String id   = parts[0];
            String role = parts.length > 1 ? parts[1] : "";

            // Circle fill
            gc.setFill(isSel ? Color.web("#cba6f7") : Color.web("#313244"));
            gc.fillOval(cx - R, cy - R, 2 * R, 2 * R);
            gc.setStroke(isSel ? Color.web("#f5c2e7") : edgeColor);
            gc.setLineWidth(isSel ? 2.5 : 1.5);
            gc.strokeOval(cx - R, cy - R, 2 * R, 2 * R);

            // Node ID inside circle
            gc.setFill(isSel ? Color.web("#1e1e2e") : Color.web("#cdd6f4"));
            gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
            gc.fillText(id, cx - id.length() * 3.7, cy - 4);

            // Role name below circle
            gc.setFill(Color.web("#cdd6f4"));
            gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 10));
            gc.fillText(role, cx - role.length() * 3, cy + R + 13);

            // Atom list shown when selected
            if (isSel) {
                gc.setFill(Color.web("#a6e3a1"));
                gc.setFont(Font.font("Monospaced", 9));
                StringBuilder sb = new StringBuilder("{ ");
                for (int ai : wAtoms[i]) {
                    sb.append(shortName(atoms[ai])).append(" ");
                }
                sb.append("}");
                gc.fillText(sb.toString(), cx - 60, cy + R + 27);
            }
        }
    }

    private void arrowHead(GraphicsContext gc, double tipX, double tipY,
                           double angle, Color color) {
        double len = 10, spread = 0.4;
        double x1 = tipX - len * Math.cos(angle - spread);
        double y1 = tipY - len * Math.sin(angle - spread);
        double x2 = tipX - len * Math.cos(angle + spread);
        double y2 = tipY - len * Math.sin(angle + spread);
        gc.setFill(color);
        gc.fillPolygon(new double[]{tipX, x1, x2}, new double[]{tipY, y1, y2}, 3);
    }

    private String shortName(String atom) {
        // keep only the last segment after underscore
        int i = atom.lastIndexOf('_');
        return i >= 0 ? atom.substring(i + 1) : atom;
    }

    private void updateValuation(Label lbl, int worldIdx, String[] atoms, int[][] wAtoms) {
        StringBuilder sb = new StringBuilder("{ ");
        for (int ai : wAtoms[worldIdx]) sb.append(atoms[ai]).append(",  ");
        if (sb.length() > 2) sb.setLength(sb.length() - 3);
        sb.append(" }");
        lbl.setText(sb.toString());
    }

    // ------------------------------------------------------------------
    private boolean evalFormula(int wIdx, boolean necessity, int aIdx,
                                int[][] wAtoms, int[][] edges) {
        if (necessity) {
            for (int[] e : edges) {
                if (e[0] == wIdx && !atomIn(aIdx, e[1], wAtoms)) return false;
            }
            return true; // vacuously true if no successors
        } else {
            for (int[] e : edges) {
                if (e[0] == wIdx && atomIn(aIdx, e[1], wAtoms)) return true;
            }
            return false;
        }
    }

    private boolean atomIn(int aIdx, int wIdx, int[][] wAtoms) {
        for (int a : wAtoms[wIdx]) if (a == aIdx) return true;
        return false;
    }

    // ------------------------------------------------------------------
    private GridPane buildPropsGrid(int n, int[][] edges) {
        List<Set<Integer>> succs = successors(n, edges);
        boolean refl = checkRefl(n, succs);
        boolean tran = checkTran(n, succs);
        boolean sym  = checkSym(n, succs);
        boolean ser  = checkSer(n, succs);

        String[][] rows = {
            {"Réflexive",  refl ? "OUI" : "NON", refl ? "#a6e3a1" : "#f38ba8"},
            {"Transitive", tran ? "OUI" : "NON", tran ? "#a6e3a1" : "#f38ba8"},
            {"Symétrique", sym  ? "OUI" : "NON", sym  ? "#a6e3a1" : "#f38ba8"},
            {"Sérielle",   ser  ? "OUI" : "NON", ser  ? "#a6e3a1" : "#f38ba8"}
        };

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(5);
        for (int i = 0; i < rows.length; i++) {
            Label k = new Label(rows[i][0] + " :"); k.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:12px;");
            Label v = new Label(rows[i][1]);
            v.setStyle("-fx-font-weight:bold;-fx-font-size:12px;-fx-text-fill:" + rows[i][2] + ";");
            grid.add(k, 0, i);
            grid.add(v, 1, i);
        }
        return grid;
    }

    private List<Set<Integer>> successors(int n, int[][] edges) {
        List<Set<Integer>> s = new ArrayList<>();
        for (int i = 0; i < n; i++) s.add(new HashSet<>());
        for (int[] e : edges) s.get(e[0]).add(e[1]);
        return s;
    }

    private boolean checkRefl(int n, List<Set<Integer>> s) {
        for (int i = 0; i < n; i++) if (!s.get(i).contains(i)) return false;
        return true;
    }

    private boolean checkTran(int n, List<Set<Integer>> s) {
        for (int w = 0; w < n; w++)
            for (int v : s.get(w))
                for (int u : s.get(v))
                    if (!s.get(w).contains(u)) return false;
        return true;
    }

    private boolean checkSym(int n, List<Set<Integer>> s) {
        for (int w = 0; w < n; w++)
            for (int v : s.get(w))
                if (!s.get(v).contains(w)) return false;
        return true;
    }

    private boolean checkSer(int n, List<Set<Integer>> s) {
        for (int w = 0; w < n; w++) if (s.get(w).isEmpty()) return false;
        return true;
    }

    // ------------------------------------------------------------------
    private String captureRun(boolean isMusic) {
        PrintStream oldOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, java.nio.charset.StandardCharsets.UTF_8)) {
            System.setOut(ps);
            if (isMusic) ModalLogicMusic.run();
            else         ModalLogicJustice.run();
        } finally {
            System.setOut(oldOut);
        }
        return baos.toString(java.nio.charset.StandardCharsets.UTF_8);
    }

    // -- UI helpers --
    private Label section(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight:bold;-fx-text-fill:#89b4fa;-fx-font-size:13px;");
        return l;
    }

    private Label field(String text) {
        Label l = new Label(text);
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
        b.setStyle("-fx-background-color:" + bg + ";-fx-font-weight:bold;-fx-font-size:12px;"
                + "-fx-padding:8 0;");
        return b;
    }
}
