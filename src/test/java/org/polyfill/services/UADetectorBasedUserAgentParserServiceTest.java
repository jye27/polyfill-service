package org.polyfill.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.polyfill.configurations.MockBrowserAliasesConfig;
import org.polyfill.interfaces.UserAgent;
import org.polyfill.interfaces.UserAgentParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader=AnnotationConfigContextLoader.class,
        classes={MockBrowserAliasesConfig.class,
                UADetectorBasedUserAgentParserService.class
        })
public class UADetectorBasedUserAgentParserServiceTest {

    private Map<String, String> UAStrings = new HashMap<String, String>(){{
        put("chrome", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36");
        put("firefox_namoroka", "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2a2pre) Gecko/20090908 Ubuntu/9.04 (jaunty) Namoroka/3.6a2pre GTB5 (.NET CLR 3.5.30729)");
        put("chrome_ios", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0_2 like Mac OS X) AppleWebKit/601.1 (KHTML, like Gecko) CriOS/53.0.2785.109 Mobile/14A456 Safari/601.1.46");
        put("firefox_ios", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_1_1 like Mac OS X) AppleWebKit/602.2.14 (KHTML, like Gecko) FxiOS/5.3 Mobile/14B100 Safari/602.2.14");
        put("opera_ios", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_1_1 like Mac OS X) AppleWebKit/602.2.14 (KHTML, like Gecko) OPiOS/14.0.0.104835 Mobile/14B100 Safari/9537.53");
        put("aol", "Mozilla/5.0 (compatible; MSIE 9.0; AOL 9.7; AOLBuild 4343.19; Windows NT 6.1; WOW64; Trident/5.0; FunWebProducts)");
    }};

    @Autowired
    private UserAgentParserService uaUtilService;

    @Test
    public void testGeneralUAInfo() {
        UserAgent ua = uaUtilService.parse(UAStrings.get("chrome"));
        assertEquals("Browser family name incorrect", "chrome", ua.getFamily());
        assertEquals("Major version incorrect", "41", ua.getMajorVersion());
        assertEquals("Minor version incorrect", "0", ua.getMinorVersion());
        assertEquals("Version number incorrect", "41.0.2227.0", ua.getVersion());
        assertEquals("toString should be in format family/version", "chrome/41.0.2227.0", ua.toString());
    }

    @Test
    public void testChromeIOSTreatedAsMobileSafari() {
        UserAgent ua = uaUtilService.parse(UAStrings.get("chrome_ios"));
        assertEquals("Browser family name incorrect", "ios_saf", ua.getFamily());
        assertEquals("Version number incorrect", "10.0.2", ua.getVersion());
    }

    @Test
    public void testFirefoxIOSTreatedAsMobileSafari() {
        UserAgent ua = uaUtilService.parse(UAStrings.get("firefox_ios"));
        assertEquals("Browser family name incorrect", "ios_saf", ua.getFamily());
        assertEquals("Version number incorrect", "10.1.1", ua.getVersion());
    }

    @Test
    public void testOperaIOSTreatedAsMobileSafari() {
        UserAgent ua = uaUtilService.parse(UAStrings.get("opera_ios"));
        assertEquals("Browser family name incorrect", "ios_saf", ua.getFamily());
        assertEquals("Version number incorrect", "10.1.1", ua.getVersion());
    }

    @Test
    public void testSimpleAlias() {
        UserAgent ua = uaUtilService.parse(UAStrings.get("firefox_namoroka"));
        assertEquals("Browser family name incorrect", "firefox", ua.getFamily());
        assertEquals("Version number incorrect", "3.6a2pre", ua.getVersion());
    }

    @Test
    public void testNonAliasedBrowser() {
        UserAgent ua = uaUtilService.parse(UAStrings.get("aol"));
        assertEquals("Browser family name incorrect", "aol explorer", ua.getFamily());
        assertEquals("Version number incorrect", "9.7", ua.getVersion());
    }
}
