package com.saga.converter_4_20ma.utilities;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {

    // DEBUGGER
    private static final boolean DEBUGGER = false;
    private static final String TAG = Utils.class.getSimpleName();

    /**
     * @param
     * @return
     */
    public static String convertRssiToString(int rssi) {
        rssi &= 0xFF;
        double rssiValue = -125 + (rssi * 0.5);
        String strRssi = String.valueOf(rssiValue);
        return strRssi;
    }

    /**
     * Convert byte array to hex string
     *
     * @param bytes
     * @return
     */
    public static String arrayBytesToStringHex(byte[] bytes) {

        StringBuilder sbuf = new StringBuilder();
        for (int idx = 0; idx < bytes.length; idx++) {
            int intVal = bytes[idx] & 0xff;
            if (intVal < 0x10) {
                sbuf.append("0");
            }
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * @param
     * @return
     */
    public static String arrayByteToBcdString(byte[] bytes) {

        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * @param
     * @return
     */
    public static byte[] hexStringToArrayByte(String strValue) {

        int len = strValue.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(strValue.charAt(i), 16) << 4) + Character.digit(strValue.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * @param
     * @return
     */
    private static byte[] bcdStringToArrayByte(String s) {
        int size = s.length();
        byte[] bytes = new byte[(size + 1) / 2];
        int index = 0;
        boolean advance = size % 2 != 0;
        for (char c : s.toCharArray()) {
            byte b = (byte) (c - '0');
            if (advance) {
                bytes[index++] |= b;
            } else {
                bytes[index] |= (byte) (b << 4);
            }
            advance = !advance;
        }
        return bytes;
    }

    /**
     * @param
     * @return
     * @throws Exception
     */
    public static byte[] serialNumberToArrayByte(String strValue) throws Exception {

        byte[] packOut = new byte[8];
        try {
            String strHex = strValue.substring(0, 8);
            String strBcd = strValue.substring(8, 16);

            byte[] hex = hexStringToArrayByte(strHex);
            byte[] bcd = bcdStringToArrayByte(strBcd);

            // Copy "hex" to "packOut"
            System.arraycopy(hex, 0, packOut, 0, 4);
            // Copy "count" to "packOut"
            System.arraycopy(bcd, 0, packOut, 4, 4);

        } catch (NumberFormatException ex) { // parseInt
            throw new Exception("Falha na conversão do número de série da UTR");
        }
        return packOut;
    }

    /**
     * @param
     * @return
     */
    public static String serialNumberToString(byte[] serial) {

        byte[] arrayHex = new byte[4];
        byte[] arrayBcd = new byte[4];

        // Copy "serial" to "arrayHex"
        System.arraycopy(serial, 0, arrayHex, 0, 4);
        // Copy "serial" to "arrayBcd"
        System.arraycopy(serial, 4, arrayBcd, 0, 4);

        String strHex = arrayBytesToStringHex(arrayHex);
        String strBcd = arrayByteToBcdString(arrayBcd);
        //
        return (strHex + strBcd);
    }

    /**
     * @param
     * @return
     */
    public static final int arrayByteToInt(byte[] bytes) {

        int out;

        if (bytes.length == 1) {
            out = (bytes[0] & 0xFF);
        } else if (bytes.length == 2) {
            out = (bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);
        } else if (bytes.length == 3) {
            out = (bytes[0] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[2] & 0xFF);
        } else if (bytes.length == 4) {
            out = bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
        } else {
            out = 0;
        }
        return out;
    }

    /**
     * @param
     * @return
     */
    public static final byte[] intToArrayByte(int value) {
        byte[] aux4 = ByteBuffer.allocate(4).putInt(value).array();
        return aux4;
    }

    /**
     * @param
     * @return
     */
    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }

    /**
     * @param
     * @return
     */
    public static byte[] dateTimeToArrayByteUtc(String datetime) {

        //
        // Format String: "yyyy-MM-ddTHH:mm:ssTZD"
        // Format byte[]: [dd, MM, yy, HH, mm, ss]

        // Ajuste 03.10.16
        // Format byte[]: [yy, MM, dd, HH, mm, ss]

        byte[] arrayDatetime = new byte[6];

        String[] strDatetime = datetime.split("T");
        String[] strDate = strDatetime[0].split("-");
        String[] strTimeZone = strDatetime[1].split("-");
        String[] strTime = strTimeZone[0].split(":");

        // Day
        arrayDatetime[2] = Byte.parseByte(strDate[2]);
        // Month
        arrayDatetime[1] = Byte.parseByte(strDate[1]);
        // Year
        arrayDatetime[0] = Byte.parseByte(strDate[0].substring(2, 4));
        // Hour
        arrayDatetime[3] = Byte.parseByte(strTime[0]);
        // Minute
        arrayDatetime[4] = Byte.parseByte(strTime[1]);
        // Secund
        arrayDatetime[5] = Byte.parseByte(strTime[2]);
        return arrayDatetime;
    }

    /**
     * @param
     * @return
     */
    public static String dateTimeToStringUtc(byte[] timestamp) {

        // Format byte[]: [dd, MM, yy, HH, mm, ss]
        // Format String: "yyyy-MM-ddTHH:mm:ssTZD"

        // Ajuste 03.10.16
        // Format byte[]: [yy, MM, dd, HH, mm, ss]

        String[] strOut = new String[timestamp.length];
        for (int i = 0; i < timestamp.length; i++) {

            if (timestamp[i] > 9) {
                strOut[i] = String.valueOf(timestamp[i]);
            } else {
                strOut[i] = "0" + String.valueOf(timestamp[i]);
            }
        }

        int year = (2000 + Integer.valueOf(strOut[0]));
        String strTimestamp = "" + year + "-" + strOut[1] + "-" +
                strOut[2] + "T" + strOut[3] + ":" + strOut[4] + ":" +
                strOut[5] + "-03:00";
        return strTimestamp;
    }

    /**
     * @param
     * @return
     */
    public static String dateTimeToStringShort(byte[] timestamp) {

        // Ajuste 03.10.16
        // Format byte[]: [yy, MM, dd, HH, mm, ss]
        // Format String Output: "dd/MM/yy - HH:mm:ss"

        String[] strOut = new String[timestamp.length];
        for (int i = 0; i < timestamp.length; i++) {
            //
            if (timestamp[i] > 9) {
                strOut[i] = String.valueOf(timestamp[i]);
            } else {
                strOut[i] = "0" + String.valueOf(timestamp[i]);
            }
        }
        String strTimestamp = "" + strOut[2] + "/" + strOut[1] + "/" +
                strOut[0] + " - " + strOut[3] + ":" + strOut[4] + ":" +
                strOut[5];

        if (DEBUGGER)
            Log.d(TAG, "dateTimeToStringShort - Timestamp: " + strTimestamp);

        return strTimestamp;
    }

    /**
     * @param
     * @return
     */
    public static String dateTimeToStringShort(String dateTimeUtc) {

        // Format String Input: "yyyy-MM-ddTHH:mm:ssTZD"
        // Format String Output: "dd/MM/yy - HH:mm:ss"

        String[] strDatetime = dateTimeUtc.split("T");
        String[] strDate = strDatetime[0].split("-");

        if (DEBUGGER)
            Log.d(TAG, "dateTimeToStringShort - Year: " + strDate[0]);

        String year = strDate[0].substring(2, 4);
        String[] strTimeZone = strDatetime[1].split("-");
        String[] strTime = strTimeZone[0].split(":");

        String strTimestamp = "" + strDate[2] + "/" + strDate[1] + "/" +
                year + " - " + strTime[0] + ":" + strTime[1] + ":" +
                strTime[2];

        return strTimestamp;
    }

    /**
     * @param
     * @return
     */
    public static byte[] getCurrentDateTimeToArrayByte(Context context) {

        byte[] aux = new byte[8];

        // Date time
        // - 1 byte - Year
        // - 1 byte - Month
        // - 1 byte - Day
        // - 1 byte - Hour
        // - 1 byte - Minute
        // - 1 byte - Second
        // - 1 byte - Day of the week
        // - 1 byte - Daylight saving

//        long time = LocationGPS.getTime(context);
//        SimpleDateFormat df = new SimpleDateFormat("yy.MM.dd.HH.mm.ss");
//        df.setTimeZone(LocationGPS.getTimeZone(context));
//        String formattedDate = df.format(time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd.HH.mm.ss");
        Calendar calendar = Calendar.getInstance();
        String formattedDate = dateFormat.format(calendar.getTime());

        //
        String[] str = formattedDate.split("\\.");

        // Year
        aux[0] = (byte) Integer.parseInt(str[0]);
        // Month
        aux[1] = (byte) Integer.parseInt(str[1]);
        // Day
        aux[2] = (byte) Integer.parseInt(str[2]);
        // Hour
        aux[3] = (byte) Integer.parseInt(str[3]);
        // Minute
        aux[4] = (byte) Integer.parseInt(str[4]);
        // Second
        aux[5] = (byte) Integer.parseInt(str[5]);

        // Day of the week
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date date = new Date();
        String dayOfTheWeek = sdf.format(date);

        // Sunday
        // Monday
        // Thursday
        // Wednesday
        // Thursday
        // Friday
        // Saturday

        byte day = 0;
        if (dayOfTheWeek.contains("domingo")) {
            day = 1;
        } else if (dayOfTheWeek.contains("segunda")) {
            day = 2;
        } else if (dayOfTheWeek.contains("terça")) {
            day = 3;
        } else if (dayOfTheWeek.contains("quarta")) {
            day = 4;
        } else if (dayOfTheWeek.contains("quinta")) {
            day = 5;
        } else if (dayOfTheWeek.contains("sexta")) {
            day = 6;
        } else if (dayOfTheWeek.contains("sábado")) {
            day = 7;
        }
        // Day of the week
        aux[6] = day;
        // Daylight saving
        aux[7] = 0;
        return aux;
    }



    /**
     * @param
     * @return
     */
    public static String dateTimeShortDiff(String dateOne, String dateTwo) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy - HH:mm:ss");
        String diff = "";

        try {
            Date oldDate = sdf.parse(dateOne);
            Date newDate = sdf.parse(dateTwo);

            long timeDiff = Math.abs(oldDate.getTime() - newDate.getTime());

            diff = String.format("%d hora(s) %d min(s)", TimeUnit.MILLISECONDS.toHours(timeDiff), TimeUnit.MILLISECONDS.toMinutes(timeDiff) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));

        } catch (ParseException e) {
            diff = "0 hora(s) 0 min(s)";
        }
        return diff;
    }

    /**
     * @param
     * @return
     */
    public static String convertInputStreamToString(InputStream inputStream) throws IOException {

        //
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        //
        String line = "";
        String result = "";

        //
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        //
        inputStream.close();

        return result;
    }

    /**
     * @param
     * @return
     */
    public static long getMaxReadout(int numberDigits) {
        long maxValue = 0;
        if (numberDigits != 0) {
            maxValue = (((long) Math.pow(10, numberDigits) * 1000) - 1);
        }
        return maxValue;
    }



}