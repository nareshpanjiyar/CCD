package com.hospital.repository;

import com.hospital.entity.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DrugRepository extends JpaRepository<Drug, Long> {
    List<Drug> findAllByOrderByNameAsc();
    
    @Query("SELECT d FROM Drug d WHERE d.stockQuantity > 0 ORDER BY d.name")
    List<Drug> findAllInStock();
    
    @Query("SELECT COUNT(d) FROM Drug d WHERE d.stockQuantity < 50")
    long countLowStock();
    
    List<Drug> findByStockQuantityLessThan(int threshold);
    
    List<Drug> findByExpiryDateBefore(java.util.Date date);
}