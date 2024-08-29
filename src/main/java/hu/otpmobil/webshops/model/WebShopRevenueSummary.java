package hu.otpmobil.webshops.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebShopRevenueSummary {

  private String webShopId;
  private BigDecimal cardRevenue;
  private BigDecimal transferRevenue;
}
