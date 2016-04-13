package com.bartoszzychal.imageViewer.controller;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.function.UnaryOperator;

import javax.swing.filechooser.FileNameExtensionFilter;

import com.bartoszzychal.imageViewer.imageGallery.ImageGallery;
import com.bartoszzychal.imageViewer.imageGallery.impl.ImageGalleryImpl;
import com.bartoszzychal.imageViewer.imageGallery.model.ImageModel;
import com.bartoszzychal.imageViewer.imageGallery.model.SlideShowState;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

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

	@FXML
	private Label fileName;
	@FXML
	private Button previousButton;
	@FXML
	private Button nextButton;
	@FXML
	private Button slideButton;
	@FXML
	private TextField timeTextField;

	private String lastDirectoryPath;
	private DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
	private ImageGallery gallery = new ImageGalleryImpl();
	private SlideShowState slideShowState = null;
	private Timeline slideShow = null;

	@FXML
	private void initialize() {
		slideShowState = SlideShowState.START;
		setZoomProperties();
		setTimeTextFieldProperties();
		setTilePaneProperties();
		slideButton.disableProperty().bind(timeTextField.textProperty().isEmpty().or(gallery.getImageGallery().emptyProperty()));
		nextButton.disableProperty().bind(gallery.getImageGallery().emptyProperty());
		previousButton.disableProperty().bind(gallery.getImageGallery().emptyProperty());
	}

	private void setTilePaneProperties() {
		tile.setPadding(new Insets(15,15,15,15));
		tile.setStyle("-fx-background-color: FFFFFF;");
	}

	private void setTimeTextFieldProperties() {
		UnaryOperator<Change> filter = change -> {
			String text = change.getText();

			if (text.matches("[0-9]*")) {
				return change;
			}

			return null;
		};
		TextFormatter<String> textFormatter = new TextFormatter<>(filter);
		timeTextField.setTextFormatter(textFormatter);
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


	@FXML
	public void setDirectory(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		if (lastDirectoryPath != null) {
			directoryChooser.setInitialDirectory(new File(lastDirectoryPath));
		}
		File selectedDirectory = directoryChooser.showDialog(anchorPane.getScene().getWindow());
		if (selectedDirectory == null && lastDirectoryPath == null) {
			directoryLabel.setText("");
		} else if (selectedDirectory != null) {
			String directoryPath = selectedDirectory.getAbsolutePath();
			lastDirectoryPath = directoryPath;
			directoryLabel.setText(directoryPath);
			readFilesFromDirectory(directoryPath);
			loadImagesIconsToTiledPane();
			imageViewViewer.setImage(null);
		}
	}

	private void readFilesFromDirectory(final String directoryPath) {
		File folder = new File(directoryPath);
		File[] listFiles = folder.listFiles(new FileFilter() {
			private final FileNameExtensionFilter filter = new FileNameExtensionFilter("Pictures files", "jpeg", "jpg",
					"bmp", "png");

			@Override
			public boolean accept(File file) {
				return filter.accept(file);
			}
		});
		gallery.clear();
		gallery.addFiles(Arrays.asList(listFiles));
	}

	private void loadImagesIconsToTiledPane() {
		tile.getChildren().clear();
		for (final ImageModel imageModel : gallery.getAll()) {
			ImageView imageView = imageModel.getSmallImageView();
			imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					loadImageToImageViewViewer(imageModel);
				}
			});
			tile.getChildren().addAll(imageView);
		}
	}

	@FXML
	public void nextPicture(ActionEvent event) {
		next();
	}

	private void next() {
		loadImageToImageViewViewer(gallery.getNext());
	}

	@FXML
	public void previousPicture(ActionEvent event) {
		previous();
	}

	private void previous() {
		loadImageToImageViewViewer(gallery.getPrevious());
	}

	private void loadImageToImageViewViewer(final ImageModel imageModel) {
		imageViewViewer.setImage(imageModel.getRealSizeImage());
		zoomProperty.set(200);
		fileName.setText(imageModel.getName());
	}

	@FXML
	public void slideshow(ActionEvent event) {
		switch (slideShowState) {
		case START:
			timeTextField.setDisable(true);
			slideButton.setText("Stop");
			String text = timeTextField.getText();
			Integer duration = Integer.valueOf(text);
			if(imageViewViewer.getImage()==null){
				imageViewViewer.setImage(gallery.getFirst().getRealSizeImage());
			}
			slideShow = new Timeline(new KeyFrame(Duration.seconds(duration), new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					next();
				}
			}));
			slideShow.setCycleCount(Timeline.INDEFINITE);
			slideShow.play();
			slideShowState = SlideShowState.STOP;
			break;
		case STOP:
			timeTextField.setDisable(false);
			slideButton.setText("Start");
			slideShow.stop();
			slideShowState = SlideShowState.START;;
			break;
		default:
			break;
		}

	}
}
