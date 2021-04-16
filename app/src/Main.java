import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String vid = "\\[20] Halloween";
        File folder = new File("D:\\Projects\\Thesis\\Chosen Videos\\LR Frames"+ vid+"\\lr_x2_BI");
        File[] listOfFiles = folder.listFiles();


        Arrays.sort(listOfFiles, (f1, f2) -> {
            String s1 = f1.getName().substring(3, f1.getName().indexOf("."));
            String s2 = f2.getName().substring(3, f2.getName().indexOf("."));
            return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
        });

//        for (File f : listOfFiles) {
//            if (f.isFile()) {
//                System.out.println(f.getName());
//            }
//        }

        for(int i = 0; i < listOfFiles.length - 9;i++){
            Mat reference = Imgcodecs.imread(listOfFiles[i].getPath());
            System.out.println(reference.size());
            System.out.println("Run: " + i);
            System.out.println("Reference: " + listOfFiles[i].getPath());

            String[] warpedImages = new String[9];
            String[] medianImages = new String[9];
            Mat[] lr_images = new Mat[9];
            for(int j = 0; j < 9; j++) {
                lr_images[j] = Imgcodecs.imread(listOfFiles[i+j+1].getPath());
//                medianImages[j-1] = listOfFiles[i+j].getPath();
//                System.out.println("To Align: " + listOfFiles[i+j].getPath());
                warpedImages[j] = vid+"warp_" + j;
                medianImages[j] = vid+"median_align_" + j;
            }

            MedianAlignmentOperator medianAlignmentOperator = new MedianAlignmentOperator(lr_images, medianImages);
            medianAlignmentOperator.perform();

            FeatureMatchingOperator featureMatchingOperator = new FeatureMatchingOperator(reference, lr_images);
            featureMatchingOperator.perform();
            MatOfKeyPoint matOfKeyPoint = featureMatchingOperator.getRefKeypoint();
            MatOfDMatch[] matOfDMatches = featureMatchingOperator.getdMatchesList();
            MatOfKeyPoint[] matOfKeyPoints = featureMatchingOperator.getLrKeypointsList();

            LRWarpingOperator lrWarpingOperator = new LRWarpingOperator(matOfKeyPoint, lr_images, warpedImages, matOfDMatches, matOfKeyPoints);
            lrWarpingOperator.perform();

//            Mat temp_warp = Imgcodecs.imread("D:\\Projects\\Thesis\\EagleEyeDesktop\\app\\temp\\warp_0.jpg");
//            Mat temp_med = Imgcodecs.imread("D:\\Projects\\Thesis\\EagleEyeDesktop\\app\\temp\\median_align_0.jpg");
//
//            System.out.println(temp_warp.size());
//            System.out.println(temp_med.size());


            WarpResultEvaluator warpResultEvaluator = new WarpResultEvaluator(reference, warpedImages, medianImages);
            warpResultEvaluator.perform();
            String[] alignedImageNames = warpResultEvaluator.getChosenAlignedNames();


            MeanFusionOperator meanFusionOperator = new MeanFusionOperator(reference, alignedImageNames);
            meanFusionOperator.perform();
            Mat result = meanFusionOperator.getResult();

            FileImageWriter fileImageWriter = new FileImageWriter();

            fileImageWriter.saveMatrixToImage(result, "IO" + vid, "HR_"+i, ImageFileAttribute.FileType.PNG);

        }
    }
}
