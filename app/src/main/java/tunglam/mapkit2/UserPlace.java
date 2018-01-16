package tunglam.mapkit2;

/**
 * Created by rexna on 1/14/2018.
 */

public class UserPlace {
    private String name;
    private String address;
    private int image;
    double lat;
    double lng;

    public UserPlace(String name, String address, int image, double lat, double lng) {
        this.name = name;
        this.address = address;
        this.image = image;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getImage() {
        return image;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
