package com.mol.fadada.config;

import com.fadada.sdk.client.FddClientBase;

public class FddBaseClient {

    /**
     * 参数
     */
    public static String APP_ID = "402664";
    public static String APP_SECRET = "xeVHW4Cbn8nWgwCWC16VDbVe";
    public static String V = "2.0";
    public static String HOST = "https://testapi.fadada.com:8443/api/";
    public static FddClientBase clientBase = new FddClientBase(APP_ID,APP_SECRET,V,HOST);


    public synchronized static FddClientBase getFddClientBase(){
        if (clientBase == null) {
            synchronized (FddBaseClient.class) {
                if (clientBase == null) {
                    clientBase = new FddClientBase(APP_ID,APP_SECRET,V,HOST);
                }
            }
        }
        return clientBase;
    }

}
