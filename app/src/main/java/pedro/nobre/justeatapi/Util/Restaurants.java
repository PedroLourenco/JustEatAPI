package pedro.nobre.justeatapi.Util;

import java.util.List;

/**
 * Created by pedro on 24/09/15.
 */
public class Restaurants {

    private String name;
    private String Address;
    private String Postcode;
    private String city;
    private String ratingStars;
    private String Image;

    public List<String> getCuisineTypes() {
        return CuisineTypes;
    }

    public void setCuisineTypes(List<String> cuisineTypes) {
        CuisineTypes = cuisineTypes;
    }

    private List<String> CuisineTypes;

    public void Restaurants (){


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPostcode() {
        return Postcode;
    }

    public void setPostcode(String postcode) {
        Postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getRatingStars() {
        return ratingStars;
    }

    public void setRatingStars(String ratingStars) {
        this.ratingStars = ratingStars;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
