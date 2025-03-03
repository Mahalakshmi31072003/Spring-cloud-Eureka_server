package com.maha.orderserver.repository;

import com.maha.orderserver.entity.Order;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
@Repository

public interface OrderRepository extends JpaRepository<Order,Long>{

}
