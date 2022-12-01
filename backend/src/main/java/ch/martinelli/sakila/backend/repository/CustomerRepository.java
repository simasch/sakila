package ch.martinelli.sakila.backend.repository;

import ch.martinelli.sakila.db.tables.records.CustomerRecord;
import org.jooq.DSLContext;
import org.jooq.OrderField;
import org.jooq.Record4;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static ch.martinelli.sakila.db.tables.Customer.CUSTOMER;
import static ch.martinelli.sakila.db.tables.CustomerList.CUSTOMER_LIST;

@Repository
@Transactional(readOnly = true)
public class CustomerRepository {

    private final DSLContext dsl;

    public CustomerRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Stream<Record4<Integer, String, String, String>> findAll(List<OrderField<?>> orderFields, int offset, int limit) {
        return dsl
                .select(CUSTOMER_LIST.ID, CUSTOMER_LIST.NAME, CUSTOMER_LIST.ADDRESS, CUSTOMER_LIST.CITY)
                .from(CUSTOMER_LIST)
                .orderBy(orderFields)
                .offset(offset)
                .limit(limit)
                .fetchStream();
    }

    public CustomerRecord newRecord() {
        return dsl.newRecord(CUSTOMER);
    }

    public Optional<CustomerRecord> findById(Integer id) {
        return dsl.selectFrom(CUSTOMER).where(CUSTOMER.CUSTOMER_ID.eq(id)).fetchOptional();
    }

    @Transactional
    public void save(CustomerRecord customer) {
        dsl.attach(customer);
        customer.store();
    }
}
