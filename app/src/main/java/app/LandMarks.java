package app;

public class LandMarks {
    String name;
    double rating;
    double distance;
    String imgurl;
    String PhoneNumber;
    double Lat;
    double Lon;
   String Reviews;


    public LandMarks(String name, double rating, double distance, double Lat, double Lon, String imgurl, String PN, String R) {
        this.name = name;
        this.rating = rating;
        this.distance = distance;
        this.Lat = Lat;
        this.Lon = Lon;
        this.imgurl = imgurl;
        this.PhoneNumber = PN;
        this.Reviews = R;
    }

    public String getReviews() {
        return Reviews;
    }

    public double getLat() {
        return Lat;
    }

    public double getLon() {
        return Lon;
    }
    public String getPhoneNumber() {
        return PhoneNumber;
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
