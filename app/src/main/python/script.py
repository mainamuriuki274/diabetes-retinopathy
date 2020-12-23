from cv2 import cv2
from skimage.measure import compare_ssim
import argparse
import numpy as np
import imutils
import base64
import io
from PIL import Image

def main(imageA,imageB):
    imageA = base64.b64decode(imageA)
    imageB = base64.b64decode(imageB)

    np_imageA = np.fromstring(imageA,np.uint8)
    np_imageB = np.fromstring(imageB,np.uint8)

    imageA = cv2.imdecode(np_imageA,cv2.IMREAD_UNCHANGED)
    imageB = cv2.imdecode(np_imageB,cv2.IMREAD_UNCHANGED)

    grayA = cv2.cvtColor(imageA, cv2.COLOR_RGB2GRAY)
    grayB = cv2.cvtColor(imageB, cv2.COLOR_RGB2GRAY)
    
    grayA = cv2.resize(grayA, (224, 224)) 
    grayB = cv2.resize(grayB, (224, 224)) 

    (score, diff) = compare_ssim(grayA, grayB, full=True)
    diff = (diff * 255).astype("uint8")
    #print("SSIM: {}".format(score))
    thresh = cv2.threshold(diff, 0, 255,
        cv2.THRESH_BINARY_INV | cv2.THRESH_OTSU)[1]
    cnts = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL,
        cv2.CHAIN_APPROX_SIMPLE)
    cnts = imutils.grab_contours(cnts)
    for c in cnts:
        (x, y, w, h) = cv2.boundingRect(c)
        cv2.rectangle(imageA, (x, y), (x + w, y + h), (0, 0, 255), 2)
        cv2.rectangle(imageB, (x, y), (x + w, y + h), (0, 0, 255), 2)
    imageA = cv2.resize(imageA, (224, 224))
    pil_im = Image.fromarray(imageA)
    buff = io.BytesIO()
    pil_im.save(buff,format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    diffimage = ""+str(img_str,'utf-8')
    result = [diffimage,"{:.2f}".format(score),diff,thresh]
        #.append([diffimage,"SSIM: {}".format(score)])
    return result