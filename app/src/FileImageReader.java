/**
 * 
 */

//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.media.ThumbnailUtils;
//import android.util.Log;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Reads images from external dir
 * @author NeilDG
 *
 */
public class FileImageReader {

	private static FileImageReader instance = null;
	public static FileImageReader getInstance() {
		if(instance==null)
			instance = new FileImageReader();
		return instance;
	}

	
	/**
	 * Loads the specified image and returns its byte data
	 */
	public byte[] getBytesFromFile(String fileName, ImageFileAttribute.FileType fileType) {
		File file = new File(FileImageWriter.getInstance().getFilePath() + "/" +fileName + ImageFileAttribute.getFileExtension(fileType));

		try {
			if(file.exists()) {
				FileInputStream inputStream = new FileInputStream(file);

				byte[] readBytes = new byte[(int) file.length()];
				inputStream.read(readBytes);
				inputStream.close();

				return readBytes;
			}
			else {
				return null;
			}
		} catch(IOException e) {
			return null;
		}
	}

	/**
	 * Reads an image from file and returns its matrix form represented by openCV
	 * @param fileName
	 * @return
	 */
	public Mat imReadOpenCV(String fileName, ImageFileAttribute.FileType fileType) {
		if(fileName.toLowerCase().contains(".jpg") == true) {
			return Imgcodecs.imread(fileName);
		}
		else {
			String completeFilePath = FileImageWriter.getInstance().getFilePath() + "/" + fileName + ImageFileAttribute.getFileExtension(fileType);
			return Imgcodecs.imread(completeFilePath);
		}
	}

	public Mat imReadFullPath(String fullPath) {
		return Imgcodecs.imread(fullPath);
	}

	public Mat imReadColor(String fileName, ImageFileAttribute.FileType fileType) {
		String completeFilePath = FileImageWriter.getInstance().getFilePath() + "/" + fileName + ImageFileAttribute.getFileExtension(fileType);

		return Imgcodecs.imread(completeFilePath, Imgcodecs.IMREAD_COLOR);
	}

	public boolean doesImageExists(String fileName, ImageFileAttribute.FileType fileType) {
		File file = new File(FileImageWriter.getInstance().getFilePath() + "/" +fileName + ImageFileAttribute.getFileExtension(fileType));
		return file.exists();
	}

//	public Bitmap loadBitmapFromFile(String fileName, ImageFileAttribute.FileType fileType) {
//		String completeFilePath = FileImageWriter.getInstance().getFilePath() + "/" + fileName + ImageFileAttribute.getFileExtension(fileType);
//		return BitmapFactory.decodeFile(completeFilePath);
//	}

//	public Bitmap loadBitmapThumbnail(String fileName, ImageFileAttribute.FileType fileType, int width, int height) {
//		Bitmap resized = ThumbnailUtils.extractThumbnail(this.loadBitmapFromFile(fileName, fileType), width, height);
//		return resized;
//	}
//
//	public Bitmap loadAbsoluteBitmapThumbnail(String absolutePath, int width, int height) {
//		Bitmap resized = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(absolutePath), width, height);
//		return resized;
//	}

	public String getDecodedFilePath(String fileName, ImageFileAttribute.FileType fileType) {
		String completeFilePath = FileImageWriter.getInstance().getFilePath() + "/" + fileName + ImageFileAttribute.getFileExtension(fileType);
		return completeFilePath;
	}
}
