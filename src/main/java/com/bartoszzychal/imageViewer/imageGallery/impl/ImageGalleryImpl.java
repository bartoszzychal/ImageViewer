package com.bartoszzychal.imageViewer.imageGallery.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bartoszzychal.imageViewer.imageGallery.ImageGallery;
import com.bartoszzychal.imageViewer.imageGallery.model.ImageModel;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class ImageGalleryImpl implements ImageGallery {

	private final ListProperty<ImageModel> imageGallery = new SimpleListProperty<>(
			FXCollections.observableList(new ArrayList<>()));
	private ObjectProperty<ImageModel> actual = new SimpleObjectProperty<>();

	public void addFile(File file) {
		imageGallery.add(new ImageModel(file));
	}

	@Override
	public void addFiles(Collection<? extends File> files) {
		files.stream().map(file -> new ImageModel(file)).forEach((imageModel) -> imageGallery.add(imageModel));
	}

	public void clear() {
		imageGallery.clear();
	}

	public ImageModel getFirst() {
		if (!imageGallery.isEmpty()) {
			actual.set(imageGallery.get(0));
		}
		return actual.get();
	}

	public ImageModel getNext() {
		int index = imageGallery.indexOf(actual.get());
		if (index == -1) {
			actual.set(imageGallery.get(0));
		} else if (index < imageGallery.size() - 1) {
			actual.set(imageGallery.get(index + 1));
		} else {
			actual.set(imageGallery.get(0));
		}
		return actual.get();
	}

	public ImageModel getPrevious() {
		int index = imageGallery.indexOf(actual.get());
		if (index == -1) {
			actual.set(imageGallery.get(0));
		} else if (index > 0) {
			actual.set(imageGallery.get(index - 1));
		} else {
			actual.set(imageGallery.get(imageGallery.size() - 1));
		}
		return actual.get();
	}

	public List<ImageModel> getAll() {
		return imageGallery;
	}

	public String getImageName() {
		return actual.getName();
	}

	public ListProperty<ImageModel> getImageGallery() {
		return imageGallery;
	}

	@Override
	public void setActual(ImageModel imageModel) {
		actual.set(imageGallery.get(imageGallery.indexOf(imageModel)));
	}

}
