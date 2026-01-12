# 🖼️ Quadtree Image Processing System

> **Developed by Bekir Ata Yıldırım**

This project implements a **Digital Image Processing System** based on the **Quadtree** data structure. It performs efficient image compression and edge detection filtering by recursively partitioning images into quadrants based on color similarity.

The system works with **PPM (Portable Pixel Map - P3)** format images and demonstrates recursive algorithms, spatial data structures, and convolution filtering.

---

## 🛠️ Key Features

* **Quadtree Decomposition:** Recursively divides the image into quadrants to analyze spatial data efficienty.
* **Adaptive Compression:** Generates compressed versions of PPM images at various quality levels (approx. 0.002 to 0.65 ratio) using dynamic error thresholds.
* **Edge Detection Filter:** Applies a weighted 3x3 convolution kernel to highlight edges while utilizing the quadtree structure for performance.
* **Visualization:** Includes a debug mode to outline quadtree leaf nodes directly on the output image.

---

## 🚀 How to Run

It uses standard Java, so no external libraries are required.

**How to compile and run it yourself:**

### 1. Download the code:

```bash
git clone https://github.com/BekirAtaYildirim/Quadtree-Image-Processing-System.git
```

### 2. Compile:
Navigate to the source directory and compile the Java files.

```bash

javac *.java
```

### 3. Run:
The program is controlled via command-line flags. You must specify an input file (-i) and a mode (-c or -e).

```bash

java Main [-c | -e] [-t] -i <inputFile> [-o <outputName>]
```
