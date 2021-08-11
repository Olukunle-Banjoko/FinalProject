package algonquin.cst2335.finalproject;
/*
  Charging Station class: which has constructor for the different parameters of the EV charging station searched.
  @author Olukunle Banjoko
  @version 1.0.0
  @param name: Title name of the charging station
         address: Address of the charging station
         _lat: latitude of the charging station
         _long: longitude of the charging station
         distance: distance of charging station from current location
         contact: contact telephone number of the charging station

 */
public class ChargingStation {
    String name;
    String address;
    float _lat;
    float _long;
    float distance;
    String contact;

    public ChargingStation(String name, String address, float _lat, float _long, float _distance, String contact) {
        this.name = name;
        this.address = address;
        this._lat = _lat;
        this._long = _long;
        this.distance = _distance;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float get_lat() {
        return _lat;
    }

    public void set_lat(float _lat) {
        this._lat = _lat;
    }

    public float get_long() {
        return _long;
    }

    public void set_long(float _long) {
        this._long = _long;
    }

    public float get_distance() {
        return distance;
    }

    public void set_distance(float _distance) {
        this.distance = _distance;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
