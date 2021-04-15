import org.opencv.core.Core;
import org.opencv.core.Mat;
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
        File folder = new File("D:\\Projects\\Thesis\\Baseline B\\SOF-VSR\\TIP\\data\\test\\Set\\[01] KITTI - City\\lr_x2_BI");
        File[] listOfFiles = folder.listFiles();


        Arrays.sort(listOfFiles, (f1, f2) -> {
            String s1 = f1.getName().substring(3, f1.getName().indexOf("."));
            String s2 = f2.getName().substring(3, f2.getName().indexOf("."));
            return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
        });

        for (File f : listOfFiles) {
            if (f.isFile()) {
                System.out.println(f.getName());
            }
        }

        for(int i = 0; i < listOfFiles.length - 9;i++){
            Mat reference = Imgcodecs.imread(listOfFiles[i].getPath());
            System.out.println("Reference: " + listOfFiles[i].getPath());
            String[] warpedImages = new String[9];
            for(int j = 1; j < 10; j++) {
                warpedImages[j-1] = listOfFiles[i+j].getPath();
                System.out.println("Reference: " + listOfFiles[i+j].getPath());

            }

            String[] medianImages = new String[9];
            for(int j = 1; j < 10; j++) {
                medianImages[j-1] = listOfFiles[i+j].getPath();
            }

            WarpResultEvaluator warpResultEvaluator = new WarpResultEvaluator(reference, warpedImages, medianImages);



        }
    }
}
