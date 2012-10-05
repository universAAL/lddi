package it.polito.elite.domotics.dog2.knxnetworkdriver;

import static org.junit.Assert.* ;
import org.junit.Test;

/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxCommandTest {

	@Test
	public void testCompatibleWith() {

		KnxCommand com = new KnxCommand("testNotification", "1/2/3", "0x80");
		
		assertTrue( com.compatibleWith("1/2/3", "80") );
		assertFalse( com.compatibleWith("1/2/3", "81") );
		assertFalse( com.compatibleWith("1/2/4", "80") );
		assertFalse( com.compatibleWith("1/2/4", "81") );

	}
}
