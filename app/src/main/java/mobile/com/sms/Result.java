package mobile.com.sms;


public class Result implements Comparable<Result>{

    public Message message;
    public int totalMessage, totalUnread;

    public Result(Message message, int totalMessage, int totalUnread) {
        this.message = message;
        this.totalMessage = totalMessage;
        this.totalUnread = totalUnread;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public int getTotalMessage() {
        return totalMessage;
    }

    public void setTotalMessage(int totalMessage) {
        this.totalMessage = totalMessage;
    }

    public int getTotalUnread() {
        return totalUnread;
    }

    public void setTotalUnread(int totalUnread) {
        this.totalUnread = totalUnread;
    }

    @Override
    public int compareTo(Result compareResult) {
        return compareResult.getTotalMessage() - this.totalMessage;
    }
}
