package autocrew.akd.enterntainment;

public class NewCarsModel {

    private String carName;
    private String carBrand;
    private String carDescription;
    private String carImageUrl;
    private String garageCarId;

    public NewCarsModel(String carName, String carBrand, String carDescription, String carImageUrl, String garageCarId) {
        this.carName = carName;
        this.carBrand = carBrand;
        this.carDescription = carDescription;
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

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarDescription() {
        return carDescription;
    }

    public void setCarDescription(String carDescription) {
        this.carDescription = carDescription;
    }

    public String getCarImageUrl() {
        return carImageUrl;
    }

    public void setCarImageUrl(String carImageUrl) {
        this.carImageUrl = carImageUrl;
    }

}
