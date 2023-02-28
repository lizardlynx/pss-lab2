import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.io.FileWriter; // Import the FileWriter class
import java.io.IOException; // Import the IOException class to handle errors
import java.io.File; // Import the File class
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.lang.Math;

// C=В*МС-D*MM;
// MF=min(B+D)*MC*MZ+MM*(MC+MM)*a.
public class Lab2 implements Callable<long[]> {

    private int minSize = 100;
    private int maxSize = 900;

    private int minFloat = 3;
    private int maxFloat = 9;

    private float a = 1.5f;
    private int m;

    // матриці
    private float[][] MC;
    private float[][] MM;
    private float[][] MZ;

    // результатні матриці
    private float[][] MP;
    private float[][] MH;
    private float[][] MF;
    
    //вектори
    private float[][] B;
    private float[][] D;

    //результатні вектори
    private float[][] N;
    private float[][] L;
    private float[][] C;

    public static SyncWriterImpl syncWriter;
    private int func;

    public void generateMatrix(float[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            generateVector(matrix[i]);
        }
    }

    public void generateVector(float[] vector) {
        Random r = new Random();
        for (int i = 0; i < vector.length; i++) {
            vector[i] = minFloat + r.nextFloat() * (maxFloat - minFloat);
        }
    }

    public void generateInputData(int num) {
        // generate sizes
        m = ThreadLocalRandom.current().nextInt(minSize, maxSize + 1);

        // generate vectors
        B = new float[1][m];
        generateMatrix(B);
        D = new float[1][m];
        generateMatrix(D);

        N = new float[1][m];
        L = new float[1][m];
        C = new float[1][m];

        MC = new float[m][m];
        generateMatrix(MC);
        MM = new float[m][m];
        generateMatrix(MM);
        MZ = new float[m][m];
        generateMatrix(MZ);

        MP = new float[m][m];
        MH = new float[m][m];
        MF = new float[m][m];

        try {
            FileWriter myWriter = new FileWriter("input_data_"+num+".txt");
            myWriter.write(Arrays.toString(B[0]).toString() + "\n");
            myWriter.write(Arrays.toString(D[0]).toString() + "\n");
            for (int i = 0; i < MC.length; i++) {
                myWriter.write(Arrays.toString(MC[i]).toString());
                if (i == MC.length - 1)
                    myWriter.write("\n");
                else
                    myWriter.write("\n");
            }
            for (int i = 0; i < MM.length; i++) {
                myWriter.write(Arrays.toString(MM[i]).toString());
                if (i == MM.length - 1)
                    myWriter.write("\n");
                else
                    myWriter.write("\n");
            }
            for (int i = 0; i < MZ.length; i++) {
                myWriter.write(Arrays.toString(MZ[i]).toString());
                if (i == MZ.length - 1)
                    myWriter.write("\n");
                else
                    myWriter.write("\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void importInputData(int iter) {
        try {
            File file = new File("input_data_"+iter+".txt");
            Scanner myReader = new Scanner(file);
            int i = 0;

            while (myReader.hasNextLine()) {
                i++;
                String data = myReader.nextLine();
                data = data.substring(1, data.length() - 1);
                String[] nums = data.split(", ");
                if (i == 1) {
                    m = nums.length;
                    B = new float[1][m];
                    D = new float[1][m];
                    MC = new float[m][m];
                    MM = new float[m][m];
                    MZ = new float[m][m];
                    for (int j = 0; j < m; j++) {
                        B[0][j] = Float.valueOf(nums[j]);
                    }
                } else if (i == 2) {
                    for (int j = 0; j < m; j++) {
                        D[0][j] = Float.valueOf(nums[j]);
                    }
                } else if (i > 2 && i <= 2 + m) {
                    for (int j = 0; j < m; j++) {
                        MC[i - 3][j] = Float.valueOf(nums[j]);
                    }
                } else if (i > 2 + m && i <= 2 + 2 * m) {
                    for (int j = 0; j < m; j++) {
                        MM[i - 3 - m][j] = Float.valueOf(nums[j]);
                    }
                } else if (i > 2 + 2 * m && i <= 2 + 3 * m) {
                    for (int j = 0; j < m; j++) {
                        MZ[i - 3 - 2 * m][j] = Float.valueOf(nums[j]);
                    }
                }
            }
            myReader.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void printMatrix(float[][] A, String name, long time) {
        Thread t = Thread.currentThread();
        String matrix = "\n" + t.getName() + ": " + name + "\n";
        if (time != 0) matrix += "Time, calculating: " + time + " nanoseconds\n" + name + "\n";
        for (int i = 0; i < A.length; i++) {
            matrix += Arrays.toString(A[i]) + "\n";
        }
        System.out.println(matrix);

        syncWriter.write(matrix);
    }

    public void printMatrix(long[][] A, String name, long time) {
        Thread t = Thread.currentThread();
        String matrix = "\n" + t.getName() + ": " + name + "\n";
        if (time != 0) matrix += "Time, calculating: " + time + " nanoseconds\n" + name + "\n";
        for (int i = 0; i < A.length; i++) {
            matrix += Arrays.toString(A[i]) + "\n";
        }
        System.out.println(matrix);
        syncWriter.write(matrix);
    }


    // C=В*МС-D*MM
    public void calcFunc1() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(4);
        List<Future<Boolean>> futureResults = new ArrayList<>();

        int n = (int)Math.ceil(MC.length/2);
        Callable<Boolean> c1 = () -> {
            Operations.multiplyMatrices( B, MC, N, 0, n);
            return true;
        };
        Callable<Boolean> c2 = () -> {
            Operations.multiplyMatrices( B, MC, N, n, MC.length);
            return true;
        };
        Callable<Boolean> c3 = () -> {
            Operations.multiplyMatrices( D, MM, L, 0, D[0].length);
            Operations.multiplyMatrixByInteger(L, -1);
            return true;
        };
        futureResults.add(service.submit(c1));
        futureResults.add(service.submit(c2));
        futureResults.add(service.submit(c3));

        for(int i = 0; i< futureResults.size(); i++) {
            futureResults.get(i).get();
        }        
        service.shutdown();
        
        Operations.addMatrices(N, L, C);
    }

    // MF=min(B+D)*MC*MZ+MM*(MC+MM)*a.
    public void calcFunc2() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(4);
        List<Future<Boolean>> futureResults = new ArrayList<>();


        Callable<Boolean> c1 = () -> {
            int n = (int)Math.ceil(MC.length/2);
            float[][] T = new float[1][m];
            Operations.addMatrices(B, D, T);
            float minValue = Operations.minValue(T[0]);
            Operations.multiplyMatrixByInteger(MZ, minValue);
            ExecutorService service1 = Executors.newFixedThreadPool(4);

            Callable<Boolean> c = () -> {
                Operations.multiplyMatrices( MC, MZ, MP, 0, n);
                return true;
            };

            Callable<Boolean> cn = () -> {
                Operations.multiplyMatrices( MC, MZ, MP, n, MC.length);
                return true;
            };

            Future<Boolean> f1 = service1.submit(c);
            Future<Boolean> f2 = service1.submit(cn);

            f1.get();
            f2.get();
            service1.shutdown();

            return true;
        };

        Callable<Boolean> c2 = () -> {
            int n = (int)Math.ceil(MM.length/2);
            float[][] T = new float[m][m];
            Operations.addMatrices(MC, MM, T);
            Operations.multiplyMatrixByInteger(T, a);
            ExecutorService service1 = Executors.newFixedThreadPool(4);

            Callable<Boolean> c = () -> {
                Operations.multiplyMatrices( MM, T, MH, 0, n);
                return true;
            };

            Callable<Boolean> cn = () -> {
                Operations.multiplyMatrices( MM, T, MH, n, MM.length);
                return true;
            };

            Future<Boolean> f1 = service1.submit(c);
            Future<Boolean> f2 = service1.submit(cn);

            f1.get();
            f2.get();
            service1.shutdown();

            return true;
        };


        futureResults.add(service.submit(c1));
        futureResults.add(service.submit(c2));

        for(int i = 0; i< futureResults.size(); i++) {
            futureResults.get(i).get();
        }        
        service.shutdown();
        
        Operations.addMatrices(MP, MH, MF);
    }

    public long[] call() throws InterruptedException, ExecutionException{
        float[][] output;
        String name;
        if (func == 1) {
            calcFunc1();
            output = C;
            name = "C";
        } else {
            calcFunc2();
            output = MF;
            name = "MF";
        }
        long startTime = System.nanoTime();
        long estimatedTime = System.nanoTime() - startTime;
        printMatrix(output, name, estimatedTime);
        return new long[] {m, estimatedTime};
    }

    public Lab2(int i, int iter) {
        func = i;
        importInputData(iter);
        N = new float[1][m];
        L = new float[1][m];
        C = new float[1][m];
        MP = new float[m][m];
        MH = new float[m][m];
        MF = new float[m][m];
    }

    public static void main(String args[]) {
        int times = 50;
        int checks = 5;
        Lab2.syncWriter = new SyncWriterImpl("result.txt");

        // generate input datas
        // Lab2 lab2 = new Lab2(0, 0);
        // for (int i = 0; i < Math.abs(times/checks); i++) {
        //   lab2.generateInputData(i);
        // }

        long[][] timing = new long[times][2]; 
        long[][] timing2 = new long[times][2];
        
        ExecutorService service = Executors.newFixedThreadPool(times);
        List<Future<long[]>> futureResults = new ArrayList<>();
        List<Future<long[]>> futureResults2 = new ArrayList<>();

        for (int i = 0; i < times; i++) {
            int iter = (int)Math.floor(i/checks);
            System.out.println("Use input "+iter);

            Future<long[]> future = service.submit(new Lab2(1, iter));
            futureResults.add(future);

            Future<long[]> future2 = service.submit(new Lab2(2, iter));
            futureResults2.add(future2);

            
        }
        for (int i = 0; i < times; i++) {
            Future<long[]> future = futureResults.get(i);
            Future<long[]> future2 = futureResults2.get(i);
            try {
                long[] res = future.get();
                timing[i][0] = res[0];
                timing[i][1] = res[1];

                long[] res2 = future2.get();
                timing2[i][0] = res2[0];
                timing2[i][1] = res2[1];
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        service.shutdown();
        Arrays.sort(timing, Comparator.comparingDouble(o -> o[0]));
        Arrays.sort(timing2, Comparator.comparingDouble(o -> o[0]));

        String x = "";
        String y = "";
        String x2 = "";
        String y2 = "";
        float[] matrixN = new float[times/checks];
        float[] middleValues = new float[times/checks]; 
        float[] middleValues2 = new float[times/checks]; 
        Arrays.fill(middleValues, 0.0f);
        for (int i = 0; i < timing2.length; i++) {
            matrixN[(int)Math.floor(i/checks)] = timing[i][0];
            middleValues[(int)Math.floor(i/checks)] += timing[i][1];
            middleValues2[(int)Math.floor(i/checks)] += timing2[i][1];
        }

        for (int i = 0; i < middleValues.length; ++ i) {
            middleValues[i] = middleValues[i]/checks;
            middleValues2[i] = middleValues2[i]/checks;
            x += matrixN[i] + ", ";
            x2 += matrixN[i] + ", ";
            y += middleValues[i] + ", ";
            y2 += middleValues2[i] + ", ";
        }

        Lab2.syncWriter.write("x: " + x.substring(0, x.length() - 2) + "\n");
        Lab2.syncWriter.write("y: " + y.substring(0, y.length() - 2) + "\n\n");
        Lab2.syncWriter.write("x2: " + x2.substring(0, x2.length() - 2) + "\n");
        Lab2.syncWriter.write("y2: " + y2.substring(0, y2.length() - 2));
    }

}
