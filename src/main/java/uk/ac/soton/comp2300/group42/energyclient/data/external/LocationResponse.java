package uk.ac.soton.comp2300.group42.energyclient.data.external;

public record LocationResponse(
        double lat,
        double lon,
        String city,
        String country
) {
}
