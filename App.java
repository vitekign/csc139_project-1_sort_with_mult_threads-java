package sort_with_threads;
import java.util.Random;


/**
 * Created by Victor Ignatenkov on 10/13/15.
 */

enum SORT_TYPES {
    INSERTION,
    QUICK,
    MERGE,
}




public class App {

    public int[] arr;
    public int[][] indices;

    int NUM_THREADS;

    int NUM_ELEMENTS;

    SORT_TYPES SORTING_ALGORITHM;




    public int getNumberOfThreads() {
        return NUM_THREADS;
    }

    public void setNumberOfThreads(int NUM_THREADS) {
        this.NUM_THREADS = NUM_THREADS;
    }




    public int getNumberOfElements() {
        return NUM_ELEMENTS;
    }

    public void setNumberOfElements(int NUM_ELEMENTS) {
        this.NUM_ELEMENTS = NUM_ELEMENTS;
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


    public static void quickSort(int[] a, int p, int r)
    {
        if(p<r)
        {
            int q=partition(a,p,r);
            quickSort(a,p,q);
            quickSort(a,q+1,r);
        }
    }

    private static int partition(int[] a, int p, int r) {

        int x = a[p];
        int i = p-1 ;
        int j = r+1 ;

        while (true) {
            i++;
            while ( i< r && a[i] < x)
                i++;
            j--;
            while (j>p && a[j] > x)
                j--;

            if (i < j)
                swap(a, i, j);
            else
                return j;
        }
    }

    private static void swap(int[] a, int i, int j) {
        // TODO Auto-generated method stub
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }




//    /**
//     * Quick Sort
//     */
//
//    private int  partition( int a[], int l, int r) {
//        int pivot, i, j, t;
//        pivot = a[l];
//        i = l; j = r+1;
//
//        while(true)
//        {
//            do ++i; while( a[i] <= pivot && i <= r );
//            do --j; while( a[j] > pivot );
//            if( i >= j ) break;
//            t = a[i]; a[i] = a[j]; a[j] = t;
//        }
//        t = a[l]; a[l] = a[j]; a[j] = t;
//        return j;
//    }
//
//    private void quickSort( int a[], int l, int r)
//    {
//        int j;
//
//        if( l < r )
//        {
//            // divide and conquer
//            j = partition( a, l, r);
//            quickSort( a, l, j-1);
//            quickSort( a, j+1, r);
//        }
//
//    }
//    /**
//     * END of Quick Sort
//     */









    private void generateArrayWithRandomNumbers(final int len, final int low, final int high){
        arr = new int[len];
        int temp = 0;
        while(temp < len){
            arr[temp] = Math.abs(new Random().nextInt())%NUM_ELEMENTS;
            temp++;
        }

    }


    private void insertionSort(int arr[], int start,  int length) {
        int i, j, tmp;
        for (i = 1; i < length; i++) {
            j = i;
            while (j > 0 && arr[start + j - 1] > arr[start + j]) {
                tmp = arr[start + j];
                arr[start + j] = arr[start + j - 1];
                arr[start + j - 1] = tmp;
                j--;
            }
        }
    }


    private void merge(int arr[], int l, int m, int r)
    {
        int i, j, k;
        int n1 = m - l + 1;
        int n2 =  r - m;

    /* create temp arrays */
        int[] L = new int[n1];
        int[] R = new int[n2];

    /* Copy data to temp arrays L[] and R[] */
        for(i = 0; i < n1; i++)
            L[i] = arr[l + i];
        for(j = 0; j < n2; j++)
            R[j] = arr[m + 1+ j];

    /* Merge the temp arrays back into arr[l..r]*/
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

    /* Copy the remaining elements of L[], if there are any */
        while (i < n1)
        {
            arr[k] = L[i];
            i++;
            k++;
        }

    /* Copy the remaining elements of R[], if there are any */
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


    private void runSort(int numElements, SORT_TYPES type, int numThreads ){
        setNumberOfElements(numElements);
        SORTING_ALGORITHM = type;
        NUM_THREADS = numThreads;

        indices = new int[20][2];
        long startTime;
        long endTime;
        long duration;



        /**
         * CHANGE TYPE OF SORT HERE !!!
         */


        int low;
        int pivot = NUM_ELEMENTS / NUM_THREADS;
        for (int i = 0, j = 1; i < NUM_THREADS; i++, j++) {
            low = i * pivot;

        /*
         * This case is only for the cases when division
         * of elements in the array by the number of threads
         * doesn't produce equal sections
         *
         * 11 elements and 4 threads
         * 11/4 = 3
         * 0-2 3-5 6-8 9-the rest of the array
         *
         */
            if (i == NUM_THREADS - 1) {
                if(  SORTING_ALGORITHM != SORTING_ALGORITHM.INSERTION ){
                    indices[i][0] = low;
                    indices[i][1] = NUM_ELEMENTS - 1;}
                else {
                    indices[i][0] = low;
                    indices[i][1] = NUM_ELEMENTS%(NUM_THREADS) + pivot;
                }

            } else {
            /*
             * In case of an array with 20 elements and 4 threads
             * 0-4 5-9 10-14 15-19
             *
             * pivot = 20 / 4 = 5
             */
                if(  SORTING_ALGORITHM != SORTING_ALGORITHM.INSERTION ){
                    indices[i][0] = low;
                    indices[i][1] = j * pivot - 1;}
                else {
                    indices[i][0] = low;
                    indices[i][1] = pivot;
                }

            }
        }


        generateArrayWithRandomNumbers(NUM_ELEMENTS, 0, 0);
        startTime = System.currentTimeMillis();

        for (int i = 0; i < NUM_THREADS; i++) {
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


//        for (int i = 0; i < NUM_ELEMENTS; i++) {
//            System.out.println(arr[i]);
//        }



        pivot = NUM_ELEMENTS / NUM_THREADS;
        for(int i = 0 ,j= 1; i < NUM_THREADS-1; i++, j++){
            low = i * pivot;
            if((NUM_THREADS-2) == i){
                merge(arr, 0, (j*pivot)-1, NUM_ELEMENTS - 1);
            }else {
                merge(arr, 0, (j*pivot)-1, (j+1)*pivot-1);
            }
        }
        endTime = System.currentTimeMillis();

        System.out.println("Time spent: " + (endTime - startTime));

        int success = assertSuccessSort(arr, NUM_ELEMENTS);
        if (success ==1 ){
            System.out.println("Sort was successful");
        }else{
            System.out.println("Sort wasn't successful");
        }




        System.out.println("\n");
//        for (int i = 0; i < NUM_ELEMENTS; i++) {
//            System.out.println(i + " : " + arr[i]);
//        }


        generateArrayWithRandomNumbers(NUM_ELEMENTS, 0,0);


        Runnable [] tasks = new Runnable[12];


        System.out.println("Sorting with multiple threads");


        startTime = System.currentTimeMillis();

        for(int i = 0; i < NUM_THREADS; i++){
            final int iInsideClosure = i;
            tasks[i] = () ->{

                if (SORTING_ALGORITHM == SORTING_ALGORITHM.MERGE) {
                    mergeSort(arr, indices[iInsideClosure][0], indices[iInsideClosure][1]);
                }
                else if (SORTING_ALGORITHM == SORTING_ALGORITHM.QUICK){
                    quickSort(arr, indices[iInsideClosure][0], indices[iInsideClosure][1]);
                }
                else if (SORTING_ALGORITHM == SORTING_ALGORITHM.INSERTION) {
                    insertionSort( arr,indices[iInsideClosure][0],  indices[iInsideClosure][1]);
                }
            };

        }

        Thread [] threads = new Thread[12];

        for(int i = 0; i < NUM_THREADS; i++){
            threads[i] = new Thread(tasks[i]);
        }

        for(int i = 0; i < NUM_THREADS; i++){
            threads[i].start();
        }

        for(int i = 0; i < NUM_THREADS; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        pivot = NUM_ELEMENTS / NUM_THREADS;
        for(int i = 0 ,j= 1; i < NUM_THREADS-1; i++, j++){
            low = i * pivot;
            if((NUM_THREADS-2) == i){
                merge(arr, 0, (j*pivot)-1, NUM_ELEMENTS - 1);
            }else {
                merge(arr, 0, (j*pivot)-1, (j+1)*pivot-1);
            }
        }

        endTime = System.currentTimeMillis();
        System.out.println("Time spent: " + (endTime - startTime));


        success = assertSuccessSort(arr, NUM_ELEMENTS);
        if (success == 1 ){
            System.out.println("Sort was successful");
        }else{
            System.out.println("Sort wasn't successful");
        }



    }



    public static void main(String[] args) {

        App app = new App();

        app.runSort(10000000, SORT_TYPES.QUICK, 4);


    }
}
