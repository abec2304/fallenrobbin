import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

class MessageParser {
    
    private final Chat chat;
    
    public MessageParser(Chat chat) {
        this.chat = chat;
    }
    
    void parseMessage(long l, String message) {
        try {
            parseMessageUnsafe(l, message);
        } catch(JSONException je) {
            je.printStackTrace();
        }
    }
    
    private void parseMessageUnsafe(long l, String message) throws JSONException {
        /* parse message as JSON */
        JSONObject root = new JSONObject(message);
        
        /* get message type */
        String type = root.getString("type");
        
        /* message fields */
        String body;
        String from;
        byte extra = 0;
        int tabIndex = 0;
        
        /* process payload */
        JSONObject payload = root.getJSONObject("payload");
        if(type.equals("chat")) {
            body = payload.getString("body");
            from = payload.getString("from");
            if(body.startsWith("/me ")) {
                body = body.substring(4);
                extra = Constants.EXTRA_USER_ACTION;
            }
            int len = Constants.CHANNELS.length;
            for(int i = 0; i < len; i++) {
                String channel = Constants.CHANNELS[i];
                if(!body.startsWith(channel)) continue;
                body = body.substring(channel.length());
                tabIndex = Constants.OFFSET_CHANNELS + i;
                break;
            }
        } else if(type.equals("vote")) {
            String vote = payload.getString("vote");
            body = null;
            from = payload.getString("from");
            if(Constants.FILTER_VOTES) tabIndex = 1;
            if(vote.equals("ABANDON")) {
                extra = Constants.EXTRA_VOTE_ABANDON;
            } else if(vote.equals("CONTINUE")) {
                extra = Constants.EXTRA_VOTE_CONTINUE;
            } else if(vote.equals("INCREASE")) {
                extra = Constants.EXTRA_VOTE_INCREASE;
            } else {
                extra = Constants.EXTRA_VOTE;
                body = vote;
            }
        } else if(type.equals("users_abandoned")) {
            JSONArray users = payload.getJSONArray("users");
            int len = users.length();
            body = len + "";
            from = null;
            extra = Constants.EXTRA_SYSTEM_ACTION;
            
            for(int i = 0; i < len; i++) {
                chat.addAbandon(((String)users.get(i)).getBytes());
            }
            
            /*String[] arr = new String[len];
            for(int i = 0; i < len; i++) {
                arr[i] = users.getString(i);
                System.out.println("X " + arr[i]);
            }*/
        } else if(type.equals("no_match")) {
            body = type.substring(0, 1);
            from = null;
            extra = Constants.EXTRA_SYSTEM_ACTION;
        } else if(type.equals("please_vote")) {
            body = type.substring(0, 1);
            from = null;
            extra = Constants.EXTRA_SYSTEM_ACTION;
        } else {
            /* TODO: support other types */
            if(!type.equals("join") && !type.equals("part"))
                System.out.println(type);
            return;
        }
        
        byte[] bytes = Util.getBytes(l, extra, from, body);
        if(bytes != null) chat.addLine(tabIndex, bytes);
    }
    
}
