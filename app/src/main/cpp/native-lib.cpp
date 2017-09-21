#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/asset_manager_jni.h>
#include <android/log.h>
using namespace cv;
using namespace std;

extern "C" {

JNIEXPORT void JNICALL
Java_software_happybubble_ImageProcessingActivity_originImage(
        JNIEnv *env,
        jobject,
        jlong addrInputImage) {

    Mat &img_input = *(Mat *) addrInputImage;

    //cvtColor( img_input, img_input, CV_BGR2RGB);
}

JNIEXPORT void JNICALL
Java_software_happybubble_ImageProcessingActivity_imageprocessing(
        JNIEnv *env,
        jobject,
        jlong addrInputImage,
        jlong addrOutputImage) {

    Mat &img_input = *(Mat *) addrInputImage;
    Mat &img_output = *(Mat *) addrOutputImage;

    //cvtColor( img_input, img_output, CV_RGB2GRAY);
    //
    blur( img_output, img_output, Size(1,1) );
    Canny( img_output, img_output, 50, 150, 5 );
}

}