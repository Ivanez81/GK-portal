package ru.geekbrains.gkportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.gkportal.entities.ContactType;

@Repository
public interface ContactTypeRepository extends JpaRepository<ContactType, Integer> {

}
