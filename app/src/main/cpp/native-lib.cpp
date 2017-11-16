#include <jni.h>
#include <opencv2/opencv.hpp>

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
        jfloat colorR,
        jfloat colorG,
        jfloat colorB) {

    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_binaryOutput = *(Mat *) addrBinaryOutputImage;
    Mat &img_colorOutput = *(Mat *) addrColorOutputImage;
    Mat temp_binary(cvSize(200, 200), CV_8UC1);
    Mat temp_color(cvSize(200, 200), CV_8UC1);
    float min[3], max[3];
    cvtColor(img_input, img_binaryOutput, CV_BGR2RGB);

    if(colorR > 225) {
        min[0] = colorR - 30;
        max[0] = 255;
    }
    else if(colorR < 30) {
        min[0] = 0;
        max[0] = colorR + 30;
    }
    else {
        min[0] = colorR - 30;
        max[0] = colorR + 30;
    }

    if(colorG > 225) {
        min[1] = colorG - 30;
        max[1] = 255;
    }
    else if(colorG < 30) {
        min[1] = 0;
        max[1] = colorG + 30;
    }
    else {
        min[1] = colorG - 30;
        max[1] = colorG + 30;
    }

    if(colorB > 225) {
        min[2] = colorB - 30;
        max[2] = 255;
    }
    else if(colorB < 30) {
        min[2] = 0;
        max[2] = colorB + 30;
    }
    else {
        min[2] = colorB - 30;
        max[2] = colorB + 30;
    }
    inRange(img_binaryOutput, Scalar(min[2], min[1], min[0]), Scalar(max[2], max[1], max[0]), img_binaryOutput);
    resize(img_binaryOutput, img_binaryOutput, temp_binary.size());
    resize(img_input, img_input, temp_binary.size());

    img_input.copyTo(img_colorOutput, img_binaryOutput);
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

JNIEXPORT jint JNICALL
Java_software_happybubble_GetImage_imageLableing(
        JNIEnv *env,
        jobject obj,
        jlong addrInputImage,
        jlong addrStatsImage,
        jlong addrCentroidsImage) {
    Mat &img_input = *(Mat *) addrInputImage;
    Mat &stats = *(Mat *) addrStatsImage;
    Mat &centroids = *(Mat *) addrCentroidsImage;
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

    int numOfLables = connectedComponentsWithStats(morphology_output, labeling_output, stats, centroids, 8, CV_32S);

    return numOfLables;
}

JNIEXPORT jboolean JNICALL
Java_software_happybubble_GetImage_getLableingImg(
        JNIEnv *env,
        jobject obj,
        jint numOfLables,
        jlong addrInputImage,
        jlong addrOutputImage,
        jlong addrStatsImage,
        jlong addrCentroidsImage) {
    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;
    Mat &stats = *(Mat *) addrStatsImage;
    Mat &centroids = *(Mat *) addrCentroidsImage;

    int area = stats.at<int>(numOfLables, CC_STAT_AREA);
    int left = stats.at<int>(numOfLables, CC_STAT_LEFT);
    int top = stats.at<int>(numOfLables, CC_STAT_TOP);
    int width = stats.at<int>(numOfLables, CC_STAT_WIDTH);
    int height = stats.at<int>(numOfLables, CC_STAT_HEIGHT);

    int x = centroids.at<double>(numOfLables, 0); //중심좌표
    int y = centroids.at<double>(numOfLables, 1);

    if (width > 200 && height > 200) {
        Rect roi(left, top, width, height);
        img_output = img_input(roi).clone();
        return true;
    }
    else return false;
}
}