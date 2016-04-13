package com.bartoszzychal.imageViewer.imageGallery;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.bartoszzychal.imageViewer.imageGallery.model.ImageModel;

import javafx.beans.property.ListProperty;

public interface ImageGallery {	
	void addFile(File file);
	void addFiles(Collection<? extends File> files);
	void clear();
	void setActual(ImageModel imageModel);
	ImageModel getNext();
	ImageModel getPrevious();
	ImageModel getFirst();
	List<ImageModel> getAll();
	ListProperty<ImageModel> getImageGallery();
}
