package rcr.ui.classical;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import rcr.classical.PropositionalLogicMusic;
import rcr.classical.PropositionalLogicJustice;
import rcr.classical.PredicateLogicMusic;
import rcr.classical.PredicateLogicJustice;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ClassicalLogicController {

    private static final String KB_PROP_MUSIC =
        "Domaine : Industrie musicale — Soolking, Feu, Tif, Flenn\n\n"
        + "Propositions atomiques :\n"
        + "  soolking, feu, tif, flenn        — est artiste (faits)\n"
        + "  signe_soolking, signe_feu        — signe a un label (faits)\n"
        + "  indep_feu                        — independant longtemps (fait)\n"
        + "  partage_soolking, partage_feu    — partage royalties\n"
        + "  royalties_tif, royalties_flenn   — royalties directes\n"
        + "  expsolo_feu                      — experience solo\n\n"
        + "Clauses CNF :\n"
        + "  Faits : soolking, feu, tif, flenn, signe_soolking, signe_feu, indep_feu\n"
        + "  c1 : !soolking | !signe_soolking | partage_soolking\n"
        + "  c2 : !feu | !signe_feu | partage_feu\n"
        + "  c3 : !tif | royalties_tif\n"
        + "  c4 : !flenn | royalties_flenn\n"
        + "  c5 : !indep_feu | expsolo_feu\n\n"
        + "Questions :\n"
        + "  Q1 : KB satisfiable ?\n"
        + "  Q2 : KB |= partage_soolking ?\n"
        + "  Q3 : KB |= expsolo_feu ?\n"
        + "  Q4 : KB + {signe_flenn} |= partage_flenn ?\n"
        + "  Q5 : KB + {!royalties_flenn} satisfiable ? (contradiction)";

    private static final String KB_PROP_JUSTICE =
        "Domaine : Systeme judiciaire — Ali, Omar, Karim\n\n"
        + "Propositions atomiques :\n"
        + "  ali, omar, karim               — accuses (faits)\n"
        + "  temoignage_ali                 — temoignage favorable (fait)\n"
        + "  recidiviste_omar               — recidiviste (fait)\n"
        + "  mineur_karim, crime_grave_karim — karim (faits)\n"
        + "  innocent_ali, innocent_omar    — presomption d'innocence\n"
        + "  acquitte_ali, peine_lourde_omar\n"
        + "  tribunal_enfants_karim\n\n"
        + "Clauses CNF :\n"
        + "  Faits : ali, omar, karim, temoignage_ali, recidiviste_omar,\n"
        + "          mineur_karim, crime_grave_karim\n"
        + "  c1 : !ali  | innocent_ali\n"
        + "  c2 : !ali  | !temoignage_ali | acquitte_ali\n"
        + "  c3 : !omar | innocent_omar\n"
        + "  c4 : !recidiviste_omar | peine_lourde_omar\n"
        + "  c5 : !mineur_karim | tribunal_enfants_karim\n"
        + "  c6 : !crime_grave_karim | !tribunal_enfants_karim  (pour Q6)\n\n"
        + "Questions :\n"
        + "  Q1 : KB (sans c6) satisfiable ?\n"
        + "  Q2 : KB |= acquitte_ali ?\n"
        + "  Q3 : KB |= innocent_ali ?\n"
        + "  Q4 : KB |= peine_lourde_omar ?\n"
        + "  Q5 : KB |= innocent_omar ?\n"
        + "  Q6 : KB + c6 satisfiable ? (conflit karim)\n"
        + "  Q7 : KB + {!innocent_omar} satisfiable ? (contradiction)";

    private static final String KB_PRED_MUSIC =
        "Domaine : Industrie musicale — soolking, feu, tif, flenn\n\n"
        + "Predicats :\n"
        + "  EstRappeurAlgerien/1, EstRappeur/1, EstArtiste/1, EstMusicien/1\n"
        + "  SigneLabel/1, AlbumSorti/1, RecitRoyalties/1, PartageRoyalties/1\n"
        + "  IndependantLongtemps/1, ExperienceSolo/1\n"
        + "  PlusDeDeuxAns/1, AlbumCertifie/1\n\n"
        + "Axiomes universels :\n"
        + "  ax1 : Ax (EstRappeurAlgerien(x) => EstRappeur(x))\n"
        + "  ax2 : Ax (EstRappeur(x) => EstArtiste(x))\n"
        + "  ax3 : Ax (EstArtiste(x) => EstMusicien(x))\n"
        + "  ax4 : Ax (EstArtiste(x) ^ AlbumSorti(x) => RecitRoyalties(x))\n"
        + "  ax5 : Ax (SigneLabel(x) => PartageRoyalties(x))\n"
        + "  ax6 : Ax (IndependantLongtemps(x) => ExperienceSolo(x))\n"
        + "  ax7 : Ax (PlusDeDeuxAns(x) ^ AlbumSorti(x) => AlbumCertifie(x))\n\n"
        + "Questions :\n"
        + "  Q1 : Ax (EstRappeurAlgerien(x) =>* RecitRoyalties(x)) ?\n"
        + "  Q2 : KB |= PartageRoyalties(soolking) ?\n"
        + "  Q3 : KB |= ExperienceSolo(feu) ?\n"
        + "  Q4 : KB |= AlbumCertifie(tif) ?\n"
        + "  Q5 : Ex ExperienceSolo(x) ?\n"
        + "  Q6 : Chaine ax1+ax2+ax3 => EstMusicien(soolking) ?";

    private static final String KB_PRED_JUSTICE =
        "Domaine : Systeme judiciaire — ali, omar, karim\n\n"
        + "Predicats :\n"
        + "  EstAccuse/1, ATemoignage/1, EstRecidiviste/1\n"
        + "  EstMineur/1, CrimeGrave/1\n"
        + "  DroitAuProces/1, DroitDefense/1\n"
        + "  CandidatAcquittement/1, PeineLourde/1, SuiviRenforce/1\n"
        + "  TribunalEnfants/1, SupJuridiction/1, NecessiteAvocatPublic/1\n\n"
        + "Axiomes universels :\n"
        + "  ax1 : Ax (EstAccuse(x) => DroitAuProces(x))\n"
        + "  ax2 : Ax (EstAccuse(x) => DroitDefense(x))\n"
        + "  ax3 : Ax (ATemoignage(x) => CandidatAcquittement(x))\n"
        + "  ax4 : Ax (EstRecidiviste(x) => PeineLourde(x))\n"
        + "  ax5 : Ax (EstMineur(x) => TribunalEnfants(x))\n"
        + "  ax6 : Ax (CrimeGrave(x) => SupJuridiction(x))\n"
        + "  ax7 : Ax (PeineLourde(x) => SuiviRenforce(x))\n"
        + "  ax8 : Ax (TribunalEnfants(x) ^ CrimeGrave(x) => NecessiteAvocatPublic(x))\n\n"
        + "Questions :\n"
        + "  Q1 : KB |= DroitAuProces(ali) ?\n"
        + "  Q2 : KB |= CandidatAcquittement(ali) ?\n"
        + "  Q3 : KB |= PeineLourde(omar) ?\n"
        + "  Q4 : KB |= SuiviRenforce(omar) ? (2 etapes)\n"
        + "  Q5 : KB |= TribunalEnfants(karim) ?\n"
        + "  Q6 : KB |= NecessiteAvocatPublic(karim) ? (conjonction)\n"
        + "  Q7 : Ex CandidatAcquittement(x) ?\n"
        + "  Q8 : Ax (EstAccuse(x) => DroitDefense(x)) verifie ?";

    public BorderPane buildUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#1e1e2e;");

        Label header = new Label("Logique Classique — Propositionnelle & Prédicats");
        header.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#cdd6f4;"
                + "-fx-padding:14 24;-fx-background-color:#181825;");
        root.setTop(header);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color:#1e1e2e;");

        Tab t1 = new Tab("  ∧∨ Prop. Musique  ");
        t1.setContent(buildPane(KB_PROP_MUSIC, 11, 0));

        Tab t2 = new Tab("  ∧∨ Prop. Justice  ");
        t2.setContent(buildPane(KB_PROP_JUSTICE, 13, 1));

        Tab t3 = new Tab("  ∀∃ Pred. Musique  ");
        t3.setContent(buildPane(KB_PRED_MUSIC, 12, 2));

        Tab t4 = new Tab("  ∀∃ Pred. Justice  ");
        t4.setContent(buildPane(KB_PRED_JUSTICE, 14, 3));

        tabs.getTabs().addAll(t1, t2, t3, t4);
        root.setCenter(tabs);
        return root;
    }

    private VBox buildPane(String theory, int rows, int mode) {
        VBox vbox = new VBox(12);
        vbox.setPadding(new Insets(18));
        vbox.setStyle("-fx-background-color:#1e1e2e;");

        Label lblTheory = section("Base de connaissances (KB)");

        TextArea theoryArea = new TextArea(theory);
        theoryArea.setEditable(false);
        theoryArea.setPrefRowCount(rows);
        theoryArea.setStyle("-fx-background-color:#f5f5f5;-fx-text-fill:#1a1a2e;"
                + "-fx-font-family:Monospaced;-fx-font-size:11.5px;");

        Label lblInfo = new Label(mode < 2
                ? "Le raisonneur SAT4J verifie la satisfiabilite et l'entailment par refutation.\n"
                  + "KB |= phi  ssi  KB + {!phi} est UNSAT.\n"
                  + "La contradiction est detectee quand KB + {clause} est UNSAT."
                : "Inference par chainage avant (forward chaining) sur le domaine fini.\n"
                  + "Les axiomes universels Ax(P(x) => Q(x)) sont instancies pour chaque individu.\n"
                  + "Les requetes existentielles Ex(P(x)) cherchent un temoin dans les faits derives.");
        lblInfo.setStyle("-fx-text-fill:#6c7086;-fx-font-size:11px;-fx-wrap-text:true;");

        Button btnRun = new Button("▶  Tout inférer");
        btnRun.setMaxWidth(Double.MAX_VALUE);
        btnRun.setStyle("-fx-background-color:#f38ba8;-fx-text-fill:#1e1e2e;"
                + "-fx-font-weight:bold;-fx-font-size:13px;-fx-padding:10 0;");

        Label lblOut = section("Résultats");
        TextArea outArea = new TextArea();
        outArea.setEditable(false);
        outArea.setStyle("-fx-background-color:#f5f5f5;-fx-text-fill:#1a1a2e;"
                + "-fx-font-family:Monospaced;-fx-font-size:11px;");
        VBox.setVgrow(outArea, Priority.ALWAYS);

        btnRun.setOnAction(e -> {
            btnRun.setDisable(true);
            outArea.setText("Calcul en cours…");
            Task<String> task = new Task<>() {
                @Override protected String call() { return captureRun(mode); }
            };
            task.setOnSucceeded(ev -> {
                outArea.setText(task.getValue());
                btnRun.setDisable(false);
            });
            task.setOnFailed(ev -> {
                outArea.setText("Erreur : " + task.getException().getMessage());
                btnRun.setDisable(false);
            });
            new Thread(task, "classical-run").start();
        });

        vbox.getChildren().addAll(
                lblTheory, theoryArea,
                lblInfo,
                new Separator(),
                btnRun,
                lblOut, outArea
        );
        return vbox;
    }

    private String captureRun(int mode) {
        PrintStream oldOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, java.nio.charset.StandardCharsets.UTF_8)) {
            System.setOut(ps);
            switch (mode) {
                case 0: PropositionalLogicMusic.run();   break;
                case 1: PropositionalLogicJustice.run(); break;
                case 2: PredicateLogicMusic.run();       break;
                case 3: PredicateLogicJustice.run();     break;
            }
        } finally {
            System.setOut(oldOut);
        }
        return baos.toString(java.nio.charset.StandardCharsets.UTF_8);
    }

    private Label section(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight:bold;-fx-text-fill:#f38ba8;-fx-font-size:13px;");
        return l;
    }
}
