package com.bartoszzychal.imageViewer.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.DirectoryChooser;

//C:\Users\ZBARTOSZ\workspace_javafx\ImageViewer\pictures
public class ImageViewerController {

	@FXML
	private ScrollPane scrollPane;
	@FXML
	private TilePane tile;
	@FXML
	private ImageView imageViewViewer;
	@FXML
	private Button directoryButton;
	@FXML
	private Label directoryLabel;
	@FXML
	private AnchorPane anchorPane;

	private String lastPath;
	private DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

	private final Logger LOG = Logger.getLogger(ImageViewerController.class);

	@FXML
	private void initialize() {
		setTileProperties();
		setZoomProperties();
	}

	private void setZoomProperties() {

		InvalidationListener listener = new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				imageViewViewer.setFitWidth(zoomProperty.get() * 4);
				imageViewViewer.setFitHeight(zoomProperty.get() * 3);
				scrollPane.requestLayout();
			}
		};
		
		zoomProperty.addListener(listener);

		scrollPane.setPannable(true);
		
		scrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if (event.getDeltaY() > 0) {
					zoomProperty.set(zoomProperty.get() * 1.1);
				} else if (event.getDeltaY() < 0) {
					zoomProperty.set(zoomProperty.get() / 1.1);
				}
			}
		});

	}

	private void setTileProperties() {
		tile.setPadding(new Insets(15, 15, 15, 15));
		tile.setHgap(15);
		tile.setStyle("-fx-background-color: FFFFFF;");
	}

	@FXML
	public void setDirectory(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		if (lastPath != null) {
			directoryChooser.setInitialDirectory(new File(lastPath));
		}
		File selectedDirectory = directoryChooser.showDialog(anchorPane.getScene().getWindow());
		if (selectedDirectory == null) {
			directoryLabel.setText("path/to/directory");
		} else {
			tile.getChildren().clear();
			String path = selectedDirectory.getAbsolutePath();
			lastPath = path;
			directoryLabel.setText(path);
			loadPicturesFromDirectory(path);
		}
	}

	private void loadPicturesFromDirectory(String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for (final File file : listOfFiles) {
			ImageView imageView;
			imageView = loadPicture(file);
			tile.getChildren().addAll(imageView);
		}
	}

	private ImageView loadPicture(File file) {
		Image imageIcon;
		ImageView imageView = null;
		try {
			imageIcon = new Image(new FileInputStream(file), 150, 0, true, true);
			imageView = new ImageView(imageIcon);
			imageView.setFitWidth(150);
			imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					try {
						Image image = new Image(new FileInputStream(file));
						imageViewViewer.setImage(image);
						zoomProperty.set(200);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return imageView;
	}

}
