#include <jni.h>
#include <opencv2/opencv.hpp>
#include <opencv/highgui.h>
#include <android/log.h>
#include <android/asset_manager_jni.h>
#include <string.h>

using namespace cv;
using namespace std;

extern "C" {

JNIEXPORT void JNICALL
Java_software_happybubble_ImageProcessingActivity_imageProcessing(
        JNIEnv *env,
        jobject,
        jlong addrInputImage,
        jlong addrOutputImage,
        jint color) {

    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;
    Mat temp_mask;

    __android_log_print(ANDROID_LOG_ERROR, "TRACKERS", "%d", color);
    cvtColor(img_input, img_output, CV_BGR2RGB);

    if(color == 0)
        inRange(img_output, Scalar(0, 0, 200, 255), Scalar(50, 50, 255, 255), temp_mask);
    else if(color == 1)
        inRange(img_output, Scalar(200, 200, 0, 255), Scalar(255, 255, 50, 255), temp_mask);
    else if(color == 2)
        inRange(img_output, Scalar(200, 0, 0, 255), Scalar(255, 50, 50, 255), temp_mask);
    else if(color == 3)
        inRange(img_output, Scalar(230, 230, 230, 255), Scalar(255, 255, 255, 255), temp_mask);
    img_input.copyTo(img_output, temp_mask);
}

JNIEXPORT void JNICALL
Java_software_happybubble_ImageProcessingActivity_imageLableing(
        JNIEnv *env,
        jobject,
        jlong addrInputImage,
        jlong addrOutputImage) {

    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;
    Mat binary_output, erode_output, dilate_output, labeling_output;
    Mat stats, centroids;
    Mat mask = getStructuringElement(MORPH_RECT, Size(3, 3), Point(1, 1));

    cvtColor(img_input, binary_output, CV_RGB2GRAY);
    threshold(binary_output, binary_output, 200, 255, THRESH_OTSU | THRESH_BINARY);
    // 이진화
    int width = binary_output.size().width;
    int height = binary_output.size().height;
    Mat morphology_output(height, width, CV_8UC1);

    // erode = 침식연산
    erode(binary_output, erode_output, mask, Point(-1, -1), 3);
    // dilate = 팽창 연산
    dilate(erode_output, dilate_output, mask, Point(-1, -1), 1);
    dilate(binary_output, binary_output, mask, Point(-1, -1), 5);
    for(int h = 0;h < height;h++)
        for(int w = 0;w < width;w++)
            morphology_output.at<uchar>(h,w) = abs(binary_output.at<uchar>(h,w) - dilate_output.at<uchar>(h,w));

    int numOfLables = connectedComponentsWithStats(morphology_output, labeling_output, stats, centroids, 8,CV_32S);

    morphology_output.copyTo(img_output);
    cvtColor(img_output, img_output, CV_GRAY2RGB);

    for (int j = 1; j < numOfLables; j++) {
        int area = stats.at<int>(j, CC_STAT_AREA);
        int left = stats.at<int>(j, CC_STAT_LEFT);
        int top = stats.at<int>(j, CC_STAT_TOP);
        int width = stats.at<int>(j, CC_STAT_WIDTH);
        int height = stats.at<int>(j, CC_STAT_HEIGHT);

        int x = centroids.at<double>(j, 0); //중심좌표
        int y = centroids.at<double>(j, 1);

        if(width > 20 || height > 20){
            circle(img_output, Point(x, y), 5, Scalar(0, 255, 0), 1);
            rectangle(img_output, Point(left, top), Point(left + width, top + height), Scalar(255, 0, 0), 5);
        }
    }
}
}