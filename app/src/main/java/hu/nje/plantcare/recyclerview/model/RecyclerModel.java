package hu.nje.plantcare.recyclerview.model;

public class RecyclerModel {
    private String text;
    public RecyclerModel(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
