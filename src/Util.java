import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Calendar;

class Util {
    
    /* digits for time */
    private static final char[] DIGITS_TIME = {
        '0', '1', '2', '3', '4',
        '5', '6', '7', '8', '9'
    };
    
    static String formatTime(Calendar cal) {
        int n1, n2, n3;
        if(cal == null) {
            n1 = n2 = n3 = 88;
        } else {
            n1 = cal.get(Calendar.HOUR_OF_DAY);
            n2 = cal.get(Calendar.MINUTE);
            n3 = cal.get(Calendar.SECOND);
        }
        
        char[] arr = {
            '[',
            DIGITS_TIME[n1 / 10 % 10],
            DIGITS_TIME[n1 % 10],
            ':',
            DIGITS_TIME[n2 / 10 % 10],
            DIGITS_TIME[n2 % 10],
            ':',
            DIGITS_TIME[n3 / 10 % 10],
            DIGITS_TIME[n3 % 10],
            ']'
        };
        
        return new String(arr);
    }
    
    static void writeLong(byte[] b, long l) {
        b[0] = (byte)(l >>> 56);
        b[1] = (byte)(l >>> 48);
        b[2] = (byte)(l >>> 40);
        b[3] = (byte)(l >>> 32);
        b[4] = (byte)(l >>> 24);
        b[5] = (byte)(l >>> 16);
        b[6] = (byte)(l >>>  8);
        b[7] = (byte)(l >>>  0);
    }
    
    static long readLong(byte[] b) {
        return ((long)
        b[0]             << 56) +
      ((b[1] & 255L)     << 48) +
      ((b[2] & 255L)     << 40) +
      ((b[3] & 255L)     << 32) +
      ((b[4] & 255L)     << 24) +
      ((b[5] & 255 )     << 16) +
      ((b[6] & 255 )     <<  8) +
      ((b[7] & 255 )     <<  0);
    }
    
    // todo: avoid null
    static String stripName(String s) {
        int len = s.length();
        char[] arr = new char[len];
        
        int j = 0;
        for(int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if(c == '-' || c == '_') continue;
            arr[j] = Character.toLowerCase(c);
            j++;
        }
        
        if(j == 0) return null;
        return new String(arr, 0, j);
    }
    
    static Class getClass(String c) {
        Class clazz = null;
        try {
            clazz = Class.forName(c);
        } catch(ClassNotFoundException cnfe) {
            
        } catch(ClassFormatError cfe) {
            
        } catch(NoClassDefFoundError ncdfe) {
            
        }
        
        return clazz;
    }
    
    static Constructor getConstructor(String c, Class[] p) {
        Class clazz = getClass(c);
        if(clazz == null) return null;
        
        Constructor constructor = null;
        try {
            constructor = clazz.getConstructor(p);
        } catch(NoSuchMethodException nsme) {
            
        }
        
        return constructor;
    }
    
    static Method getMethod(String c, String m, Class[] p) {
        Class clazz = getClass(c);
        if(clazz == null) return null;
        
        Method method = null;
        try {
            method = clazz.getMethod(m, p);
        } catch(NoSuchMethodException nsme) {
            
        }
        
        return method;
    }
    
    static Object newInstance(Constructor c, Object[] p) {
        if(c == null) return null;
        
        try {
            return c.newInstance(p);
        } catch(IllegalAccessException iae) {
            
        } catch(InvocationTargetException ite) {
            
        } catch(InstantiationException ie) {
            
        }
        
        return null;
    }
    
    static Object invoke(Method m, Object o, Object[] p) {
        if(m == null) return null;
        
        try {
            return m.invoke(o, p);
        } catch(IllegalAccessException iae) {
            
        } catch(InvocationTargetException ite) {
            
        }
        
        return null;
    }
    
    static byte[] getBytes(long l, byte extra, String from, String body) {        
        /* get bytes for username, encoding shouldn't matter */
        byte[] fromBytes;
        if(from == null) {
            fromBytes = new byte[0];
        } else {
            fromBytes = from.getBytes();
        }
        
        /* get username length */
        byte usernameLen = (byte)fromBytes.length;
        
        /* get bytes for message body, encoding matters */
        byte[] bodyBytes;
        try {
            bodyBytes = body == null ? new byte[0] : body.getBytes("UTF8");
        } catch(UnsupportedEncodingException uee) {
            return null;
        }
        
        /* calculate array offsets and array length */
        final int attrOff = 8;
        final int fromOff = attrOff + 2;
        int bodyOff = fromOff + fromBytes.length;
        int len = bodyOff + bodyBytes.length;
        
        /* create array */
        byte[] bytes = new byte[len];   
        
        /* set timestamp */
        writeLong(bytes, l);
        
        /* set attributes */
        bytes[attrOff] = extra;
        bytes[attrOff + 1] = usernameLen;
        
        /* set username */
        System.arraycopy(fromBytes, 0, bytes, fromOff, usernameLen);
        
        /* set message */
        System.arraycopy(bodyBytes, 0, bytes, bodyOff, bodyBytes.length);
        
        /* return */
        return bytes;
    }
    
}
