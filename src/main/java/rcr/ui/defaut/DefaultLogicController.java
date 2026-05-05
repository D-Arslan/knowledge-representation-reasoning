package rcr.ui.defaut;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import rcr.defaut.DefaultLogicJustice;
import rcr.defaut.DefaultLogicMusic;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class DefaultLogicController {

    private static final String JUSTICE_THEORY =
            "d1 : Accuse(x)      :: Innocent(x)           / Innocent(x)\n"
            + "d2 : Recidiviste(x) :: PeineLourde(x)         / PeineLourde(x)\n"
            + "d3 : Mineur(x)      :: TribunalEnfants(x)     / TribunalEnfants(x)\n"
            + "d4 : CrimeGrave(x)  :: !TribunalEnfants(x)   / !TribunalEnfants(x)\n"
            + "d5 : Temoignage(x)  :: Acquittement(x)        / Acquittement(x)\n"
            + "d6 : Recidiviste(x) :: !Innocent(x)           / !Innocent(x)\n"
            + "\n"
            + "Individus :\n"
            + "  ali     : Accuse + Temoignage              → aucun conflit\n"
            + "  omar    : Accuse + Recidiviste             → conflit d1 vs d6\n"
            + "  karim   : Accuse + Mineur + CrimeGrave     → conflit d3 vs d4\n"
            + "  ali+NM  : + Recidiviste(ali)               → démo non-monotonie";

    private static final String MUSIC_THEORY =
            "d1 : EstArtiste(x)                    :: LibreDroits(x)       / LibreDroits(x)\n"
            + "d2 : SigneLabel(x)                    :: !LibreDroits(x)      / !LibreDroits(x)\n"
            + "d3 : EstArtiste(x) && AlbumSorti(x)  :: Royalties(x)         / Royalties(x)\n"
            + "d4 : SigneLabel(x)                    :: PartageRoyalties(x)  / PartageRoyalties(x)\n"
            + "d5 : IndependantLongtemps(x)          :: ExperienceSolo(x)    / ExperienceSolo(x)\n"
            + "d6 : NouveauContrat(x)               :: !LibreDroits(x)      / !LibreDroits(x)\n"
            + "d7 : PlusDeDeuxAns(x)&&AlbumSorti(x) :: AlbumCertifie(x)     / AlbumCertifie(x)\n"
            + "\n"
            + "Artistes :\n"
            + "  soolking : EstArtiste + SigneLabel + AlbumSorti          → conflit d1 vs d2\n"
            + "  feu      : EstArtiste + IndependantLongtemps + NvContrat → conflit d1 vs d6\n"
            + "  tif      : EstArtiste + AlbumSorti + PlusDeDeuxAns       → aucun conflit\n"
            + "  flenn    : EstArtiste + AlbumSorti + PlusDeDeuxAns       → démo non-monotonie\n"
            + "  flenn+NM : + SigneLabel(flenn)                           → invalide LibreDroits";

    public BorderPane buildUI() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#1e1e2e;");

        Label header = new Label("Logique des Défauts — Extensions de Reiter");
        header.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#cdd6f4;"
                + "-fx-padding:14 24;-fx-background-color:#181825;");
        root.setTop(header);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color:#1e1e2e;");

        Tab tabJustice = new Tab("  Système Judiciaire  ");
        tabJustice.setContent(buildPane(JUSTICE_THEORY, false));

        Tab tabMusic = new Tab("  Industrie Musicale  ");
        tabMusic.setContent(buildPane(MUSIC_THEORY, true));

        tabs.getTabs().addAll(tabJustice, tabMusic);
        root.setCenter(tabs);
        return root;
    }

    private VBox buildPane(String theory, boolean isMusic) {
        VBox vbox = new VBox(12);
        vbox.setPadding(new Insets(18));
        vbox.setStyle("-fx-background-color:#1e1e2e;");

        // Theory display
        Label lblTheory = section("Théorie Δ = (W, D)");

        TextArea theoryArea = new TextArea(theory);
        theoryArea.setEditable(false);
        theoryArea.setPrefRowCount(isMusic ? 12 : 11);
        theoryArea.setStyle("-fx-background-color:#f5f5f5;-fx-text-fill:#1a1a2e;"
                + "-fx-font-family:Monospaced;-fx-font-size:11.5px;");

        // Info label
        Label lblInfo = new Label(
                "Le raisonneur calcule les extensions (ensembles crédules stables).\n"
                + "Un conflit entre deux défauts produit plusieurs extensions distinctes.\n"
                + "La non-monotonie est démontrée en ajoutant un fait à la théorie.");
        lblInfo.setStyle("-fx-text-fill:#6c7086;-fx-font-size:11px;-fx-wrap-text:true;");

        // Run button
        Button btnRun = new Button("▶  Calculer toutes les extensions");
        btnRun.setMaxWidth(Double.MAX_VALUE);
        btnRun.setStyle("-fx-background-color:#cba6f7;-fx-text-fill:#1e1e2e;"
                + "-fx-font-weight:bold;-fx-font-size:13px;-fx-padding:10 0;");

        // Output
        Label lblOut = section("Résultats");
        TextArea outArea = new TextArea();
        outArea.setEditable(false);
        outArea.setStyle("-fx-background-color:#f5f5f5;-fx-text-fill:#1a1a2e;"
                + "-fx-font-family:Monospaced;-fx-font-size:11px;");
        VBox.setVgrow(outArea, Priority.ALWAYS);

        btnRun.setOnAction(e -> {
            btnRun.setDisable(true);
            outArea.setText("Calcul des extensions en cours…\n"
                    + "(peut prendre quelques secondes)");
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
            new Thread(task, "defaut-run").start();
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

    private String captureRun(boolean isMusic) {
        PrintStream oldOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream ps = new PrintStream(baos, true, java.nio.charset.StandardCharsets.UTF_8)) {
            System.setOut(ps);
            if (isMusic) DefaultLogicMusic.run();
            else         DefaultLogicJustice.run();
        } finally {
            System.setOut(oldOut);
        }
        return baos.toString(java.nio.charset.StandardCharsets.UTF_8);
    }

    private Label section(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight:bold;-fx-text-fill:#cba6f7;-fx-font-size:13px;");
        return l;
    }
}
