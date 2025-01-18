package com.daveaw.dataviewer.frame;

public final class DataFrame {

	private final byte[] data;
	private final int length; // Length of data frame in bits
	private final int number;
	
	private static final String[] HEXMAP = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
	private static final int[] BIT_MASK = {0x00, 0x80, 0xC0, 0xE0, 0xF0, 0xF8, 0xFC, 0xFE};
	
	public DataFrame(byte[] data) {
		this(data, data.length*8);
	}
	
	public DataFrame(byte[] data, int length) {
		this(data, length, -1);
	}
	
	public DataFrame(byte[] data, int length, int number) {
		this.data = data;
		this.length = length;
		this.number = number;
	}
	
	public DataFrame(DataFrame copy) {
		this(copy, copy.number);
	}
	
	public DataFrame(DataFrame copy, int number) {
		data = new byte[copy.data.length];
		for (int i = 0; i < copy.data.length; i++) {
			data[i] = copy.data[i];
		}
		this.length = copy.length;
		this.number = number;
	}
	
	public byte[] getData() {
		return data;
	}
	
	/**
	 * Returns the value of the bit at the specified index
	 * @param bit_index The index which bit is to be returned
	 * @return The value of the bit at the specified index
	 * @throws IndexOutOfBoundsException If the specified index is negative or larger than the length of this data frame
	 */
	public boolean get(int bit_index) throws IndexOutOfBoundsException {
		if (bit_index > length || bit_index < 0)
			throw new IndexOutOfBoundsException(bit_index);
		return (data[bit_index/8] & (int)Math.pow(2, 7-(bit_index%8))) != 0;
	}
	
	/**
	 * Returns a new data frame containing the data from bit position bit_index_from to bit_index_to
	 * @param bit_index_from the starting bit to grab data from
	 * @param bit_index_to  the last bit to grab data from
	 * @return A new data frame containing the data from bit position bit_index_from to bit_index_to
	 * @throws IndexOutOfBoundsException if bit_index_from or bit_index_to is negative or larger than the length of this data frame, or bit_index_from is larger than bit_index_to
	 */
	public DataFrame get(int bit_index_from, int bit_index_to) throws IndexOutOfBoundsException {
		if (bit_index_from > bit_index_to || bit_index_to > length || bit_index_from < 0 || bit_index_to < 0)
			throw new IndexOutOfBoundsException();
		
		int length = bit_index_to - bit_index_from;
		int size = length/8 + (length%8!=0?1:0);
		
		byte[] truncated_data = new byte[size];
		int start = bit_index_from / 8;
		int start_offset = bit_index_from % 8;
		
		for (int i = 0; i < size; i++) {
			if (length-i*8 < 8) {
				truncated_data[i] = (byte) ((data[start+i] << start_offset) & BIT_MASK[length-i*8]);
				break;
			}
			truncated_data[i] = (byte) (data[start+i] << start_offset);
			truncated_data[i] = (byte) (truncated_data[i] ^ ((data[start+i+1] & BIT_MASK[start_offset]) >>> (8-start_offset)));
		}
		
		return new DataFrame(truncated_data, length, this.number);
	}
	
	/**
	 * Applies a logical AND operation to this data frame using the provided data frame.
	 * @param frame data frame to use for the operation
	 * @param bit_index starting bit to apply the operation
	 * @param repeat repeat the specified data over the entirety of this data frame 
	 */
	public void and(DataFrame frame, int bit_index, boolean repeat) {
		for (int i = bit_index; i < length; i++) {
			int mask_index = i - bit_index;
			if (mask_index >= frame.length && !repeat)
				break;
			if (get(i) && frame.get(mask_index%frame.length))
				set(i, true);
			else
				set(i, false);
		}
	}
	
	/**
	 * Applies a logical AND operation to this data frame using the provided data frame.
	 * @param frame data frame to use for the operation
	 * @param repeat repeat the specified data over the entirety of this data frame
	 */
	public void and(DataFrame frame, boolean repeat) {
		and(frame, 0, repeat);
	}
	
	/**
	 * Applies a logical AND operation to this data frame using the provided data frame.
	 * @param frame data frame to use for the operation
	 */
	public void and(DataFrame frame) {
		and(frame, 0, false);
	}
	
	/**
	 * Applies an inverse logical AND operation to this data frame using the provided data frame.
	 * @param frame data frame to use for the operation
	 * @param bit_index starting bit to apply the operation
	 * @param repeat repeat the specified data over the entirety of this data frame
	 */
	public void andNot(DataFrame frame, int bit_index, boolean repeat) {
		for (int i = bit_index; i < length; i++) {
			int mask_index = i - bit_index;
			if (mask_index >= frame.length && !repeat)
				break;
			if (get(i) && frame.get(mask_index%frame.length))
				set(i, false);
			else
				set(i, true);
		}
	}
	
	/**
	 * Applies an inverse logical AND operation to this data frame using the provided data frame.
	 * @param frame data frame to use for the operation
	 * @param repeat repeat the specified data over the entirety of this data frame
	 */
	public void andNot(DataFrame frame, boolean repeat) {
		andNot(frame, 0, repeat);
	}
	
	/**
	 * Applies an inverse logical AND operation to this data frame using the provided data frame.
	 * @param frame data frame to use for the operation
	 */
	public void andNot(DataFrame frame) {
		andNot(frame, 0, false);
	}
	
	/**
	 * Applies a logical OR operation to this data frame using the provided data frame.
	 * @param frame data frame to use for the operation
	 * @param bit_index starting bit to apply the operation
	 * @param repeat repeat the specified data over the entirety of this data frame
	 */
	public void or(DataFrame frame, int bit_index, boolean repeat) {
		for (int i = bit_index; i < length; i++) {
			int mask_index = i - bit_index;
			if (mask_index >= frame.length && !repeat)
				break;
			if (get(i) || frame.get(mask_index%frame.length))
				set(i, true);
			else
				set(i, false);
		}
	}
	
	/**
	 * Applies a logical OR operation to this data frame using the provided data frame.
	 * @param frame data frame to use for the operation
	 * @param repeat repeat the specified data over the entirety of this data frame
	 */
	public void or(DataFrame frame, boolean repeat) {
		or(frame, 0, repeat);
	}
	
	/**
	 * Applies a logical OR operation to this data frame using the provided data frame.
	 * @param frame data frame to use for the operation
	 */
	public void or(DataFrame frame) {
		or(frame, 0, false);
	}
	
	/**
	 * Applies a logical EXCLUSIVE OR (XOR) operation to this data frame using the provided data frame.
	 * @param frame data frame to use for the operation
	 * @param bit_index starting bit to apply the operation
	 * @param repeat repeat the specified data over the entirety of this data frame
	 */
	public void xor(DataFrame frame, int bit_index, boolean repeat) {
		for (int i = bit_index; i < length; i++) {
			int mask_index = i - bit_index;
			if (mask_index >= frame.length && !repeat)
				break;
			if (get(i) == frame.get(mask_index%frame.length))
				set(i, false);
			else
				set(i, true);
		}
	}
	
	/**
	 * Applies a logical EXCLUSIVE OR (XOR) operation to this data frame using the provided data frame.
	 * @param frame data frame to use for the operation
	 * @param repeat repeat the specified data over the entirety of this data frame
	 */
	public void xor(DataFrame frame, boolean repeat) {
		xor(frame, 0, repeat);
	}
	
	/**
	 * Applies a logical EXCLUSIVE OR (XOR) operation to this data frame using the provided data frame.
	 * @param frame data frame to use for the operation
	 */
	public void xor(DataFrame frame) {
		xor(frame, 0, false);
	}
	
	/**
	 * Returns the number of bits set to 1 in this data frame.	    
	 * @return the number of bits set to 1 in this data frame.
	 */
	public int cardinality() {
		int one_count = 0;
		for (int i = 0; i < data.length; i++) 
			for (int mask = 1; mask < 0x100; mask = mask << 1) 
				if ((data[i] & mask) != 0)
					one_count++;
		return one_count;
	}
	
	/**
	 * Sets the bit at the specified index to the complement of its current value.
	 * @param bit_index Index of the bit to be flipped.
	 * @throws IndexOutOfBoundsException if the specified index is negative or larger than the length of this data frame
	 */
	public void flip(int bit_index) throws IndexOutOfBoundsException{
		if (bit_index > length || bit_index < 0)
			throw new IndexOutOfBoundsException(bit_index);
				
		data[bit_index/8] = (byte)(data[bit_index/8] ^ (int)Math.pow(2, 7-(bit_index%8)));
	}
	
	/**
	 * Sets each bit from the specified fromIndex (inclusive) to the specified toIndex (exclusive) to the complement of its current value.
	 * @param bit_index_from index of the first bit to flip
	 * @param bit_index_to index after the last bit to flip
	 * @throws IndexOutOfBoundsException if bit_index_from or bit_index_to is negative or larger than the length of this data frame, or bit_index_from is larger than bit_index_to
	 */
	public void flip(int bit_index_from, int bit_index_to) {
		if (bit_index_from > length || bit_index_from < 0 
				|| bit_index_to > length || bit_index_to < 0 
				|| bit_index_from > bit_index_to)
			throw new IndexOutOfBoundsException();
		
		for (int i = bit_index_from; i < bit_index_to; i++) {
			data[i/8] = (byte)(data[i/8] ^ (int)Math.pow(2, 7-(i%8)));
		}
	}
	
	/**
	 * Sets the bit at the specified index to either 1 (true) or 0 (false) based on value parameter.
	 * @param bit_index bit position to set
	 * @param value true for 1 false for 0
	 * @throws IndexOutOfBoundsException if the specified index is negative or larger than the length of this data frame
	 */
	public void set(int bit_index, boolean value) throws IndexOutOfBoundsException {
		if (bit_index > length || bit_index < 0)
			throw new IndexOutOfBoundsException(bit_index);
		
		int index = bit_index/8;
		int mask = (int)Math.pow(2, 7-(bit_index%8));
		int sum = data[index] & mask;
		if ((sum == 0 && value) || (sum != 0 && !value))
			data[index] = (byte)(data[index] ^ mask);
	}
	
	/**
	 * Sets the bit at the specified index to 1.
	 * @param bit_index
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int bit_index) throws IndexOutOfBoundsException {
		set(bit_index, true);
	}
	
	/**
	 * Sets the bits from the specified bit_index_from (inclusive) to the specified bit_index_to (exclusive) to 1 or 0 based on the value parameter.
	 * @param bit_index_from
	 * @param bit_index_to
	 * @throws IndexOutOfBoundsException if bit_index_from or bit_index_to is negative or larger than the length of this data frame, or bit_index_from is larger than bit_index_to
	 */
	public void set(int bit_index_from, int bit_index_to, boolean value) throws IndexOutOfBoundsException {
		if (bit_index_from > length || bit_index_from < 0 
				|| bit_index_to > length || bit_index_to < 0 
				|| bit_index_from > bit_index_to)
			throw new IndexOutOfBoundsException();
		
		for (int i = bit_index_from; i < bit_index_to; i++) {
			set(i, value);
		}
	}
	
	/**
	 * Sets the bits from the specified bit_index_from (inclusive) to the specified bit_index_to (exclusive) to 1.
	 * @param bit_index_from
	 * @param bit_index_to
	 * @throws IndexOutOfBoundsException if bit_index_from or bit_index_to is negative or larger than the length of this data frame, or bit_index_from is larger than bit_index_to
	 */
	public void set(int bit_index_from, int bit_index_to) throws IndexOutOfBoundsException {
		set(bit_index_from, bit_index_to, true);
	}
	
	/**
	 * Clears the bit at the specified index to 0.
	 * @param bit_index
	 * @throws IndexOutOfBoundsException if the specified index is negative or larger than the length of this data frame
	 */
	public void clear(int bit_index) throws IndexOutOfBoundsException {
		set(bit_index, false);
	}
	
	/**
	 * Clears each bit from the specified bit_index_from (inclusive) to the specified bit_index_to (exclusive) to 0.
	 * @param bit_index_from index of the first bit to clear
	 * @param bit_index_to index after the last bit to clear
	 * @throws IndexOutOfBoundsException if bit_index_from or bit_index_to is negative or larger than the length of this data frame, or bit_index_from is larger than bit_index_to
	 */
	public void clear(int bit_index_from, int bit_index_to) throws IndexOutOfBoundsException {
		set(bit_index_from, bit_index_to, false);
	}
	
	/**
	 * Clears the entire data frame to set all bits to 0
	 */
	public void clear() {
		set(0, length, false);
	}
	
	/**
	 * Returns the hexadecimal value of the byte at the specified index.
	 * @param index the index of the byte to represent
	 * @return the hexadecimal value of the byte at the specified index
	 * @throws IndexOutOfBoundsException 
	 */
	public String getHexValueAt(int index) throws IndexOutOfBoundsException{
		if (index >= data.length || index < 0)
			throw new IndexOutOfBoundsException(index);
		return HEXMAP[(data[index]&0xF0)>>>4] + HEXMAP[data[index]&0xF];
	}
	
	/**
	 * Returns the hexadecimal representation of this data frame.
	 * @return the hexadecimal representation of this data frame
	 */
	@Override
	public String toString() {
		String out = "";
		for (byte b : data)
			out += HEXMAP[(b&0xF0)>>>4] + HEXMAP[b&0xF];
		return out;
	}
	
	/**
	 * Returns the hexadecimal representation of this data frame with the specified string separating the data.
	 * @return the hexadecimal representation of this data frame
	 */
	public String toString(String divider) {
		String out = "";
		for (byte b : data)
			out += HEXMAP[(b&0xF0)>>>4] + HEXMAP[b&0xF] + divider;
		return out;
	}
	
	/**
	 * Returns the binary representation of this data frame.
	 * @return the binary representation of this data frame
	 */
	public String toBinaryString() {
		String out = "";
		for (byte b : data)
			out += Integer.toBinaryString(b);
		return out;
	}
	
	/**
	 * Returns the amount of bits in this data frame.
	 * @return the amount of bits in this data frame
	 */
	public int length() {
		return length;
	}
	
	/**
	 * Returns the byte count of this data frame.
	 * @return the byte count of this data frame
	 */
	public int count() {
		return data.length;
	}
	
	/**
	 * Returns the frame number this data frame was initialized with.
	 * @return this data frame's initial frame number
	 */
	public int getNumber() {
		return number;
	}
	
}
