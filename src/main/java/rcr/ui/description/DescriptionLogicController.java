package rcr.ui.description;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import rcr.description.DescriptionLogicJustice;
import rcr.description.DescriptionLogicMusic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class DescriptionLogicController {

    private static final String MUSIC_TBOX =
            "TBox — Industrie Musicale Algérienne\n"
            + "─────────────────────────────────────────────────────\n"
            + "Artiste        ≡ Personne ⊓ ∃produit.Oeuvre\n"
            + "Rappeur        ≡ Artiste ⊓ ∃pratique.Rap ⊓ ∀produit.TitreRap\n"
            + "RappeurAlgerien ≡ Rappeur ⊓ ∃chante_en.Darija\n"
            + "Label          ≡ Organisation ⊓ ∃distribue.Album\n"
            + "MajorLabel     ≡ Label ⊓ ∃signe.PlusDe100Artistes\n"
            + "IndependantLabel ≡ Label ⊓ ¬MajorLabel\n"
            + "Album          ≡ Oeuvre ⊓ ∃contient.Titre ⊓ ∃produit_par.Artiste\n"
            + "AlbumCertifie  ≡ Album ⊓ ∃recoit.Certification\n"
            + "AlbumOr        ≡ AlbumCertifie ⊓ ∃recoit.CertificationOr\n"
            + "ArtisteSigné   ≡ Artiste ⊓ ∃signe_chez.Label\n"
            + "ArtisteMajor   ≡ Artiste ⊓ ∃signe_chez.MajorLabel\n"
            + "ArtisteIndependant ≡ Artiste ⊓ ¬∃signe_chez.MajorLabel\n"
            + "ArtisteLibreDroits ≡ Artiste ⊓ ¬∃signe_chez.Label\n"
            + "─────────────────────────────────────────────────────\n"
            + "ABox\n"
            + "  soolking, feu, tif, flenn  : RappeurAlgerien\n"
            + "  universal                  : MajorLabel\n"
            + "  believe                    : IndependantLabel\n"
            + "  guerilla, derniere_heure   : Album\n"
            + "  signe_chez(soolking, universal)   signe_chez(feu, believe)\n"
            + "  recoit(guerilla, certOr)          recoit(derniere_heure, certPlatine)\n"
            + "  tif, flenn : ArtisteLibreDroits   [OWA → assertion explicite]\n"
            + "─────────────────────────────────────────────────────\n"
            + "Requêtes R1–R9 + R10 démo non-monotonie\n"
            + "  R10 : ajout signe_chez(flenn, universal)\n"
            + "        flenn : ArtisteLibreDroits  VRAI → FAUX";

    private static final String JUSTICE_TBOX =
            "TBox — Système Judiciaire\n"
            + "─────────────────────────────────────────────────────\n"
            + "Magistrat          ≡ ActeurJudiciaire ⊓ ∃siege_a.Tribunal\n"
            + "Juge               ≡ Magistrat ⊓ ∃preside.Audience\n"
            + "Procureur          ≡ Magistrat ⊓ ∃requiert.Peine\n"
            + "AccuseMineur       ≡ Accuse ⊓ ∃a_age.MoinsDe18Ans\n"
            + "AccuseRecidiviste  ≡ Accuse ⊓ ∃a_antecedent.Condamnation\n"
            + "AccusePresumInnocent ≡ Accuse ⊓ ¬Condamne\n"
            + "AccuseAvecTemoignage ≡ Accuse ⊓ ∃beneficie.TemoignageFavorable\n"
            + "CasSimple          ≡ Accuse ⊓ ¬AccuseRecidiviste ⊓ ¬AccuseMineur\n"
            + "CasComplexe        ≡ Accuse ⊓ (AccuseRecidiviste ⊔ AccuseMineur)\n"
            + "─────────────────────────────────────────────────────\n"
            + "ABox\n"
            + "  ali, omar, karim             : Accuse\n"
            + "  juge_salem                   : Juge\n"
            + "  avocat_riad                  : Avocat\n"
            + "  procureur_malik              : Procureur\n"
            + "  beneficie(ali, temoignage_ali:TemoignageFavorable)\n"
            + "  a_antecedent(omar, antecedent_omar:Condamnation)\n"
            + "  fait_objet(karim, crime_karim:CrimeGrave)\n"
            + "  ali  : AccusePresumInnocent + CasSimple   [OWA → assertion]\n"
            + "  karim: AccuseMineur                       [OWA → assertion]\n"
            + "─────────────────────────────────────────────────────\n"
            + "Requêtes R1–R11\n"
            + "  R9  : CasSimple ⊓ CasComplexe = ⊥  (disjoints)\n"
            + "  R10 : Juge ⊓ Procureur ≠ ⊥         (non disjoints)\n"
            + "  R11 : cohérence avec DefaultLogicJustice";

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
        tabMusic.setContent(buildPane(MUSIC_TBOX, true));

        Tab tabJustice = new Tab("  Système Judiciaire  ");
        tabJustice.setContent(buildPane(JUSTICE_TBOX, false));

        tabs.getTabs().addAll(tabMusic, tabJustice);
        root.setCenter(tabs);
        return root;
    }

    private VBox buildPane(String tbox, boolean isMusic) {
        VBox vbox = new VBox(12);
        vbox.setPadding(new Insets(18));
        vbox.setStyle("-fx-background-color:#1e1e2e;");

        Label lblTbox = section("TBox + ABox");

        TextArea tboxArea = new TextArea(tbox);
        tboxArea.setEditable(false);
        tboxArea.setPrefRowCount(15);
        tboxArea.setStyle("-fx-background-color:#f5f5f5;-fx-text-fill:#1a1a2e;"
                + "-fx-font-family:Monospaced;-fx-font-size:11.5px;");

        Label lblInfo = new Label(
                "HermiT calcule les appartenances de classe (isEntailed), les instances (getInstances),\n"
                + "les subsomptions et les disjonctions. OWA : l'absence de fait ne se déduit pas automatiquement.\n"
                + "La non-monotonie est démontrée via deux ontologies AVANT/APRÈS.");
        lblInfo.setStyle("-fx-text-fill:#6c7086;-fx-font-size:11px;-fx-wrap-text:true;");

        Button btnRun = new Button("▶  Lancer le raisonneur HermiT");
        btnRun.setMaxWidth(Double.MAX_VALUE);
        btnRun.setStyle("-fx-background-color:#a6e3a1;-fx-text-fill:#1e1e2e;"
                + "-fx-font-weight:bold;-fx-font-size:13px;-fx-padding:10 0;");

        Label lblOut = section("Résultats HermiT");
        TextArea outArea = new TextArea();
        outArea.setEditable(false);
        outArea.setStyle("-fx-background-color:#f5f5f5;-fx-text-fill:#1a1a2e;"
                + "-fx-font-family:Monospaced;-fx-font-size:11px;");
        VBox.setVgrow(outArea, Priority.ALWAYS);

        btnRun.setOnAction(e -> {
            btnRun.setDisable(true);
            outArea.setText("Initialisation de l'ontologie OWL et du raisonneur HermiT…\n"
                    + "(première exécution peut prendre quelques secondes)");
            Task<String> task = new Task<>() {
                @Override protected String call() { return captureRun(isMusic); }
            };
            task.setOnSucceeded(ev -> {
                outArea.setText(task.getValue());
                btnRun.setDisable(false);
            });
            task.setOnFailed(ev -> {
                outArea.setText("Erreur : " + task.getException().getMessage());
                btnRun.setDisable(false);
            });
            new Thread(task, "dl-run").start();
        });

        vbox.getChildren().addAll(
                lblTbox, tboxArea,
                lblInfo,
                new Separator(),
                btnRun,
                lblOut, outArea
        );
        return vbox;
    }

    private String captureRun(boolean isMusic) {
        PrintStream oldOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, java.nio.charset.StandardCharsets.UTF_8)) {
            System.setOut(ps);
            if (isMusic) DescriptionLogicMusic.run();
            else         DescriptionLogicJustice.run();
        } finally {
            System.setOut(oldOut);
        }
        return baos.toString(java.nio.charset.StandardCharsets.UTF_8);
    }

    private Label section(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight:bold;-fx-text-fill:#a6e3a1;-fx-font-size:13px;");
        return l;
    }
}
