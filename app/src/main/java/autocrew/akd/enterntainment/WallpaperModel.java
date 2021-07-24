package autocrew.akd.enterntainment;


public class WallpaperModel {
    private String imageUrl4k;
    private String imageUrl1080p;
    private String imageUrl720p;
    private String imageUrl480p;
    private String imageId;
    private Long downloads;
    private Long views;
    private String resolution;

    public WallpaperModel(String imageUrl4k, String imageUrl1080p, String imageUrl720p, String imageUrl480p, String imageId, Long downloads, Long views, String resolution) {
        this.imageUrl4k = imageUrl4k;
        this.imageUrl1080p = imageUrl1080p;
        this.imageUrl720p = imageUrl720p;
        this.imageUrl480p = imageUrl480p;
        this.imageId = imageId;
        this.downloads = downloads;
        this.views = views;
        this.resolution = resolution;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public Long getDownloads() {
        return downloads;
    }

    public void setDownloads(Long downloads) {
        this.downloads = downloads;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getImageUrl4k() {
        return imageUrl4k;
    }

    public void setImageUrl4k(String imageUrl4k) {
        this.imageUrl4k = imageUrl4k;
    }

    public String getImageUrl1080p() {
        return imageUrl1080p;
    }

    public void setImageUrl1080p(String imageUrl1080p) {
        this.imageUrl1080p = imageUrl1080p;
    }

    public String getImageUrl720p() {
        return imageUrl720p;
    }

    public void setImageUrl720p(String imageUrl720p) {
        this.imageUrl720p = imageUrl720p;
    }

    public String getImageUrl480p() {
        return imageUrl480p;
    }

    public void setImageUrl480p(String imageUrl480p) {
        this.imageUrl480p = imageUrl480p;
    }
}
