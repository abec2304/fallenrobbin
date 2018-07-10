import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

class Channels {
    
    static String[] getChannels() {
        if(1 == 1) return null;
        //if(!Constants.FILTER_MESSAGES) return null;
        
        File f = new File("channels.txt");
        if(!f.isFile()) return null;
        
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(f));
        } catch(IOException ioe) {
            return null;
        }
        
        Vector vector = new Vector();
        
        for(;;) {
            String s;
            try {
                s = in.readLine();
            } catch(IOException ioe) {
                break;
            }
            
            if(s == null) break;
            
            s = s + ' ';
            
            if(s.length() == 1 || vector.contains(s)) continue;
            
            vector.addElement(s);
        }
        
        String[] arr = new String[vector.size()];
        vector.copyInto(arr);
        
        return arr;
    }
    
}
