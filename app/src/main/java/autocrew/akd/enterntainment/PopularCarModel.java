package autocrew.akd.enterntainment;

public class PopularCarModel {
    private String carName;
    private String carBrand;
    private String carImageUrl;
    private String garageCarId;

    public PopularCarModel(String carName, String carBrand, String carImageUrl, String garageCarId) {
        this.carName = carName;
        this.carBrand = carBrand;
        this.carImageUrl = carImageUrl;
        this.garageCarId = garageCarId;
    }

    public String getGarageCarId() {
        return garageCarId;
    }

    public void setGarageCarId(String garageCarId) {
        this.garageCarId = garageCarId;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarImageUrl() {
        return carImageUrl;
    }

    public void setCarImageUrl(String carImageUrl) {
        this.carImageUrl = carImageUrl;
    }
}
