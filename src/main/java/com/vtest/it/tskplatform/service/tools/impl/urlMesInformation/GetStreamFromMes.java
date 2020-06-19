package com.vtest.it.tskplatform.service.tools.impl.urlMesInformation;

import com.vtest.it.tskplatform.pojo.mes.MesProperties;
import com.vtest.it.tskplatform.service.vtptmt.impl.VtptmtInforImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class GetStreamFromMes {
    @Autowired
    private VtptmtInforImpl vtptmtInfor;

    private static final Logger LOGGER = LoggerFactory.getLogger(GetStreamFromMes.class);

    public BufferedReader getStream(String URL) throws IOException {
        return getStream(URL, 0);
    }

    public BufferedReader getStream(String URL, int times) throws IOException {
        try {
            MesProperties properties = vtptmtInfor.getProperties();
            URL = URL.replaceAll("#", "%23");
            URL url = new URL(properties.getInitUrl() + URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("HOST", properties.getHost());
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader IsReader = new InputStreamReader(inputStream, "utf8");
            BufferedReader bufferedReader = new BufferedReader(IsReader);
            return bufferedReader;
        } catch (Exception e) {
            if (times < 5) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                LOGGER.error("total mes data upload by url time:" + times + " & info:" + URL);
                LOGGER.error("error reasons: " + e.getMessage());
                getStream(URL, times++);
            }
        }
        return null;
    }
}
