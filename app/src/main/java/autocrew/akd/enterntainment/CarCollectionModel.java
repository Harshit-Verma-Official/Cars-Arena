package autocrew.akd.enterntainment;

public class CarCollectionModel {
    private String carCollectionName;
    private String carCollectionImageUrl;

    public CarCollectionModel(String carCollectionName, String carCollectionImageUrl) {
        this.carCollectionName = carCollectionName;
        this.carCollectionImageUrl = carCollectionImageUrl;
    }

    public String getCarCollectionName() {
        return carCollectionName;
    }

    public void setCarCollectionName(String carCollectionName) {
        this.carCollectionName = carCollectionName;
    }

    public String getCarCollectionImageUrl() {
        return carCollectionImageUrl;
    }

    public void setCarCollectionImageUrl(String carCollectionImageUrl) {
        this.carCollectionImageUrl = carCollectionImageUrl;
    }
}
