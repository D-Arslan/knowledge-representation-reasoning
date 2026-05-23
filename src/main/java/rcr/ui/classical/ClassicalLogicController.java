package rcr.ui.classical;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.tweetyproject.logics.pl.syntax.*;
import rcr.classical.PredicateLogicJustice;
import rcr.classical.PredicateLogicMusic;
import rcr.classical.PropositionalLogicJustice;
import rcr.classical.PropositionalLogicMusic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller JavaFX — Logique Classique (refonte interactive).
 *
 * Apports par rapport à la version précédente (TextArea + bouton "Tout inférer") :
 *   - Pour les onglets Propositionnels : sélecteur de proposition + bouton
 *     « KB |= φ ? » avec résultat coloré, et bouton « KB satisfiable ? ».
 *     L'utilisateur peut aussi ajouter temporairement une clause à la KB.
 *   - Pour les onglets Prédicats : affichage des faits dérivés (forward chaining)
 *     groupés par prédicat, sélecteur (Prédicat × Individu) + bouton « Dérivé ? »
 *     pour tester une formule, plus une requête existentielle ∃x P(x).
 *   - Historique des requêtes coloré ✅ vert / ❌ rouge.
 *   - Bouton « Tout inférer » conservé pour la démo console.
 */
public class ClassicalLogicController {

    public BorderPane buildUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#1e1e2e;");

        Label header = new Label("Logique Classique — Propositionnelle (SAT4J) & Prédicats (forward chaining)");
        header.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:#cdd6f4;"
                + "-fx-padding:14 24;-fx-background-color:#181825;");
        root.setTop(header);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color:#1e1e2e;");

        tabs.getTabs().add(new Tab("  ∧∨ Prop · Musique  ",
                buildPropPane(PropDomain.MUSIC)));
        tabs.getTabs().add(new Tab("  ∧∨ Prop · Justice  ",
                buildPropPane(PropDomain.JUSTICE)));
        tabs.getTabs().add(new Tab("  ∀∃ Pred · Musique  ",
                buildPredPane(PredDomain.MUSIC)));
        tabs.getTabs().add(new Tab("  ∀∃ Pred · Justice  ",
                buildPredPane(PredDomain.JUSTICE)));

        root.setCenter(tabs);
        return root;
    }

    // =========================================================
    //  PANE PROPOSITIONNEL (Music / Justice)
    // =========================================================
    private enum PropDomain { MUSIC, JUSTICE }

    private SplitPane buildPropPane(PropDomain domain) {
        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.HORIZONTAL);
        split.setDividerPositions(0.45);
        split.setStyle("-fx-background-color:#1e1e2e;");

        PlBeliefSet kb = (domain == PropDomain.MUSIC)
                ? PropositionalLogicMusic.buildKB()
                : PropositionalLogicJustice.buildKB();

        split.getItems().addAll(
                buildPropKbPane(domain, kb),
                buildPropQueryPane(domain, kb)
        );
        return split;
    }

    private VBox buildPropKbPane(PropDomain domain, PlBeliefSet kb) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(14));
        vbox.setStyle("-fx-background-color:#1e1e2e;");

        Label title = section("Base de connaissances (CNF)");
        Label desc = new Label(
                "Faits + clauses sous forme normale conjonctive.\n"
              + "Le raisonneur SAT4J vérifie satisfiabilité et entailment par réfutation.");
        desc.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:11px;-fx-wrap-text:true;");

        // Séparer faits (clauses unitaires) et clauses composées
        List<String> faits = new ArrayList<>();
        List<String> clauses = new ArrayList<>();
        for (PlFormula f : kb) {
            String s = f.toString();
            if (s.contains("||") || s.contains("|")) clauses.add(s);
            else faits.add(s);
        }
        Collections.sort(faits);

        Label lblFaits = field("Faits (atomes positifs) :");
        TextArea taFaits = new TextArea(String.join("\n", faits));
        taFaits.setEditable(false);
        taFaits.setPrefRowCount(Math.min(8, Math.max(3, faits.size())));
        taFaits.setStyle("-fx-control-inner-background:#181825;-fx-background-color:#181825;"
                + "-fx-text-fill:#a6e3a1;-fx-font-family:Monospaced;-fx-font-size:11.5px;");

        Label lblClauses = field("Clauses (CNF) :");
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (String c : clauses) sb.append("c").append(i++).append(" : ").append(c).append("\n");
        TextArea taClauses = new TextArea(sb.toString());
        taClauses.setEditable(false);
        taClauses.setPrefRowCount(Math.min(10, Math.max(3, clauses.size())));
        taClauses.setStyle("-fx-control-inner-background:#181825;-fx-background-color:#181825;"
                + "-fx-text-fill:#cdd6f4;-fx-font-family:Monospaced;-fx-font-size:11.5px;");
        VBox.setVgrow(taClauses, Priority.ALWAYS);

        // ── Bouton "Tout inférer" (capture console) ──
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#45475a;");

        Label lblConsole = field("Console (Q1–Q7 du domaine) :");
        TextArea consoleOut = new TextArea();
        consoleOut.setEditable(false);
        consoleOut.setPrefRowCount(8);
        consoleOut.setStyle("-fx-control-inner-background:#181825;-fx-background-color:#181825;"
                + "-fx-text-fill:#cdd6f4;-fx-font-family:Monospaced;-fx-font-size:10.5px;");
        Button btnConsole = new Button("▶  Lancer toutes les questions du domaine (console)");
        btnConsole.setMaxWidth(Double.MAX_VALUE);
        btnConsole.setStyle("-fx-background-color:#45475a;-fx-text-fill:#cdd6f4;"
                + "-fx-font-size:11.5px;-fx-padding:8 0;");
        btnConsole.setOnAction(e -> {
            btnConsole.setDisable(true);
            consoleOut.setText("Calcul…");
            Task<String> t = new Task<>() {
                @Override protected String call() {
                    return captureRun(() -> {
                        if (domain == PropDomain.MUSIC) PropositionalLogicMusic.run();
                        else                            PropositionalLogicJustice.run();
                    });
                }
            };
            t.setOnSucceeded(ev -> { consoleOut.setText(t.getValue()); btnConsole.setDisable(false); });
            t.setOnFailed(ev   -> { consoleOut.setText("Erreur : " + t.getException()); btnConsole.setDisable(false); });
            new Thread(t, "prop-console").start();
        });

        vbox.getChildren().addAll(title, desc,
                lblFaits, taFaits, lblClauses, taClauses,
                sep, lblConsole, consoleOut, btnConsole);
        return vbox;
    }

    private VBox buildPropQueryPane(PropDomain domain, PlBeliefSet kb) {
        VBox vbox = new VBox(12);
        vbox.setPadding(new Insets(14));
        vbox.setStyle("-fx-background-color:#1e1e2e;");

        Label title = section("Requêtes interactives (SAT4J)");
        Label desc = new Label(
                "Sélectionne une proposition et teste l'entailment KB |= φ.\n"
              + "Tu peux aussi ajouter temporairement une formule pour observer\n"
              + "l'effet (monotonie, contradiction).");
        desc.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:11px;-fx-wrap-text:true;");

        // Liste des propositions extraite de la KB
        Set<String> propNames = new TreeSet<>();
        for (PlFormula f : kb) {
            for (Proposition p : f.getAtoms()) propNames.add(p.getName());
        }
        List<String> propList = new ArrayList<>(propNames);

        // ── Log historique ──
        TextFlowLog log = new TextFlowLog();
        log.append("→ Aucune requête lancée.", "#6c7086");

        // ── Test satisfiabilité ──
        Label lblSat = field("Test de satisfiabilité de la KB :");
        Button btnSat = new Button("▶  KB est-elle satisfiable ?");
        btnSat.setMaxWidth(Double.MAX_VALUE);
        btnSat.setStyle("-fx-background-color:#89b4fa;-fx-text-fill:#1e1e2e;"
                + "-fx-font-weight:bold;-fx-padding:8 0;");
        btnSat.setOnAction(e -> {
            boolean sat = (domain == PropDomain.MUSIC)
                    ? PropositionalLogicMusic.isSatisfiable(kb)
                    : PropositionalLogicJustice.isSatisfiable(kb);
            log.append((sat ? "✅" : "❌") + " KB satisfiable ?  →  " + sat,
                    sat ? "#a6e3a1" : "#f38ba8");
        });

        // ── Test d'entailment ──
        Separator sep1 = sep();
        Label lblEnt = field("Test d'entailment  KB |= φ :");
        ComboBox<String> propBox = new ComboBox<>();
        propBox.getItems().addAll(propList);
        if (!propList.isEmpty()) propBox.getSelectionModel().select(0);
        propBox.setMaxWidth(Double.MAX_VALUE);
        CheckBox negBox = new CheckBox("Tester la négation (KB |= ¬φ)");
        negBox.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:11.5px;");

        Button btnEnt = new Button("▶  Tester  KB |= φ");
        btnEnt.setMaxWidth(Double.MAX_VALUE);
        btnEnt.setStyle("-fx-background-color:#cba6f7;-fx-text-fill:#1e1e2e;"
                + "-fx-font-weight:bold;-fx-padding:8 0;");
        btnEnt.setOnAction(e -> {
            String name = propBox.getValue();
            if (name == null) return;
            Proposition p = new Proposition(name);
            PlFormula phi = negBox.isSelected() ? new Negation(p) : p;
            boolean res = (domain == PropDomain.MUSIC)
                    ? PropositionalLogicMusic.entails(kb, phi)
                    : PropositionalLogicJustice.entails(kb, phi);
            String label = "KB |= " + (negBox.isSelected() ? "¬" : "") + name;
            log.append((res ? "✅" : "❌") + " " + label + "  →  " + res,
                    res ? "#a6e3a1" : "#f38ba8");
        });

        // ── Ajout temporaire d'une formule ──
        Separator sep2 = sep();
        Label lblAdd = field("Ajouter temporairement une formule à la KB :");
        Label lblAddDesc = new Label("Sélectionne une proposition et ajoute-la (ou sa négation) "
                + "à une copie de la KB, puis teste satisfiabilité ou entailment.");
        lblAddDesc.setStyle("-fx-text-fill:#6c7086;-fx-font-size:10.5px;-fx-wrap-text:true;");
        ComboBox<String> addBox = new ComboBox<>();
        addBox.getItems().addAll(propList);
        if (!propList.isEmpty()) addBox.getSelectionModel().select(0);
        addBox.setMaxWidth(Double.MAX_VALUE);
        CheckBox addNegBox = new CheckBox("Ajouter la négation (¬φ)");
        addNegBox.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:11.5px;");

        Button btnAddSat = new Button("▶  KB + {φ}  satisfiable ?");
        btnAddSat.setMaxWidth(Double.MAX_VALUE);
        btnAddSat.setStyle("-fx-background-color:#fab387;-fx-text-fill:#1e1e2e;"
                + "-fx-font-weight:bold;-fx-padding:8 0;");
        btnAddSat.setOnAction(e -> {
            String name = addBox.getValue();
            if (name == null) return;
            Proposition p = new Proposition(name);
            PlFormula phi = addNegBox.isSelected() ? new Negation(p) : p;
            PlBeliefSet kbExt = new PlBeliefSet();
            kbExt.addAll(kb);
            kbExt.add(phi);
            boolean sat = (domain == PropDomain.MUSIC)
                    ? PropositionalLogicMusic.isSatisfiable(kbExt)
                    : PropositionalLogicJustice.isSatisfiable(kbExt);
            String label = "KB + {" + (addNegBox.isSelected() ? "¬" : "") + name + "}  satisfiable ?";
            log.append((sat ? "✅" : "❌ (contradiction)") + " " + label + "  →  " + sat,
                    sat ? "#a6e3a1" : "#f38ba8");
        });

        // ── Historique ──
        Separator sep3 = sep();
        Label lblHist = section("Historique des requêtes");
        ScrollPane histScroll = new ScrollPane(log.flow);
        histScroll.setFitToWidth(true);
        histScroll.setStyle("-fx-background-color:#181825;-fx-background:#181825;");
        VBox.setVgrow(histScroll, Priority.ALWAYS);

        Button btnClear = new Button("✕  Effacer historique");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setStyle("-fx-background-color:#45475a;-fx-text-fill:#cdd6f4;"
                + "-fx-font-size:11px;-fx-padding:6 0;");
        btnClear.setOnAction(e -> {
            log.clear();
            log.append("→ Historique effacé.", "#6c7086");
        });

        vbox.getChildren().addAll(title, desc,
                lblSat, btnSat,
                sep1, lblEnt, propBox, negBox, btnEnt,
                sep2, lblAdd, lblAddDesc, addBox, addNegBox, btnAddSat,
                sep3, lblHist, histScroll, btnClear);
        return vbox;
    }

    // =========================================================
    //  PANE PRÉDICAT (Music / Justice)
    // =========================================================
    private enum PredDomain { MUSIC, JUSTICE }

    private SplitPane buildPredPane(PredDomain domain) {
        SplitPane split = new SplitPane();
        split.setOrientation(Orientation.HORIZONTAL);
        split.setDividerPositions(0.50);
        split.setStyle("-fx-background-color:#1e1e2e;");

        String[] individus = (domain == PredDomain.MUSIC)
                ? PredicateLogicMusic.INDIVIDUS : PredicateLogicJustice.INDIVIDUS;
        String[] predicats = (domain == PredDomain.MUSIC)
                ? PredicateLogicMusic.PREDICATS : PredicateLogicJustice.PREDICATS;
        Set<String> faitsBase = (domain == PredDomain.MUSIC)
                ? PredicateLogicMusic.buildFacts() : PredicateLogicJustice.buildFacts();
        Set<String> derives = (domain == PredDomain.MUSIC)
                ? PredicateLogicMusic.deriverTout() : PredicateLogicJustice.deriverTout();

        split.getItems().addAll(
                buildPredFactsPane(domain, faitsBase, derives, predicats, individus),
                buildPredQueryPane(domain, derives, predicats, individus)
        );
        return split;
    }

    private VBox buildPredFactsPane(PredDomain domain, Set<String> faitsBase,
                                     Set<String> derives, String[] predicats, String[] individus) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(14));
        vbox.setStyle("-fx-background-color:#1e1e2e;");

        Label title = section("Forward chaining — faits dérivés");
        Label desc = new Label(String.format(
                "Individus : %s\n"
              + "Faits de base : %d  ·  Faits dérivés (point fixe) : %d  ·  Inférés : %d",
                String.join(", ", individus),
                faitsBase.size(), derives.size(), derives.size() - faitsBase.size()));
        desc.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:11.5px;");

        // Grouper par prédicat
        Map<String, List<String>> parPredicat = new TreeMap<>();
        for (String p : predicats) parPredicat.put(p, new ArrayList<>());
        for (String fait : derives) {
            int paren = fait.indexOf('(');
            if (paren > 0) {
                String pred = fait.substring(0, paren);
                parPredicat.computeIfAbsent(pred, k -> new ArrayList<>()).add(fait);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> e : parPredicat.entrySet()) {
            if (e.getValue().isEmpty()) continue;
            sb.append(e.getKey()).append(" :\n");
            Collections.sort(e.getValue());
            for (String f : e.getValue()) {
                String marker = faitsBase.contains(f) ? "  · " : "  ⇒ ";
                sb.append(marker).append(f).append("\n");
            }
            sb.append("\n");
        }

        Label lblLegend = new Label("Légende :  ·  fait initial    ⇒  fait dérivé par chaînage avant");
        lblLegend.setStyle("-fx-text-fill:#6c7086;-fx-font-size:11px;");

        TextArea factsArea = new TextArea(sb.toString());
        factsArea.setEditable(false);
        factsArea.setStyle("-fx-control-inner-background:#181825;-fx-background-color:#181825;"
                + "-fx-text-fill:#cdd6f4;-fx-font-family:Monospaced;-fx-font-size:11.5px;");
        VBox.setVgrow(factsArea, Priority.ALWAYS);

        // Bouton console
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#45475a;");
        Label lblConsole = field("Console (Q1–Q8 du domaine) :");
        TextArea consoleOut = new TextArea();
        consoleOut.setEditable(false);
        consoleOut.setPrefRowCount(6);
        consoleOut.setStyle("-fx-control-inner-background:#181825;-fx-background-color:#181825;"
                + "-fx-text-fill:#cdd6f4;-fx-font-family:Monospaced;-fx-font-size:10.5px;");
        Button btnConsole = new Button("▶  Lancer toutes les questions du domaine (console)");
        btnConsole.setMaxWidth(Double.MAX_VALUE);
        btnConsole.setStyle("-fx-background-color:#45475a;-fx-text-fill:#cdd6f4;"
                + "-fx-font-size:11.5px;-fx-padding:8 0;");
        btnConsole.setOnAction(e -> {
            btnConsole.setDisable(true);
            consoleOut.setText("Calcul…");
            Task<String> t = new Task<>() {
                @Override protected String call() {
                    return captureRun(() -> {
                        if (domain == PredDomain.MUSIC) PredicateLogicMusic.run();
                        else                            PredicateLogicJustice.run();
                    });
                }
            };
            t.setOnSucceeded(ev -> { consoleOut.setText(t.getValue()); btnConsole.setDisable(false); });
            t.setOnFailed(ev   -> { consoleOut.setText("Erreur : " + t.getException()); btnConsole.setDisable(false); });
            new Thread(t, "pred-console").start();
        });

        vbox.getChildren().addAll(title, desc, lblLegend, factsArea,
                sep, lblConsole, consoleOut, btnConsole);
        return vbox;
    }

    private VBox buildPredQueryPane(PredDomain domain, Set<String> derives,
                                     String[] predicats, String[] individus) {
        VBox vbox = new VBox(12);
        vbox.setPadding(new Insets(14));
        vbox.setStyle("-fx-background-color:#1e1e2e;");

        Label title = section("Requêtes interactives (FOL ground)");
        Label desc = new Label(
                "Teste si une formule atomique P(a) appartient à la clôture déductive,\n"
              + "ou si une requête existentielle ∃x P(x) admet un témoin.");
        desc.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:11px;-fx-wrap-text:true;");

        TextFlowLog log = new TextFlowLog();
        log.append("→ Aucune requête lancée.", "#6c7086");

        // ── Test P(a) ──
        Label lblAtom = field("Test atomique :  P(a) est-il dérivé ?");
        ComboBox<String> predBox = new ComboBox<>();
        predBox.getItems().addAll(predicats);
        predBox.getSelectionModel().select(0);
        predBox.setMaxWidth(Double.MAX_VALUE);
        ComboBox<String> indBox = new ComboBox<>();
        indBox.getItems().addAll(individus);
        indBox.getSelectionModel().select(0);
        indBox.setMaxWidth(Double.MAX_VALUE);

        Button btnAtom = new Button("▶  Tester  P(a)");
        btnAtom.setMaxWidth(Double.MAX_VALUE);
        btnAtom.setStyle("-fx-background-color:#cba6f7;-fx-text-fill:#1e1e2e;"
                + "-fx-font-weight:bold;-fx-padding:8 0;");
        btnAtom.setOnAction(e -> {
            String p = predBox.getValue();
            String a = indBox.getValue();
            if (p == null || a == null) return;
            String formula = p + "(" + a + ")";
            boolean res = derives.contains(formula);
            log.append((res ? "✅" : "❌") + " " + formula + "  →  " + (res ? "dérivé" : "non dérivé"),
                    res ? "#a6e3a1" : "#f38ba8");
        });

        // ── Test universel ∀x P(x) ──
        Separator sep1 = sep();
        Label lblUniv = field("Test universel :  ∀x P(x) ?");
        Label lblUnivDesc = new Label("Vérifie si P(x) est dérivé pour TOUS les individus.");
        lblUnivDesc.setStyle("-fx-text-fill:#6c7086;-fx-font-size:10.5px;-fx-wrap-text:true;");
        ComboBox<String> univBox = new ComboBox<>();
        univBox.getItems().addAll(predicats);
        univBox.getSelectionModel().select(0);
        univBox.setMaxWidth(Double.MAX_VALUE);

        Button btnUniv = new Button("▶  Tester  ∀x P(x)");
        btnUniv.setMaxWidth(Double.MAX_VALUE);
        btnUniv.setStyle("-fx-background-color:#89b4fa;-fx-text-fill:#1e1e2e;"
                + "-fx-font-weight:bold;-fx-padding:8 0;");
        btnUniv.setOnAction(e -> {
            String p = univBox.getValue();
            if (p == null) return;
            List<String> manquants = new ArrayList<>();
            for (String x : individus) {
                if (!derives.contains(p + "(" + x + ")")) manquants.add(x);
            }
            boolean ok = manquants.isEmpty();
            String label = "∀x " + p + "(x)";
            String suffix = ok ? "" : "   contre-exemples : {" + String.join(", ", manquants) + "}";
            log.append((ok ? "✅" : "❌") + " " + label + "  →  " + ok + suffix,
                    ok ? "#a6e3a1" : "#f38ba8");
        });

        // ── Test existentiel ∃x P(x) ──
        Separator sep2 = sep();
        Label lblExist = field("Test existentiel :  ∃x P(x) ?");
        ComboBox<String> existBox = new ComboBox<>();
        existBox.getItems().addAll(predicats);
        existBox.getSelectionModel().select(0);
        existBox.setMaxWidth(Double.MAX_VALUE);

        Button btnExist = new Button("▶  Tester  ∃x P(x)");
        btnExist.setMaxWidth(Double.MAX_VALUE);
        btnExist.setStyle("-fx-background-color:#fab387;-fx-text-fill:#1e1e2e;"
                + "-fx-font-weight:bold;-fx-padding:8 0;");
        btnExist.setOnAction(e -> {
            String p = existBox.getValue();
            if (p == null) return;
            Optional<String> witness = Arrays.stream(individus)
                    .filter(x -> derives.contains(p + "(" + x + ")"))
                    .findFirst();
            String label = "∃x " + p + "(x)";
            if (witness.isPresent()) {
                log.append("✅ " + label + "  →  true   témoin : " + witness.get(), "#a6e3a1");
            } else {
                log.append("❌ " + label + "  →  false  (aucun témoin)", "#f38ba8");
            }
        });

        // ── Historique ──
        Separator sep3 = sep();
        Label lblHist = section("Historique des requêtes");
        ScrollPane histScroll = new ScrollPane(log.flow);
        histScroll.setFitToWidth(true);
        histScroll.setStyle("-fx-background-color:#181825;-fx-background:#181825;");
        VBox.setVgrow(histScroll, Priority.ALWAYS);

        Button btnClear = new Button("✕  Effacer historique");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setStyle("-fx-background-color:#45475a;-fx-text-fill:#cdd6f4;"
                + "-fx-font-size:11px;-fx-padding:6 0;");
        btnClear.setOnAction(e -> {
            log.clear();
            log.append("→ Historique effacé.", "#6c7086");
        });

        vbox.getChildren().addAll(title, desc,
                lblAtom, predBox, indBox, btnAtom,
                sep1, lblUniv, lblUnivDesc, univBox, btnUniv,
                sep2, lblExist, existBox, btnExist,
                sep3, lblHist, histScroll, btnClear);
        return vbox;
    }

    // =========================================================
    //  UTILITAIRES
    // =========================================================
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

    private String captureRun(Runnable r) {
        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, java.nio.charset.StandardCharsets.UTF_8)) {
            System.setOut(ps);
            r.run();
        } finally {
            System.setOut(old);
        }
        return baos.toString(java.nio.charset.StandardCharsets.UTF_8);
    }

    private Label section(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight:bold;-fx-text-fill:#f38ba8;-fx-font-size:13px;");
        return l;
    }
    private Label field(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:#a6adc8;-fx-font-size:11.5px;");
        return l;
    }
    private Separator sep() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color:#45475a;");
        return s;
    }
}
