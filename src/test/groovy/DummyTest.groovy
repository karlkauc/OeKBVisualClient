import spock.lang.Specification


class DummyTest  extends Specification  {

    def "maximum of two numbers"() {
        expect:
            Math.max(1, 3) == 3
            Math.max(7, 4) == 7
            Math.max(0, 0) == 0
    }

    /*
    void testNewTime() {
        println LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_H_m_s"))
    }
    */


}
