package net.kaleidos.hibernate.json

import grails.test.mixin.integration.Integration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import spock.lang.Unroll
import test.Address
import test.json.TestListJsonb

@Integration
@Transactional
class PostgresqlJsonbListDomainIntegrationSpec extends Specification {

    @Unroll
    void 'save and read a domain class with a list to json'() {
        setup:
            def addr1 = new Address([
                label     : 'Home',
                houseNum  : '1234',
                streetName: 'New Port Pavonia',
                city      : 'New York',
                zipcode   : '07310',
                country   : 'USA'
            ])

            def addr2 = new Address([
                label     : 'Work',
                houseNum  : '1234',
                streetName: 'Washington Street',
                city      : 'Princeton',
                zipcode   : '06990',
                country   : 'USA'
            ])

            def testListJsonb = new TestListJsonb(name: 'John', addresses: [addr1, addr2])

        when:
            // Domain saving and retrieving should be in different sessions. Only in that case Hibernate will invoke
            // nullSafeGet on the corresponding user type and will not use current session's cache.
            TestListJsonb.withNewSession {
                testListJsonb.save(flush: true)
            }

        then:
            testListJsonb.hasErrors() == false

        and:
            def obj = testListJsonb.get(testListJsonb.id)
            obj.addresses.size() == 2
    }

    void 'save and read a domain class with a list #listAddresses to json'() {
        setup:
            def testListJsonb = new TestListJsonb(name: 'John', addresses: listAddresses)

        when:
            // Domain saving and retrieving should be in different sessions. Only in that case Hibernate will invoke
            // nullSafeGet on the corresponding user type and will not use current session's cache.
            TestListJsonb.withNewSession {
                testListJsonb.save(flush: true)
            }

        then:
            testListJsonb.hasErrors() == false

        where:
            listAddresses << [[], null]
    }
}