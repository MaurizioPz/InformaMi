package lib;

import play.mvc.Router;

public class Facebook {
    // get these from your FB Dev App
	
	private static final String api_key = "5bc3606c3e355c858133cdd9086ac14e";
    private static final String secret = "419695d70a09f23dff8735026f7a22f4";
    private static final String client_id = "141083772629937";
    private static final String redirect_uri = "http://kefacciamo.appspot.com/fbauth";//*/
    /*private static final String api_key = "5bc3606c3e355c858133cdd9086ac14e";
    private static final String secret = "43473f3efa37691661389dce5fb48064";
    private static final String client_id = "202159310993";
    private static final String redirect_uri = "http://localhost:9000/fbauth";//*/
	
    // set this to your servlet URL for the authentication servlet/filter
    
    
    /// set this to the list of extended permissions you want
    //private static final String[] perms = new String[] {"publish_stream", "email"};

    public static String getAPIKey() {
        return api_key;
    }

    public static String getSaecret() {
        return secret;
    }

    public static String getLoginRedirectURL() {
        return "https://graph.facebook.com/oauth/authorize?client_id=" +
            client_id + "&display=page&redirect_uri=" +
            redirect_uri+"&scope=publish_stream,email,user_location,user_birthday";//+StringUtil.delimitObjectsToString(",", perms);
    }

    public static String getAuthURL(String authCode) {
        return "https://graph.facebook.com/oauth/access_token?client_id=" +
            client_id+"&redirect_uri=" +
            redirect_uri+"&client_secret="+secret+"&code="+authCode;
    }
}