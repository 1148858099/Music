package com.yang.music.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;


public class DeviceUtil {

    private static int api;
    private static TelephonyManager telephonyManager;
    private static String IMSI;
    private static ConnectivityManager cm;
    private static NetworkInfo info;

    /**
     * 整合3种获取设备唯一标识
     */
    public static String getDeviceId(Context context) {

        try {
            api = Integer.valueOf(Build.VERSION.SDK);
        } catch (Exception e) {
            api = 0;
            e.printStackTrace();
        }

        if (api < 23) {
            try {
                TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String szImei = TelephonyMgr.getDeviceId();

                String szDevIDShort = "35" + //we make this look like a valid IMEI
                        Build.BOARD.length() % 10 +
                        Build.BRAND.length() % 10 +
                        Build.CPU_ABI.length() % 10 +
                        Build.DEVICE.length() % 10 +
                        Build.DISPLAY.length() % 10 +
                        Build.HOST.length() % 10 +
                        Build.ID.length() % 10 +
                        Build.MANUFACTURER.length() % 10 +
                        Build.MODEL.length() % 10 +
                        Build.PRODUCT.length() % 10 +
                        Build.TAGS.length() % 10 +
                        Build.TYPE.length() % 10 +
                        Build.USER.length() % 10; //13 digits

                String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                String m_szLongID = szImei + szDevIDShort
                        + m_szAndroidID;
                // compute md5
                MessageDigest m = null;
                try {
                    m = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
                // get md5 bytes
                byte p_md5Data[] = m.digest();
                // create a hex string
                String m_szUniqueID = new String();
                for (int i = 0; i < p_md5Data.length; i++) {
                    int b = (0xFF & p_md5Data[i]);
                    // if it is a single digit, make sure it have 0 in front (proper padding)
                    if (b <= 0xF)
                        m_szUniqueID += "0";
                    // add number to string
                    m_szUniqueID += Integer.toHexString(b);
                }   // hex string to uppercase
                m_szUniqueID = m_szUniqueID.toUpperCase();
                return m_szUniqueID;
            } catch (Exception e) {
                e.printStackTrace();
                return "serial";
            }

        } else {
            String serial = null;

            String m_szDevIDShort = "35" +
                    Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                    Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                    Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                    Build.USER.length() % 10; //13 位
            try {
                serial = Build.class.getField("SERIAL").get(null).toString();
                //API>=9 使用serial号
                return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
            } catch (Exception exception) {
                //serial需要一个初始化
                serial = "serial"; // 随便一个初始化
            }
            //使用硬件信息拼凑出来的15位号码
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        }
    }

    /**
     * 获取设备的版本
     *
     * @return 返回版本号
     */
    public static String getDeviceVersion() {

        return Build.VERSION.RELEASE;
    }

    /**
     * 获取手机的型号 设备名称
     *
     * @return
     */
    public static String getDeviceModel() {

        return Build.MODEL;
    }

    /**
     * 获取手机语言
     *
     * @return
     */
    public static String getDeviceLanguage() {

        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取手机设置的国家
     *
     * @return
     */
    public static String getDeviceContry() {

        return Locale.getDefault().getCountry();
    }

    /**
     * 获取手机卡运营商
     * Role:Telecom service providers获取手机服务商信息 <BR>
     * 需要加入权限<uses-permission
     * android:name="android.permission.READ_PHONE_STATE"/> <BR>
     * Date:2012-3-12 <BR>
     *
     * @author CODYY)peijiangping
     */
    public static String getProvidersName(Context context) {
        String ProvidersName = "";
        // 返回唯一的用户ID;就是这张卡的编号神马的
        try {
            telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            IMSI = telephonyManager.getSubscriberId();
            // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
            System.out.println(IMSI);
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ProvidersName;
    }

    /**
     * 得到当前的手机网络类型
     *
     * @param context
     * @return
     */
    public static String getCurrentNetType(Context context) {
        String type = "";
        if (!isConnected(context)) {
            type = "null";
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            type = "wifi";
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int subType = info.getSubtype();
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                type = "2g";
            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                type = "3g";
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
                type = "4g";
            }
        }
        return type;
    }

    public static boolean isConnected(Context context){
        if(cm == null){
            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        info = cm.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }else {
            return true;
        }
    }
}
