import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String vid = "\\";
        System.out.println("Enter video name: ");
        Scanner sc = new Scanner(System.in);
        vid += sc.nextLine();

        File folder = new File("E:\\Projects\\Thesis\\Chosen Videos\\LR Frames"+ vid+"\\lr_x2_BI");
//        String vid = "\\Jade's Images";
//        File folder = new File("D:\\Projects\\Thesis\\Photos\\Safety Check" + vid + "\\lr_x2_BI" );
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
//            Mat reference = Imgcodecs.imread(listOfFiles[i].getPath());
//            System.out.println(reference.size());
            Mat reference = new Mat();
            System.out.println("Run: " + i);

            String[] warpedImages = new String[10];
            String[] medianImages = new String[10];
            Mat[] lr_images = new Mat[10];

            Mat[] energyInputMatList = new Mat[10];
            InputImageEnergyReader[] energyReaders = new InputImageEnergyReader[energyInputMatList.length];
            //load images and use Y channel as input for succeeding operators
            try {
                Semaphore energySem = new Semaphore(energyInputMatList.length);
                for(int j = 0; j < energyReaders.length; j++) {
                    lr_images[j] = Imgcodecs.imread(listOfFiles[i+j].getPath());
                    energyReaders[j] = new InputImageEnergyReader(energySem, lr_images[j]);
                    energyReaders[j].startWork();
                }

                energySem.acquire(energyInputMatList.length);
                for(int j = 0; j < energyReaders.length; j++) {
                    energyInputMatList[j] = energyReaders[j].getOutputMat();
                }

            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            YangFilter yangFilter = new YangFilter(energyInputMatList);
            yangFilter.perform();


            SharpnessMeasure.SharpnessResult sharpnessResult = SharpnessMeasure.getSharedInstance().measureSharpness(yangFilter.getEdgeMatList());

//            Integer[] inputIndices = SharpnessMeasure.getSharedInstance().trimMatList(ImageInputMap.numImages(), sharpnessResult, 0.0);
//            Mat[] rgbInputMatList = new Mat[inputIndices.length];


            for(int j = 0; j < 10; j++) {
                lr_images[j] = Imgcodecs.imread(listOfFiles[i+j].getPath());
//                medianImages[j-1] = listOfFiles[i+j].getPath();
//                System.out.println("To Align: " + listOfFiles[i+j].getPath());
                UnsharpMaskOperator unsharpMaskOperator = new UnsharpMaskOperator(lr_images[j], j);
                unsharpMaskOperator.perform();
                lr_images[j] = unsharpMaskOperator.getResult();
//                System.out.println("Best Index Main: " + sharpnessResult.getBestIndex());
                if(sharpnessResult.getBestIndex() == j){
                    lr_images[j].copyTo(reference);
                }
                
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

//    private static void interpolateImage(int index){
//        Mat inputMat = FileImageReader.getInstance().imReadFullPath(ImageInputMap.getInputImage(index));
//
//        Mat outputMat = ImageOperator.performInterpolation(inputMat, ParameterConfig.getScalingFactor(), Imgproc.INTER_LINEAR);
//        FileImageWriter.getInstance().saveMatrixToImage(outputMat, "temp", "linear_" + index, ImageFileAttribute.FileType.JPEG);
//        outputMat.release();
//
//        inputMat.release();
//        System.gc();
//    }
}
