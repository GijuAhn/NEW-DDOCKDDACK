import base64
import json
import os
import cv2
import numpy as np
import urllib.request
from urllib.parse import quote_plus
import math
# from skimage.metrics import structural_similarity as ssim

# TODO: aws lambda on docker?

# S3 for OpenCV external libs
# access_key = os.environ.get('access_key')
# secret_key = os.environ.get('secret_key')

def lambda_handler(event, context):
    
    resizer_px = 256
    
    # 한글-safe image URL
    target_img_path = quote_plus(event['target'], safe='://' )
    input_img_path = event['input']
    
    print(f'target_img_path!!!-> {target_img_path}, \n input_img_path!!!-> {input_img_path}')
    
    
    def retrieve_s3_image(img_path):
        # Retrieve the image content from the URL
        try:
            response = urllib.request.urlopen(img_path)
            image_content = np.asarray(bytearray(response.read()), dtype=np.uint8)
            image = cv2.imdecode(image_content, cv2.IMREAD_COLOR)
            return image
        except Exception as e:
            print(e)
            raise e


    # Get images from S3 object URL *must be unique
    target_img = retrieve_s3_image(target_img_path)
    input_img = retrieve_s3_image(input_img_path)
    
    
    # kaze
    def preprocess_image(img):
        # img = cv2.imread(img_path)
        # img = retrieve_s3_image(img_path)
        
        print(f'image read successfully, image size is!!!->{len(img)}')
        
        img = cv2.resize(img, (resizer_px, resizer_px))
        # skip grayscaling for remove background
        # gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        blur = cv2.GaussianBlur(img, (5, 5), 0)
        return blur

    
    def extract_kaze_features(img):
        kaze = cv2.KAZE_create()
        keypoints, descriptors = kaze.detectAndCompute(img, None)
        return keypoints, descriptors
    
    
    def match_kaze_features(desc1, desc2):
        bf = cv2.BFMatcher(cv2.NORM_L1, crossCheck=True)
        matches = bf.match(desc1, desc2)
        return matches
    
    
    def remove_background(img):
        # img = cv2.imread(img_path)
        mask = np.zeros(img.shape[:2], np.uint8)
        bgd_model = np.zeros((1,65), np.float64)
        fgd_model = np.zeros((1,65), np.float64)
        rect = (50, 50, img.shape[1]-50, img.shape[0]-50)
        cv2.grabCut(img, mask, rect, bgd_model, fgd_model, 5, cv2.GC_INIT_WITH_RECT)
        mask2 = np.where((mask==2)|(mask==0), 0, 1).astype('uint8')
        img = img*mask2[:, :, np.newaxis]
        return img
    
    
    # --------------------------------------------
    # **intercept** preprocessed for every metrics
    target_img_resized_blurred = preprocess_image(target_img)
    input_img_resized_blurred = preprocess_image(input_img)
    
    target_img_grabcut = remove_background(target_img_resized_blurred)
    input_img_grabcut = remove_background(input_img_resized_blurred)
    # --------------------------------------------
    
    
    # kaze --continued
    def measure_kaze_similarity(img1, img2):
        # img1 = preprocess_image(img1)
        # img2 = preprocess_image(img2)
    
        # img1 = remove_background(img1)
        # img2 = remove_background(img2)
    
        kp1, desc1 = extract_kaze_features(img1)
        kp2, desc2 = extract_kaze_features(img2)
    
        matches = match_kaze_features(desc1, desc2)
    
        score = len(matches) / float(max(len(desc1), len(desc2)))
        # the higher the score, the more similar the images
        return score
        
        
    score_kaze_feature = measure_kaze_similarity(target_img_grabcut, input_img_grabcut)
    print(f'!!! score_kaze_feature !!! === {score_kaze_feature}')
    
    
    
    # histogram
    def histogram_analysis(image1, image2):
    
        # Convert images to grayscale
        gray_image1 = cv2.cvtColor(image1, cv2.COLOR_BGR2GRAY)
        gray_image2 = cv2.cvtColor(image2, cv2.COLOR_BGR2GRAY)
    
        # Calculate the histograms
        hist_image1 = cv2.calcHist([gray_image1], [0], None, [256], [0, 256])
        hist_image2 = cv2.calcHist([gray_image2], [0], None, [256], [0, 256])
    
        # Normalize the histograms
        cv2.normalize(hist_image1, hist_image1, alpha=0, beta=1, norm_type=cv2.NORM_MINMAX)
        cv2.normalize(hist_image2, hist_image2, alpha=0, beta=1, norm_type=cv2.NORM_MINMAX)
    
        # Calculate the correlation coefficient between the histograms
        correlation_coefficient = cv2.compareHist(hist_image1, hist_image2, cv2.HISTCMP_CORREL)
    
        return correlation_coefficient
    
    score_histogram = histogram_analysis(target_img, input_img)
    print(f'!!! score_histogram !!! === {score_histogram}')
    
    
    
    # perceptual hashing
    def preprocess_image_for_hashing(img):
        # Load image in grayscale
        # img = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)
        gray_image = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        # Resize image to 16x16
        img = cv2.resize(gray_image, (16, 16))
        return img
    
    
    def calculate_dct_coefficients(img):
        # Calculate DCT coefficients
        dct_coeffs = cv2.dct(np.float32(img))
        # Keep top-left 4x4 coefficients
        dct_coeffs = dct_coeffs[:4, :4]
        return dct_coeffs
    
    
    def hash_image(img):
        # Preprocess image
        img = preprocess_image_for_hashing(img)
        # Calculate DCT coefficients
        dct_coeffs = calculate_dct_coefficients(img)
        # Compute median of coefficients
        median = np.median(dct_coeffs)
        # Threshold coefficients to generate hash
        hash = (dct_coeffs > median).flatten()
        return hash
        
    
    def hamming_distance(hash1, hash2):
        # Calculate Hamming distance between two hashes
        return np.sum(hash1 != hash2)
    
    
    def measure_hash_similarity(image1, image2):
        # Calculate perceptual hash for both images
        hash1 = hash_image(image1)
        hash2 = hash_image(image2)
        # Compute Hamming distance between the two hashes
        distance = hamming_distance(hash1, hash2)
        # Compute similarity score
        score = 1 - (distance / float(hash1.size))
        # the higher the score, the more similar the images
        return score


    score_hashing = measure_hash_similarity(target_img, input_img)
    print(f'!!! score_hashing !!! === {score_hashing}')
    
    
    
    # SSIM --colored
    def calculate_color_ssim(img1, img2):
        
        img1 = cv2.resize(img1, (500, 500))
        img2 = cv2.resize(img2, (500, 500))
        
        # constants for SSIM calculation
        k1 = 0.01
        k2 = 0.03
        L = 255
    
        # compute means and standard deviations
        mu1 = cv2.mean(img1)[0]
        mu2 = cv2.mean(img2)[0]
        sigma1 = cv2.meanStdDev(img1)[1][0][0]
        sigma2 = cv2.meanStdDev(img2)[1][0][0]
        sigma12 = cv2.meanStdDev(cv2.multiply(img1, img2))[1][0][0]
    
        # compute SSIM score
        C1 = (k1*L)**2
        C2 = (k2*L)**2
        C3 = C2/2
        ssim = ((2*mu1*mu2 + C1)*(2*sigma12 + C2)) / ((mu1**2 + mu2**2 + C1)*(sigma1**2 + sigma2**2 + C2))
    
        # return single float score
        return np.mean(ssim)
        

    score_color_ssim = calculate_color_ssim(target_img_grabcut, input_img_grabcut)
    print(f'!!! score_color_ssim !!! === {score_color_ssim}')
    
    
    
    # SSIM --grayscaled
    def calculate_gray_ssim(img1, img2):
        # Resize the images to the same size
        img1 = cv2.resize(img1, (img2.shape[1], img2.shape[0]))
    
        # Convert the images to grayscale
        gray1 = cv2.cvtColor(img1, cv2.COLOR_BGR2GRAY)
        gray2 = cv2.cvtColor(img2, cv2.COLOR_BGR2GRAY)
    
        # Set the constants for the SSIM formula
        C1 = (0.01 * 255) ** 2
        C2 = (0.03 * 255) ** 2
    
        # Compute the means of the two images
        mean1 = cv2.mean(gray1)[0]
        mean2 = cv2.mean(gray2)[0]
    
        # Compute the variances of the two images
        var1 = cv2.meanStdDev(gray1)[1]**2
        var2 = cv2.meanStdDev(gray2)[1]**2
    
        # Compute the covariance of the two images
        covar = cv2.mean(gray1 * gray2)[0] - mean1 * mean2
    
        # Compute the SSIM value
        numerator = (2 * mean1 * mean2 + C1) * (2 * covar + C2)
        denominator = (mean1 ** 2 + mean2 ** 2 + C1) * (var1 + var2 + C2)
        ssim = numerator / denominator
    
        return ssim[0][0]


    score_gray_ssim = calculate_gray_ssim(target_img, input_img)
    print(f'!!! score_gray_ssim !!! === {score_gray_ssim}')
    
    
    
    ensemble_score = int(
                     10000*score_kaze_feature +\
                     10000*score_histogram +\
                     200*score_hashing +\
                     200*score_color_ssim \
                     )


    return str(ensemble_score)    

    # return {
    #     'statusCode': 200,
    #     'result': ensemble_score
    # }
    
