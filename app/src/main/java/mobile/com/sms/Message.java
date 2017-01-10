package mobile.com.sms;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;


public class Message extends RealmObject {

    @PrimaryKey
    private String id;

    @Index
    private String address;

    private String message;
    private Date time;
    private boolean isSent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public static void add(Realm realm, String address, String message, long timeInMillis, boolean isSent) {
        Message newMessage = new Message();
        newMessage.setId(address + String.valueOf(timeInMillis));
        newMessage.setAddress(address);
        newMessage.setMessage(message);
        newMessage.setTime(DateTimeUtils.fromTimeInMillis(timeInMillis));
        newMessage.setSent(isSent);

        realm.copyToRealmOrUpdate(newMessage);
    }
}
