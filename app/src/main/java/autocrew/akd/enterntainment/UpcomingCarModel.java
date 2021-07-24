package autocrew.akd.enterntainment;

public class UpcomingCarModel {
    private String carTitle;
    private String carSubtitle;
    private String carImageUrl;
    private String garageCarId;

    public UpcomingCarModel(String carTitle, String carSubtitle, String carImageUrl, String garageCarId) {
        this.carTitle = carTitle;
        this.carSubtitle = carSubtitle;
        this.carImageUrl = carImageUrl;
        this.garageCarId = garageCarId;
    }

    public String getGarageCarId() {
        return garageCarId;
    }

    public void setGarageCarId(String garageCarId) {
        this.garageCarId = garageCarId;
    }

    public String getCarTitle() {
        return carTitle;
    }

    public void setCarTitle(String carTitle) {
        this.carTitle = carTitle;
    }

    public String getCarSubtitle() {
        return carSubtitle;
    }

    public void setCarSubtitle(String carSubtitle) {
        this.carSubtitle = carSubtitle;
    }

    public String getCarImageUrl() {
        return carImageUrl;
    }

    public void setCarImageUrl(String carImageUrl) {
        this.carImageUrl = carImageUrl;
    }
}
