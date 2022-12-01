package com.example.mychatbox;

public class Mensagem {

    private String texto;
    private long timestamp;
    private String fromId;
    private String toId;

    public String getTexto() {
        return texto;
    }
    public long getTimesStamp() {
        return timestamp;
    }
    public String getFromId() {
        return fromId;
    }
    public String getToId() {
        return toId;
    }
    public void setTexto(String texto) {
        this.texto = texto;
    }
    public void setToId(String toId) {
        this.toId = toId;
    }
    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
    public void setTimesStamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
