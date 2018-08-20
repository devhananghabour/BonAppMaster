package app.bonapp.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class BillingAddressModel {

    private String address;
    private String countryName;
    private String countryId;
    private String cityName;
    private String cityId;
    private String stateName;
    private String stateId;
    private String isoCode;
    private String socialType;
    private String postalCode;
}
