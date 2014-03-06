package com.septeni.polaris.data;

/**
 * Data spinner item <br>
 * - Type - MV value
 */
public class SpinnerMV {

	private String type; // type of item.
	private String mv; // mv value of item.

	/**
	 * Constructor initialize all variables.
	 * 
	 * @param type
	 *            the type to set.
	 * @param mv
	 *            the mv to set.
	 */
	public SpinnerMV(String type, String mv) {
		this.type = type;
		this.mv = mv;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the mv
	 */
	public String getMv() {
		return mv;
	}

	/**
	 * @param mv
	 *            the mv to set
	 */
	public void setMv(String mv) {
		this.mv = mv;
	}

	@Override
	public String toString() {
		return type + " " + mv;
	}
}
