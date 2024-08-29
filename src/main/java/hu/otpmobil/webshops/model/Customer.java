package hu.otpmobil.webshops.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

  private String webShopId;
  private String customerId;
  private String customerName;
  private String customerAddress;
}
