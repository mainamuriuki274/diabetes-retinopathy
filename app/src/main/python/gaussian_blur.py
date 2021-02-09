from cv2 import cv2
import numpy as np
import os
import base64
import io
from PIL import Image

def crop_image_from_gray(img,tol=7):
    if img.ndim ==2:
        mask = img>tol
        return img[np.ix_(mask.any(1),mask.any(0))]
    elif img.ndim==3:
        gray_img = cv2.cvtColor(img, cv2.COLOR_RGB2GRAY)
        mask = gray_img>tol
        
        check_shape = img[:,:,0][np.ix_(mask.any(1),mask.any(0))].shape[0]
        if (check_shape == 0): # image is too dark so that we crop out everything,
            return img # return original image
        else:
            img1=img[:,:,0][np.ix_(mask.any(1),mask.any(0))]
            img2=img[:,:,1][np.ix_(mask.any(1),mask.any(0))]
            img3=img[:,:,2][np.ix_(mask.any(1),mask.any(0))]
    #         print(img1.shape,img2.shape,img3.shape)
            img = np.stack([img1,img2,img3],axis=-1)
    #         print(img.shape)
        return img

def gaussian_blur(image):
    image = base64.b64decode(image)
    np_image = np.fromstring(image,np.uint8)
    image = cv2.imdecode(np_image,cv2.IMREAD_UNCHANGED)
    #DIRECTORY = r"C:/Users/hp/Documents/Dataset(ORIGINAL)/train"
    #image = os.path.join(DIRECTORY, image)
    #image = cv2.imread(image,cv2.IMREAD_COLOR)
    image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    image = crop_image_from_gray(image)
    image = cv2.resize(image, (224, 224),interpolation=cv2.INTER_CUBIC)
    image=cv2.addWeighted ( image,4, cv2.GaussianBlur( image , (0,0) , 30) ,-4 ,128)
    pil_im = Image.fromarray(image)
    buff = io.BytesIO()
    pil_im.save(buff,format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    diffimage = ""+str(img_str,'utf-8')
    return diffimage
    #cv2.imshow("Gaussian", image)
    #cv2.waitKey(0)
#if __name__ == '__main__':
#    gaussian_blur("16_left.jpeg")