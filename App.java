package sort_with_threads;
import java.util.Random;


/* Created by Victor Ignatenkov on 10/13/15. */

enum SORT_TYPES {
    INSERTION,
    QUICK,
    MERGE,
}


public class App {

    private int[] arr;
    private int[][] indices;
    private int NUM_OF_THREADS;
    private int NUM_OF_ELEMENTS;
    private SORT_TYPES SORTING_ALGORITHM;
    private   String sortName;

    public int getNumberOfThreads() {
        return NUM_OF_THREADS;
    }
    public void setNumberOfThreads(int NUM_THREADS) {
        this.NUM_OF_THREADS = NUM_THREADS;
    }
    public int getNumberOfElements() {
        return NUM_OF_ELEMENTS;
    }
    public void setNumberOfElements(int NUM_ELEMENTS) {
        this.NUM_OF_ELEMENTS = NUM_ELEMENTS;
    }


    int assertSuccessSort( int arr[], int length){
        int i = 0;
        while(i != length-1 ){
            if(arr[i] > arr[i+1]){
                return -1;
            }
            i++;
        }
        return 1;
    }

    private static int separateInTwoHalves(int[] arr, int l, int h) {
      /*    i   |           |    j             | pivot
            2       4     6      7      1      3
            <   |    >      |                  |     */

        int pivot = arr[h];
        int i = l - 1;
        for(int j = l; j < h; j++){
            if(arr[j] < pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        i++;
        swap(arr, i, h);
        return i;
    }

    public static void quickSort(int[] arr, int l, int h) {
        if(l < h){
            int pivot = separateInTwoHalves(arr, l, h);
            quickSort(arr, l, pivot-1);
            quickSort(arr, pivot+1, h);
        }
    }

    private static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }


    private void generateArrayWithRandomNumbers(final int len, final int low, final int high){
            arr = new int[len];
            int temp = 0;
            while(temp < len){
                arr[temp] = Math.abs(new Random().nextInt())% NUM_OF_ELEMENTS;
                temp++;
            }
    }


    private void insertionSort(int arr[], int l,  int n) {
       for(int i = l + 1; i < l + n; i++){
           int key = arr[i];
           int j = i - 1;
           while(j >= l && key < arr[j]){
               arr[j + 1] = arr[j];
               j--;
           }
           j++;
           arr[j] = key;
       }
    }


    private void merge(int arr[], int l, int m, int r) {
        int i, j, k;
        int n1 = m - l + 1;
        int n2 =  r - m;

        int[] L = new int[n1];
        int[] R = new int[n2];

        for(i = 0; i < n1; i++)
            L[i] = arr[l + i];
        for(j = 0; j < n2; j++)
            R[j] = arr[m + 1+ j];

        i = 0;
        j = 0;
        k = l;
        while (i < n1 && j < n2)
        {
            if (L[i] <= R[j])
            {
                arr[k] = L[i];
                i++;
            }
            else
            {
                arr[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1)
        {
            arr[k] = L[i];
            i++;
            k++;
        }

        while (j < n2)
        {
            arr[k] = R[j];
            j++;
            k++;
        }
    }

    private void mergeSort(int arr[], int l, int r)
    {
        if (l < r)
        {
            int m = l+(r-l)/2; //Same as (l+r)/2, but avoids overflow for large l and h
            mergeSort(arr, l, m);
            mergeSort(arr, m+1, r);
            merge(arr, l, m, r);
        }
    }

    private void runSort(int numOfElements, SORT_TYPES type, int numOfThreads ){
        setNumberOfElements(numOfElements);
        SORTING_ALGORITHM = type;
        NUM_OF_THREADS = numOfThreads;


        indices = new int[numOfThreads][2];
        long startTime;
        long endTime;

        int low;
        int pivot = NUM_OF_ELEMENTS / NUM_OF_THREADS;
        for (int i = 0, j = 1; i < NUM_OF_THREADS; i++, j++) {
            low = i * pivot;

        /* This case is only for the cases when division
         * of elements in the array by the number of threads
         * doesn't produce equal sections
         *
         * 11 elements and 4 threads
         * 11/4 = 3
         * 0-2 3-5 6-8 9-the rest of the array  */
            if (i == NUM_OF_THREADS - 1) {
                if(  SORTING_ALGORITHM != SORTING_ALGORITHM.INSERTION ){
                    indices[i][0] = low;
                    indices[i][1] = NUM_OF_ELEMENTS - 1;}
                else {
                    indices[i][0] = low;
                    indices[i][1] = NUM_OF_ELEMENTS %(NUM_OF_THREADS) + pivot;
                }

            } else {
            /* In case of an array with 20 elements and 4 threads
             * 0-4 5-9 10-14 15-19
             *
             * pivot = 20 / 4 = 5 */
                if(  SORTING_ALGORITHM != SORTING_ALGORITHM.INSERTION ){
                    indices[i][0] = low;
                    indices[i][1] = j * pivot - 1;}
                else {
                    indices[i][0] = low;
                    indices[i][1] = pivot;
                }

            }
        }

        generateArrayWithRandomNumbers(NUM_OF_ELEMENTS, 0, 0);
        startTime = System.currentTimeMillis();

        for (int i = 0; i < NUM_OF_THREADS; i++) {
            if (SORTING_ALGORITHM == SORTING_ALGORITHM.MERGE) {
                mergeSort(arr, indices[i][0], indices[i][1]);
            }
            else if (SORTING_ALGORITHM == SORTING_ALGORITHM.QUICK){
                quickSort(arr, indices[i][0], indices[i][1]);
               }
            else if (SORTING_ALGORITHM == SORTING_ALGORITHM.INSERTION) {
                insertionSort( arr,indices[i][0],  indices[i][1]);
                }
        }


        pivot = NUM_OF_ELEMENTS / NUM_OF_THREADS;
        for(int i = 0, j = 1; i < NUM_OF_THREADS -1; i++, j++){
            if((NUM_OF_THREADS -2) == i){
                merge(arr, 0, (j*pivot)-1, NUM_OF_ELEMENTS - 1);
            }else {
                merge(arr, 0, (j*pivot)-1, (j+1)*pivot-1);
            }
        }
        endTime = System.currentTimeMillis();

        System.out.println("Sorting with a single thread:");
        System.out.println("\tTime spent: " + (endTime - startTime));

        int success = assertSuccessSort(arr, NUM_OF_ELEMENTS);
        if (success == 1 ){
            System.out.println("\tSort with " + sortName + " was accurate");
        }else{
            System.out.println("\tSort with " + sortName + " inaccurate");
        }

        System.out.println("\n");

        generateArrayWithRandomNumbers(NUM_OF_ELEMENTS, 0,0);
        System.out.println("Sorting with multiple threads:");
        startTime = System.currentTimeMillis();


        Thread [] threads = new Thread[NUM_OF_THREADS];
        for (int i = 0; i < NUM_OF_THREADS; i++) {
            final int iInsideClosure = i;
            threads[i] = new Thread( ()-> {
                if (SORTING_ALGORITHM == SORTING_ALGORITHM.MERGE) {
                    mergeSort(arr, indices[iInsideClosure][0], indices[iInsideClosure][1]);
                }
                else if (SORTING_ALGORITHM == SORTING_ALGORITHM.QUICK){
                    quickSort(arr, indices[iInsideClosure][0], indices[iInsideClosure][1]);
                }
                else if (SORTING_ALGORITHM == SORTING_ALGORITHM.INSERTION) {
                    insertionSort( arr,indices[iInsideClosure][0],  indices[iInsideClosure][1]);
                }
            });
        }
        for(int i = 0; i < NUM_OF_THREADS; i++){
            threads[i].start();
        }
        for(int i = 0; i < NUM_OF_THREADS; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        pivot = NUM_OF_ELEMENTS / NUM_OF_THREADS;
        for(int i = 0, j = 1; i < NUM_OF_THREADS -1; i++, j++){
            if((NUM_OF_THREADS -2) == i){
                merge(arr, 0, (j*pivot)-1, NUM_OF_ELEMENTS - 1);
            }else {
                merge(arr, 0, (j*pivot)-1, (j+1)*pivot-1);
            }
        }

        endTime = System.currentTimeMillis();
        System.out.println("\tTime spent: " + (endTime - startTime));

        success = assertSuccessSort(arr, NUM_OF_ELEMENTS);
        if (success == 1 ){
            System.out.println("\tSort with " + sortName + " was accurate");
        }else{
            System.out.println("\tSort with " + sortName + " inaccurate");
        }
    }


    public static void main(String[] args) {
        SORT_TYPES tempSortType;
        //DEFAULT VALUE, if parameters weren't provided
        tempSortType = SORT_TYPES.QUICK;

        int length;
        char typeOfSort;
        int numOfThreads;
        App app = new App();

        if(args.length == 3) {
            length = Integer.parseInt(args[0]);
            numOfThreads = Integer.parseInt(args[1]);
            typeOfSort = (args[2].toCharArray())[0];
            if (typeOfSort == 'i' || typeOfSort == 'I') {
                tempSortType = SORT_TYPES.INSERTION;
                app.sortName = "Insertion";
            } else if (typeOfSort == 'm' || typeOfSort == 'M') {
                tempSortType = SORT_TYPES.MERGE;
                app.sortName = "Merge";
            }
            else if (typeOfSort == 'q' || typeOfSort == 'Q') {
                tempSortType = SORT_TYPES.QUICK;
                app.sortName = "Quick";
            }
            app.runSort(length, tempSortType, numOfThreads);
        } else {
            app.sortName = "Quick";
            app.runSort(1000000, SORT_TYPES.QUICK, 4);
        }
    }
}

   /* * * * *  TEST RESULTS * * * * *
     1. Run InsertionSort using two threads with array sizes 10K, 100K and 300K.
                    1 thread                                                 2 threads
     10 K           83|33|24|43                                              157|69|49|80
     100 K          1834|1822|1897|1763|1929                                 880|814|896|908|900
     300 K          15016|14862|14933|14869                                  7911|7880|7977|7893


     2. Run InsertionSort using four threads with an array size of 100K.
                     1 thread                                                4 threads
     100 K           884|941|1013|937|                                       410|441|453|412



     3. Run QuickSort using two threads with array sizes 1M, 10M and 100M.
                     1 thread                                                 2 threads
     1M              222|230|207|190                                         150|145|148|140
     10M             1497|1542|1551|1672|1619                                 739|776|833|831|779
     100M            16122|15355|15250                                        8035|7889|7680



     4. Run QuickSort using four threads with an array size of 10M.
                     1 thread                                                 4 threads
     10M             1622|1664|1504|1579|1704                                 620|718|656|652|759
     */
