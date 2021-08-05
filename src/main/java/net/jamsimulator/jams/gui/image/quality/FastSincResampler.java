/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.image.quality;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class FastSincResampler {

    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(THREADS);

    public static IntBuffer resample(IntBuffer in, int inWidth, int inHeight, int outWidth, int outHeight, int tasks) {
        try {
            return new FastSincResampler(in, inWidth, inHeight, outWidth, outHeight, tasks).output;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final IntBuffer output;

    private final int inputWidth, inputHeight;
    private final int outputWidth, outputHeight;

    private final float horizontalFilterLength;
    private final float verticalFilterLength;

    private final float[] inVertical;
    private final float[] inHorizontal;

    private final AtomicInteger sharedY = new AtomicInteger(0);
    private final AtomicInteger sharedX = new AtomicInteger(0);

    public FastSincResampler(IntBuffer input, int intputWidth, int inputHeight,
                             int outputWidth, int outputHeight, int tasks)
            throws Exception {
        this.inputWidth = intputWidth;
        this.inputHeight = inputHeight;
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;

        horizontalFilterLength = Math.max((float) intputWidth / outputWidth, 1) * 4.0f;
        verticalFilterLength = Math.max((float) intputWidth / outputWidth, 1) * 4.0f;

        output = IntBuffer.allocate(outputWidth * outputHeight);

        inVertical = new float[intputWidth * inputHeight * 4];
        inHorizontal = new float[intputWidth * inputHeight * 4];

        unpackPixels(input);

        if (tasks > 1) {
            executeTasks(tasks, FirstStepTask::new);
            executeTasks(tasks, SecondStepTask::new);
        } else {
            new FirstStepTask().run();
            new SecondStepTask().run();
        }
    }

    private void executeTasks(int tasks, Callable<Runnable> creator) throws Exception {
        var futures = new ArrayList<Future<?>>(10);
        for (int i = 0; i < tasks; i++) {
            futures.add(EXECUTOR.submit(creator.call()));
        }
        for (Future<?> f : futures) {
            f.get();
        }
    }

    private void unpackPixels(IntBuffer input) {
        int index = 0;
        while (index < inVertical.length && input.position() < input.limit()) {
            int argb = input.get();
            inVertical[index++] = (argb >>> 24);
            inVertical[index++] = (argb >>> 16) & 0xFF;
            inVertical[index++] = (argb >>> 8) & 0xFF;
            inVertical[index++] = argb & 0xFF;
        }
    }

    private class FirstStepTask implements Runnable {

        private final float scale = Math.min((float) outputHeight / inputHeight, 1);
        private final float[] weights = new float[(int) verticalFilterLength + 1];

        @Override
        public void run() {
            int y;
            while ((y = sharedY.getAndIncrement()) < outputHeight) {

                var weightSum = 0.0f;
                var center = (y + 0.5f) * inputHeight / outputHeight;
                var filterStart = center - verticalFilterLength / 2.0f;
                var start = (int) Math.ceil(filterStart - 0.5f);

                // Calculate weights.
                for (int i = 0; i < weights.length; i++) {
                    var input = start + i;
                    var weight = sinc((input + 0.5f - center) * scale,
                            (input + 0.5f - filterStart) / verticalFilterLength);
                    weights[i] = weight;
                    weightSum += weight;
                }

                for (int i = 0; i < weights.length; i++) {
                    var weight = weights[i] / weightSum;
                    var clippedInput = Math.min(Math.max(start + i, 0), inputHeight - 1);
                    for (int x = 0; x < inputWidth; x++) {
                        int from = (clippedInput * inputWidth + x) * 4;
                        int to = (x * outputHeight + y) * 4;
                        inHorizontal[to] += inVertical[from] * weight;
                        inHorizontal[to + 1] += inVertical[from + 1] * weight;
                        inHorizontal[to + 2] += inVertical[from + 2] * weight;
                        inHorizontal[to + 3] += inVertical[from + 3] * weight;
                    }
                }

            }
        }
    }

    private class SecondStepTask implements Runnable {

        private final float scale = Math.min((float) outputWidth / inputWidth, 1);
        private final float[] weights = new float[(int) verticalFilterLength + 1];
        private final float[] outputHorizontalColor = new float[outputHeight * 4];

        @Override
        public void run() {
            int x;
            while ((x = sharedX.getAndIncrement()) < outputWidth) {

                var weightSum = 0.0f;
                var center = (x + 0.5f) * inputWidth / outputWidth;
                var filterStart = center - horizontalFilterLength / 2.0f;
                var start = (int) Math.ceil(filterStart - 0.5f);

                // Calculate weights.
                for (int i = 0; i < weights.length; i++) {
                    var input = start + i;
                    var weight = sinc((input + 0.5f - center) * scale,
                            (input + 0.5f - filterStart) / horizontalFilterLength);
                    weights[i] = weight;
                    weightSum += weight;
                }

                Arrays.fill(outputHorizontalColor, 0);
                for (int i = 0; i < weights.length; i++) {
                    var weight = weights[i] / weightSum;
                    var clippedInput = Math.min(Math.max(start + i, 0), inputWidth - 1);
                    for (int y = 0; y < outputHeight; y++) {
                        int from = (clippedInput * outputHeight + y) * 4;
                        int to = y * 4;
                        outputHorizontalColor[to] += inHorizontal[from] * weight;
                        outputHorizontalColor[to + 1] += inHorizontal[from + 1] * weight;
                        outputHorizontalColor[to + 2] += inHorizontal[from + 2] * weight;
                        outputHorizontalColor[to + 3] += inHorizontal[from + 3] * weight;
                    }
                }

                for (int y = 0; y < outputHeight; y++) {
                    var from = y * 4;
                    var a = clamp0225(outputHorizontalColor[from]);
                    var r = clamp0225(outputHorizontalColor[from + 1]);
                    var g = clamp0225(outputHorizontalColor[from + 2]);
                    var b = clamp0225(outputHorizontalColor[from + 3]);

                    var pixel = (int) (a + 0.5) << 24
                            | ((int) (r + 0.5) & 0xFF) << 16
                            | ((int) (g + 0.5) & 0xFF) << 8
                            | ((int) (b + 0.5) & 0xFF);
                    output.put(y * outputWidth + x, pixel);
                }

            }
        }
    }

    private static float sinc(float x, float y) {
        x *= Math.PI;
        var sinc = x != 0 ? (float) Math.sin(x) / x : 1;
        var window = y >= 0 && y <= 1 ? 1 - Math.abs(y - 0.5f) * 2 : 0;
        return sinc * window;
    }

    private static float clamp0225(float f) {
        return Math.max(Math.min(f, 255), 0);
    }
}
