package hu.otpmobil.webshops.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPaymentSummary {

  private String name;
  private String address;
  private BigDecimal spending;
}
