package matwes.zpi.domain;

public class ReturnEvent {
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public ReturnEvent(String eventId) {
        this.eventId = eventId;
    }

    private String eventId;


}
