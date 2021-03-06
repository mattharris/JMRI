package jmri.jmrix.can.adapters.lawicell.canusb.serialdriver;

import jmri.util.JUnitUtil;
import jmri.util.junit.annotations.ToDo;

import org.junit.jupiter.api.*;

/**
 * Tests for ConnectionConfig class.
 *
 * @author Paul Bender Copyright (C) 2016
 **/
public class ConnectionConfigTest extends jmri.jmrix.AbstractSerialConnectionConfigTestBase  {

    @Test
    @Disabled("parent class test is unreliable on appveyor due to serial port identification")
    @ToDo("find and correct the reason why the test fails on appveyor for this class")
    @Override
    public void testGetInfo(){
    }

   @BeforeEach
    @Override
   public void setUp() {
        JUnitUtil.setUp();

        JUnitUtil.initDefaultUserMessagePreferences();
        cc = new ConnectionConfig();
        cc.setManufacturer("Lawicell");
   }

   @AfterEach
    @Override
   public void tearDown(){
        cc = null;
        JUnitUtil.tearDown();
   }

}
