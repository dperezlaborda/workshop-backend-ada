package ar.com.manflack.ada.domain.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.manflack.ada.domain.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long>{
	
}
