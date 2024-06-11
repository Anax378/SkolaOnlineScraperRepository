package net.anax.skolaOnlineScraper.util;

public class ArrayUtilities {

    @SuppressWarnings("unchecked")
    public static <T> T[] concatenateArrays(T[] arr1, T[] arr2){
        Object[] arr = new Object[arr1.length + arr2.length];
        for(int i = 0; i < arr.length; i++){
            arr[i] = i < arr1.length ? arr1[i] : arr2[i - arr1.length];
        }
        return (T[]) arr;
    }
}
