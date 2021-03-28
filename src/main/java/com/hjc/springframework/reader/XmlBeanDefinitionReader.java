package com.hjc.springframework.reader;

import com.hjc.springframework.annotation.*;
import com.hjc.springframework.entity.BeanDefinition;
import com.hjc.springframework.entity.BeanReference;
import com.hjc.springframework.entity.PropertyValue;
import com.hjc.springframework.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 读取XML文件获得BeanDefinition
 *
 * @author kei
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    public XmlBeanDefinitionReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public void loadBeanDefinitions(String location) throws Exception {
        InputStream inputStream = getResourceLoader().getResource(location).getInputStream();
        doLoadBeanDefinitions(inputStream);
    }

    protected void doLoadBeanDefinitions(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputStream);
        // 解析xml文件
        registerBeanDefinitions(document);
        inputStream.close();
    }

    public void registerBeanDefinitions(Document document) {
        Element root = document.getDocumentElement();
        parseBeanDefinitions(root);
    }

    /**
     * 解析xml文件
     * @param root
     */
    protected void parseBeanDefinitions(Element root) {
        NodeList nodeList = root.getChildNodes();
        // 检查是否存在component-scan，是否是注解配置
        String basePackage = null;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                if ("component-scan".equals(element.getTagName())) {
                    basePackage = element.getAttribute("base-package");
                    break;
                }
            }
        }
        // 存在component-scan，是注解配置
        // 是否要加return，加了return岂不是无法同时使用注解配置和xml配置了？
        if (basePackage != null) {
            parseAnnotation(basePackage);
            return;
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                processBeanDefinition((Element) node);
            }
        }
    }

    protected void processBeanDefinition(Element element) {
        // 获取组件id
        String name = element.getAttribute("id");

        // 获取组件class
        String className = element.getAttribute("class");
        if (className == null || className.length() == 0) {
            throw new IllegalArgumentException("bean的class属性不能为空");
        }
        if (name == null || name.length() == 0) {
            // throw new IllegalArgumentException("bean的id不能为空");
            // 如果id为空，id默认为class名字，首字母小写
//            int index = className.lastIndexOf('.');
//            String tmpClassName = className.substring(index + 1);
//            name = Character.toLowerCase(tmpClassName.charAt(0)) + tmpClassName.substring(1);

            // 如果id为空，id默认为className，全限定类名
            name = className;
        }
        boolean singleton = true;
        // 如果有scope属性，且scope属性是prototype
        if (element.hasAttribute("scope") && "prototype".equals(element.getAttribute("scope"))) {
            singleton = false;
        }
        BeanDefinition beanDefinition = new BeanDefinition();
        processProperty(element, beanDefinition);
        beanDefinition.setBeanClassName(className);
        beanDefinition.setSingleton(singleton);
        getRegistry().put(name, beanDefinition);
    }

    private void processProperty(Element element, BeanDefinition beanDefinition) {
        NodeList propertyNode = element.getElementsByTagName("property");
        for (int i = 0; i < propertyNode.getLength(); i++) {
            Node node = propertyNode.item(i);
            if (node instanceof Element) {
                Element propertyEle = (Element) node;
                String name = propertyEle.getAttribute("name");
                String value = propertyEle.getAttribute("value");
                if (value != null && value.length() > 0) {
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, value));
                } else {
                    String ref = propertyEle.getAttribute("ref");
                    if (ref == null || ref.length() == 0) {
                        throw new IllegalArgumentException("Configuration problem: <property> element for property '" + name + "' must specify a ref or value");
                    }
                    BeanReference beanReference = new BeanReference(ref);
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, beanReference));
                }
            }
        }
    }

    protected void parseAnnotation(String basePackage) {
        Set<Class<?>> classes = getClasses(basePackage);
        for (Class clazz : classes) {
            processAnnotationBeanDefinition(clazz);
        }
    }

    /**
     * 解析注解标注的类
     * @param clazz
     */
    protected void processAnnotationBeanDefinition(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Service.class) || clazz.isAnnotationPresent(Repository.class)) {
            String name = null;
            if (clazz.isAnnotationPresent(Component.class)) {
                name = clazz.getAnnotation(Component.class).name();
            } else if (clazz.isAnnotationPresent(Service.class)) {
                name = clazz.getAnnotation(Service.class).name();
            } else if (clazz.isAnnotationPresent(Repository.class)) {
                name = clazz.getAnnotation(Repository.class).name();
            }

            // 注解内没有name属性，默认是首字母小写的类名？
            if (name == null || name.length() == 0) {
                // name默认是首字母小写的类名
                String className = clazz.getName();
                int index = className.lastIndexOf(".");
                String tmpClassName = className.substring(index + 1);
                name = Character.toLowerCase(tmpClassName.charAt(0)) + tmpClassName.substring(1);

                // 如果注解内没有标注name属性，默认的名字name就是全限定类名
//                name = clazz.getName();
            }
            String className = clazz.getName();
            boolean singleton = true;
            if (clazz.isAnnotationPresent(Scope.class) && "prototype".equals(clazz.getAnnotation(Scope.class).value())) {
                singleton = false;
            }
            BeanDefinition beanDefinition = new BeanDefinition();
            processAnnotationProperty(clazz, beanDefinition);
            beanDefinition.setBeanClassName(className);
            beanDefinition.setSingleton(singleton);
            getRegistry().put(name, beanDefinition);
        }
    }

    /**
     * 注入注解标注的类实例的属性
     * @param clazz
     * @param beanDefinition
     */
    protected void processAnnotationProperty(Class<?> clazz, BeanDefinition beanDefinition) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            // 如果属性上标注了@Value注解，获取注解中的值，并进行注入
            if (field.isAnnotationPresent(Value.class)) {
                Value valueAnnotation = field.getAnnotation(Value.class);
                String value = valueAnnotation.value();
                if (value != null && value.length() > 0) {
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, value));
                }
            } else if (field.isAnnotationPresent(Autowired.class)) {
                // 如果属性上标注了@Autowired注解
                // 且标注了@Qualified注解，定义了被引用类的名字
                if (field.isAnnotationPresent(Qualifier.class)) {
                    Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
                    String refBeanName = qualifierAnnotation.value();
                    if (refBeanName == null || refBeanName.length() == 0) {
                        throw new IllegalArgumentException("the value of Qualifier should not be null!");
                    }
                    BeanReference beanReference = new BeanReference(refBeanName);
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, beanReference));
                } else {
                    // 如果没有标注@Qualified注解，使用被引用的类型去寻找要注入的bean
                    // getType() 获取属性类型
                    String refBeanName = field.getType().getName();
                    BeanReference beanReference = new BeanReference(refBeanName);
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, beanReference));
                }
            }
        }
    }

    /**
     * 获得packageName路径包下的所有类
     * @param packageName
     * @return
     */
    protected Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        boolean recursive = true;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                // 如果是以文件的形式存储
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 扫描整个包下的文件，添加到classes集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包的形式存储
                    JarFile jar;
                    try {
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry jarEntry = entries.nextElement();
                            String name = jarEntry.getName();
                            if (name.charAt(0) == '/') {
                                name = name.substring(1);
                            }
                            if (name.startsWith(packageDirName)) {
                                int index = name.lastIndexOf('/');
                                if (index != -1) {
                                    packageName = name.substring(0, index).replace('/', '.');
                                }
                                if ((index != -1) || recursive) {
                                    if (name.endsWith(".class") && !jarEntry.isDirectory()) {
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * 将文件路径下的所有类加入classes集合中
     * @param packageName
     * @param filePath
     * @param recursive
     * @param classes
     */
    private void findAndAddClassesInPackageByFile(String packageName, String filePath, boolean recursive, Set<Class<?>> classes) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirFiles = dir.listFiles(pathname -> (recursive && pathname.isDirectory()) || (pathname.getName().endsWith(".class")));
        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
