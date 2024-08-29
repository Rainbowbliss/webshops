package hu.otpmobil.webshops.service;

import hu.otpmobil.webshops.model.Payment;
import hu.otpmobil.webshops.util.CsvReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class PaymentService {

  public List<Payment> getPayments() {
    return CsvReaderUtil.readPayments("data/payments.csv");
  }
}
