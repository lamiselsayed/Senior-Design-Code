import cv2
import numpy as np

def dct2(input):
    rows = len(input)
    cols = len(input[0])
    output = np.zeros((rows, cols))

    alpha = 1.0 / np.sqrt(2.0)

    for u in range(rows):
        for v in range(cols):
            sum_val = 0.0
            for x in range(rows):
                for y in range(cols):
                    cosX = np.cos(np.pi * (u + 0.5) * x / rows)
                    cosY = np.cos(np.pi * (v + 0.5) * y / cols)
                    sum_val += input[x][y] * cosX * cosY

            coefficient = alpha * (1.0 if u == 0 else np.sqrt(2.0))
            output[u][v] = coefficient * sum_val

    return output

def extractImage(imagePath):
    frame = cv2.imread(imagePath)
    gray_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    gray_frame_float64 = gray_frame.astype(np.float64)

    return gray_frame_float64


imagePath =  r"C:\Users\zaher.salman\Desktop\LINA JAN 24\Senior II\ArabSign Stacks Cropped\01\train\0001\01_0001_(10_03_21_20_37_17)_c\word_0\5.png"
image = extractImage(imagePath)

output = dct2(image)

topLeftCorner = image[:45, :45]


print("DCT2 result:")
print(topLeftCorner)
