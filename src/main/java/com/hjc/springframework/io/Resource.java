package com.hjc.springframework.io;

import java.io.InputStream;

/**
 * 获取配置文件的接口
 *
 * @author kei
 */
public interface Resource {

    /**
     * 获得一个输入流
     * @return
     * @throws Exception
     */
    InputStream getInputStream() throws Exception;
}
