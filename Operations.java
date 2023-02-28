import java.util.concurrent.locks.ReentrantLock;

public class Operations {
    
    public static float addKahan(float[] arr) {
        float sum = 0.0f;
        float err = 0.0f;
        for (float item : arr) {
            float y = item - err;
            float t = sum + y;
            err = (t - sum) - y;
            sum = t;
        }
        return sum;
    }


    public static float[] addTwoVectors(float[] A, float[] B) {
        float[] result = new float[A.length];
        for (int i = 0; i < A.length; i++) {
            result[i] = A[i] + B[i];
        }
        return result;
    }

    public static void multiplyMatrices(float[][] A, float[][] B, float[][] result, int start, int end) {
        ReentrantLock lock = new ReentrantLock(true);
        for (int i = 0; i < A.length; i++) {
            for (int j = start; j < end; j++) {
                for (int k = 0; k < B.length; k++) {
                    lock.lock();
                    try {
                        result[i][j] = addKahan(new float[] { result[i][j], A[i][k] * B[k][j] });
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
    }

    public static void multiplyMatrixByInteger(float[][] A, float a) {
        ReentrantLock lock = new ReentrantLock(true);
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[i].length; j++) {
                lock.lock();
                try {
                    A[i][j] = a * A[i][j];
                } finally {
                    lock.unlock();
                }
                
            }
        }
    }

    public static float maxValue(float[] A) {
        float maxValue = A[0];
        for (int i = 1; i < A.length; i++) {
            maxValue = Math.max(maxValue, A[i]);
        }
        return maxValue;
    }

    public static float minValue(float[] A) {
        float minValue = A[0];
        for (int i = 1; i < A.length; i++) {
            minValue = Math.min(minValue, A[i]);
        }
        return minValue;
    }

    public static void addMatrices(float[][] A, float[][] B, float[][] C) {
        ReentrantLock lock = new ReentrantLock(true);
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[i].length; j++) {
                lock.lock();
                try {
                    C[i][j] = A[i][j] + B[i][j];
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}
