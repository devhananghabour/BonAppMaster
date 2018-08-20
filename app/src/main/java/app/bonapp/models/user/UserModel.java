package app.bonapp.models.user;

import java.util.List;

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
public class UserModel {

     String id;
     String userName;
     String userType;
     String accessToken;
     String email;
     String mobile;
     BillingAddressModel billingAddress;
     List<AddressModel> shippingAddress;

}
