package app;

public class LandMarks {
    String name;
    double rating;
    double distance;
    String imgurl;
    double Lat;
    double Lon;

    public LandMarks(String name, double rating, double distance, double Lat, double Lon, String imgurl) {
        this.name = name;
        this.rating = rating;
        this.distance = distance;
        this.Lat = Lat;
        this.Lon = Lon;
        this.imgurl = imgurl;
    }

    public double getLat() {
        return Lat;
    }

    public double getLon() {
        return Lon;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public double getDistance() {
        return distance;
    }

    public String getImgurl() { return imgurl; }

}
