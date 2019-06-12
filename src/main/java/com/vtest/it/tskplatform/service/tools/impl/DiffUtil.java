package com.vtest.it.tskplatform.service.tools.impl;

import java.io.*;
import java.security.MessageDigest;

public class DiffUtil {

    /**
     *
     * @param file1
     * @param file2
     * @return
     */
    public static boolean check(File file1, File file2) {
        boolean isSame = false;
        String img1Md5 = getMD5(file1);
        String img2Md5 = getMD5(file2);
        if (img1Md5.equals(img2Md5)) {
            isSame = true;
        } else {
            isSame = false;
        }
        return isSame;
    }

    public static byte[] getByte(File file) {
        byte[] b = new byte[(int) file.length()];
        try {
            InputStream in = new FileInputStream(file);
            try {
                in.read(b);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return b;
    }

    public static String getMD5(byte[] bytes) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] strTemp = bytes;
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMD5(File file) {
        return getMD5(getByte(file));
    }

}
