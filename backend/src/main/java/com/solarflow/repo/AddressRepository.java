package com.solarflow.repo;

import com.solarflow.model.Address;
import com.solarflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}
