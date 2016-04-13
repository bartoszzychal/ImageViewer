package com.bartoszzychal.imageViewer.imageGallery.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageModel {
	private Image smallImage;
	private Image normalImage;
	private File file;

	public ImageModel(File file) {
		this.file = file;
	}

	public ImageView getSmallImageView() {
		ImageView imageView = null;
		if (smallImage == null && file.exists()) {

			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				smallImage = new Image(fileInputStream, 150, 0, true, true);
				imageView = new ImageView(smallImage);
				imageView.setFitWidth(150);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return imageView;
	}

	public Image getRealSizeImage() {
		if (normalImage == null && file.exists()) {
			try(FileInputStream fileInputStream = new FileInputStream(file)) {
				normalImage = new Image(fileInputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return normalImage;
	}

	public String getName() {
		return file.getName();
	}

}
