import com.churchclerk.baseapi.model.ApiCaller
import com.churchclerk.demoapi.Demo
import com.churchclerk.demoapi.DemoApi
import com.churchclerk.demoapi.DemoService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spock.lang.Specification
import spock.lang.Subject

class DemoApiSpec extends Specification {

    @Subject
    DemoApi testSubject;

    DemoService demoService;

    def setup() {
        testSubject = new DemoApi();

        demoService = Mock()
        testSubject.service = demoService;

        testSubject.apiCaller = new ApiCaller("testadmin|", "SUPER,ADMIN,CLERK,OFFICIAL,MEMBER,NONMEMBER");

        setupMocks();
    }

    Page<? extends Demo>    mockedPage;
    Pageable                savedPageable;
    Demo                    savedCriteria;

    def setupMocks() {
        mockedPage = null;
        savedPageable = null;
        savedCriteria = null;
        demoService.getResources(_,_) >> {
            savedPageable = it[0];
            savedCriteria = it[1];
            return mockedPage;
        }
    }

    def "createCriteria with null parameter(s)"() {
        given: "parameter(s)"
        testSubject.testDataLike    = testDataLike;
        testSubject.active          = testActive;

        when: "the method is executed"
        def actual = testSubject.createCriteria();

        then: "no exception should be thrown"
        noExceptionThrown()

        and: "the actual should match"
        actual != null
        actual.active == expectedActive
        actual.id == null
        actual.testData == testDataLike

        where:
        testDataLike    | testActive    | expectedActive    | expectedId
        null            | null          | true              | null
        ""              | true          | true              | null
        "Unit Test"     | false         | false             | null
    }

    def "createCriteria with non super user role"() {
        given: "parameter(s)"
        testSubject.apiCaller       = new ApiCaller("testadmin|", "ADMIN,CLERK,OFFICIAL,MEMBER,NONMEMBER");

        when: "the method is executed"
        def actual = testSubject.createCriteria();

        then: "no exception should be thrown"
        noExceptionThrown()

        and: "the actual should match"
        actual != null
        actual.active == true
        actual.id != null
        actual.testData == null
    }

    def "doGet with null parameters"() {
        given: "parameter(s)"
        Pageable pageable = null;

        when: "the method is executed"
        def actual = testSubject.doGet(pageable);

        then: "no exception should be thrown"
        noExceptionThrown()

        and: "the actual should match"
        actual == null

        and: "the saved should match"
        savedPageable == pageable;
        savedCriteria != null
        savedCriteria.testData == null
        savedCriteria.id == null
        savedCriteria.active == true
    }
}