package rcr.ui.semantic;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import rcr.semantic.SemanticNetworkExample;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * Controller JavaFX — Réseaux Sémantiques (version responsive).
 *
 * Nouveautés :
 *   - Canvas lié à la taille du panneau (ResizableCanvas)
 *   - Positions des nœuds en coordonnées relatives [0..1]
 *   - Zoom molette + Pan glisser-déposer
 *   - Bouton reset zoom/position
 *
 * Conventions cours Chap.5 USTHB :
 *   "C" = Concept → rectangle   "I" = Individu → ovale
 *   "S" = is-a strict  "NS" = is-a non-strict
 *   "ES"= exception stricte  "R" = relation  "M" = modal
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
    //  { id, label, type, rx, ry }   rx,ry ∈ [0..1]
    // =========================================================
    private static final Object[][] MUS_NODES = {
        {"Personne",      "Personne",              "C", 0.50, 0.05},
        {"OrgMusicale",   "Organisation\nMusicale", "C", 0.88, 0.05},
        {"MondeMusicale", "Monde Musical",          "C", 0.18, 0.05},
        {"Album",         "Album",                  "C", 0.72, 0.40},
        {"AlbumPlatine",  "Album Platine",          "C", 0.72, 0.55},
        {"Titre",         "Titre",                  "C", 0.90, 0.38},
        {"Droits",        "Droits",                 "C", 0.90, 0.20},
        {"Croyances",     "Croyances",              "C", 0.08, 0.42},
        {"Negations",     "Négations",              "C", 0.08, 0.60},
        {"Artiste",       "Artiste",                "I", 0.50, 0.18},
        {"ArtisteSigne",  "Artiste Signé",          "I", 0.36, 0.36},
        {"ArtisteIndep",  "Artiste Indép.",         "I", 0.62, 0.22},
        {"Label",         "Label",                  "I", 0.86, 0.18},
        {"Soolking",      "Soolking",               "I", 0.36, 0.55},
        {"Universal",     "Universal",              "I", 0.52, 0.38},
        {"MachiBessah",   "Machi Bessah",           "I", 0.50, 0.72},
        {"Fan",           "Fan",                    "I", 0.22, 0.18},
        {"C1",            "C1",                     "I", 0.08, 0.22},
        {"N1",            "N1",                     "I", 0.08, 0.78},
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
        {"Personne",         "Personne",           "C", 0.50, 0.04},
        {"ActeurJudiciaire", "Acteur Judiciaire",  "C", 0.28, 0.15},
        {"PartieProces",     "Partie au Procès",   "C", 0.72, 0.15},
        {"Culpabilite",      "Culpabilité",        "C", 0.88, 0.42},
        {"Innocence",        "Innocence\nprésumée","C", 0.88, 0.60},
        {"Preuve",           "Preuve",             "C", 0.88, 0.05},
        {"Condamnation",     "Condamnation",       "C", 0.88, 0.24},
        {"Croyances",        "Croyances",          "C", 0.10, 0.38},
        {"Negations",        "Négations",          "C", 0.10, 0.60},
        {"Juge",             "Juge",               "I", 0.12, 0.15},
        {"Avocat",           "Avocat",             "I", 0.26, 0.34},
        {"AvoDefense",       "Avocat\nDéfense",    "I", 0.26, 0.52},
        {"Procureur",        "Procureur",          "I", 0.46, 0.34},
        {"Temoin",           "Témoin",             "I", 0.12, 0.34},
        {"Accuse",           "Accusé",             "I", 0.64, 0.34},
        {"Recidiviste",      "Récidiviste",        "I", 0.64, 0.52},
        {"C1",               "C1",                "I", 0.10, 0.22},
        {"C2",               "C2",                "I", 0.26, 0.70},
        {"N1",               "N1",                "I", 0.10, 0.78},
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
        split.setDividerPositions(0.57);
        split.setStyle("-fx-background-color:#1e1e2e;");

        // ── État partagé ───────────────────────────────────────
        int[]       selected  = {-1};
        Set<String> m1Set     = new HashSet<>();
        Set<String> m2Set     = new HashSet<>();
        double[]    zoom      = {1.0};
        double[]    pan       = {0.0, 0.0};
        double[]    dragStart = {0.0, 0.0};

        // ── Canvas responsive ──────────────────────────────────
        ResizableCanvas canvas = new ResizableCanvas();
        canvas.setWidth(600);
        canvas.setHeight(500);

        Pane canvasPane = new Pane(canvas);
        canvasPane.setStyle("-fx-background-color:#181825;");
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        // Redessiner à chaque changement de taille
        canvas.widthProperty().addListener(o ->
                redraw(canvas, nodes, arcs, selected[0], m1Set, m2Set, zoom[0], pan));
        canvas.heightProperty().addListener(o ->
                redraw(canvas, nodes, arcs, selected[0], m1Set, m2Set, zoom[0], pan));

        // Zoom molette
        canvas.addEventHandler(ScrollEvent.SCROLL, ev -> {
            double f = ev.getDeltaY() > 0 ? 1.1 : 0.91;
            zoom[0] = Math.max(0.3, Math.min(zoom[0] * f, 4.0));
            redraw(canvas, nodes, arcs, selected[0], m1Set, m2Set, zoom[0], pan);
            ev.consume();
        });

        // Pan (glisser)
        canvas.setOnMousePressed(ev -> {
            dragStart[0] = ev.getX() - pan[0];
            dragStart[1] = ev.getY() - pan[1];
        });
        canvas.setOnMouseDragged(ev -> {
            pan[0] = ev.getX() - dragStart[0];
            pan[1] = ev.getY() - dragStart[1];
            redraw(canvas, nodes, arcs, selected[0], m1Set, m2Set, zoom[0], pan);
        });

        // Clic nœud
        canvas.setOnMouseClicked(ev -> {
            double W = canvas.getWidth(), H = canvas.getHeight();
            int hit = -1;
            for (int i = 0; i < nodes.length; i++) {
                double[] px = toPx(nodes[i], W, H, zoom[0], pan);
                double R = 34 * zoom[0];
                double dx = ev.getX() - px[0], dy = ev.getY() - px[1];
                if (dx*dx + dy*dy < R*R) { hit = i; break; }
            }
            selected[0] = (selected[0] == hit) ? -1 : hit;
            redraw(canvas, nodes, arcs, selected[0], m1Set, m2Set, zoom[0], pan);
        });

        Label hint = new Label("  🖱  Molette = zoom  •  Glisser = déplacer  •  Clic = sélectionner");
        hint.setStyle("-fx-text-fill:#6c7086;-fx-font-size:11px;-fx-padding:6 12;");
        VBox leftBox = new VBox(hint, canvasPane);
        VBox.setVgrow(canvasPane, Priority.ALWAYS);
        leftBox.setStyle("-fx-background-color:#181825;");

        // Panel droit
        VBox infoPanel = buildInfoPanel(canvas, nodes, arcs, selected,
                m1Set, m2Set, zoom, pan, question, m1Default, m2Default, answer);
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
                                int[] selected, Set<String> m1Set, Set<String> m2Set,
                                double[] zoom, double[] pan,
                                String question, String m1Default, String m2Default,
                                String answer) {
        VBox vbox = new VBox(12);
        vbox.setPadding(new Insets(16));
        vbox.setStyle("-fx-background-color:#1e1e2e;");

        // Légende
        Label secLeg = section("Légende des liens");
        GridPane legend = new GridPane();
        legend.setHgap(10); legend.setVgap(4);
        String[][] leg = {
            {"─────", "#a6e3a1", "is-a strict (S)"},
            {"─ ─ ─", "#89b4fa", "is-a non-strict (NS)"},
            {"╳────", "#f38ba8", "exception stricte (¬relation)"},
            {"─·─·─", "#f9e2af", "relation sémantique ordinaire"},
            //{"〜〜〜", "#cba6f7", "lien modal (agent, objet, argument)"},
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

        // Reset zoom
        Button btnReset = btn("⟳  Réinitialiser zoom / position", "#313244");
        btnReset.setStyle(btnReset.getStyle() + "-fx-text-fill:#cdd6f4;");
        btnReset.setOnAction(e -> {
            zoom[0] = 1.0; pan[0] = 0; pan[1] = 0;
            redraw(canvas, nodes, arcs, selected[0], m1Set, m2Set, zoom[0], pan);
        });

        // Question
        Separator sep1 = sep();
        Label secQ = section("Question de propagation");
        Label lblQ = new Label(question);
        lblQ.setStyle("-fx-text-fill:#f9e2af;-fx-font-size:12px;-fx-wrap-text:true;-fx-font-style:italic;");

        // Propagation
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
            m1Set.clear(); m1Set.addAll(ancestors(id1, arcs)); m1Set.add(id1);
            m2Set.clear(); m2Set.addAll(ancestors(id2, arcs)); m2Set.add(id2);
            Set<String> inter = new LinkedHashSet<>(m1Set);
            inter.retainAll(m2Set);
            redraw(canvas, nodes, arcs, selected[0], m1Set, m2Set, zoom[0], pan);
            StringBuilder sb = new StringBuilder();
            sb.append("M1 : "); appendLabels(sb, m1Set, nodes);
            sb.append("\n\nM2 : "); appendLabels(sb, m2Set, nodes);
            sb.append("\n\n");
            if (inter.isEmpty()) { sb.append("❌ Aucune intersection."); }
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
            resultProp.setText("—");
            resultProp.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:12px;-fx-wrap-text:true;");
            redraw(canvas, nodes, arcs, selected[0], m1Set, m2Set, zoom[0], pan);
        });

        // Réponse attendue
        Separator sep3 = sep();
        Label secAns = section("Réponse attendue");
        Label lblAns = new Label("✅  " + answer);
        lblAns.setStyle("-fx-text-fill:#a6e3a1;-fx-font-size:12px;-fx-wrap-text:true;-fx-font-weight:bold;");

        // Console
        Separator sep4 = sep();
        Label secOut = section("Sortie console complète");
        TextArea outArea = new TextArea();
        outArea.setEditable(false);
        outArea.setPrefRowCount(10);
        outArea.setStyle("-fx-background-color:#f5f5f5;-fx-text-fill:#1a1a2e;"
                + "-fx-font-family:Monospaced;-fx-font-size:10.5px;");
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
                sep3, secAns, lblAns,
                sep4, secOut, btnRun, outArea
        );
        return vbox;
    }

    // =========================================================
    //  DESSIN (zoom + pan)
    // =========================================================
    private void redraw(Canvas canvas, Object[][] nodes, String[][] arcs,
                        int selected, Set<String> m1, Set<String> m2,
                        double zoom, double[] pan) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double W = canvas.getWidth(), H = canvas.getHeight();
        if (W <= 0 || H <= 0) return;

        gc.setFill(Color.web("#181825"));
        gc.fillRect(0, 0, W, H);

        // Arcs
        for (String[] arc : arcs) {
            double[] fp = toPx(findNode(arc[0], nodes), W, H, zoom, pan);
            double[] tp = toPx(findNode(arc[1], nodes), W, H, zoom, pan);
            if (fp == null || tp == null) continue;

            Color col; double[] dash = null;
            switch (arc[3]) {
                case "NS": col = Color.web("#89b4fa"); dash = new double[]{6*zoom, 3*zoom}; break;
                case "ES": col = Color.web("#f38ba8"); dash = new double[]{4*zoom, 3*zoom}; break;
                case "EN": col = Color.web("#fab387"); dash = new double[]{4*zoom, 3*zoom}; break;
                case "M":  col = Color.web("#cba6f7"); dash = new double[]{2*zoom, 3*zoom}; break;
                case "R":  col = Color.web("#f9e2af"); break;
                default:   col = Color.web("#a6e3a1"); break;
            }

            double angle = Math.atan2(tp[1]-fp[1], tp[0]-fp[0]);
            double R = 32 * zoom;
            double sx = fp[0] + R*Math.cos(angle), sy = fp[1] + R*Math.sin(angle);
            double ex = tp[0] - (R+5*zoom)*Math.cos(angle), ey = tp[1] - (R+5*zoom)*Math.sin(angle);

            gc.setStroke(col);
            gc.setLineWidth(1.6 * zoom);
            gc.setLineDashes(dash != null ? dash : null);

            if (arc[2].startsWith("¬")) {
                double mx=(sx+ex)/2, my=(sy+ey)/2, cs=5*zoom;
                gc.setLineDashes(null);
                gc.strokeLine(mx-cs,my-cs,mx+cs,my+cs);
                gc.strokeLine(mx+cs,my-cs,mx-cs,my+cs);
            }

            gc.strokeLine(sx, sy, ex, ey);
            gc.setLineDashes(null);
            arrow(gc, ex, ey, angle, col, zoom);

            gc.setFill(col);
            gc.setFont(Font.font("Monospaced", 9 * zoom));
            gc.fillText(arc[2], (sx+ex)/2+3, (sy+ey)/2-3);
        }

        // Nœuds
        for (int i = 0; i < nodes.length; i++) {
            String id    = (String) nodes[i][0];
            String label = (String) nodes[i][1];
            String type  = (String) nodes[i][2];
            double[] px  = toPx(nodes[i], W, H, zoom, pan);
            if (px == null) continue;
            double cx = px[0], cy = px[1], R = 32*zoom;

            boolean isSel=i==selected, inM1=m1.contains(id), inM2=m2.contains(id), both=inM1&&inM2;
            Color fill, stroke;
            if      (both)  { fill=Color.web("#f9e2af"); stroke=Color.web("#fab387"); }
            else if (inM1)  { fill=Color.web("#a6e3a1"); stroke=Color.web("#40a02b"); }
            else if (inM2)  { fill=Color.web("#89b4fa"); stroke=Color.web("#1e66f5"); }
            else if (isSel) { fill=Color.web("#cba6f7"); stroke=Color.web("#f5c2e7"); }
            else            { fill=Color.web("#313244"); stroke=Color.web("#45475a"); }

            String[] lines = label.split("\n");
            int maxLen = Arrays.stream(lines).mapToInt(String::length).max().orElse(6);
            double bw = Math.max(74, maxLen * 7.2) * zoom;
            double bh = (20 + lines.length * 16.0) * zoom;

            gc.setFill(fill); gc.setStroke(stroke);
            gc.setLineWidth((isSel ? 2.5 : 1.5) * zoom);

            if (type.equals("C")) {
                gc.fillRoundRect(cx-bw/2, cy-bh/2, bw, bh, 10*zoom, 10*zoom);
                gc.strokeRoundRect(cx-bw/2, cy-bh/2, bw, bh, 10*zoom, 10*zoom);
            } else {
                gc.fillOval(cx-R, cy-R, 2*R, 2*R);
                gc.strokeOval(cx-R, cy-R, 2*R, 2*R);
            }

            gc.setFill((inM1||inM2||isSel) ? Color.web("#1e1e2e") : Color.web("#cdd6f4"));
            gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 9*zoom));
            for (int li=0; li<lines.length; li++) {
                double ty = cy - (lines.length-1)*7*zoom + li*14*zoom;
                gc.fillText(lines[li], cx - lines[li].length()*2.9*zoom, ty+3*zoom);
            }

            gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 10*zoom));
            if (both) { gc.setFill(Color.web("#f38ba8")); gc.fillText("M1∩M2", cx-18*zoom, cy+R+14*zoom); }
            else if (inM1) { gc.setFill(Color.web("#a6e3a1")); gc.fillText("M1", cx-7*zoom, cy+R+14*zoom); }
            else if (inM2) { gc.setFill(Color.web("#89b4fa")); gc.fillText("M2", cx-7*zoom, cy+R+14*zoom); }
        }
    }

    // =========================================================
    //  UTILITAIRES
    // =========================================================
    /** Coordonnées relatives [0..1] → pixels avec zoom et pan */
    private double[] toPx(Object[] node, double W, double H, double zoom, double[] pan) {
        if (node == null) return null;
        return new double[]{
            (double) node[3] * W * zoom + pan[0],
            (double) node[4] * H * zoom + pan[1]
        };
    }

    private Object[] findNode(String id, Object[][] nodes) {
        for (Object[] n : nodes) if (n[0].equals(id)) return n;
        return null;
    }

    private Set<String> ancestors(String startId, String[][] arcs) {
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
        double len=9*zoom, sp=0.4;
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