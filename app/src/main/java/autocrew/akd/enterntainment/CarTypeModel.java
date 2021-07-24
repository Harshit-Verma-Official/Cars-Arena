package autocrew.akd.enterntainment;

public class CarTypeModel {
    private String carTypeName;
    private String carTypeImageUrl;

    public CarTypeModel(String carTypeName, String carTypeImageUrl) {
        this.carTypeName = carTypeName;
        this.carTypeImageUrl = carTypeImageUrl;
    }

    public String getCarTypeName() {
        return carTypeName;
    }

    public void setCarTypeName(String carTypeName) {
        this.carTypeName = carTypeName;
    }

    public String getCarTypeImageUrl() {
        return carTypeImageUrl;
    }

    public void setCarTypeImageUrl(String carTypeImageUrl) {
        this.carTypeImageUrl = carTypeImageUrl;
    }
}
