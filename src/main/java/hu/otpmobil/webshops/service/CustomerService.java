package hu.otpmobil.webshops.service;

import hu.otpmobil.webshops.model.Customer;
import hu.otpmobil.webshops.util.CsvReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class CustomerService {

  public List<Customer> getCustomers() {
    return CsvReaderUtil.readCustomers("data/customer.csv");
  }
}
