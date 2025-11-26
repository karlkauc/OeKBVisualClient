package dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class FundEnhancerTest {

    @BeforeEach
    void setUp() throws Exception {
        // Reset the singleton before each test to ensure isolation
        FundEnhancer.reset();

        // Load the test data from test resources
        FundEnhancer enhancer = FundEnhancer.getInstance();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("isinlei.csv")) {
            assertNotNull(is, "Test CSV file not found in resources.");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                enhancer.loadDataFromReader(reader);
            }
        }
    }

    @Test
    @DisplayName("Should return correct fund name for a given ISIN")
    void testGetFundNameByISIN() {
        String fundName = FundEnhancer.getFundNameByID("TEST_ISIN_1");
        assertEquals("My Test Fund", fundName);
    }

    @Test
    @DisplayName("Should return correct fund name for a given LEI")
    void testGetFundNameByLEI() {
        String fundName = FundEnhancer.getFundNameByID("TEST_LEI_2");
        assertEquals("Another Fund", fundName);
    }
    
    @Test
    @DisplayName("Should return correct fund name for a given OENB_ID")
    void testGetFundNameByOenbId() {
        String fundName = FundEnhancer.getFundNameByID("TEST_OENB_1");
        assertEquals("My Test Fund", fundName);
    }

    @Test
    @DisplayName("Should return 'not found' for a non-existent ID")
    void testGetFundName_NotFound() {
        String fundName = FundEnhancer.getFundNameByID("NON_EXISTENT_ID");
        assertEquals("not found", fundName);
    }

    @Test
    @DisplayName("Should correctly remove suffixes like (A) and (T) from fund names")
    void testSuffixRemoval() {
        // This is implicitly tested by the other tests, but an explicit test is good.
        assertEquals("My Test Fund", FundEnhancer.getFundNameByID("TEST_ISIN_1"));
        assertEquals("Another Fund", FundEnhancer.getFundNameByID("TEST_LEI_2"));
    }
    
    @Test
    @DisplayName("Should use cache for subsequent lookups")
    void testCache() {
        // First lookup
        String fundName1 = FundEnhancer.getFundNameByID("TEST_ISIN_1");
        assertEquals("My Test Fund", fundName1);
        
        // The cache should now be populated. We can't directly check the cache,
        // but we can call it again to ensure it still returns the correct value.
        String fundName2 = FundEnhancer.getFundNameByID("TEST_ISIN_1");
        assertEquals("My Test Fund", fundName2);
    }

    
}
