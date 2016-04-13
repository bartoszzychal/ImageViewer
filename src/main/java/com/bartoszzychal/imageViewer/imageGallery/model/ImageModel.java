package com.bartoszzychal.imageViewer.imageGallery.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageModel {
	private Image smallImage;
	private Image normalImage;
	private File file;
	
	public ImageModel(File file) {
		this.file = file;
	}

	public ImageView getSmallImageView(){
		ImageView imageView = null;
		if(smallImage == null){
			try {
				smallImage = new Image(new FileInputStream(file), 150, 0, true, true);
				imageView = new ImageView(smallImage);
				imageView.setFitWidth(150);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return imageView;
	}
	
	public Image getRealSizeImage(){
		if(normalImage == null){
			try {
				normalImage = new Image(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return normalImage;
	}
	
	public String getName(){
		return file.getName();
	}
	
}
