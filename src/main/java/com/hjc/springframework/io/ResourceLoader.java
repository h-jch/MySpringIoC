package com.hjc.springframework.io;

import java.net.URL;

/**
 * 资源加载器
 * 可以通过路径加载资源
 *
 * @author kei
 */
public class ResourceLoader {

    public Resource getResource(String location) {
        URL url = this.getClass().getClassLoader().getResource(location);
        return new UrlResource(url);
    }
}
