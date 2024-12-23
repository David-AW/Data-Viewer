package com.daveaw.dataviewer.frame;

public class RawDataStore extends DataFrameStorage{

	private byte[] original_data;
	
	private int total_bits;
	private int width;
	
	public RawDataStore(byte[] data) {
		original_data = data;
		total_bits = data.length * 8;
		this.data.add(new DataFrame(data, total_bits));
		count = 1;
		max_length = data.length;
		width = total_bits;
	}
	
	public int getLineCount() {
		return data.size();
	}
	
	public int getByteCount() {
		return original_data.length;
	}
	
	public int getBitCount() {
		return total_bits;
	}
	
	private final int[] masks = {0x00,0x80,0xC0,0xE0,0xF0,0xF8,0xFC,0xFE,0xFF};
	
	/*
	 * Should have probably used a BitSet object offered by Java Libraries for this. 
	 * Wanted to challenge myself to write an algorithm to assign rows of certain bit widths.
	 */
	public void setWidth(int bits) {
		data.clear();
		width = bits;
		
		int bytes_to_allocate = bits / 8 + (bits % 8 > 0 ? 1 : 0);
		int lines = total_bits / bits + (total_bits % bits > 0 ? 1 : 0);
		int bitpos = 0;
		int bits_remaining_in_byte = 8;
		
		count = lines;
		max_length = bytes_to_allocate;
		
		for (int line = 0; line < lines; line++) { // Create a byte array of variable amount (function argument) of bits for each line
			int bits_remaining = total_bits - (line * bits);
			int bits_needed_in_line = bits;
			int bits_needed_for_byte;
			
			if (bits_remaining < bits) {
				bytes_to_allocate = bits_remaining / 8 + (bits_remaining % 8 > 0 ? 1 : 0); // Allocate amount of bytes needed to store the amount of bits
				bits_needed_in_line = bits_remaining;
			}
			
			int bits_in_line = bits_needed_in_line; // Store this for future use since bits_needed_in_line is used to track bits needing to be read
			
			byte[] bytes = new byte[bytes_to_allocate];
			
			for (int b = 0; b < bytes.length; b++) { // iterate through allocated bytes for this line
				
				bits_needed_for_byte = bits_needed_in_line >= 8 ? 8 : bits_needed_in_line; // Should need 8 bits for this byte unless width is less than 8 or the amount of bits left in line is less than 8
				
				int shift_amount = 8 - bits_remaining_in_byte; // Get amount to shift remaining bits to reach the most significant bit position (MSB)
				int amount_bits_shifted = bits_remaining_in_byte;
				int byte_num = bitpos/8;

				bytes[b] = (byte) ((original_data[byte_num] << shift_amount) & masks[bits_needed_for_byte]); // Shift remaining bits in this byte to the MSB
				bitpos += Math.min(bits_needed_for_byte, bits_remaining_in_byte); // Can't remember the edge case this was for
				bits_needed_in_line -= bits_remaining_in_byte;
				
				if (shift_amount == 0) {
					bits_remaining_in_byte -= bits_needed_for_byte;
				}
				
				if (bits_needed_for_byte == 8) {
					bits_needed_for_byte -= amount_bits_shifted; // If we are not reaching a point in the line where we need less than 8 bits, reduce the amount still needed by how many were grabbed
				}else {
					bits_needed_for_byte -= shift_amount;
					bits_remaining_in_byte -= shift_amount;
				}
				
				if (byte_num != bitpos/8) { // Compare byte position, if we are in a new byte, we need to reset the bit count
					bits_remaining_in_byte = 8;
				}
				
				if (shift_amount > 0 && bits_needed_for_byte > 0 && bits_needed_in_line > 0) { // if we shifted an incomplete byte to MSB, grab remaining bits from next byte, unless we don't need anymore
					int shifted_data = Byte.toUnsignedInt(original_data[bitpos/8]) >>> amount_bits_shifted;
					bytes[b] ^= shifted_data;
					bitpos += shift_amount;
					bits_remaining_in_byte = 8 - (bitpos%8);
					bits_needed_in_line -= shift_amount;
				}
				
			}
			
			data.add(new DataFrame(bytes, bits_in_line));
			
		}
	}
	
	public int getWidth() {
		return width;
	}

}
