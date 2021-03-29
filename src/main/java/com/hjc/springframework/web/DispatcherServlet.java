package com.hjc.springframework.web;

import com.hjc.springframework.annotation.Controller;
import com.hjc.springframework.annotation.RequestMapping;
import com.hjc.springframework.context.ClassPathXmlApplicationContext;
import com.hjc.springframework.entity.BeanDefinition;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @author kei
 */
public class DispatcherServlet extends HttpServlet {

    /**
     * 保存配置文件的内容，扫描包路径
     */
    private Properties properties = new Properties();

    /**
     * 保存扫描路径下的类
     */
    private List<String> classNames = new ArrayList<>();

    /**
     * 保存Controller类中的方法，url映射到调用方法
     */
    private Map<String, Method> handlerMapping = new HashMap<>();

    /**
     * 保存Controller类
     */
    private Set<Class> classes = new HashSet<>();

    /**
     * 保存Controller类，url映射到Controller实例
     */
    private Map<String, Object> controllerMap = new HashMap<>();

    /**
     * IOC容器
     */
    private ClassPathXmlApplicationContext xmlApplicationContext;

    @Override
    public void init(ServletConfig config) {
        try {
            xmlApplicationContext = new ClassPathXmlApplicationContext("application-annotation.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        String location = config.getInitParameter("contextConfigLocation");
        location = location.substring(location.lastIndexOf(":") + 1);
        doLoadConfig(location);

//        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        doScanner(properties.getProperty("scanPackage"));
        doInstance();
        initHandlerMapping();
    }

    public void doDispatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (handlerMapping.isEmpty()) {
            return;
        }
        // uri包括contextPath，需要把contextPath删掉
        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        if (!handlerMapping.containsKey(url)) {
            response.getWriter().write("404 NOT FOUND!");
            return;
        }
        Method method = handlerMapping.get(url);
        Class<?>[] parameterTypes = method.getParameterTypes();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Object[] paramValues = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            String requestParam = parameterTypes[i].getSimpleName();
            if ("HttpServletRequest".equals(requestParam)) {
                paramValues[i] = request;
            } else if ("HttpServletResponse".equals(requestParam)) {
                paramValues[i] = response;
            } else if ("String".equals(requestParam)) {
                for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
                    String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
                    paramValues[i] = value;
                }
            }
        }
        // 调用controller方法
        // 通过查找url获得相应的controller方法
        try {
            method.invoke(controllerMap.get(url), paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Server Exception!");
        }
    }

    /**
     * 加载contextConfigLocation处的配置文件，保存为properties类
     * @param location
     */
    private void doLoadConfig(String location) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 扫描properties文件配置的包路径
     * @param packageName
     */
    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            // 如果是文件夹，递归扫描
            if (file.isDirectory()) {
                doScanner(packageName + "." + file.getName());
            } else {
                String className = packageName + "." + file.getName().replace(".class", "");
                classNames.add(className);
            }
        }
    }

    /**
     * 将保存好的classNames数组中的类实例化
     */
    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }
        for (String className : classNames) {
            try {
               Class<?> clazz = Class.forName(className);
               // 如果类上标注了@Controller注解
               if (clazz.isAnnotationPresent(Controller.class)) {
                   classes.add(clazz);
                   BeanDefinition definition = new BeanDefinition();
                   definition.setSingleton(true);
                   definition.setBeanClassName(clazz.getName());
                   xmlApplicationContext.addNewBeanDefinition(clazz.getName(), definition);
               }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            xmlApplicationContext.refreshBeanFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化HandlerMapping
     */
    private void initHandlerMapping() {
        if (classes.isEmpty()) {
            return;
        }
        try {
            for (Class<?> clazz : classes) {
                String baseUrl = "";
                // 如果类上标注了@RequestMapping注解
                if (clazz.isAnnotationPresent(RequestMapping.class)) {
                    baseUrl = clazz.getAnnotation(RequestMapping.class).value();
                }
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    // 如果方法上标注了@RequestMapping注解
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        String url = method.getAnnotation(RequestMapping.class).value();
                        url = (baseUrl + "/" + url).replaceAll("/+", "/");
                        handlerMapping.put(url, method);
                        controllerMap.put(url, xmlApplicationContext.getBean(clazz));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
