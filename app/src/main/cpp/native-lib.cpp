#include <jni.h>
#include <opencv2/opencv.hpp>
#define setting_size 100

using namespace cv;
using namespace std;

extern "C" {
JNIEXPORT void JNICALL
Java_software_happybubble_PictureProcessing_imageProcessing(
        JNIEnv *env,
        jobject,
        jlong addr_input_image,
        jlong addr_binary_output_image,
        jlong addr_color_output_image,
        jfloat origin_w,
        jfloat origin_h,
        jfloat color_r,
        jfloat color_g,
        jfloat color_b) {

    Mat &img_input = *(Mat *) addr_input_image;
    Mat &img_binary_output = *(Mat *) addr_binary_output_image;
    Mat &img_color_output = *(Mat *) addr_color_output_image;
    Mat stats, centroids, img_morph;
    float min[3], max[3];
    cvtColor(img_input, img_binary_output, CV_BGR2RGB);

    if(color_r > 240) {
        min[0] = 225;
        max[0] = 255;
    }
    else if(color_r < 15) {
        min[0] = 0;
        max[0] = 30;
    }
    else {
        min[0] = color_r - 15;
        max[0] = color_r + 15;
    }

    if(color_g > 240) {
        min[1] = 225;
        max[1] = 255;
    }
    else if(color_g < 15) {
        min[1] = 0;
        max[1] = 30;
    }
    else {
        min[1] = color_g - 15;
        max[1] = color_g + 15;
    }

    if(color_b > 240) {
        min[2] = 225;
        max[2] = 255;
    }
    else if(color_b < 15) {
        min[2] = 0;
        max[2] = 30;
    }
    else {
        min[2] = color_b - 15;
        max[2] = color_b + 15;
    }

    inRange(img_binary_output, Scalar(min[2], min[1], min[0]), Scalar(max[2], max[1], max[0]), img_binary_output);

    Mat mask = getStructuringElement(MORPH_RECT, Size(3, 3), Point(1, 1));
    erode(img_binary_output, img_morph, mask, Point(-1, -1), 3);
    dilate(img_morph, img_morph, mask, Point(-1, -1), 1);
    int width = img_binary_output.size().width;
    int height = img_binary_output.size().height;
    Mat morphology_output(height, width, CV_8UC1);
    for (int h = 0; h < height; h++)
        for (int w = 0; w < width; w++)
            morphology_output.at<uchar>(h, w) = abs(img_binary_output.at<uchar>(h, w) - img_morph.at<uchar>(h, w));
    int num_of_lables = connectedComponentsWithStats(morphology_output, morphology_output, stats, centroids, 8, CV_32S);
    for(int i = 1;i < num_of_lables;i++) {
        int left = stats.at<int>(i, CC_STAT_LEFT);
        int top = stats.at<int>(i, CC_STAT_TOP);
        int width = stats.at<int>(i, CC_STAT_WIDTH);
        int height = stats.at<int>(i, CC_STAT_HEIGHT);
        if(width  < 10 && height < 10){
            rectangle(img_binary_output, Point(left, top), Point(left + width, top + height), Scalar(0), -1);
        }
    }

    jfloat setting_h = setting_size;
    jfloat setting_w = setting_size;
    if(origin_h >= origin_w)  setting_w = setting_size * (origin_w / origin_h);
    else setting_h = setting_size * (origin_h / origin_w);
    resize(img_binary_output, img_binary_output, Size(setting_w,setting_h));
    resize(img_input, img_input, Size(setting_w,setting_h));

    img_input.copyTo(img_color_output, img_binary_output);
}

JNIEXPORT jstring JNICALL
Java_software_happybubble_GetImage_imageLableing(
        JNIEnv *env,
        jobject,
        jlong addr_input_image,
        jlong addr_stats_image) {
    Mat &img_input = *(Mat *) addr_input_image;
    Mat &stats = *(Mat *) addr_stats_image;
    Mat centroids;
    Mat binary_output, erode_output, dilate_output, labeling_output;
    Mat mask = getStructuringElement(MORPH_RECT, Size(3, 3), Point(1, 1));

    cvtColor(img_input, binary_output, CV_RGB2GRAY);
    threshold(binary_output, binary_output, 200, 255, THRESH_OTSU | THRESH_BINARY);
    int width = binary_output.size().width;
    int height = binary_output.size().height;
    Mat morphology_output(height, width, CV_8UC1);
    erode(binary_output, erode_output, mask, Point(-1, -1), 3);
    dilate(erode_output, dilate_output, mask, Point(-1, -1), 1);
    dilate(binary_output, binary_output, mask, Point(-1, -1), 1);
    for (int h = 0; h < height; h++)
        for (int w = 0; w < width; w++)
            morphology_output.at<uchar>(h, w) = abs(binary_output.at<uchar>(h, w) - dilate_output.at<uchar>(h, w));
    int num_of_lables = connectedComponentsWithStats(morphology_output, labeling_output, stats, centroids, 8, CV_32S);

    jstring return_string;
    char temp_string[num_of_lables * 8];
    memset(temp_string, 0, num_of_lables * 8 * sizeof(char));
    char cat_char[8];
    memset(cat_char, 0, 8 * sizeof(char));
    jint k = 0;
    for(int i = 0;i < num_of_lables;i++){
        int width = stats.at<int>(i, CC_STAT_WIDTH);
        int height = stats.at<int>(i, CC_STAT_HEIGHT);
        if (width > 200 && height > 200) {
            sprintf(cat_char, "%d,", i);
            strcat(temp_string, cat_char);
        }
    }
    return_string = env->NewStringUTF(temp_string);

    return return_string;
}

JNIEXPORT void JNICALL
Java_software_happybubble_GetImage_getLableingImg(
        JNIEnv *env,
        jobject obj,
        jint num_of_lable,
        jlong addr_input_image,
        jlong addr_output_image,
        jlong addr_stats_image) {
    Mat &img_input = *(Mat *) addr_input_image;
    Mat &img_output = *(Mat *) addr_output_image;
    Mat &stats = *(Mat *) addr_stats_image;

    int left = stats.at<int>(num_of_lable, CC_STAT_LEFT);
    int top = stats.at<int>(num_of_lable, CC_STAT_TOP);
    int width = stats.at<int>(num_of_lable, CC_STAT_WIDTH);
    int height = stats.at<int>(num_of_lable, CC_STAT_HEIGHT);

    Rect roi(left, top, width, height);
    img_output = img_input(roi).clone();
}

JNIEXPORT void JNICALL
Java_software_happybubble_GetImage_removeBackground(
        JNIEnv *env,
        jobject obj,
        jlong addr_input_image,
        jlong addr_output_image) {
    Mat &img_input = *(Mat *) addr_input_image;
    Mat &img_output = *(Mat *) addr_output_image;
    Mat img_thresh, img_morph, img_dilate_bg;

    cvtColor(img_input, img_output, COLOR_BGR2GRAY);
    threshold(img_output, img_thresh, 0, 255, THRESH_OTSU | THRESH_BINARY_INV);

    Mat mask = getStructuringElement(MORPH_RECT, Size(3, 3), Point(1, 1));
    morphologyEx(img_thresh, img_morph, MORPH_OPEN, mask, Point(-1, -1), 2);
    dilate(img_morph, img_dilate_bg, mask, Point(-1, -1), 3);

    img_input.copyTo(img_output, img_dilate_bg);
}
}