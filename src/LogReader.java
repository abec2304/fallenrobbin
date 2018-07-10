import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class LogReader {
    
    private final MessageParser messageParser;
    
    LogReader(MessageParser messageParser) {
        this.messageParser = messageParser;
    }
    
    void readLog(File f) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(f);
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            return;
        }
        
        BufferedReader in = new BufferedReader(fileReader);
        
        System.out.println("reading log...");
        long time1 = System.currentTimeMillis();
        readLines(in);
        long time2 = System.currentTimeMillis();
        System.out.println("...read log (took " + (time2 - time1) + "ms)");
        
        try {
            in.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private void readLines(BufferedReader in) {
        /* id of robin room */
        try {
            String timestamp = in.readLine();
            if(timestamp == null) return;
            String roomId = in.readLine();
            if(roomId == null) return;
            System.out.println("roomId: " + roomId);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return;
        }
        
        for(;;) {
            /* read timestamp */
            String timestamp;
            try {
                timestamp = in.readLine();
            } catch(IOException ioe) {
                ioe.printStackTrace();
                break;
            }
            
            if(timestamp == null) break;
            
            /* convert timestamp to long */
            long l;
            try {
                l = Long.parseLong(timestamp);
            } catch(NumberFormatException nfe) {
                l = System.currentTimeMillis();
            }
            
            /* read message */
            String message;
            try {
                message = in.readLine();
            } catch(IOException ioe) {
                ioe.printStackTrace();
                break;
            }
            
            if(message == null) break;
            
            messageParser.parseMessage(l, message);
        }
    }
    
}
