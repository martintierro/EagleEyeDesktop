
import org.opencv.core.Mat;

import java.util.concurrent.Semaphore;
/**
 * Reads a given input image, downsamples it and converts it to energy mat
 * Created by NeilDG on 1/11/2017.
 */

public class InputImageEnergyReader extends FlaggingThread {
    private final static String TAG = "InputImageEnergyReader";
    private Mat inputImage;
    private Mat outputMat;

    public InputImageEnergyReader(Semaphore semaphore, Mat inputImage) {
        super(semaphore);
        this.inputImage = inputImage;
    }

    @Override
    public void run() {

        Mat inputMat = this.inputImage;
        inputMat = ImageOperator.downsample(inputMat, 0.125f); //downsample

        Mat[] yuvMat = ColorSpaceOperator.convertRGBToYUV(inputMat);

        this.outputMat = yuvMat[ColorSpaceOperator.Y_CHANNEL];
        inputMat.release();

        this.finishWork();

    }

    public Mat getOutputMat() {
        return this.outputMat;
    }



}
