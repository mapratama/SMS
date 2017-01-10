package mobile.com.sms;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;


public class Preferences {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public Preferences(Context context) {
        preferences = context.getSharedPreferences("messageUnreadPreferences", context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void add(String address) {
        editor.putInt(address, preferences.getInt(address, 0) + 1);
        editor.commit();
    }

    public void delete(String address){
        preferences.edit().remove(address).commit();
    }

    public HashMap<String, Integer> getUnreadList() {
        HashMap<String, Integer> results = new HashMap<>();
        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet())
            results.put(entry.getKey(), (Integer) entry.getValue());

        return results;
    }

    public int getTotalUnread() {
        int total = 0;
        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet())
            total = total + (Integer) entry.getValue();

        return total;
    }
}
