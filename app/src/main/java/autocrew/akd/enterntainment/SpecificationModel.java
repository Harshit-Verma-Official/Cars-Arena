package autocrew.akd.enterntainment;

public class SpecificationModel {

    public static final int SPECIFICATION_TITLE = 0;
    public static final int SPECIFICATION_BODY = 1;
    private int type;

    //////// specification title
    private String title;

    public SpecificationModel(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    //////// specification title
}
