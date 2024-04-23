package seniorII;

import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class DirectionalATD {
	
	public static ArrayList<Mat> extractFrames(String videoPath) {
		VideoCapture capture = new VideoCapture();
		ArrayList<Mat> extractedFramesList = new ArrayList<>();
		Mat frame = new Mat();
		int noFrames = 0;

		capture.open(videoPath);
		if (capture.isOpened()) {
			while (capture.read(frame)) {
				Mat resizedFrame = new Mat(299, 299, CvType.CV_16S);
				Imgproc.resize(frame, resizedFrame, new Size(299, 299), 0, 0, Imgproc.INTER_AREA);
				
				Mat grayscaleFrame = new Mat();
				Imgproc.cvtColor(resizedFrame, grayscaleFrame, Imgproc.COLOR_BGR2GRAY);
				grayscaleFrame.convertTo(grayscaleFrame, CvType.CV_16S);
				
				extractedFramesList.add(grayscaleFrame);
				
				noFrames++;
			}
			capture.release();
		}
		
		System.out.println("Total Number of Frames = " + noFrames);
		return extractedFramesList;
		//return convertToGrayscale(extractedFramesList);
	} 
	
	public static Mat applyATD(ArrayList<Mat> frames, int index) {
		ArrayList<Mat> ATD_Stacked = new ArrayList<>();
		
		Mat forwardATD = applyForwardDiff(frames);
		Mat backwardATD = applyBackwardDiff(frames);
		Mat bidirectATD = applyBidirectionalDiff(frames);
		
		ATD_Stacked.add(forwardATD);
		ATD_Stacked.add(backwardATD);
		ATD_Stacked.add(bidirectATD);
		
		Mat stackedImage = new Mat();
		Core.merge(ATD_Stacked, stackedImage);
		stackedImage.convertTo(stackedImage, CvType.CV_8UC3);
//		
//		stackedImage.convertTo(stackedImage, CvType.CV_16SC3);
//		Core.add(stackedImage, new Scalar(128), stackedImage);
//		
//		Mat maskZeros = new Mat();
//		Mat mask255 = new Mat();
//		
//		Core.compare(stackedImage, new Scalar(0), maskZeros, Core.CMP_LT);
//		Core.compare(stackedImage, new Scalar(255), mask255, Core.CMP_GT);
//		
//		Mat updatedStackedImage = stackedImage.clone();
//		updatedStackedImage.setTo(new Scalar(0), maskZeros);
//		updatedStackedImage.setTo(new Scalar(255), mask255);
//		
//		updatedStackedImage.convertTo(updatedStackedImage, CvType.CV_8UC3);
		
		//Imgcodecs.imwrite("C:\\Users\\salma\\Downloads\\word_" + index + ".jpg", stackedImage);
		return stackedImage;
	}
	
	public static Mat applyForwardDiff(ArrayList<Mat> grayImagesList) {
		ArrayList<Mat> listOfFrames = new ArrayList<>();
		Mat forward_ImageDiff = new Mat();
		
		for (int index = 0; index < grayImagesList.size() - 1; index++) {
			Core.subtract(grayImagesList.get(index), grayImagesList.get(index + 1), forward_ImageDiff);		
			Mat thresholdedImage = performThreshold(forward_ImageDiff);
			Mat binarizedImage = binarize(thresholdedImage);
			listOfFrames.add(binarizedImage);
		}
		
		return accumulateFrames(listOfFrames);
	}
	
	public static Mat applyBackwardDiff(ArrayList<Mat> grayImagesList) {
		ArrayList<Mat> listOfFrames = new ArrayList<>();
		Mat backward_ImageDiff = new Mat();
		
		for (int index = 1; index < grayImagesList.size(); index++) {
			Core.subtract(grayImagesList.get(index), grayImagesList.get(index - 1), backward_ImageDiff);
			Mat thresholdedImage = performThreshold(backward_ImageDiff);
			Mat binarizedImage = binarize(thresholdedImage);
			listOfFrames.add(binarizedImage);
		}
		
		return accumulateFrames(listOfFrames);
	}
	
	public static Mat applyBidirectionalDiff(ArrayList<Mat> grayImagesList) {
		ArrayList<Mat> listOfFrames = new ArrayList<>();
		Mat bidirectional_ImageDiff = new Mat();
		Mat average_frame = new Mat();
		
		for (int index = 1; index < grayImagesList.size() - 1; index++) {
	        Core.addWeighted(grayImagesList.get(index - 1), 0.5, grayImagesList.get(index + 1), 0.5, 0, average_frame);
			Core.subtract(grayImagesList.get(index), average_frame, bidirectional_ImageDiff);
			Mat thresholdedImage = performThreshold(bidirectional_ImageDiff);
			Mat binarizedImage = binarize(thresholdedImage);
			listOfFrames.add(binarizedImage);
		}
		
		return accumulateFrames(listOfFrames);
	}
	
	public static Mat performThreshold(Mat diffFrame) {
		Mat mask = new Mat();
		
		Core.inRange(diffFrame, new Scalar(-13), new Scalar(13), mask);
		diffFrame.setTo(new Scalar(0), mask);
		
		return diffFrame;
	}
	
	public static Mat binarize(Mat noiseless_image) {
		Scalar mean = Core.mean(noiseless_image);
		Mat maskZeros = new Mat();
		Mat maskOnes = new Mat();
		
		Core.compare(noiseless_image, mean, maskZeros, Core.CMP_LT);
		Core.compare(noiseless_image, mean, maskOnes, Core.CMP_GE);
		
		Mat binarizedImage = noiseless_image.clone();
		binarizedImage.setTo(new Scalar(0), maskZeros);
		binarizedImage.setTo(new Scalar(1), maskOnes);
		
		return binarizedImage;
	}
	
	public static Mat accumulateFrames(ArrayList<Mat> framesList) {
		Mat accumulatedFrame = framesList.get(0);
		
		for (int i = 1; i < framesList.size(); i++)
			Core.add(accumulatedFrame, framesList.get(i), accumulatedFrame);
		
		return accumulatedFrame;
	}
	
	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		String videoPath = "C:\\Users\\salma\\Downloads\\Testing Sample\\02_0037_(23_03_21_20_45_14)_c.mp4";
		
		ArrayList<Mat> frames = new ArrayList<>();

		frames = extractFrames(videoPath);
		
		long endTime = System.currentTimeMillis();
		System.out.println("\nTime to Extract Frames & Convert to Grayscale: " + (endTime - startTime) / 1000.0 + " seconds");
		
		ArrayList<Mat> ATD_Frames = new ArrayList<>();
		
		int noFrames = frames.size();
		int noWords = 4;
		int split = noFrames / noWords;
		
		int index;
		
		for (int i = 0; i < noWords; i++) {
			index = split * i;
			if (i == noWords - 1) 
				ATD_Frames.add(applyATD(new ArrayList<Mat>(frames.subList(index, frames.size())), i));
			else
				ATD_Frames.add(applyATD(new ArrayList<Mat>(frames.subList(index, index + split)), i));			
		}
		
		for (int i = 0; i < ATD_Frames.size(); i++)
			Imgcodecs.imwrite("C:\\Users\\salma\\Downloads\\word_" + i + ".jpg", ATD_Frames.get(i));
		
				
		long endTime2 = System.currentTimeMillis();
		System.out.println("\nTime to Execute Directional ATD w/o Stacking & Store Frames: " + (endTime2 - startTime) / 1000.0 + " seconds");	
	}


}
