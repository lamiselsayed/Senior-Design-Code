package seniorII;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class readImage {

	public static void main(String[] args) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		String imagePath1 = "C:\\Users\\salma\\Downloads\\updated_B_word_0.jpg"; 
        Mat image1 = Imgcodecs.imread(imagePath1);
        
        String imagePath2 = "C:\\Users\\salma\\Downloads\\updated_word_0.jpg";
        Mat image2 = Imgcodecs.imread(imagePath2);
        
        if(image1.empty() || image2.empty()) {
        	System.out.println("Error: Unable to load image.");
            return;
        }
        
        System.out.println("Image 1 Size: " + image1.size() + "\nImage 2 Size: " + image2.size());
        
        if (image1.size().equals(image2.size()) && image1.channels() == image2.channels()) {
            // Subtract the Mats
            Mat diff = new Mat();
            Core.absdiff(image1, image2, diff);
            
            System.out.println("Diff Image Size: " + diff.size());

            // Count non-zero pixels in the difference image for each channel
//            int nonZeroCount = 0;
//            for (int i = 0; i < diff.channels(); i++) {
//                Mat channel = new Mat();
//                Core.extractChannel(diff, channel, i);
//                nonZeroCount += Core.countNonZero(channel);
//            }
            
            int nonZeroCount = 0;
            for (int y = 0; y < diff.rows(); y++) {
                for (int x = 0; x < diff.cols(); x++) {
                    double[] pixelDiff = diff.get(y, x);
                    if (pixelDiff[0] != 0 || pixelDiff[1] != 0 || pixelDiff[2] != 0) {
                        nonZeroCount++;
                    }
                }
            }

            System.out.println("Number of non-zero pixels in the difference image: " + nonZeroCount);
        } else {
            System.out.println("Error: Images have different sizes or number of channels.");
        }
        
	}

}
