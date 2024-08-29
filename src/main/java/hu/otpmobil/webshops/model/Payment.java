package hu.otpmobil.webshops.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

  private WebShopCustomerId webShopCustomerId;
  private PaymentType paymentType;
  private BigDecimal amount;
  private String bankAccountNumber;
  private String cardNumber;
  private LocalDate paymentTime;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class WebShopCustomerId {
    private String webShopId;
    private String customerId;
  }
}
