package panoramajoinner.linefollowers;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
public class Sobel {

    public enum Direction {

        HOR, VER, BOTH;
    }

    private static class SobelOperator {

        int[] input;
        int[] output;
        float[] template = {-1, 0, 1, -2, 0, 2, -1, 0, 1};

        ;
        int progress;
        int templateSize = 3;
        int width;
        int height;
        double[] direction;
        private Direction ddirection;

        public SobelOperator(Direction d) {
            progress = 0;
            ddirection = d;
        }

        private void init(int[] original, int widthIn, int heightIn) {
            width = widthIn;
            height = heightIn;
            input = new int[width * height];
            output = new int[width * height];
            direction = new double[width * height];
            input = original;
        }

        private int[] process() {
            float[] GY = new float[width * height];
            float[] GX = new float[width * height];
            int[] total = new int[width * height];
            progress = 0;
            int sum = 0;
            int max = 0;

            for (int x = (templateSize - 1) / 2; x < width - (templateSize + 1) / 2; x++) {
                progress++;
                for (int y = (templateSize - 1) / 2; y < height - (templateSize + 1) / 2; y++) {
                    sum = 0;
                    if (ddirection == Direction.BOTH || ddirection == Direction.VER) {
                        for (int x1 = 0; x1 < templateSize; x1++) {
                            for (int y1 = 0; y1 < templateSize; y1++) {
                                int x2 = (x - (templateSize - 1) / 2 + x1);
                                int y2 = (y - (templateSize - 1) / 2 + y1);
                                float value = (input[y2 * width + x2] & 0xff) * (template[y1 * templateSize + x1]);
                                sum += value;
                            }
                        }
                        GY[y * width + x] = sum;
                    } else {
                        GY[y * width + x] = 0;
                    }

                    if (ddirection == Direction.BOTH || ddirection == Direction.HOR) {
                        for (int x1 = 0; x1 < templateSize; x1++) {
                            for (int y1 = 0; y1 < templateSize; y1++) {
                                int x2 = (x - (templateSize - 1) / 2 + x1);
                                int y2 = (y - (templateSize - 1) / 2 + y1);
                                float value = (input[y2 * width + x2] & 0xff) * (template[x1 * templateSize + y1]);
                                sum += value;
                            }
                        }
                        GX[y * width + x] = sum;
                    } else {
                        GX[y * width + x] = 0;
                    }

                }
            }
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    total[y * width + x] = (int) Math.sqrt(GX[y * width + x] * GX[y * width + x] + GY[y * width + x] * GY[y * width + x]);
                    direction[y * width + x] = Math.atan2(GX[y * width + x], GY[y * width + x]);
                    if (max < total[y * width + x]) {
                        max = total[y * width + x];
                    }
                }
            }
            float ratio = (float) max / 255;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    sum = (int) (total[y * width + x] / ratio);
                    output[y * width + x] = 0xff000000 | ((int) sum << 16 | (int) sum << 8 | (int) sum);
                }
            }
            progress = width;
            return output;
        }

        private double[] getDirection() {
            return direction;
        }

        public int getProgress() {
            return progress;
        }

        public int[] threshold(int[] original, int value) {
            for (int x = 0; x < original.length; x++) {
                if ((original[x] & 0xff) >= value) {
                    original[x] = 0xffffffff;
                } else {
                    original[x] = 0xff000000;
                }
            }
            return original;
        }
    }

    public static BufferedImage processImage(BufferedImage image, final Integer treshold, Direction direction) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int[] orig = new int[width * height];
        PixelGrabber grabber = new PixelGrabber(image, 0, 0, width, height, orig, 0, width);
        try {
            grabber.grabPixels();
        } catch (InterruptedException e2) {
           e2.printStackTrace();
        }

        final SobelOperator edgedetector = new SobelOperator(direction);






        edgedetector.init(orig, width, height);
        int[] res = edgedetector.process();
        if (treshold != null) {
            res = edgedetector.threshold(res, treshold.intValue());
        }
        final Image toolkitImage = new Component() {
        }.createImage(new MemoryImageSource(width, height, res, 0, width));
        BufferedImage br = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        br.createGraphics().drawImage(toolkitImage, 0, 0, null);
        return br;

    }
}
