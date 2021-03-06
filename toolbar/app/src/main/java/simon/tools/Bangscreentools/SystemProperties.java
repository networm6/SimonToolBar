package simon.tools.Bangscreentools;

import android.util.Log;

import java.lang.reflect.Method;


/**
 * Created by 易庞宙 on 2018 2018/10/16 11:21
 * email: 1986545332@qq.com
 */
public class SystemProperties {
    private static  Method getStringProperty;
    private static  SystemProperties single;

    public static SystemProperties getSingle() {
        if (single==null){
            synchronized (SystemProperties.class){
                single = new SystemProperties();
            }
        }
        return single;
    }

    private SystemProperties(){
        getStringProperty = getMethod(getClass("android.os.SystemProperties"));
    }

    private Class getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            
            try {
                return ClassLoader.getSystemClassLoader().loadClass(name);
            } catch (ClassNotFoundException e1) {
                
            }
        }
        return null;
    }

    private Method getMethod(Class clz) {
        Method method;
        try {
            method = clz != null ? clz.getMethod("get", String.class) : null;
        } catch (Exception e) {
            
            method = null;
        }
        return method;
    }


    public final String get(String key) {
        if (key == null) return "";
        String value;
        try {
            value = (String) (getStringProperty != null ? getStringProperty.invoke( null, key) : null);
            if (value != null) {
                return value.trim();
            }else return "";
        } catch (Exception var4) {
            return "";
        }
    }


}
