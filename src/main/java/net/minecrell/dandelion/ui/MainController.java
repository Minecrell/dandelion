package net.minecrell.dandelion.ui;

import javafx.fxml.FXML;
import net.minecrell.dandelion.Dandelion;

public final class MainController  {

    private Dandelion dandelion;

    @FXML
    private void initialize() {
        dandelion = Dandelion.getInstance();
    }

    @FXML
    private void onCloseClick() {
        dandelion.getPrimaryStage().close();
    }

}
