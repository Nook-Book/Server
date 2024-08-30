package com.nookbook.domain.collection.domain.repository;

import com.nookbook.domain.collection.domain.CollectionBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionBookRepository extends JpaRepository<CollectionBook, Long> {

}
