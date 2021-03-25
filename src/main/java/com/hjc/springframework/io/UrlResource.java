package com.hjc.springframework.io;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 通过URl的方式获取资源
 *
 * @author kei
 */
public class UrlResource implements Resource {

    private URL url;

    public UrlResource(URL url) {
        this.url = url;
    }

    /**
     * 通过URL返回一个资源的输入流
     * @return
     * @throws Exception
     */
    @Override
    public InputStream getInputStream() throws Exception {
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
        return urlConnection.getInputStream();
    }
}
