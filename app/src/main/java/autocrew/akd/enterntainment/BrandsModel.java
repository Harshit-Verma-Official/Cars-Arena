package autocrew.akd.enterntainment;

public class BrandsModel {
    private String brandLogoUrl;
    private String brandBackImageUrl;
    private String brandName;

    public BrandsModel(String brandLogoUrl, String brandBackImageUrl, String brandName) {
        this.brandLogoUrl = brandLogoUrl;
        this.brandBackImageUrl = brandBackImageUrl;
        this.brandName = brandName;
    }

    public String getBrandLogoUrl() {
        return brandLogoUrl;
    }

    public void setBrandLogoUrl(String brandLogoUrl) {
        this.brandLogoUrl = brandLogoUrl;
    }

    public String getBrandBackImageUrl() {
        return brandBackImageUrl;
    }

    public void setBrandBackImageUrl(String brandBackImageUrl) {
        this.brandBackImageUrl = brandBackImageUrl;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
