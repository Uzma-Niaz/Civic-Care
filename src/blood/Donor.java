package blood;

public class Donor {
    public String name;
    public String bloodGroup;
    public String contact;
    public String city;

    public Donor(String name, String bloodGroup, String contact, String city) {
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.contact = contact;
        this.city = city;
    }

    @Override
    public String toString() {
        return "Name: " + name + " | Group: " + bloodGroup + " | Contact: " + contact + " | City: " + city;
    }
}