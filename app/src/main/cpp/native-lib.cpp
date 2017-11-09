#include <jni.h>
#include <opencv2/opencv.hpp>
#include <opencv/highgui.h>
#include <android/log.h>
#include <android/asset_manager_jni.h>
#include <string.h>
#include <stdlib.h>

using namespace cv;
using namespace std;

extern "C" {

JNIEXPORT void JNICALL
Java_software_happybubble_PictureProcessing_imageProcessing(
        JNIEnv *env,
        jobject,
        jlong addrInputImage,
        jlong addrBinaryOutputImage,
        jlong addrColorOutputImage,
        jfloat h,
        jfloat s,
        jfloat v) {

    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_binaryOutput = *(Mat *) addrBinaryOutputImage;
    Mat &img_colorOutput = *(Mat *) addrColorOutputImage;
    Mat temp_mask;
    float min[3], max[3];
    cvtColor(img_input, img_binaryOutput, CV_BGR2RGB);

    if(h > 225) {
        min[0] = h - 30;
        max[0] = 255;
    }
    else if(h < 30) {
        min[0] = 0;
        max[0] = h + 30;
    }
    else {
        min[0] = h - 30;
        max[0] = h + 30;
    }

    if(s > 225) {
        min[1] = s - 30;
        max[1] = 255;
    }
    else if(s < 30) {
        min[1] = 0;
        max[1] = s + 30;
    }
    else {
        min[1] = s - 30;
        max[1] = s + 30;
    }

    if(v > 225) {
        min[2] = v - 30;
        max[2] = 255;
    }
    else if(v < 30) {
        min[2] = 0;
        max[2] = v + 30;
    }
    else {
        min[2] = v - 30;
        max[2] = v + 30;
    }
    inRange(img_binaryOutput, Scalar(min[2], min[1], min[0]), Scalar(max[2], max[1], max[0]), temp_mask);

    img_input.copyTo(img_colorOutput, temp_mask);
}

JNIEXPORT void JNICALL
ava_software_happybubble_ImageProcessingActivity_imageProcessing(
        JNIEnv *env,
        jobject,
        jlong addrInputImage,
        jlong addrOutputImage,
        jint color) {

    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;
    Mat temp_mask;

    cvtColor(img_input, img_output, CV_BGR2HSV);
}

JNIEXPORT jstring JNICALL
Java_software_happybubble_ImageProcessingActivity_imageLableing(
        JNIEnv *env,
        jobject obj,
        jlong addrInputImage,
        jlong addrStatsImage,
        jlong addrCentroidsImage) {
    Mat &img_input = *(Mat *) addrInputImage;
    Mat &stats = *(Mat *) addrStatsImage;
    Mat &centroids = *(Mat *) addrStatsImage;
    Mat statsTemp, centroidsTemp;
    Mat binary_output, erode_output, dilate_output, labeling_output;
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
    dilate(binary_output, binary_output, mask, Point(-1, -1), 1);
    for (int h = 0; h < height; h++)
        for (int w = 0; w < width; w++)
            morphology_output.at<uchar>(h, w) = abs(
                    binary_output.at<uchar>(h, w) - dilate_output.at<uchar>(h, w));

    int numOfLables = connectedComponentsWithStats(morphology_output, labeling_output, statsTemp, centroidsTemp, 8, CV_32S);

    char *info = "";
    char *temp = "1";
    for (int j = 1; j < numOfLables; j++) {
        int area = statsTemp.at<int>(j, CC_STAT_AREA);
        int left = statsTemp.at<int>(j, CC_STAT_LEFT);
        int top = statsTemp.at<int>(j, CC_STAT_TOP);
        int width = statsTemp.at<int>(j, CC_STAT_WIDTH);
        int height = statsTemp.at<int>(j, CC_STAT_HEIGHT);

        int x = centroidsTemp.at<double>(j, 0); //중심좌표
        int y = centroidsTemp.at<double>(j, 1);

        if(width > 200 && height > 200){
            //sprintf(info,"%d,%d,%d,%d,%d,%d,%d",area,left,top,width,height,x,y);
            strcat(info, temp);
        }
    }
    jstring result;
    result = env->NewStringUTF("test");
    return result;
}

JNIEXPORT void JNICALL
Java_software_happybubble_ImageProcessingActivity_getLableingImg(
        JNIEnv *env,
        jobject obj,
        jint numOfLables,
        jint returnLable,
        jlong addrInputImage,
        jlong addrOutputImage,
        jlong addrStatsImage,
        jlong addrCentroidsImage) {
    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;
    Mat &stats = *(Mat *) addrStatsImage;
    Mat &centroids = *(Mat *) addrCentroidsImage;
    int lableFilter = 0;

    printf("return : %d\n", returnLable);
    printf("lable : %d\n", lableFilter);
    //morphology_output.copyTo(img_input);
    for (int j = 1; j < numOfLables; j++) {
        int area = stats.at<int>(j, CC_STAT_AREA);
        int left = stats.at<int>(j, CC_STAT_LEFT);
        int top = stats.at<int>(j, CC_STAT_TOP);
        int width = stats.at<int>(j, CC_STAT_WIDTH);
        int height = stats.at<int>(j, CC_STAT_HEIGHT);

        int x = centroids.at<double>(j, 0); //중심좌표
        int y = centroids.at<double>(j, 1);

        if(width > 200 && height > 200){
            circle(img_input, Point(x, y), 5, Scalar(0, 255, 0), 1);
            rectangle(img_input, Point(left, top), Point(left + width, top + height), Scalar(255, 0, 0), 5);
            //lableingImg[result++].create(height, width, CV_8UC1);
            Rect roi(left, top, width, height);
            img_output = img_input(roi).clone();
            //env->CallVoidMethod(obj, cb, env->NewStringUTF("Lable"));
            if(returnLable == lableFilter)  break;
            lableFilter++;
        }
    }
}
}