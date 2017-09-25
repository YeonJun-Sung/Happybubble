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
Java_software_happybubble_ImageProcessingActivity_originImage(
        JNIEnv *env,
        jobject,
        jlong addrInputImage) {

    Mat &img_input = *(Mat *) addrInputImage;

    cvtColor( img_input, img_input, CV_BGR2RGB);
}

JNIEXPORT void JNICALL
Java_software_happybubble_ImageProcessingActivity_imageprocessing(
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
        inRange(img_output, Scalar(0, 0, 200, 255), Scalar(255, 255, 255, 255), temp_mask);
    else if(color == 1)
        inRange(img_output, Scalar(0, 200, 0, 255), Scalar(255, 255, 255, 255), temp_mask);
    else if(color == 2)
        inRange(img_output, Scalar(200, 0, 0, 255), Scalar(255, 255, 255, 255), temp_mask);
    else if(color == 3)
        inRange(img_output, Scalar(0, 0, 0, 255), Scalar(50, 50, 50, 255), temp_mask);
    img_input.copyTo(img_output, temp_mask);
}

}