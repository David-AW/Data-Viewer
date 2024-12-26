package com.daveaw.dataviewer.frame;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataFrameTest {

	DataFrame frame;
	byte[] data;
	
	@BeforeEach
	void setUp() throws Exception {
		data = new byte[2];
		data[0] = 0x55;
		data[1] = (byte) 0xAA;
		frame = new DataFrame(data);
	}

	@AfterEach
	void tearDown() throws Exception {
		frame = null;
		data = null;
	}

	@Test
	void testCardinality() {
		frame = new DataFrame(data);
		assertEquals(8, frame.cardinality());
	}
	
	@Test
	void testFlip() {
		frame = new DataFrame(data);
		frame.flip(0);
		frame.flip(2);
		frame.flip(13);
		frame.flip(15);
		assertEquals("F5AF", frame.toString());
	}
	
	@Test
	void testFlipRange() {
		frame = new DataFrame(data);
		frame.flip(4,12);
		assertEquals("5A5A", frame.toString());
	}
	
	@Test
	void testSetSingleBitByValue() {
		frame = new DataFrame(data);
		frame.set(0, false);
		frame.set(1, false);
		frame.set(2, true);
		frame.set(3, true);
		assertEquals("35AA", frame.toString());
	}

	@Test
	void testSetRangeByValue() {
		frame = new DataFrame(data);
		frame.set(0,9,false);
		frame.set(9,16,true);
		assertEquals("007F", frame.toString());
	}
	
	@Test
	void testSetSingleBit() {
		frame = new DataFrame(data);
		frame.set(0);
		frame.set(1);
		frame.set(2);
		frame.set(3);
		assertEquals("F5AA", frame.toString());
	}
	
	@Test
	void testSetRange() {
		frame = new DataFrame(data);
		frame.set(0,9);
		assertEquals("FFAA", frame.toString());
	}
	
	@Test
	void testClearSingleBit() {
		frame = new DataFrame(data);
		frame.clear(0);
		frame.clear(1);
		frame.clear(2);
		frame.clear(3);
		assertEquals("05AA", frame.toString());
	}
	
	@Test
	void testClearRange() {
		frame = new DataFrame(data);
		frame.clear(0,9);
		assertEquals("002A", frame.toString());
	}
	
	@Test
	void testClear() {
		frame = new DataFrame(data);
		frame.clear();
		assertEquals("0000", frame.toString());
	}
	
	@Test
	void testAND() {
		frame = new DataFrame(data);
		byte[] mask_data = {(byte)0xE7};
		DataFrame mask = new DataFrame(mask_data);
		frame.and(mask, 2, false);
		assertEquals("51AA", frame.toString());
		frame.and(mask, true);
		assertEquals("41A2", frame.toString());
		mask_data[0] = 0x0F;
		mask = new DataFrame(mask_data);
		frame.and(mask);
		assertEquals("01A2", frame.toString());
	}
	
	@Test
	void testOR() {
		frame = new DataFrame(data);
		byte[] mask_data = {(byte)0xE7};
		DataFrame mask = new DataFrame(mask_data);
		frame.or(mask, 2, false);
		assertEquals("7DEA", frame.toString());
		frame.or(mask, true);
		assertEquals("FFEF", frame.toString());
	}
	
	@Test
	void testGetHexValueAt() {
		frame = new DataFrame(data);
		assertEquals("55", frame.getHexValueAt(0));
		assertEquals("AA", frame.getHexValueAt(1));
	}
	
	@Test
	void testGetSingleBit() {
		frame = new DataFrame(data);
		assertTrue(frame.get(1));
		assertFalse(frame.get(9));
	}
	
	@Test
	void testGetRange() {
		frame = new DataFrame(data);
		assertEquals("5680", frame.get(2, 11).toString());
	}
	
}
